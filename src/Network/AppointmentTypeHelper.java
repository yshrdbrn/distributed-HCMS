package Network;


/**
* Network/AppointmentTypeHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from Appointment.idl
* Saturday, October 26, 2019 11:45:54 o'clock PM EDT
*/

abstract public class AppointmentTypeHelper
{
  private static String  _id = "IDL:Network/AppointmentType:1.0";

  public static void insert (org.omg.CORBA.Any a, Network.AppointmentType that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static Network.AppointmentType extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CORBA.ORB.init ().create_enum_tc (Network.AppointmentTypeHelper.id (), "AppointmentType", new String[] { "PHYSICIAN", "SURGEON", "DENTAL", "NONE"} );
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static Network.AppointmentType read (org.omg.CORBA.portable.InputStream istream)
  {
    return Network.AppointmentType.from_int (istream.read_long ());
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, Network.AppointmentType value)
  {
    ostream.write_long (value.value ());
  }

}
