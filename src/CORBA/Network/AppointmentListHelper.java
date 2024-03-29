package CORBA.Network;


/**
* CORBA.Network/AppointmentListHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from Response.idl
* Saturday, November 23, 2019 4:09:19 o'clock PM EST
*/

abstract public class AppointmentListHelper
{
  private static String  _id = "IDL:CORBA.Network/AppointmentList:1.0";

  public static void insert (org.omg.CORBA.Any a, CORBA.Network.Appointment[] that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static CORBA.Network.Appointment[] extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = CORBA.Network.AppointmentHelper.type ();
      __typeCode = org.omg.CORBA.ORB.init ().create_sequence_tc (0, __typeCode);
      __typeCode = org.omg.CORBA.ORB.init ().create_alias_tc (CORBA.Network.AppointmentListHelper.id (), "AppointmentList", __typeCode);
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static CORBA.Network.Appointment[] read (org.omg.CORBA.portable.InputStream istream)
  {
    CORBA.Network.Appointment value[] = null;
    int _len0 = istream.read_long ();
    value = new CORBA.Network.Appointment[_len0];
    for (int _o1 = 0;_o1 < value.length; ++_o1)
      value[_o1] = CORBA.Network.AppointmentHelper.read (istream);
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, CORBA.Network.Appointment[] value)
  {
    ostream.write_long (value.length);
    for (int _i0 = 0;_i0 < value.length; ++_i0)
      CORBA.Network.AppointmentHelper.write (ostream, value[_i0]);
  }

}
