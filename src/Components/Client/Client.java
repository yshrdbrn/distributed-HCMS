package Components.Client;

import CORBA.Network.*;
import Utility.Logger;
import Utility.Util;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

class Client {
    private Scanner scanner;

    private Server userServer;
    private String userID;
    private String logFileName;

    private Request currentRequest;

    Client(Server userServer, String userID) {
        this.userServer = userServer;
        this.userID = userID;
        scanner = new Scanner(System.in);
        logFileName = "user_" + userID + ".txt";
    }

    void start() {
        try {
            mainLoop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean IsUserAdmin() {
        return userID.charAt(3) == 'A';
    }

    private void mainLoop() {
        ArrayList<RequestType> requestTypes = new ArrayList<>(Arrays.asList(RequestType.BOOK_APPOINTMENT,
                                                                            RequestType.CANCEL_APPOINTMENT,
                                                                            RequestType.SWAP_APPOINTMENT,
                                                                            RequestType.GET_APPOINTMENT_SCHEDULE));
        if (IsUserAdmin())
            requestTypes.addAll(Arrays.asList(RequestType.ADD_APPOINTMENT,
                                              RequestType.REMOVE_APPOINTMENT,
                                              RequestType.LIST_APPOINTMENT_AVAILABILITY));

        while (true) {
            int choice = getOperation(requestTypes);
            if (choice == -1)
                break;
            currentRequest = Util.InitNetworkRequest();

            switch (choice) {
                case 1:
                    currentRequest.requestType = RequestType.BOOK_APPOINTMENT;
                    currentRequest.userID = getPatientID();
                    BookAppointment();
                    break;
                case 2:
                    currentRequest.requestType = RequestType.CANCEL_APPOINTMENT;
                    currentRequest.userID = getPatientID();
                    CancelAppointment();
                    break;
                case 3:
                    currentRequest.requestType = RequestType.SWAP_APPOINTMENT;
                    currentRequest.userID = getPatientID();
                    SwapAppointment();
                    break;
                case 4:
                    currentRequest.requestType = RequestType.GET_APPOINTMENT_SCHEDULE;
                    currentRequest.userID = getPatientID();
                    GetAppointmentSchedule();
                    break;
                case 5:
                    currentRequest.requestType = RequestType.ADD_APPOINTMENT;
                    currentRequest.userID = getPatientID();
                    AddAppointment();
                    break;
                case 6:
                    currentRequest.requestType = RequestType.REMOVE_APPOINTMENT;
                    currentRequest.userID = getPatientID();
                    RemoveAppointment();
                    break;
                case 7:
                    currentRequest.requestType = RequestType.LIST_APPOINTMENT_AVAILABILITY;
                    currentRequest.userID = getPatientID();
                    ListAppointmentAvailability();
                    break;
                default:
                    assert false;
            }
        }
    }

    private int getOperation(ArrayList<RequestType> requestTypes) {
        System.out.println("\nPlease select an operation or -1 to exit:");
        for (int i = 0; i < requestTypes.size(); i++) {
            System.out.print((i+1) + ". ");
            switch (requestTypes.get(i).value()) {
                case RequestType._BOOK_APPOINTMENT:
                    System.out.println("Book an appointment");
                    break;
                case RequestType._CANCEL_APPOINTMENT:
                    System.out.println("Cancel an appointment");
                    break;
                case RequestType._SWAP_APPOINTMENT:
                    System.out.println("Swap an appointment with another appointment");
                    break;
                case RequestType._GET_APPOINTMENT_SCHEDULE:
                    System.out.println("Get appointments schedule");
                    break;
                case RequestType._ADD_APPOINTMENT:
                    System.out.println("Add an appointment");
                    break;
                case RequestType._REMOVE_APPOINTMENT:
                    System.out.println("Remove an appointment");
                    break;
                case RequestType._LIST_APPOINTMENT_AVAILABILITY:
                    System.out.println("List all the appointment availabilities");
                    break;
                default:
                    assert false;
            }
        }

        int choice;
        while (true) {
            choice = scanner.nextInt();
            if (choice == -1 || (choice >= 1 && choice <= requestTypes.size()))
                break;
            System.out.println("Error. enter the choice again.");
        }
        return choice;
    }

    // User interaction methods with server

    private void BookAppointment() {
        currentRequest.appointmentID = getAppointmentID();
        currentRequest.appointmentType = getAppointmentType();
        Response response = userServer.BookAppointment(currentRequest);
        System.out.println(response.message);

        Logger.logEvent(logFileName, currentRequest, response, LocalDateTime.now());
    }

    private void CancelAppointment() {
        currentRequest.appointmentID = getAppointmentID();
        currentRequest.appointmentType = getAppointmentType();
        Response response = userServer.CancelAppointment(currentRequest);
        System.out.println(response.message);

        Logger.logEvent(logFileName, currentRequest, response, LocalDateTime.now());
    }

    private void SwapAppointment() {
        System.out.println("First appointment:");
        currentRequest.appointmentID = getAppointmentID();
        currentRequest.appointmentType = getAppointmentType();
        System.out.println("Second appointment:");
        currentRequest.secondAppointmentID = getAppointmentID();
        currentRequest.secondAppointmentType = getAppointmentType();

        Response response = userServer.SwapAppointment(currentRequest);
        System.out.println(response.message);

        Logger.logEvent(logFileName, currentRequest, response, LocalDateTime.now());
    }

    private void GetAppointmentSchedule() {
        Response response = userServer.GetAppointmentSchedule(currentRequest);
        if (response.success)
            for (Appointment appointment : response.appointments)
                System.out.println(Logger.Print(appointment));
        else
            System.out.println(response.message);

        Logger.logEvent(logFileName, currentRequest, response, LocalDateTime.now());
    }

    private void AddAppointment() {
        currentRequest.appointmentID = getAppointmentID();
        currentRequest.appointmentType = getAppointmentType();
        System.out.println("Enter the capacity:");
        currentRequest.capacity = scanner.nextInt();
        Response response = userServer.AddAppointment(currentRequest);
        System.out.println(response.message);

        Logger.logEvent(logFileName, currentRequest, response, LocalDateTime.now());
    }

    private void RemoveAppointment() {
        currentRequest.appointmentID = getAppointmentID();
        currentRequest.appointmentType = getAppointmentType();
        Response response = userServer.RemoveAppointment(currentRequest);
        System.out.println(response.message);

        Logger.logEvent(logFileName, currentRequest, response, LocalDateTime.now());
    }

    private void ListAppointmentAvailability() {
        currentRequest.appointmentType = getAppointmentType();
        Response response = userServer.ListAppointmentAvailability(currentRequest);
        if (response.success)
            for (Appointment appointment : response.appointments)
                System.out.println(Logger.Print(appointment, true));
        else
            System.out.println(response.message);

        Logger.logEvent(logFileName, currentRequest, response, LocalDateTime.now());
    }

    private String getPatientID() {
        if (!IsUserAdmin())
            return userID;
        if (currentRequest.requestType == RequestType.ADD_APPOINTMENT ||
            currentRequest.requestType == RequestType.REMOVE_APPOINTMENT ||
                currentRequest.requestType == RequestType.LIST_APPOINTMENT_AVAILABILITY
        )
            return userID;

        System.out.println("Enter the patient ID:");
        return scanner.next();
    }

    private String getAppointmentID() {
        System.out.println("Enter the appointment ID:");
        String toReturn;
        while (true) {
            toReturn = scanner.next();
            if (IsAppointmentValid(toReturn))
                break;
            System.out.println("Invalid appointment ID. Try again.");
        }
        return toReturn;
    }

    private AppointmentType getAppointmentType() {
        String[] types = {"Physician", "Surgeon", "Dental"};
        System.out.println("Choose one of the appointment types below:");
        for (int i = 0; i < types.length; i++)
            System.out.println((i+1) + ". " + types[i]);

        int choice;
        while (true) {
            choice = scanner.nextInt();
            if (choice >= 1 && choice <= types.length)
                break;
            System.out.println("Error. Select again");
        }

        switch (choice) {
            case 1:
                return AppointmentType.PHYSICIAN;
            case 2:
                return AppointmentType.SURGEON;
            case 3:
                return AppointmentType.DENTAL;
            default:
                assert false;
                return null;
        }
    }

    private boolean IsAppointmentValid(String appointmentID) {
        if (appointmentID.length() != 10)
            return false;
        boolean cityValid = false;
        for (String city: ClientMain.serverNames) {
            if (city.equals(appointmentID.substring(0, 3))) {
                cityValid = true;
                break;
            }
        }
        if (!cityValid)
            return false;
        char timeOfDay = appointmentID.charAt(3);
        return timeOfDay == 'A' || timeOfDay == 'M' || timeOfDay == 'E';
    }
}
