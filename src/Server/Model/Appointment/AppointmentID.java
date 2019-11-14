package Server.Model.Appointment;

import java.time.LocalDate;

public class AppointmentID implements Comparable<AppointmentID> {
    private String rawID;
    private String city;
    private LocalDate date;
    private TimeOfDay timeOfDay;

    public AppointmentID() {
    }

    public AppointmentID(String appointmentID) {
        rawID = appointmentID;
        city = appointmentID.substring(0, 3);

        int year = 2000;
        year += Integer.parseInt(appointmentID.substring(8));
        int month = Integer.parseInt(appointmentID.substring(6, 8));
        int day = Integer.parseInt(appointmentID.substring(4, 6));
        date = LocalDate.of(year, month, day);

        switch (appointmentID.charAt(3)) {
            case 'M':
                timeOfDay = TimeOfDay.MORNING;
                break;
            case 'A':
                timeOfDay = TimeOfDay.AFTERNOON;
                break;
            case 'E':
                timeOfDay = TimeOfDay.EVENING;
                break;
        }
    }

    public String getCity() {
        return city;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getRawID() {
        return rawID;
    }

    @Override
    public int compareTo(AppointmentID o) {
        if (date.compareTo(o.date) != 0)
            return date.compareTo(o.date);
        else {
            return timeOfDay.compareTo(o.timeOfDay);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppointmentID that = (AppointmentID) o;

        if (!city.equals(that.city)) return false;
        if (!date.equals(that.date)) return false;
        return timeOfDay == that.timeOfDay;
    }

    @Override
    public int hashCode() {
        int result = city.hashCode();
        result = 31 * result + date.hashCode();
        result = 31 * result + timeOfDay.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return  "ID = '" + rawID + '\'' +
                ", City = '" + city + '\'' +
                ", Date = " + date +
                ", Time of day = " + timeOfDay;
    }
}