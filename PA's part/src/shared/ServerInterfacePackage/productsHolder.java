package shared.ServerInterfacePackage;


/**
* shared/ServerInterfacePackage/productsHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from IDL.idl
* Tuesday, October 20, 2020 9:19:02 o'clock PM EDT
*/

public final class productsHolder implements org.omg.CORBA.portable.Streamable
{
  public String value[] = null;

  public productsHolder ()
  {
  }

  public productsHolder (String[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = shared.ServerInterfacePackage.productsHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    shared.ServerInterfacePackage.productsHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return shared.ServerInterfacePackage.productsHelper.type ();
  }

}