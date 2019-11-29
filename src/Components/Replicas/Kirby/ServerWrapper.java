package Components.Replicas.Kirby;

import Model.Appointment.Appointment;
import Model.Network.Request;
import Model.Network.Response;
import Utility.Logger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class ServerWrapper {
    private LocalServer localServer;
    private UDPServer udpServer;
    private String serverName;
    private String logFileName;

    ServerWrapper(String serverName, List<ServerInfo> serverInfoList) {
        this.serverName = serverName;
        localServer = new LocalServer();
        udpServer = new UDPServer(serverName, localServer, serverInfoList);
        logFileName = "server_" + serverName + ".txt";
    }

    String getServerName() {
        return serverName;
    }

    Response resolveRequest(Request request) {
        switch (request.getRequestType()) {
            case ADD_APPOINTMENT:
                return addAppointment(request);
            case REMOVE_APPOINTMENT:
                return removeAppointment(request);
            case LIST_APPOINTMENT_AVAILABILITY:
                return listAppointmentAvailability(request);
            case BOOK_APPOINTMENT:
                return bookAppointment(request);
            case CANCEL_APPOINTMENT:
                return cancelAppointment(request);
            case SWAP_APPOINTMENT:
                return swapAppointment(request);
            case GET_APPOINTMENT_SCHEDULE:
                return getAppointmentSchedule(request);
            default:
                assert false;
                return null;
        }
    }

    //
    // Utility methods
    //

    private Response HandleRequestForAdmin(Request request, boolean sendToAllServers) {
        Response response;
        if (!request.getUser().isAdmin())
            response = new Response(false, "User is not admin.");
        else {
            if (sendToAllServers)
                response = SendRequestAndCombine(request);
            else
                response = SendRequest(request);
        }

        Logger.logEvent(logFileName, request, response, LocalDateTime.now());
        return response;
    }

    private Response HandleRequestForPatient(Request request, boolean internalRequest) {
        Response response = SendRequest(request);
        if (!internalRequest)
            Logger.logEvent(logFileName, request, response, LocalDateTime.now());
        return response;
    }


    private Response SendRequest(Request request) {
        if (request.getAppointmentID().getCity().equals(serverName))
            return localServer.handleRequest(request);
        else
            return udpServer.sendRequest(request);
    }

    private Response SendRequestAndCombine(Request request) {
        Response remoteResponse = udpServer.sendRequest(request);
        Response localResponse = localServer.handleRequest(request);
        remoteResponse.getData().addAll(localResponse.getData());
        return remoteResponse;
    }

    //
    // Individual functions
    //

    private Response addAppointment(Request request) {
        return HandleRequestForAdmin(request, false);
    }

    private Response removeAppointment(Request request) {
        return HandleRequestForAdmin(request, false);
    }

    private Response listAppointmentAvailability(Request request) {
        return HandleRequestForAdmin(request, true);
    }

    private Response bookAppointment(Request request) {
        return bookAppointment(request, false);
    }

    private Response bookAppointment(Request request, boolean internalRequest) {
        // Get all of user's appointments
        Request allAppointmentsRequest = new Request(Model.Network.RequestType.GET_APPOINTMENT_SCHEDULE, request.getUser().getId());
        Response allAppointmentResponse = getAppointmentSchedule(allAppointmentsRequest, true);

        // Send the BookAppointment request to the right server
        ArrayList<Appointment> appointments = new ArrayList<>();
        if (allAppointmentResponse.getData() != null) {
            List<Appointment> userAppointments = allAppointmentResponse.getData();
            for (Appointment userAppointment : userAppointments) {
                if (request.getSecondAppointmentID() != null ||
                        !(userAppointment.equals(new Appointment(request.getSecondAppointmentID(), request.getSecondAppointmentType(), 0)))) {
                    appointments.add(userAppointment);
                }
            }
        }
        request.setAppointments(appointments);
        Response response = SendRequest(request);

        // Log the response
        if (!internalRequest)
            Logger.logEvent(logFileName, request, response, LocalDateTime.now());
        return response;
    }

    private Response cancelAppointment(Request request) {
        return cancelAppointment(request, false);
    }

    private Response cancelAppointment(Request request, boolean internalRequest) {
        return HandleRequestForPatient(request, internalRequest);
    }

    private Response swapAppointment(Request request) {
        // Get all of user's appointments
        Request allAppointmentsRequest = new Request(Model.Network.RequestType.GET_APPOINTMENT_SCHEDULE, request.getUser().getId());
        Response allAppointmentResponse = getAppointmentSchedule(allAppointmentsRequest, true);

        // Check if the first appointment exists in allAppointResponse
        boolean exists = false;
        if (allAppointmentResponse.getData() != null) {
            for (Appointment appointment : allAppointmentResponse.getData()) {
                if (appointment.getId().equals(request.getAppointmentID()) && appointment.getType() == request.getAppointmentType()) {
                    exists = true;
                    break;
                }
            }
        }
        if (!exists) {
            Response res =  new Response(false, "User has not booked the first appointment");
            Logger.logEvent(logFileName, request, res, LocalDateTime.now());
            return res;
        }

        // Book the second appointment
        Request bookRequest = new Request(Model.Network.RequestType.BOOK_APPOINTMENT, request.getUser().getId());
        bookRequest.setAppointmentID(request.getSecondAppointmentID());
        bookRequest.setAppointmentType(request.getSecondAppointmentType());
        bookRequest.setSecondAppointmentID(request.getAppointmentID());
        bookRequest.setSecondAppointmentType(request.getAppointmentType());
        Response bookResponse = bookAppointment(bookRequest, true);

        // Check if there were no failures
        if (!bookResponse.isSuccessful()) {
            bookResponse.setMessage("Second Appointment -> " + bookResponse.getMessage());
            Logger.logEvent(logFileName, request, bookResponse, LocalDateTime.now());
            return bookResponse;
        }

        // Cancel the first appointment
        Request cancelRequest = new Request(Model.Network.RequestType.CANCEL_APPOINTMENT, request.getUser().getId());
        cancelRequest.setAppointmentID(request.getAppointmentID());
        cancelRequest.setAppointmentType(request.getAppointmentType());

        Response response = cancelAppointment(cancelRequest, true);
        Logger.logEvent(logFileName, request, response, LocalDateTime.now());
        return response;
    }

    private Response getAppointmentSchedule(Request request) {
        return getAppointmentSchedule(request, false);
    }

    private Response getAppointmentSchedule(Request request, boolean internalRequest) {
        Response response = SendRequestAndCombine(request);
        if (!internalRequest)
            Logger.logEvent(logFileName, request, response, LocalDateTime.now());
        return response;
    }
}
