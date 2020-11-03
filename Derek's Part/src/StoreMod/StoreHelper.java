package StoreMod;


/**
* StoreMod/StoreHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from F:/SOEN/SOEN423/Assignment2/build/classes/Store.idl
* Thursday, October 15, 2020 4:01:35 o'clock PM EDT
*/

abstract public class StoreHelper
{
  private static String  _id = "IDL:StoreMod/Store:1.0";

  public static void insert (org.omg.CORBA.Any a, StoreMod.Store that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static StoreMod.Store extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CORBA.ORB.init ().create_interface_tc (StoreMod.StoreHelper.id (), "Store");
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static StoreMod.Store read (org.omg.CORBA.portable.InputStream istream)
  {
    return narrow (istream.read_Object (_StoreStub.class));
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, StoreMod.Store value)
  {
    ostream.write_Object ((org.omg.CORBA.Object) value);
  }

  public static StoreMod.Store narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof StoreMod.Store)
      return (StoreMod.Store)obj;
    else if (!obj._is_a (id ()))
      throw new org.omg.CORBA.BAD_PARAM ();
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      StoreMod._StoreStub stub = new StoreMod._StoreStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

  public static StoreMod.Store unchecked_narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof StoreMod.Store)
      return (StoreMod.Store)obj;
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      StoreMod._StoreStub stub = new StoreMod._StoreStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

}
