package CORBA.Network;


/**
* CORBA.Network/ServerHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from Server.idl
* Saturday, November 23, 2019 4:09:22 o'clock PM EST
*/

abstract public class ServerHelper
{
  private static String  _id = "IDL:CORBA.Network/Server:1.0";

  public static void insert (org.omg.CORBA.Any a, CORBA.Network.Server that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static CORBA.Network.Server extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CORBA.ORB.init ().create_interface_tc (CORBA.Network.ServerHelper.id (), "Server");
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static CORBA.Network.Server read (org.omg.CORBA.portable.InputStream istream)
  {
    return narrow (istream.read_Object (_ServerStub.class));
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, CORBA.Network.Server value)
  {
    ostream.write_Object ((org.omg.CORBA.Object) value);
  }

  public static CORBA.Network.Server narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof CORBA.Network.Server)
      return (CORBA.Network.Server)obj;
    else if (!obj._is_a (id ()))
      throw new org.omg.CORBA.BAD_PARAM ();
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      CORBA.Network._ServerStub stub = new CORBA.Network._ServerStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

  public static CORBA.Network.Server unchecked_narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof CORBA.Network.Server)
      return (CORBA.Network.Server)obj;
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      CORBA.Network._ServerStub stub = new CORBA.Network._ServerStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

}
