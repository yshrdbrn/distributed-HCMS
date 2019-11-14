package Server.Model.Network;

import Server.Model.Appointment.Appointment;

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

    public List<Appointment> getData() {
        return data;
    }

    public Network.Response ToNetworkResponse() {
        Network.Response toRet = new Network.Response();
        toRet.success = successful;
        toRet.message = message;
        toRet.appointments = new Network.Appointment[0];
        if (data != null) {
            toRet.appointments = new Network.Appointment[data.size()];
            for (int i = 0; i < data.size(); i++)
                toRet.appointments[i] = data.get(i).ToNetworkAppointment();
        }

        return toRet;
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
