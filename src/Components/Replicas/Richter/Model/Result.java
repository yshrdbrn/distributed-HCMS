package Components.Replicas.Richter.Model;

import Model.Network.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Result implements Serializable {
    private ResultStatus resultStatus;
    private String ID;
    private List<Appointment> payload;
    private String message;

    public Result(ResultStatus resultStatus, String ID, List<Appointment> response) {
        this.resultStatus = resultStatus;
        this.ID = ID;
        this.payload = response;
    }

    public Result(ResultStatus resultStatus, String message) {
        this.resultStatus = resultStatus;
        this.message = message;
    }

    public Result(ResultStatus resultStatus, List<Appointment> payload) {
        this.resultStatus = resultStatus;
        this.payload = payload;
    }

    public Result() {
        this.resultStatus = ResultStatus.FAILURE;
        this.ID = null;
        this.payload = null;
        this.message = null;
    }

    public ResultStatus getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(ResultStatus resultStatus) {
        this.resultStatus = resultStatus;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public List<Appointment> getPayload() {
        return payload;
    }

    public void setPayload(List<Appointment> payload) {
        this.payload = payload;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public byte[] getBytes(Result result) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(result);
        oos.flush();
        byte[] bytes = bos.toByteArray();
        return bytes;
    }

    public static Response toGeneralResponse(Result result) {
        boolean isSuccess = result.getResultStatus() == ResultStatus.SUCCESS;
        ArrayList<Model.Appointment.Appointment> payload = null;
        if(result.getPayload() != null) {
            payload = new ArrayList<>();
            for (Appointment appointment : result.getPayload()) {
                payload.add(Appointment.toGeneralAppointment(appointment));
            }
        }

        Response toReturn = new Response(isSuccess, payload);
        toReturn.setMessage(result.getMessage());
        return toReturn;
    }
    public void toStringAppointments() {
        for(Appointment appointment : payload) {
            System.out.println(appointment.toString());
        }
    }

    @Override
    public String toString() {
        return "Result {" +
                "resultStatus = " + resultStatus +
                ", ID ='" + ID + '\'' +
                ", message = '" + message + '\'' +
                '}';
    }
}

