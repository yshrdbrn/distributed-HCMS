package Utility;

import CORBA.Network.AppointmentType;
import CORBA.Network.Request;

public class Util {
    public static Request InitNetworkRequest() {
        Request toRet = new Request();
        // Dummy request type
        toRet.requestType = CORBA.Network.RequestType.GET_APPOINTMENT_SCHEDULE;
        toRet.userID = "";
        toRet.appointmentID = "";
        toRet.appointmentType = AppointmentType.NONE;
        toRet.secondAppointmentID = "";
        toRet.secondAppointmentType = AppointmentType.NONE;
        toRet.capacity = -1;
        return toRet;
    }
}
