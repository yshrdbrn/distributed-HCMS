package Server;

import Network.AppointmentType;
import Network.RequestType;
import Network.ServerPOA;
import Server.Model.*;
import Server.Model.Appointment.Appointment;
import Server.Model.Network.Request;
import Server.Model.Network.Response;
import Utility.Logger;
import Utility.Util;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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

    private Network.Response HandleRequestForAdmin(Network.Request networkRequest, boolean sendToAllServers) {
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

        Network.Response networkResponse = response.ToNetworkResponse();
        Logger.logEvent(logFileName, networkRequest, networkResponse, LocalDateTime.now());
        return networkResponse;
    }

    private Network.Response HandleRequestForPatient(Network.Request networkRequest) {
        return HandleRequestForPatient(networkRequest, false);
    }

    private Network.Response HandleRequestForPatient(Network.Request networkRequest, boolean internalRequest) {
        Network.Response response = SendRequest(new Request(networkRequest)).ToNetworkResponse();
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
    public Network.Response AddAppointment(Network.Request request) {
        return HandleRequestForAdmin(request, false);
    }

    @Override
    public Network.Response RemoveAppointment(Network.Request request) {
        return HandleRequestForAdmin(request, false);
    }

    @Override
    public Network.Response ListAppointmentAvailability(Network.Request request) {
        return HandleRequestForAdmin(request, true);
    }

    @Override
    public Network.Response BookAppointment(Network.Request request) {
        return BookAppointment(request, false);
    }

    private Network.Response BookAppointment(Network.Request networkRequest, boolean internalRequest) {
        // Get all of user's appointments
        Network.Request allAppointmentsRequest = Util.InitNetworkRequest();
        allAppointmentsRequest.requestType = Network.RequestType.GET_APPOINTMENT_SCHEDULE;
        allAppointmentsRequest.userID = networkRequest.userID;
        Network.Response allAppointmentResponse = GetAppointmentSchedule(allAppointmentsRequest, true);

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
        Network.Response networkResponse = SendRequest(bookAppointmentRequest).ToNetworkResponse();

        // Log the response
        if (!internalRequest)
            Logger.logEvent(logFileName, networkRequest, networkResponse, LocalDateTime.now());
        return networkResponse;
    }

    @Override
    public Network.Response CancelAppointment(Network.Request request) {
        return CancelAppointment(request, false);
    }

    private Network.Response CancelAppointment(Network.Request request, boolean internalRequest) {
        return HandleRequestForPatient(request, internalRequest);
    }

    @Override
    public Network.Response SwapAppointment(Network.Request networkRequest) {
        // Get all of user's appointments
        Network.Request allAppointmentsRequest = Util.InitNetworkRequest();
        allAppointmentsRequest.requestType = Network.RequestType.GET_APPOINTMENT_SCHEDULE;
        allAppointmentsRequest.userID = networkRequest.userID;
        Network.Response allAppointmentResponse = GetAppointmentSchedule(allAppointmentsRequest, true);

        // Check if the first appointment exists in allAppointResponse
        boolean exists = false;
        for (Network.Appointment appointment: allAppointmentResponse.appointments) {
            if (appointment.id.equals(networkRequest.appointmentID) &&
                    appointment.type.value() == networkRequest.appointmentType.value()) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            Network.Response res =  new Response(false, "User has not booked the first appointment").ToNetworkResponse();
            Logger.logEvent(logFileName, networkRequest, res, LocalDateTime.now());
            return res;
        }

        // Book the second appointment
        Network.Request bookRequest = Util.InitNetworkRequest();
        bookRequest.requestType = RequestType.BOOK_APPOINTMENT;
        bookRequest.userID = networkRequest.userID;
        bookRequest.appointmentID = networkRequest.secondAppointmentID;
        bookRequest.appointmentType = networkRequest.secondAppointmentType;
        bookRequest.secondAppointmentID = networkRequest.appointmentID;
        bookRequest.secondAppointmentType = networkRequest.appointmentType;
        Network.Response bookResponse = BookAppointment(bookRequest, true);

        // Check if there were no failures
        if (!bookResponse.success) {
            bookResponse.message = "Second Appointment -> " + bookResponse.message;
            Logger.logEvent(logFileName, networkRequest, bookResponse, LocalDateTime.now());
            return bookResponse;
        }

        // Cancel the first appointment
        Network.Request cancelRequest = Util.InitNetworkRequest();
        cancelRequest.requestType = RequestType.CANCEL_APPOINTMENT;
        cancelRequest.userID = networkRequest.userID;
        cancelRequest.appointmentID = networkRequest.appointmentID;
        cancelRequest.appointmentType = networkRequest.appointmentType;

        Network.Response networkResponse = CancelAppointment(cancelRequest, true);
        Logger.logEvent(logFileName, networkRequest, networkResponse, LocalDateTime.now());
        return networkResponse;
    }

    @Override
    public Network.Response GetAppointmentSchedule(Network.Request networkRequest) {
        return GetAppointmentSchedule(networkRequest, false);
    }

    private Network.Response GetAppointmentSchedule(Network.Request networkRequest, boolean internalRequest) {
        Network.Response networkResponse = SendRequestAndCombine(new Request(networkRequest)).ToNetworkResponse();
        if (!internalRequest)
            Logger.logEvent(logFileName, networkRequest, networkResponse, LocalDateTime.now());
        return networkResponse;
    }

    @Override
    public String GetFullID(String id) {
        return localServer.GetFullID(id);
    }
}
