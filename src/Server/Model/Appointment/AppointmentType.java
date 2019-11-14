package Server.Model.Appointment;

public enum AppointmentType {
    PHYSICIAN, SURGEON, DENTAL, NONE;

    private Network.AppointmentType networkType;

    static {
        PHYSICIAN.networkType = Network.AppointmentType.PHYSICIAN;
        SURGEON.networkType = Network.AppointmentType.SURGEON;
        DENTAL.networkType = Network.AppointmentType.DENTAL;
        NONE.networkType = Network.AppointmentType.NONE;
    }

    public Network.AppointmentType ToNetworkAppointmentType() {
        return networkType;
    }
}
