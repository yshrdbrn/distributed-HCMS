package Model.Appointment;

public enum AppointmentType {
    PHYSICIAN, SURGEON, DENTAL, NONE;

    private CORBA.Network.AppointmentType networkType;

    static {
        PHYSICIAN.networkType = CORBA.Network.AppointmentType.PHYSICIAN;
        SURGEON.networkType = CORBA.Network.AppointmentType.SURGEON;
        DENTAL.networkType = CORBA.Network.AppointmentType.DENTAL;
        NONE.networkType = CORBA.Network.AppointmentType.NONE;
    }

    public CORBA.Network.AppointmentType ToNetworkAppointmentType() {
        return networkType;
    }
}
