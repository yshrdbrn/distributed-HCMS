package CORBA.Network;


/**
* CORBA.Network/AppointmentListHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from Response.idl
* Saturday, November 23, 2019 4:09:19 o'clock PM EST
*/

public final class AppointmentListHolder implements org.omg.CORBA.portable.Streamable
{
  public CORBA.Network.Appointment value[] = null;

  public AppointmentListHolder ()
  {
  }

  public AppointmentListHolder (CORBA.Network.Appointment[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = CORBA.Network.AppointmentListHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    CORBA.Network.AppointmentListHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return CORBA.Network.AppointmentListHelper.type ();
  }

}
