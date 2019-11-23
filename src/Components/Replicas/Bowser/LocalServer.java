package Components.Replicas.Bowser;

import Model.Appointment.Appointment;
import Model.Appointment.AppointmentID;
import Model.Appointment.AppointmentType;
import Model.Network.Request;
import Model.Network.Response;
import Model.User;

import java.time.LocalDate;
import java.util.*;

class LocalServer {
    private HashMap<String, User> userbase;
    private HashMap<AppointmentType, TreeMap<AppointmentID, Appointment>> database;

    LocalServer() {
        super();
        init();
    }

    private void init() {
        userbase = new HashMap<>();
        database = new HashMap<>();
        for(AppointmentType type: AppointmentType.values())
            database.put(type, new TreeMap<>());
    }

    // Main Gateway Methods

    Response handleRequest(Request request) {
        switch (request.getRequestType()) {
            case ADD_APPOINTMENT:
                return addAppointment(request.getAppointmentID(), request.getAppointmentType(), request.getCapacity());
            case REMOVE_APPOINTMENT:
                return removeAppointment(request.getAppointmentID(), request.getAppointmentType());
            case LIST_APPOINTMENT_AVAILABILITY:
                return listAppointmentAvailability(request.getAppointmentType());
            case BOOK_APPOINTMENT:
                return bookAppointment(request.getUser(), request.getAppointmentID(), request.getAppointmentType(), request.getAppointments());
            case CANCEL_APPOINTMENT:
                return cancelAppointment(request.getUser(), request.getAppointmentID(), request.getAppointmentType());
            case GET_APPOINTMENT_SCHEDULE:
                return getAppointmentSchedule(request.getUser());
        }

        // Shouldn't reach this line
        assert false;
        return null;
    }

    String GetFullID(String id) {
        User user = userbase.get(id);
        if (user != null)
            return user.getId();
        return "-1";
    }

    void addUserForTest(User user) {
        userbase.put(user.getLocalID(), user);
    }

    // Admin Methods

    private synchronized Response addAppointment(AppointmentID appointmentID, AppointmentType appointmentType, int capacity) {
        Appointment appointment = database.get(appointmentType).get(appointmentID);
        // Appointment already exists
        if (appointment != null)
            return new Response(false, "Appointment already exists.");

        appointment = new Appointment(appointmentID, appointmentType, capacity);
        database.get(appointmentType).put(appointment.getId(), appointment);
        return new Response(true);
    }

    private synchronized Response removeAppointment(AppointmentID appointmentID, AppointmentType appointmentType) {
        Appointment appointment = database.get(appointmentType).get(appointmentID);
        if (appointment == null)
            return new Response(false, "Appointment does not exist.");

        Collection<Appointment> appointmentsAfter = database.get(appointmentType).tailMap(appointmentID, false).values();
        ArrayList<User> users = appointment.getUsers();

        int totalAvailableSpaces = 0;
        for (Appointment ap : appointmentsAfter)
            totalAvailableSpaces += ap.getCapacity();
        if (totalAvailableSpaces < users.size())
            return new Response(false, "Cannot place appointment's patients to other appointments");

        for (User u: users) {
            for (Appointment ap : appointmentsAfter) {
                if (!ap.isFull()) {
                    ap.addUser(u);
                    break;
                }
            }
        }
        database.get(appointmentType).remove(appointmentID);
        return new Response(true);
    }

    private synchronized Response listAppointmentAvailability(AppointmentType appointmentType) {
        return new Response(true, new ArrayList<>(database.get(appointmentType).values()));
    }

    // Patient Methods

    private synchronized Response bookAppointment(User user, AppointmentID appointmentID, AppointmentType appointmentType, List<Appointment> userAppointments) {
        Appointment appointment = database.get(appointmentType).get(appointmentID);
        if (appointment == null)
            return new Response(false, "Appointment does not exist.");
        if (appointment.isFull())
            return new Response(false, "Appointment is full.");
        if (appointment.userAlreadyBooked(user))
            return new Response(false, "User already booked this appointment");

        return checkBookingRelatedConditions(user, appointment, userAppointments);
    }

    private Response checkBookingRelatedConditions(User user, Appointment appointment, List<Appointment> userAppointments) {
        // Check if user already booked any appointment with the same day
        // and same appointment type.
        for (Appointment userAppointment : userAppointments) {
            if (userAppointment.getType().equals(appointment.getType()) && userAppointment.getId().getDate().equals(appointment.getId().getDate()))
                return new Response(false, "User already has an appointment with the same type for this day.");
        }

        if (!user.getCity().equals(appointment.getId().getCity())) {
            // Check if user already booked 3 appointments outside their city within the week
            LocalDate appointmentDate = appointment.getId().getDate();
            LocalDate lastSunday = appointmentDate.minusDays(appointmentDate.getDayOfWeek().getValue());
            LocalDate nextMonday = appointmentDate.plusDays(8 - appointmentDate.getDayOfWeek().getValue());
            int totalAppointmentsOutside = 0;
            for (Appointment userAppointment : userAppointments) {
                LocalDate userAppointmentDate = userAppointment.getId().getDate();
                String city = userAppointment.getId().getCity();
                if (!city.equals(user.getCity()) && userAppointmentDate.isAfter(lastSunday) && userAppointmentDate.isBefore(nextMonday))
                    totalAppointmentsOutside++;
            }
            if (totalAppointmentsOutside >= 3)
                return new Response(false, "User already has 3 appointments outside their city within the week.");
        }

        appointment.addUser(user);
        return new Response(true);
    }


    private synchronized Response cancelAppointment(User patient, AppointmentID appointmentID, AppointmentType appointmentType) {
        Appointment appointment = database.get(appointmentType).get(appointmentID);
        if (appointment == null)
            return new Response(false, "Appointment does not exist.");
        if (!appointment.userAlreadyBooked(patient))
            return new Response(false, "User did not book this appointment before");

        appointment.removeUser(patient);
        return new Response(true);
    }

    private synchronized Response getAppointmentSchedule(User user) {
        ArrayList<Appointment> appointments = new ArrayList<>();
        for (AppointmentType type : AppointmentType.values()) {
            for (Appointment appointment : database.get(type).values()) {
                if (appointment.userAlreadyBooked(user))
                    appointments.add(appointment);
            }
        }
        return new Response(true, appointments);
    }
}
