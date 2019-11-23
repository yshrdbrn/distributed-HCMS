package CORBA.Network;


/**
* CORBA.Network/Response.java .
* Generated by the CORBA.IDL-to-Java compiler (portable), version "3.2"
* from Response.idl
* Thursday, October 24, 2019 12:38:17 o'clock AM EDT
*/

public final class Response implements org.omg.CORBA.portable.IDLEntity
{
  public boolean success = false;
  public String message = null;
  public CORBA.Network.Appointment appointments[] = null;

  public Response ()
  {
  } // ctor

  public Response (boolean _success, String _message, CORBA.Network.Appointment[] _appointments)
  {
    success = _success;
    message = _message;
    appointments = _appointments;
  } // ctor

} // class Response