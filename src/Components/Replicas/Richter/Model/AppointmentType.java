package Components.Replicas.Richter.Model;

import java.io.Serializable;

public enum AppointmentType implements Serializable {
    PHYSICIAN, SURGEON, DENTAL;

    public static AppointmentType toLocalAppointmentType(Model.Appointment.AppointmentType appointmentType) {
        switch (appointmentType) {
            case DENTAL:
                return DENTAL;
            case SURGEON:
                return SURGEON;
            case PHYSICIAN:
                return PHYSICIAN;
            default:
                return null;
        }
    }

    public static Model.Appointment.AppointmentType toGeneralAppointmentType(AppointmentType appointmentType) {
        switch (appointmentType) {
            case DENTAL:
                return Model.Appointment.AppointmentType.DENTAL;
            case SURGEON:
                return Model.Appointment.AppointmentType.SURGEON;
            case PHYSICIAN:
                return Model.Appointment.AppointmentType.PHYSICIAN;
            default:
                return Model.Appointment.AppointmentType.NONE;
        }
    }

}

