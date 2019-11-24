package Model.Network;

import Model.Appointment.Appointment;

import java.util.Collections;
import java.util.List;

public class Response {
    private boolean successful;
    private String message;
    private List<Appointment> data;

    public Response() {
    }

    // Assumes the input is always TRUE
    public Response(boolean successful) {
        this.successful = successful;
        this.data = null;
        message = "Success.";
    }

    public Response(boolean successful, String message) {
        this.successful = successful;
        this.message = "Error: " + message;
        data = null;
    }

    public Response(boolean successful, List<Appointment> data) {
        this.successful = successful;
        this.data = data;
        message = "Success.";
    }

    public boolean isSuccessful() {
        return successful;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Appointment> getData() {
        return data;
    }

    public CORBA.Network.Response ToNetworkResponse() {
        CORBA.Network.Response toRet = new CORBA.Network.Response();
        toRet.success = successful;
        toRet.message = message;
        toRet.appointments = new CORBA.Network.Appointment[0];
        if (data != null) {
            toRet.appointments = new CORBA.Network.Appointment[data.size()];
            for (int i = 0; i < data.size(); i++)
                toRet.appointments[i] = data.get(i).ToNetworkAppointment();
        }

        return toRet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Response response = (Response) o;

        if (successful != response.successful) return false;
        if (!message.equals(response.message)) return false;
        if (data != null ^ response.data != null) return false;
        if (data == null) return true;
        if (data.size() != response.data.size()) return false;
        // Compare their data
        Collections.sort(data);
        Collections.sort(response.data);
        for (int i = 0; i < data.size(); i++) {
            if (!data.get(i).equals(response.data.get(i)))
                return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = (successful ? 1 : 0);
        result = 31 * result + message.hashCode();
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        String toRet;
        toRet = "Response{" +
                "\n\tsuccessful=" + successful +
                ",\n\tmessage='" + message + '\'';
        if (data != null) {
            toRet += ",\n\tappointments=[";
            for (Appointment appointment : data) {
                toRet += "\n\t\t{" + appointment + "},";
            }
            toRet += "\n\t],";
        }
        toRet += "\n}";
        return toRet;
    }
}
