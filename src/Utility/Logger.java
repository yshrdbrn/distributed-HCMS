package Utility;

import CORBA.Network.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class Logger {
    public static void logEvent(String logFileName, Request request, Response response, LocalDateTime dateTime) {
        PrintWriter printWriter = null;
        try {
            FileWriter fileWriter = new FileWriter("logs/" + logFileName, true);
            printWriter = new PrintWriter(fileWriter);
            printWriter.println("***");
            printWriter.println("dateTime = " + dateTime);
            printWriter.println(Print(request));
            printWriter.println(Print(response, request.userID.charAt(3) == 'A'));
            printWriter.println("\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (printWriter != null)
                printWriter.close();
        }
    }

    public static void logEvent(String logFileName, Model.Network.Request request, Model.Network.Response response, LocalDateTime dateTime) {
        PrintWriter printWriter = null;
        try {
            FileWriter fileWriter = new FileWriter("logs/" + logFileName, true);
            printWriter = new PrintWriter(fileWriter);
            printWriter.println("***");
            printWriter.println("dateTime = " + dateTime);
            printWriter.println(request);
            printWriter.println(response);
            printWriter.println("\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (printWriter != null)
                printWriter.close();
        }
    }

    public static String Print(Request request) {
        String toRet;
        toRet = "Request{" +
                "\n\trequestType = " + Print(request.requestType) +
                ",\n\tuser = " + request.userID;
        if (!request.appointmentID.equals(""))
            toRet += ",\n\tappointmentID = " + request.appointmentID;
        if (request.appointmentType != AppointmentType.NONE)
            toRet += ",\n\tappointmentType = " + Print(request.appointmentType);
        if (request.capacity != -1)
            toRet += ",\n\tcapacity = " + request.capacity;
        toRet += "\n}";
        return toRet;
    }

    public static String Print(Response response, boolean isAdmin) {
        String toRet;
        toRet = "Response{" +
                "\n\tsuccessful = " + response.success +
                ",\n\tmessage = '" + response.message + '\'';
        if (response.appointments.length != 0) {
            toRet += ",\n\tappointments = [";
            for (Appointment appointment : response.appointments) {
                toRet += "\n\t\t{" + Print(appointment, isAdmin) + "},";
            }
            toRet += "\n\t],";
        }
        toRet += "\n}";
        return toRet;
    }

    public static String Print(Appointment appointment) {
        return Print(appointment, false);
    }

    public static String Print(Appointment appointment, boolean printCapacity) {
        String toRet = "Appointment ID = " + appointment.id;
        toRet += ", Type = " + Print(appointment.type);
        if (printCapacity)
            toRet += ", Capacity = " + (appointment.maxCapacity - appointment.users.length);
        return toRet;
    }

    public static String Print(AppointmentType appointmentType) {
        String toRet = "";
        switch (appointmentType.value()) {
            case AppointmentType._PHYSICIAN:
                toRet = "PHYSICIAN";
                break;
            case AppointmentType._SURGEON:
                toRet = "SURGEON";
                break;
            case AppointmentType._DENTAL:
                toRet = "DENTAL";
                break;
            default:
                assert false;
        }
        return toRet;
    }

    public static String Print(RequestType requestType) {
        String toRet = "";
        switch (requestType.value()) {
            case RequestType._BOOK_APPOINTMENT:
                toRet = "BOOK_APPOINTMENT";
                break;
            case RequestType._CANCEL_APPOINTMENT:
                toRet = "CANCEL_APPOINTMENT";
                break;
            case RequestType._SWAP_APPOINTMENT:
                toRet = "SWAP_APPOINTMENT";
                break;
            case RequestType._GET_APPOINTMENT_SCHEDULE:
                toRet = "GET_APPOINTMENT_SCHEDULE";
                break;
            case RequestType._ADD_APPOINTMENT:
                toRet = "ADD_APPOINTMENT";
                break;
            case RequestType._REMOVE_APPOINTMENT:
                toRet = "REMOVE_APPOINTMENT";
                break;
            case RequestType._LIST_APPOINTMENT_AVAILABILITY:
                toRet = "LIST_APPOINTMENT_AVAILABILITY";
                break;
            default:
                assert false;
        }

        return toRet;
    }
}
