package CORBA.Network;

/**
* CORBA.Network/AppointmentTypeHolder.java .
* Generated by the CORBA.IDL-to-Java compiler (portable), version "3.2"
* from Appointment.idl
* Saturday, October 26, 2019 11:45:54 o'clock PM EDT
*/

public final class AppointmentTypeHolder implements org.omg.CORBA.portable.Streamable
{
  public CORBA.Network.AppointmentType value = null;

  public AppointmentTypeHolder ()
  {
  }

  public AppointmentTypeHolder (CORBA.Network.AppointmentType initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = CORBA.Network.AppointmentTypeHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    CORBA.Network.AppointmentTypeHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return CORBA.Network.AppointmentTypeHelper.type ();
  }

}