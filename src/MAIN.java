import jdk.jfr.events.ExceptionThrownEvent;

import javax.xml.crypto.Data;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MAIN {

    private ConcurrentLinkedQueue<DatagramPacket> requestQueue;

    public MAIN () {
        requestQueue = new ConcurrentLinkedQueue<>();

        //Thread used to listen for inbound UDP messages
        Thread requestReceiver = new Thread () {
            public void run () {

                //Loop forever - continually listen
                while (true) {
                    try {
                        //Spin up socket for packet
                        DatagramSocket socket = new DatagramSocket(2500);
                        byte[] buffer = new byte[1024];
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        socket.receive(packet);

                        //Save the packet for use
                        requestQueue.add(packet);

                        //Close the socket
                        socket.close();
                    } catch (Exception e) {
                        System.out.println("Inbound error:\n" + e);
                    }
                }
            }
        };
        requestReceiver.setDaemon(true);
        requestReceiver.start();


        Thread requestHandler = new Thread() {
            public void run () {
                try {
                    while (true){
                        //Do nothing if queue empty
                        if (requestQueue.isEmpty()) continue;

                        //Grab an available packet
                        DatagramPacket sourcePacket = requestQueue.remove();

                        //Build a socket and a packet to go to google
                        DatagramSocket socket = new DatagramSocket();
                        DatagramPacket outPacket = new DatagramPacket(sourcePacket.getData(), sourcePacket.getLength(),
                                InetAddress.getByName("8.8.8.8"), 53);
                        socket.send(outPacket);

                        System.out.println("SENT PACKET");

                        //Wait for a response from google
                        byte[] receiveData = new byte[1024];
                        DatagramPacket inPacket = new DatagramPacket(receiveData, receiveData.length);
                        socket.receive(inPacket);

                        System.out.println("Received a response:");
                        System.out.println(inPacket.getAddress() + " / " + inPacket.getPort() + " / " +
                                inPacket.getData().toString());

                        //Replace any blocked websites
                        inPacket = modifyPacket(inPacket);

                        //Send off the finalized response to the original DNS request
                        DatagramPacket response = new DatagramPacket(inPacket.getData(), inPacket.getLength(),
                                sourcePacket.getAddress(), sourcePacket.getPort());
                        socket.send(response);

                        //Close the socket for the next run through to have resources
                        socket.close();
                    }
                } catch (Exception e) {
                    System.out.println("Outbound error:" + e);
                }
            }

            /**
             * Modifies the provided packet to remove blocked addresses
             * @param packet Packet to modify
             * @return Modifies packet
             */
            private DatagramPacket modifyPacket (DatagramPacket packet) {
                return null;
            }
        };
        requestHandler.setDaemon(true);
        requestHandler.start();
    }

    /**
     * Runnable Main static method for class
     * @param args
     */
    public static void main (String[] args) {
        new MAIN();

        while (true){
            System.out.println("Press q to quit");
            if (new Scanner(System.in).nextLine().equals("q")) break;
        }
    }
}
