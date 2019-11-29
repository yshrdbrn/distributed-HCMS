package Components.Replicas.Richter;

import Components.Replicas.Replica;
import Model.Network.Request;
import Model.Network.Response;
import Components.Replicas.Richter.Model.*;
import Utility.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Richter extends Replica {
    public static ArrayList<Server> servers = new ArrayList<Server>();
    private Router MTLRouter;
    private Router QUERouter;
    private Router SHERouter;

    public Richter() {
        ArrayList<Connection> connections = null;
        try {
            connections = initConnections();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Server MTLServer = new Server("MTL", connections);
        Server QUEServer = new Server("QUE", connections);
        Server SHEServer = new Server("SHE", connections);

        servers.add(MTLServer);
        servers.add(QUEServer);
        servers.add(SHEServer);

        MTLRouter = new Router(MTLServer);
        QUERouter = new Router(QUEServer);
        SHERouter = new Router(SHEServer);

        MTLRouter.setServers(servers);
        QUERouter.setServers(servers);
        SHERouter.setServers(servers);
    }

    private static ArrayList<Connection> initConnections() throws UnknownHostException {
        ArrayList<Connection> connections = new ArrayList<>();
        connections.add(new Connection("MTL", InetAddress.getLocalHost(), 5000));
        connections.add(new Connection("QUE", InetAddress.getLocalHost(), 5005));
        connections.add(new Connection("SHE", InetAddress.getLocalHost(), 5010));
        return connections;
    }

    private Router getProperRouter(Request request) {
        Router toReturn = null;
        switch (request.getUser().getCity()) {
            case "MTL":
                toReturn = MTLRouter;
                break;
            case "QUE":
                toReturn = QUERouter;
                break;
            case "SHE":
                toReturn = SHERouter;
                break;
        }

        return toReturn;
    }
    @Override
        public Response resolveRequest(Request request) {
        Router router = getProperRouter(request);
        String appointmentID = "";
        String secondAppointmentID = "";
        if (request.getAppointmentID() != null)
            appointmentID = request.getAppointmentID().getRawID();
        if (request.getSecondAppointmentID() != null)
            secondAppointmentID = request.getSecondAppointmentID().getRawID();

        AppointmentType appointmentType = null;
        AppointmentType secondAppointmentType = null;
        if (request.getAppointmentType() != null)
            appointmentType = AppointmentType.toLocalAppointmentType(request.getAppointmentType());
        if (request.getSecondAppointmentType() != null) {
            secondAppointmentType = AppointmentType.toLocalAppointmentType(request.getSecondAppointmentType());
        }

        String patientID = "";
        if (request.getUser() != null)
            patientID = request.getUser().getId();

        int capacity = request.getCapacity();

        Response response = null;
        switch (request.getRequestType()) {
            case ADD_APPOINTMENT:
                response = Result.toGeneralResponse(router.addAppointment(appointmentID, appointmentType, capacity));
                Logger.logEvent("Richter_" + request.getAppointmentID().getCity() + ".txt", request, response, LocalDateTime.now());
                return response;
            case REMOVE_APPOINTMENT:
                response = Result.toGeneralResponse(router.removeAppointment(appointmentID, appointmentType));
                Logger.logEvent("Richter_" + request.getAppointmentID().getCity() + ".txt", request, response, LocalDateTime.now());
                return response;
            case LIST_APPOINTMENT_AVAILABILITY:
                response = Result.toGeneralResponse(router.listAppointmentAvailability(appointmentType));
                Logger.logEvent("Richter_" + request.getAppointmentID().getCity() + ".txt", request, response, LocalDateTime.now());
                return response;
            case BOOK_APPOINTMENT:
                response = Result.toGeneralResponse(router.bookAppointment(patientID, appointmentID, appointmentType));
                Logger.logEvent("Richter_" + request.getAppointmentID().getCity() + ".txt", request, response, LocalDateTime.now());
                return response;
            case CANCEL_APPOINTMENT:
                response = Result.toGeneralResponse(router.cancelAppointment(patientID, appointmentID, appointmentType));
                Logger.logEvent("Richter_" + request.getAppointmentID().getCity() + ".txt", request, response, LocalDateTime.now());
                return response;
            case GET_APPOINTMENT_SCHEDULE:
                response = Result.toGeneralResponse(router.getAppointmentSchedule(patientID));
                Logger.logEvent("Richter_" + request.getAppointmentID().getCity() + ".txt", request, response, LocalDateTime.now());
                return response;
            case SWAP_APPOINTMENT:
                response = Result.toGeneralResponse(router.swapAppointment(patientID, appointmentID, appointmentType, secondAppointmentID, secondAppointmentType));
                Logger.logEvent("Richter_" + request.getAppointmentID().getCity() + ".txt", request, response, LocalDateTime.now());
                return response;
        }
        return null;
    }
}
