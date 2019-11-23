package Components.Replicas.Kirby;

import CORBA.Network.RequestType;
import CORBA.Network.ServerPOA;
import Model.*;
import Model.Appointment.Appointment;
import Model.Network.Request;
import Model.Network.Response;
import Utility.Logger;
import Utility.Util;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServerWrapper extends ServerPOA {
    private LocalServer localServer;
    private UDPServer udpServer;
    private String serverName;
    private String logFileName;

    ServerWrapper(String serverName, List<ServerInfo> serverInfoList) {
        this.serverName = serverName;
        localServer = new LocalServer();
        udpServer = new UDPServer(serverName, localServer, serverInfoList);
        createSampleUsers();
        logFileName = "server_" + serverName + ".txt";
    }

    private void createSampleUsers() {
        if (serverName.equals("MTL")) {
            localServer.addUserForTest(new User("MTLA0001"));
            localServer.addUserForTest(new User("MTLP0002"));
        } else if (serverName.equals("SHE")) {
            localServer.addUserForTest(new User("SHEA0003"));
            localServer.addUserForTest(new User("SHEP0004"));
        } else { // serverName.equals("QUE")
            localServer.addUserForTest(new User("QUEA0005"));
            localServer.addUserForTest(new User("QUEP0006"));
        }
    }

    String getServerName() {
        return serverName;
    }

    //
    // Utility methods
    //

    private CORBA.Network.Response HandleRequestForAdmin(CORBA.Network.Request networkRequest, boolean sendToAllServers) {
        Request request = new Request(networkRequest);
        Response response;
        if (!request.getUser().isAdmin())
            response = new Response(false, "User is not admin.");
        else {
            if (sendToAllServers)
                response = SendRequestAndCombine(request);
            else
                response = SendRequest(request);
        }

        CORBA.Network.Response networkResponse = response.ToNetworkResponse();
        Logger.logEvent(logFileName, networkRequest, networkResponse, LocalDateTime.now());
        return networkResponse;
    }

    private CORBA.Network.Response HandleRequestForPatient(CORBA.Network.Request networkRequest) {
        return HandleRequestForPatient(networkRequest, false);
    }

    private CORBA.Network.Response HandleRequestForPatient(CORBA.Network.Request networkRequest, boolean internalRequest) {
        CORBA.Network.Response response = SendRequest(new Request(networkRequest)).ToNetworkResponse();
        if (!internalRequest)
            Logger.logEvent(logFileName, networkRequest, response, LocalDateTime.now());
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
    // Overrides
    //

    @Override
    public CORBA.Network.Response AddAppointment(CORBA.Network.Request request) {
        return HandleRequestForAdmin(request, false);
    }

    @Override
    public CORBA.Network.Response RemoveAppointment(CORBA.Network.Request request) {
        return HandleRequestForAdmin(request, false);
    }

    @Override
    public CORBA.Network.Response ListAppointmentAvailability(CORBA.Network.Request request) {
        return HandleRequestForAdmin(request, true);
    }

    @Override
    public CORBA.Network.Response BookAppointment(CORBA.Network.Request request) {
        return BookAppointment(request, false);
    }

    private CORBA.Network.Response BookAppointment(CORBA.Network.Request networkRequest, boolean internalRequest) {
        // Get all of user's appointments
        CORBA.Network.Request allAppointmentsRequest = Util.InitNetworkRequest();
        allAppointmentsRequest.requestType = CORBA.Network.RequestType.GET_APPOINTMENT_SCHEDULE;
        allAppointmentsRequest.userID = networkRequest.userID;
        CORBA.Network.Response allAppointmentResponse = GetAppointmentSchedule(allAppointmentsRequest, true);

        // Send the BookAppointment request to the right server
        Request bookAppointmentRequest = new Request(networkRequest);
        ArrayList<Appointment> appointments = new ArrayList<>();
        for (int i = 0; i < allAppointmentResponse.appointments.length; i++) {
            if (networkRequest.secondAppointmentID.equals("") ||
                    !(allAppointmentResponse.appointments[i].id.equals(networkRequest.secondAppointmentID) &&
                            allAppointmentResponse.appointments[i].type.value() == networkRequest.secondAppointmentType.value())) {
                appointments.add(new Appointment(allAppointmentResponse.appointments[i]));
            }
        }
        bookAppointmentRequest.setAppointments(appointments);
        CORBA.Network.Response networkResponse = SendRequest(bookAppointmentRequest).ToNetworkResponse();

        // Log the response
        if (!internalRequest)
            Logger.logEvent(logFileName, networkRequest, networkResponse, LocalDateTime.now());
        return networkResponse;
    }

    @Override
    public CORBA.Network.Response CancelAppointment(CORBA.Network.Request request) {
        return CancelAppointment(request, false);
    }

    private CORBA.Network.Response CancelAppointment(CORBA.Network.Request request, boolean internalRequest) {
        return HandleRequestForPatient(request, internalRequest);
    }

    @Override
    public CORBA.Network.Response SwapAppointment(CORBA.Network.Request networkRequest) {
        // Get all of user's appointments
        CORBA.Network.Request allAppointmentsRequest = Util.InitNetworkRequest();
        allAppointmentsRequest.requestType = CORBA.Network.RequestType.GET_APPOINTMENT_SCHEDULE;
        allAppointmentsRequest.userID = networkRequest.userID;
        CORBA.Network.Response allAppointmentResponse = GetAppointmentSchedule(allAppointmentsRequest, true);

        // Check if the first appointment exists in allAppointResponse
        boolean exists = false;
        for (CORBA.Network.Appointment appointment: allAppointmentResponse.appointments) {
            if (appointment.id.equals(networkRequest.appointmentID) &&
                    appointment.type.value() == networkRequest.appointmentType.value()) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            CORBA.Network.Response res =  new Response(false, "User has not booked the first appointment").ToNetworkResponse();
            Logger.logEvent(logFileName, networkRequest, res, LocalDateTime.now());
            return res;
        }

        // Book the second appointment
        CORBA.Network.Request bookRequest = Util.InitNetworkRequest();
        bookRequest.requestType = RequestType.BOOK_APPOINTMENT;
        bookRequest.userID = networkRequest.userID;
        bookRequest.appointmentID = networkRequest.secondAppointmentID;
        bookRequest.appointmentType = networkRequest.secondAppointmentType;
        bookRequest.secondAppointmentID = networkRequest.appointmentID;
        bookRequest.secondAppointmentType = networkRequest.appointmentType;
        CORBA.Network.Response bookResponse = BookAppointment(bookRequest, true);

        // Check if there were no failures
        if (!bookResponse.success) {
            bookResponse.message = "Second Appointment -> " + bookResponse.message;
            Logger.logEvent(logFileName, networkRequest, bookResponse, LocalDateTime.now());
            return bookResponse;
        }

        // Cancel the first appointment
        CORBA.Network.Request cancelRequest = Util.InitNetworkRequest();
        cancelRequest.requestType = RequestType.CANCEL_APPOINTMENT;
        cancelRequest.userID = networkRequest.userID;
        cancelRequest.appointmentID = networkRequest.appointmentID;
        cancelRequest.appointmentType = networkRequest.appointmentType;

        CORBA.Network.Response networkResponse = CancelAppointment(cancelRequest, true);
        Logger.logEvent(logFileName, networkRequest, networkResponse, LocalDateTime.now());
        return networkResponse;
    }

    @Override
    public CORBA.Network.Response GetAppointmentSchedule(CORBA.Network.Request networkRequest) {
        return GetAppointmentSchedule(networkRequest, false);
    }

    private CORBA.Network.Response GetAppointmentSchedule(CORBA.Network.Request networkRequest, boolean internalRequest) {
        CORBA.Network.Response networkResponse = SendRequestAndCombine(new Request(networkRequest)).ToNetworkResponse();
        if (!internalRequest)
            Logger.logEvent(logFileName, networkRequest, networkResponse, LocalDateTime.now());
        return networkResponse;
    }
}
