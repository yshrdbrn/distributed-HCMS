package Components.Replicas.Richter;

import Components.Replicas.Richter.Model.*;
public interface ServerInterface {

    Result addAppointment(String appointmentID, AppointmentType appointmentType, int capacity);
    Result removeAppointment(String appointmentID, AppointmentType appointmentType);
    Result listAppointmentAvailability (AppointmentType appointmentType);
    Result bookAppointment (String patientID, String appointmentID, AppointmentType appointmentType);
    Result cancelAppointment (String patientID, String appointmentID, AppointmentType appointmentType);
    Result getAppointmentSchedule (String patientID);
    Result swapAppointment(String patienID, String firstAppointmentID, AppointmentType firstAppointmentType, String secondAppointmentID, AppointmentType secondAppointmentType);
}
