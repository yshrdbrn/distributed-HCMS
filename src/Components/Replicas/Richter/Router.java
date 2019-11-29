package Components.Replicas.Richter;


import Components.Replicas.Richter.Model.*;
import Utility.Logger;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Router implements ServerInterface {
    private Server server;
    private UDPServer udpServer;
    private ArrayList<Server> servers = new ArrayList<Server>();

    public Router(Server server) {
        super();
        this.server = server;
    }

    public Router() {
        super();
    }


    @Override
    public Result addAppointment(String appointmentID, AppointmentType appointmentType, int capacity) {
        Request clientRequest = new Request("addAppointment", new Appointment(appointmentType, appointmentID, capacity));
        return hitProperServer(clientRequest);
    }

    @Override
    public Result removeAppointment(String appointmentID, AppointmentType appointmentType) {
        Request clientRequest = new Request("removeAppointment", new Appointment(appointmentType, appointmentID));
        return hitProperServer(clientRequest);
    }

    @Override
    public Result listAppointmentAvailability(AppointmentType appointmentType) {
        Request clientRequest = new Request("listAppointmentAvailability", new Appointment(appointmentType));
        Result result;
        ArrayList<Result> results = new ArrayList<>();
        //call the local server
        results.add(server.handleRequest(clientRequest));
        //call upd servers
        for(Server s : servers) {
            if(s.getServerName().equals(server.getServerName())) continue;
            udpServer = new UDPServer(getProperConnection(s), clientRequest, s);
            results.add(udpServer.sendRequest(clientRequest));
        }

        ArrayList<Appointment> allAppointments = new ArrayList<>();

        for(Result r : results) {
            if(r.getPayload() != null)
                allAppointments.addAll(r.getPayload());
        }

        result = new Result(ResultStatus.SUCCESS, allAppointments);
        result.setMessage("we found " + allAppointments.size() + " appointments for appointment type " + appointmentType);
        return result;
    }

    @Override
    public Result bookAppointment(String patientID, String appointmentID, AppointmentType appointmentType) {
        Request clientRequest = new Request("bookAppointment", new Appointment(appointmentType, appointmentID), patientID);
        return hitProperServer(clientRequest);
    }

    @Override
    public Result cancelAppointment(String patientID, String appointmentID, AppointmentType appointmentType) {
        Request clientRequest = new Request("cancelAppointment", new Appointment(appointmentType, appointmentID), patientID);
        return hitProperServer(clientRequest);
    }

    @Override
    public Result getAppointmentSchedule(String patientID) {
        Request clientRequest = new Request("getAppointmentSchedule", patientID);
        clientRequest.putDestination(patientID);
        Result result = null;
        ArrayList<Result> results = new ArrayList<>();
        //call the local server
        results.add(server.handleRequest(clientRequest));
        //call upd servers
        for(Server s : servers) {
            if(s.getServerName().equals(server.getServerName())) continue;
            udpServer = new UDPServer(getProperConnection(s), clientRequest, s);
            results.add(udpServer.sendRequest(clientRequest));
        }

        ArrayList<Appointment> allAppointments = new ArrayList<>();
        for(Result r : results) {
            if(r.getPayload() != null)
            allAppointments.addAll(r.getPayload());
        }

        result = new Result(ResultStatus.SUCCESS, allAppointments);
        result.setMessage("we found " + allAppointments.size() + " appointments for patient ID: " + patientID);
        return result;
    }

    @Override
    public Result swapAppointment(String patientID, String firstAppointmentID, AppointmentType firstAppointmentType, String secondAppointmentID, AppointmentType secondAppointmentType) {
        Appointment firstAppointment = new Appointment(firstAppointmentType, firstAppointmentID);
        Appointment secondAppointment = new Appointment(secondAppointmentType, secondAppointmentID);
        Request clientRequest = new Request("swapAppointment", firstAppointment, secondAppointment, patientID);
        return hitProperServer(clientRequest);
    }


    private Connection getProperConnection(Request clientRequest){
        for (Connection c: server.getConnections()) {
            if(c.getName().equals(clientRequest.getDestination().name())) {
                return c;
            }
        }
        return null;
    }

    private Connection getProperConnection(Server s) {
        for(Connection c : server.getConnections()) {
            if (c.getName().equals(s.getServerName())) {
                return c;
            }
        }
        return null;
    }

    private Server getProperServer(Connection connection) {
        for(Server s : servers) {
            if(s.getServerName().equals(connection.getName())) {
                return s;
            }
        }
        return null;
    }

    private Result hitProperServer(Request clientRequest) {
        Result result = null;
        if(server.getServerName().equals(clientRequest.getDestination().name())) {
            //call the local server
            result = server.handleRequest(clientRequest);
        } else {
            //call udp server
            setupUDPServer(clientRequest);
            result = udpServer.sendRequest(clientRequest);
        }
        return result;
    }

    private void setupUDPServer(Request clientRequest) {
        Connection properConnection = getProperConnection(clientRequest);
        Server properServer = getProperServer(properConnection);
        udpServer = new UDPServer(properConnection, clientRequest, properServer);
    }

    public ArrayList<Server> getServers() {
        return servers;
    }

    public void setServers(ArrayList<Server> servers) {
        this.servers = servers;
    }


}

