package Model.Network;

import Model.Appointment.Appointment;
import Model.Appointment.AppointmentID;
import Model.Appointment.AppointmentType;
import Model.User;

import java.util.List;

public class Request {
    private RequestType requestType;
    private User user;
    private AppointmentID appointmentID;
    private AppointmentType appointmentType;
    private AppointmentID secondAppointmentID;
    private AppointmentType secondAppointmentType;
    private int capacity;
    private List<Appointment> appointments;
    private int label;

    public Request() {
        this.requestType = null;
        user = null;
        appointmentID = null;
        appointmentType = null;
        secondAppointmentID = null;
        secondAppointmentType = null;
        capacity = -1;
        appointments = null;
        label = 0;
    }

    public Request(RequestType requestType, String userID) {
        this.requestType = requestType;
        user = new User(userID);
        appointmentID = null;
        appointmentType = null;
        secondAppointmentID = null;
        secondAppointmentType = null;
        capacity = -1;
        appointments = null;
        label = 0;
    }

    public Request(CORBA.Network.Request request) {
        assert request.requestType != null;
        switch (request.requestType.value()) {
            case CORBA.Network.RequestType._ADD_APPOINTMENT:
                this.requestType = RequestType.ADD_APPOINTMENT;
                break;
            case CORBA.Network.RequestType._BOOK_APPOINTMENT:
                this.requestType = RequestType.BOOK_APPOINTMENT;
                break;
            case CORBA.Network.RequestType._CANCEL_APPOINTMENT:
                this.requestType = RequestType.CANCEL_APPOINTMENT;
                break;
            case CORBA.Network.RequestType._GET_APPOINTMENT_SCHEDULE:
                this.requestType = RequestType.GET_APPOINTMENT_SCHEDULE;
                break;
            case CORBA.Network.RequestType._LIST_APPOINTMENT_AVAILABILITY:
                this.requestType = RequestType.LIST_APPOINTMENT_AVAILABILITY;
                break;
            case CORBA.Network.RequestType._REMOVE_APPOINTMENT:
                this.requestType = RequestType.REMOVE_APPOINTMENT;
                break;
            case CORBA.Network.RequestType._SWAP_APPOINTMENT:
                this.requestType = RequestType.SWAP_APPOINTMENT;
                break;
            default:
                assert false;
        }

        assert request.userID != null;
        this.user = new User(request.userID);

        if (!request.appointmentID.equals(""))
            this.appointmentID = new AppointmentID(request.appointmentID);
        if (!request.secondAppointmentID.equals(""))
            this.secondAppointmentID = new AppointmentID(request.secondAppointmentID);

        if (request.appointmentType != CORBA.Network.AppointmentType.NONE) {
            this.appointmentType = NetworkAppointmentTypeToServerAppointmentType(request.appointmentType);
        }
        if (request.secondAppointmentType != CORBA.Network.AppointmentType.NONE) {
            this.secondAppointmentType = NetworkAppointmentTypeToServerAppointmentType(request.secondAppointmentType);
        }

        this.capacity = request.capacity;
        this.appointments = null;
        this.label = 0;
    }

    private AppointmentType NetworkAppointmentTypeToServerAppointmentType(CORBA.Network.AppointmentType type) {
        switch (type.value()) {
            case CORBA.Network.AppointmentType._DENTAL:
                return AppointmentType.DENTAL;
            case CORBA.Network.AppointmentType._PHYSICIAN:
                return AppointmentType.PHYSICIAN;
            case CORBA.Network.AppointmentType._SURGEON:
                return AppointmentType.SURGEON;
            default:
                assert false;
                return AppointmentType.NONE;
        }
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public User getUser() {
        return user;
    }

    public AppointmentID getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID(AppointmentID appointmentID) {
        this.appointmentID = appointmentID;
    }

    public AppointmentType getAppointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(AppointmentType appointmentType) {
        this.appointmentType = appointmentType;
    }

    public AppointmentID getSecondAppointmentID() {
        return secondAppointmentID;
    }

    public void setSecondAppointmentID(AppointmentID secondAppointmentID) {
        this.secondAppointmentID = secondAppointmentID;
    }

    public AppointmentType getSecondAppointmentType() {
        return secondAppointmentType;
    }

    public void setSecondAppointmentType(AppointmentType secondAppointmentType) {
        this.secondAppointmentType = secondAppointmentType;
    }

    public int getCapacity() {
        return capacity;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    @Override
    public String toString() {
        String toRet;
        toRet = "Request{" +
                "\n\trequestType = " + requestType +
                ",\n\tuser = " + user.getId();
        if (appointmentID != null)
            toRet += ",\n\tappointmentID = {" + appointmentID + "}";
        if (appointmentType != null)
            toRet += ",\n\tappointmentType = " + appointmentType;
        if (capacity != -1)
            toRet += ",\n\tcapacity = " + capacity;
        if (appointments != null)
            toRet += ",\n\tappointments = " + appointments;
        toRet += "\n}";
        return toRet;
    }
}
