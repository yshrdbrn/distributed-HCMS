package CORBA.Network;


/**
* CORBA.Network/Request.java .
* Generated by the CORBA.IDL-to-Java compiler (portable), version "3.2"
* from Request.idl
* Sunday, October 27, 2019 12:19:24 o'clock AM EDT
*/

public final class Request implements org.omg.CORBA.portable.IDLEntity
{
  public CORBA.Network.RequestType requestType = null;
  public String userID = null;
  public String appointmentID = null;
  public CORBA.Network.AppointmentType appointmentType = null;
  public String secondAppointmentID = null;
  public CORBA.Network.AppointmentType secondAppointmentType = null;
  public int capacity = (int)0;

  public Request ()
  {
  } // ctor

  public Request (CORBA.Network.RequestType _requestType, String _userID, String _appointmentID, CORBA.Network.AppointmentType _appointmentType, String _secondAppointmentID, CORBA.Network.AppointmentType _secondAppointmentType, int _capacity)
  {
    requestType = _requestType;
    userID = _userID;
    appointmentID = _appointmentID;
    appointmentType = _appointmentType;
    secondAppointmentID = _secondAppointmentID;
    secondAppointmentType = _secondAppointmentType;
    capacity = _capacity;
  } // ctor

} // class Request