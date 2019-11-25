package Components.Replicas.Richter;

import Components.Replicas.Replica;
import Model.Network.Request;
import Model.Network.Response;
import Components.Replicas.Richter.Model.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

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

//        MTLServer.setDatabase(initMTLDatabase());
//        QUEServer.setDatabase(initQUEDatabase());
//        SHEServer.setDatabase(initSHEDatabase());

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

    private static HashMap<AppointmentType, HashMap<String, Appointment>> initMTLDatabase() {
        HashMap<AppointmentType, HashMap<String, Appointment>> database = new HashMap<>();
        HashMap<String, Appointment> value = new HashMap<>();
        value.put("MTLA100919", new Appointment(AppointmentType.PHYSICIAN, "MTLA100919", 3));
        value.put("MTLM051019", new Appointment(AppointmentType.PHYSICIAN, "MTLM051019", 5));
        value.put("MTLE111219", new Appointment(AppointmentType.PHYSICIAN, "MTLE111219", 4));
        database.put(AppointmentType.PHYSICIAN, value);
        value = new HashMap<>();
        value.put("MTLE101119", new Appointment(AppointmentType.DENTAL, "MTLE101119", 1));
        database.put(AppointmentType.DENTAL, value);
        value = new HashMap<>();
        value.put("MTLA051019", new Appointment(AppointmentType.SURGEON, "MTLM051019", 2));
        database.put(AppointmentType.SURGEON, value);
        return database;
    }

    private static HashMap<AppointmentType, HashMap<String, Appointment>> initQUEDatabase() {
        HashMap<AppointmentType, HashMap<String, Appointment>> database = new HashMap<>();
        HashMap<String, Appointment> value = new HashMap<>();
        value.put("QUEM100919", new Appointment(AppointmentType.DENTAL, "QUEM100919", 5));
        value.put("QUEE111019", new Appointment(AppointmentType.DENTAL, "QUEE111019", 6));
        database.put(AppointmentType.DENTAL, value);
        value = new HashMap<>();
        value.put("QUEA091119", new Appointment(AppointmentType.PHYSICIAN, "QUEA091119", 2));
        database.put(AppointmentType.PHYSICIAN, value);
        value = new HashMap<>();
        value.put("QUEM051019", new Appointment(AppointmentType.SURGEON, "QUEM051019", 2));
        database.put(AppointmentType.SURGEON, value);

        return database;
    }

    private static HashMap<AppointmentType, HashMap<String, Appointment>> initSHEDatabase() {
        HashMap<AppointmentType, HashMap<String, Appointment>> database = new HashMap<>();
        HashMap<String, Appointment> value = new HashMap<>();
        value.put("SHEM051019", new Appointment(AppointmentType.SURGEON, "SHEM051019", 2));
        database.put(AppointmentType.SURGEON, value);
        value = new HashMap<>();
        value.put("SHEM170419", new Appointment(AppointmentType.DENTAL, "SHEM170419", 2));
        database.put(AppointmentType.DENTAL, value);
        value = new HashMap<>();
        value.put("SHEE150219", new Appointment(AppointmentType.PHYSICIAN, "SHEE150219", 2));
        database.put(AppointmentType.PHYSICIAN, value);
        return database;
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
        if (request.getAppointmentID() != null)
            appointmentID = request.getAppointmentID().getRawID();
        AppointmentType appointmentType = null;
        if (request.getAppointmentType() != null)
            appointmentType = AppointmentType.toLocalAppointmentType(request.getAppointmentType());
        String patientID = "";
        if (request.getUser() != null) {
            patientID = request.getUser().getId();
        }
        int capacity = request.getCapacity();

        switch (request.getRequestType()) {
            case ADD_APPOINTMENT:
                return Result.toGeneralResponse(router.addAppointment(appointmentID, appointmentType, capacity));
            case REMOVE_APPOINTMENT:
                return Result.toGeneralResponse(router.removeAppointment(appointmentID, appointmentType));
            case LIST_APPOINTMENT_AVAILABILITY:
                return Result.toGeneralResponse(router.listAppointmentAvailability(appointmentType));
            case BOOK_APPOINTMENT:
                return Result.toGeneralResponse(router.bookAppointment(patientID, appointmentID, appointmentType));
            case CANCEL_APPOINTMENT:
                return Result.toGeneralResponse(router.cancelAppointment(patientID, appointmentID, appointmentType));
            case GET_APPOINTMENT_SCHEDULE:
                return Result.toGeneralResponse(router.getAppointmentSchedule(patientID));
        }
        return null;
    }
}
