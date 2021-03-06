package store;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class StoreServer {

    public String branchID;
    int port;
    public HashMap<String, Item> itemList = new HashMap<>();
    public Logger logger;
    public HashMap<String, Customer> customerList = new HashMap<>();
    public ArrayList<String[]> purchaseLogs = new ArrayList<>();

    public StoreServer(String branchID, int port) {

        this.branchID = branchID;
        this.port = port;

        this.logger = this.initiateLogger();
        this.logger.info("Initializing Server ...");

    }
    public int findPort(String id) {
        switch (id) {
            case "QC":
                return 2000;
            case "ON":
                return 3000;
            case "BC":
                return 4000;
            default:
                return 0;
        }
    }
    public Logger initiateLogger() {

        Logger logger = Logger.getLogger(StoreServer.class.getName());
        FileHandler fh;
        try {
            fh = new FileHandler("/Users/jonathanfrey/Documents/java_store_imp/src/Logs/" + this.branchID + " Server.Store Log.txt");
            logger.setUseParentHandlers(false);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return logger;
    }

    public String addItem(String managerID, String itemID, String name, int quantity, double price) {

        if(managerID.charAt(2) != 'M'){
            logger.info("ERROR: Non manager accessing add item");
            return "ERROR: Non manager accessing add item";
        }

        Item item = itemList.get(itemID);
        String result;

        if (item != null) {

            if (!item.name.equals(name)) {
                result = "ERROR: Added item name " +name + " does not match item with id " + itemID;
            } else if (item.price != price) {
                result = "ERROR: Added item price " + price + " does not match item with id " + itemID;
            } else {
                item.quantity += quantity;
                result = "SUCCESS: Added " + quantity + " item " + itemID + " to store";
            }
        } else {
            Item newItem = new Item(itemID, name, quantity, price);
            itemList.put(itemID, newItem);
            result = "SUCCESS: Added " + quantity + " item " + itemID + " to store";
        }
        this.logger.info("addItem() called. Parameters: " + managerID + " " + itemID + " " + name + " " + quantity + " " + price + "\nStatus: " + result);
        return result;
    }

    public String removeItem(String managerID, String itemID, int quantity) {

        if(managerID.charAt(2) != 'M'){
            logger.info("ERROR: Non manager accessing remove item");
            return "ERROR: Non manager accessing remove item";
        }

        Item item = itemList.get(itemID);
        String result;

        if (item != null) {
            item.quantity -= quantity;
            if (item.quantity < 0) item.quantity = 0;
            result = "SUCCESS: Removed " + quantity + " items " + itemID + " from the store";
        } else {
            result = "ERROR: Removed item ID " + itemID + " does not exist in store";
        }
        this.logger.info("removeItem() called. Parameters: " + managerID + " " + itemID + " " + quantity + "\nStatus: " + result);
        return result;
    }

    public String[] listItemAvailability(String managerID) {

        String[] temp = null;
        int i=0;
        StringBuilder result = new StringBuilder();
        for (String key : itemList.keySet()) {
            temp[i] =  itemList.get(key).toString();
            i++;
            result.append(itemList.get(key).toString()).append("\n");
        }
        this.logger.info("listItemAvailability() called. Parameters: " + managerID);
        return temp;
    }

    public synchronized String purchaseItem(String customerID, String itemID, String dateOfPurchase) {

        if(customerID.charAt(2) != 'U'){
            logger.info("ERROR: Non customer accessing purchase item");
            return "ERROR: Non customer accessing purchase item";
        }

        String result="";
        String item_branchID = itemID.substring(0, 2);

        Customer customer;
        if (customerList.get(customerID) == null) {
            customer = new Customer(customerID);
            customerList.put(customerID, customer);
        } else {
            customer = customerList.get(customerID);
        }

        if (item_branchID.equals(this.branchID)) {

            if (!itemList.containsKey(itemID)) {
                result += "ERROR: Purchased item ID " + itemID + " does not exist in store";
                return result;
            } else if (itemList.get(itemID).quantity == 0) {
                result += "ERROR: Not enough of item " + itemID+ ", contacting customer";
                //waitlist
                return result;
            } else {
                if (customer.budget > itemList.get(itemID).price) {
                    customer.purchaseItem(itemList.get(itemID).price);
                    itemList.get(itemID).quantity--;
                    String[] purchaseLog = {customerID, itemID, dateOfPurchase};
                    purchaseLogs.add(purchaseLog);
                    result += "SUCCESS: Purchased item " + itemID + " from sever " + item_branchID;
                } else {
                    result += "ERROR: Customer could not afford item " + itemID;
                }
            }
        } else {
            int serverPort = findPort(item_branchID);

            try (DatagramSocket aSocket = new DatagramSocket()) {
                byte[] message = ("PFI" + customerID + "," + itemID + "," + dateOfPurchase).getBytes();
                InetAddress aHost = InetAddress.getByName("localhost");

                DatagramPacket request = new DatagramPacket(message, ("PFI" + customerID + "," + itemID + "," + dateOfPurchase).length(), aHost, serverPort);
                aSocket.send(request);

                byte[] buffer = new byte[1000];
                DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

                aSocket.receive(reply);
                String temp = new String(reply.getData()).trim();

                if (temp.charAt(0) == 'S') {
                    customer.purchaseItem(Integer.parseInt(temp.substring(1)));
                    result = "SUCCESS: Purchased item " + itemID + " from sever " + item_branchID;
                }
                else{
                    result = temp;
                }

            } catch (SocketException e) {
                System.out.println("Socket: " + e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("IO: " + e.getMessage());
            }
        }
        return result;
    }
    public synchronized String purchaseForeignItem(String customerID, String itemID, String dateOfPurchase) {
        String result;

        boolean customerOnePurchaseLimit = false;
        for(int i =0;i<purchaseLogs.size();i++){
            if(purchaseLogs.get(i)[0].equals(customerID))
                customerOnePurchaseLimit = true;
        }

        if (!itemList.containsKey(itemID))
            result = "ERROR: Purchased item ID " + itemID + " does not exist in store";
        else if (itemList.get(itemID).quantity == 0)
            result = "ERROR: Not enough of item " + itemID+ ", contacting customer";
        else if(customerOnePurchaseLimit)
            result = "ERROR: Cannot purchase multiple items from inter-province store";
        else {
            itemList.get(itemID).quantity--;
            String[] purchaseLog = {customerID, itemID, dateOfPurchase};
            purchaseLogs.add(purchaseLog);
            result = "S" + itemList.get(itemID).price;
        }
        return result;
    }

    public synchronized String[] findItem(String customerID, String itemName) {
    	
        StringBuilder result = new StringBuilder("findItem() called. Parameters: " + customerID + " " + itemName + "\n");

        boolean contained = false;
        ArrayList results = new ArrayList();
        String[] temp = null;
        int i=0;
        for (String key : itemList.keySet()) {
            if (itemList.get(key).name.equals(itemName)) {
                temp[i] = itemList.get(key).toString();
                result.append(itemList.get(key).toString());
                contained = true;
            }
        }

        String[] branch_ids = {"QC", "ON", "BC"};
        int[] UDPPorts = {2000, 3000, 4000};

        for (int j = 0; i < branch_ids.length; i++) {
            if (!branch_ids[i].equals(this.branchID)) {

                try (DatagramSocket aSocket = new DatagramSocket()) {

                    byte[] message = ("FFI" + itemName).getBytes();
                    InetAddress aHost = InetAddress.getByName("localhost");
                    int serverPort = UDPPorts[i];

                    DatagramPacket request = new DatagramPacket(message, ("FFI" + itemName).length(), aHost, serverPort);
                    aSocket.send(request);

                    byte[] buffer = new byte[1000];
                    DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                    aSocket.receive(reply);
                    
                	ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(reply.getData()));
					String[] foundMatches = null;
					try {
						foundMatches = (String[]) inputStream.readObject();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

                    results.addAll(new ArrayList<String>(Arrays.asList(foundMatches)));
                } catch (SocketException e) {
                    System.out.println("Socket: " + e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("IO: " + e.getMessage());
                }
            }
        }
        this.logger.info(result.toString());
        return temp;
    }

    public synchronized String[] findForeignItem(String itemName) {
        String result = "";
        String[] temp = null;
        int i =0;
        boolean contained = false;
        for (String key : itemList.keySet()) {
            if (itemList.get(key).name.equals(itemName)) {
                temp[i] = itemList.get(key).toString();
                i++;
                result += itemList.get(key).toString();
                contained = true;
                break;
            }
        }

        return temp;
    }


    public String returnItem(String customerID, String itemID, String dateOfReturn) {

        if(customerID.charAt(2) != 'U'){
            logger.info("ERROR: Non customer accessing return item");
            return "ERROR: Non customer accessing return item";
        }

        String result = "";
        String item_branchID = itemID.substring(0, 2);

        Customer customer;
        if (customerList.get(customerID) == null) {
            customer = new Customer(customerID);
            customerList.put(customerID, customer);
        } else {
            customer = customerList.get(customerID);
        }

        LocalDate purchaseDate = null;
        LocalDate returnDate = LocalDate.parse(dateOfReturn);

        if (item_branchID.equals(this.branchID)) {
            for (int i = 0; i < purchaseLogs.size(); i++) {

                if (purchaseLogs.get(i)[0].equals(customerID) && purchaseLogs.get(i)[1].equals(itemID)) {
                    purchaseDate = LocalDate.parse(purchaseLogs.get(i)[2]);

                    if (Math.abs((int)ChronoUnit.DAYS.between(purchaseDate, returnDate)) < 30){
                        itemList.get(itemID).quantity++;
                        customer.budget += itemList.get(itemID).price;
                        purchaseLogs.remove(i);
                        result += "SUCCESS: Customer returned item " + itemID;
                        this.logger.info(result);
                        return result;
                    }
                    else{
                        result += "ERROR: Returned item " + itemID + " has passed return policy deadline";
                        this.logger.info(result);
                        return result;
                    }

                }
            }
            result += "ERROR: Returned item ID " + itemID +" does not exist with customer";
            this.logger.info(result);
            return result;


        } else {

            int serverPort = findPort(item_branchID);
            try (DatagramSocket aSocket = new DatagramSocket()) {
                byte[] message = ("RFI" + customerID + "," + itemID + "," + dateOfReturn).getBytes();
                InetAddress aHost = InetAddress.getByName("localhost");

                DatagramPacket request = new DatagramPacket(message, ("RFI" + customerID + "," + itemID + "," + dateOfReturn).length(), aHost, serverPort);
                aSocket.send(request);

                byte[] buffer = new byte[1000];
                DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

                aSocket.receive(reply);
                String temp = new String(reply.getData()).trim();

                if (temp.charAt(0) == 'S') {
                    customer.returnItem(Integer.parseInt(temp.substring(1)));
                    result = "SUCCESS: Customer returned item " + itemID;
                }
                else{
                    result = temp;
                }

            } catch (SocketException e) {
                System.out.println("Socket: " + e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("IO: " + e.getMessage());
            }
        }
        this.logger.info(result);
        return result;
    }

    public String returnForeignItem(String customerID, String itemID, String dateOfReturn) {
        String result = "";

        LocalDate purchaseDate = null;
        LocalDate returnDate = LocalDate.parse(dateOfReturn);

        for (int i = 0; i < purchaseLogs.size(); i++) {

            if (purchaseLogs.get(i)[0].equals(customerID) && purchaseLogs.get(i)[1].equals(itemID)) {
                purchaseDate = LocalDate.parse(purchaseLogs.get(i)[2]);

                if (Math.abs((int)ChronoUnit.DAYS.between(purchaseDate, returnDate)) < 30){
                    itemList.get(itemID).quantity++;
                    purchaseLogs.remove(i);
                    result = "S"+itemList.get(itemID).price;
                    return result;
                }
                else{
                    result = "ERROR: Returned item " + itemID + " has passed return policy deadline";
                    return result;
                }

            }
        }
        result = "ERROR: Returned item ID " + itemID +" does not exist with customer";
        this.logger.info(result);
        return result;

    }

    public String exchangeItem(String customerID, String newItemID, String oldItemId, String dateOfReturn) {

        if(customerID.charAt(2) != 'U'){
            logger.info("ERROR: Non customer accessing return item");
            return "ERROR: Non customer accessing return item";
        }
        String result;
        LocalDate date = LocalDate.now();
        result = this.returnItem(customerID, oldItemId, date.toString()) + "\n";
        if (!result.contains("SUCCESS")) {
            return result;
        }
        result = this.purchaseItem(customerID, newItemID, date.toString());
        if (!result.contains("SUCCESS")) {
            result = this.purchaseItem(customerID, oldItemId, date.toString());
            return result;
        }
        result = "SUCCESS: Customer " + customerID + " exchanged item " + oldItemId + " with item " + newItemID;
        logger.info(result);
        return result;
    }
    public void close(){

    }



}