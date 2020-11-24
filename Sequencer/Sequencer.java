import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Sequencer {

    DatagramSocket sequencerSocket;

    InetAddress localAddress;
    InetAddress sequencerAddress;
    InetAddress RM1Address;
    InetAddress RM2Address;
    InetAddress RM3Address;

    int localPort;
    int sequencerPort;
    int RM1Port;
    int RM2Port;
    int RM3Port;

    int sequence_number;

    private void ReadAddresses() throws UnknownHostException, FileNotFoundException {
        Scanner fScn = new Scanner(new File("addresses.txt"));
        String data;

        while( fScn.hasNextLine() ){
            data = fScn.nextLine();

            String[] token = data.split(",");
            this.localAddress = InetAddress.getByName(token[0]);
            this.localPort = Integer.parseInt(token[1]);

            this.sequencerAddress = InetAddress.getByName(token[2]);
            this.sequencerPort = Integer.parseInt(token[3]);

            this.RM1Address = InetAddress.getByName(token[4]);
            this.RM1Port = Integer.parseInt(token[5]);

            this.RM2Address = InetAddress.getByName(token[6]);
            this.RM2Port = Integer.parseInt(token[7]);

            this.RM3Address = InetAddress.getByName(token[8]);
            this.RM3Port = Integer.parseInt(token[9]);

        }
        fScn.close();
    }


    public Sequencer() throws UnknownHostException, SocketException, FileNotFoundException {

        ReadAddresses();
        sequence_number = 0;
        this.sequencerSocket = new DatagramSocket(this.sequencerPort, this.sequencerAddress);
    }

    public void run() throws IOException {

        boolean not_received = true;
        byte[] buffer;

        while (true) {

            buffer = new byte[1000];
            DatagramPacket FE_request = new DatagramPacket(buffer, buffer.length);
            sequencerSocket.receive(FE_request);
            String request = new String(FE_request.getData());

            String[] splitMessage = request.split("|");
            String header = splitMessage[0] +","+ sequence_number ;
            sequence_number++;

            String response = header + "|" + splitMessage[1];
            not_received =true;

            while (not_received) {

                DatagramPacket request1 = new DatagramPacket(response.getBytes(), response.length(), RM1Address, RM1Port);
                DatagramPacket request2 = new DatagramPacket(response.getBytes(), response.length(), RM2Address, RM2Port);
                DatagramPacket request3 = new DatagramPacket(response.getBytes(), response.length(), RM3Address, RM3Port);
                sequencerSocket.send(request1);
                sequencerSocket.send(request2);
                sequencerSocket.send(request3);

                try{
                    sequencerSocket.setSoTimeout(1000);
                    byte[] buff = new byte[1000];

                    DatagramPacket reply1 = new DatagramPacket(buff, buff.length);
                    DatagramPacket reply2 = new DatagramPacket(buff, buff.length);
                    DatagramPacket reply3 = new DatagramPacket(buff, buff.length);

                    sequencerSocket.receive(reply1);
                    sequencerSocket.receive(reply2);
                    sequencerSocket.receive(reply3);

                    if (new String(reply1.getData()).toUpperCase().equals("RECEIVED") && new String(reply2.getData()).toUpperCase().equals("RECEIVED") && new String(reply3.getData()).toUpperCase().equals("RECEIVED")) {
                        not_received = false;
                    }
                } catch (SocketTimeoutException e) {
                }
            }

        }
    }
    public static void main(String args[]){
        Sequencer seq = new Sequencer();
        seq.run();
    }



}
