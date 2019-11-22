package Model.Appointment;

import Model.User;

import java.util.ArrayList;

public class Appointment implements Comparable<Appointment> {
    private AppointmentID id;
    private AppointmentType type;
    private final int MAX_CAPACITY;
    private ArrayList<User> users;

    public Appointment() {
        MAX_CAPACITY = -1;
    }

    public Appointment(AppointmentID id, AppointmentType type, int MAX_CAPACITY) {
        this.id = id;
        this.type = type;
        this.MAX_CAPACITY = MAX_CAPACITY;
        users = new ArrayList<>();
    }

    public Appointment(CORBA.Network.Appointment appointment) {
        assert appointment.type != null;
        switch (appointment.type.value()) {
            case CORBA.Network.AppointmentType._PHYSICIAN:
                type = AppointmentType.PHYSICIAN;
                break;
            case CORBA.Network.AppointmentType._SURGEON:
                type = AppointmentType.SURGEON;
                break;
            case CORBA.Network.AppointmentType._DENTAL:
                type = AppointmentType.DENTAL;
                break;
            case CORBA.Network.AppointmentType._NONE:
                type = AppointmentType.NONE;
                break;
            default:
                assert false;
        }

        id = new AppointmentID(appointment.id);
        MAX_CAPACITY = appointment.maxCapacity;
        users = new ArrayList<>();
        for (int i = 0; i < appointment.users.length; i++)
            users.add(new User(appointment.users[i]));
    }

    public AppointmentID getId() {
        return id;
    }

    public AppointmentType getType() {
        return type;
    }

    public boolean isFull() {
        return users.size() >= MAX_CAPACITY;
    }

    public int getCapacity() {
        return MAX_CAPACITY - users.size();
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void removeUser(User user) {
        users.remove(user);
    }

    public boolean userAlreadyBooked(User user) {
        return users.contains(user);
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public CORBA.Network.Appointment ToNetworkAppointment() {
        CORBA.Network.Appointment toRet = new CORBA.Network.Appointment();
        toRet.id = id.getRawID();
        toRet.type = type.ToNetworkAppointmentType();
        toRet.maxCapacity = MAX_CAPACITY;

        toRet.users = new String[0];
        if (users != null) {
            toRet.users = new String[users.size()];
            for (int i = 0; i < users.size(); i++)
                toRet.users[i] = users.get(i).getId();
        }

        return toRet;
    }

    @Override
    public int compareTo(Appointment o) {
        if (id.compareTo(o.id) != 0)
            return id.compareTo(o.id);
        else
            return type.compareTo(o.type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Appointment that = (Appointment) o;

        if (!id.equals(that.id)) return false;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return id + ", Type = " + type;
    }
}
