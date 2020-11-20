package shared;


/**
* shared/ServerInterfacePOA.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from IDL.idl
* Tuesday, October 20, 2020 9:19:02 o'clock PM EDT
*/

public abstract class ServerInterfacePOA extends org.omg.PortableServer.Servant
 implements shared.ServerInterfaceOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("ReturnItem", new java.lang.Integer (0));
    _methods.put ("PurchaseItem", new java.lang.Integer (1));
    _methods.put ("FindItem", new java.lang.Integer (2));
    _methods.put ("AddCustomerToWaitList", new java.lang.Integer (3));
    _methods.put ("AddItem", new java.lang.Integer (4));
    _methods.put ("RemoveItem", new java.lang.Integer (5));
    _methods.put ("ListItemAvailability", new java.lang.Integer (6));
    _methods.put ("exchangeItem", new java.lang.Integer (7));
    _methods.put ("shutdown", new java.lang.Integer (8));
  }

  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

    switch (__method.intValue ())
    {
       case 0:  // shared/ServerInterface/ReturnItem
       {
         String customerId = in.read_string ();
         String itemId = in.read_string ();
         String dateOfReturn = in.read_string ();
         String $result = null;
         $result = this.ReturnItem (customerId, itemId, dateOfReturn);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 1:  // shared/ServerInterface/PurchaseItem
       {
         String customerId = in.read_string ();
         String itemId = in.read_string ();
         String dateOfPurchase = in.read_string ();
         String $result = null;
         $result = this.PurchaseItem (customerId, itemId, dateOfPurchase);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 2:  // shared/ServerInterface/FindItem
       {
         String customerId = in.read_string ();
         String itemDescription = in.read_string ();
         String $result[] = null;
         $result = this.FindItem (customerId, itemDescription);
         out = $rh.createReply();
         shared.ServerInterfacePackage.productsHelper.write (out, $result);
         break;
       }

       case 3:  // shared/ServerInterface/AddCustomerToWaitList
       {
         String itemId = in.read_string ();
         String customerId = in.read_string ();
         String $result = null;
         $result = this.AddCustomerToWaitList (itemId, customerId);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 4:  // shared/ServerInterface/AddItem
       {
         String managerId = in.read_string ();
         String itemId = in.read_string ();
         String itemName = in.read_string ();
         int quantity = in.read_long ();
         double price = in.read_double ();
         String $result = null;
         $result = this.AddItem (managerId, itemId, itemName, quantity, price);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 5:  // shared/ServerInterface/RemoveItem
       {
         String managerId = in.read_string ();
         String itemId = in.read_string ();
         int quantity = in.read_long ();
         String $result = null;
         $result = this.RemoveItem (managerId, itemId, quantity);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 6:  // shared/ServerInterface/ListItemAvailability
       {
         String managerId = in.read_string ();
         String $result[] = null;
         $result = this.ListItemAvailability (managerId);
         out = $rh.createReply();
         shared.ServerInterfacePackage.productsHelper.write (out, $result);
         break;
       }

       case 7:  // shared/ServerInterface/exchangeItem
       {
         String customerId = in.read_string ();
         String newItemId = in.read_string ();
         String oldItemId = in.read_string ();
         String dateOfReturn = in.read_string ();
         String $result = null;
         $result = this.exchangeItem (customerId, newItemId, oldItemId, dateOfReturn);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 8:  // shared/ServerInterface/shutdown
       {
         this.shutdown ();
         out = $rh.createReply();
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:shared/ServerInterface:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public ServerInterface _this() 
  {
    return ServerInterfaceHelper.narrow(
    super._this_object());
  }

  public ServerInterface _this(org.omg.CORBA.ORB orb) 
  {
    return ServerInterfaceHelper.narrow(
    super._this_object(orb));
  }


} // class ServerInterfacePOA