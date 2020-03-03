import com.sun.org.apache.xml.internal.serializer.Encodings;
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

        int[][] blockedAddresses = {{50, 28, 51, 184}, {208, 80, 154, 224}};
        int[] redirect = {139, 157, 100, 6};

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

                        System.out.println(buffer.toString());
                        System.out.println("-------------------------------------------------------------------------");

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

                        System.out.println();
                        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

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

                int answerCount = packet.getData()[6] * 256 + packet.getData()[7];
                System.out.println(answerCount + " answers received");

                //grab data and start offset at question data
                byte[] data = packet.getData();
                int offset = 12;

                //iterate through question size
                while (data[offset] != 0x00){
                    offset += (data[offset] + 1);
                }
                offset += 8;

                System.out.println("Answer Type: " + (data[offset] & 0xFF));

                if ((data[offset] & 0xFF) == 0x01)
                    offset += 9;
                else if ((data[offset] & 0xFF) == 0x05)
                    offset +=


                int startOffset = 32;

                for (int i = 0; i < 200; i++){
                    if (i == 28) System.out.println(">>");
                    if (i % 4 == 0) System.out.println("");
                    //if (i % 8 == 0) System.out.print("|\t\t");
                    System.out.print((packet.getData()[i] & 0xFF) + "\t\t");
                }

                for (int i = 0; i < answerCount; i++) {

                    int[] sourceIP = {packet.getData()[startOffset++]&0xFF, packet.getData()[startOffset++]&0xFF,
                            packet.getData()[startOffset++]&0xFF, packet.getData()[startOffset++]&0xFF};

                    for (int t : sourceIP) System.out.print(t + ".");

                    for (int x = 0; x < blockedAddresses.length; x++) {
                        if (blockedAddresses[x][0] == sourceIP[0] && blockedAddresses[x][1] == sourceIP[1] &&
                        blockedAddresses[x][2] == sourceIP[2] && blockedAddresses[x][3] == sourceIP[3]){
                            System.out.println("match");
                        }
                        else System.out.println("NONE");
                    }
                    startOffset += 12;
                }

                return packet;
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
