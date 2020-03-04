import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class PacketHandler {

    private static int[][] blockedAddresses = {{50, 28, 51, 184}, {208, 80, 154, 224}};
    private static int[] redirect = {139, 157, 100, 6};

    /**
     * Constructor
     */
    public PacketHandler () throws Exception {

        System.out.println("\n-------------------------------------------------\n");

        //Spin up socket for packet and wait for it
        DatagramSocket sourceSocket = new DatagramSocket(2500);
        byte[] buffer = new byte[1024];
        DatagramPacket sourcePacket = new DatagramPacket(buffer, buffer.length);
        sourceSocket.receive(sourcePacket);

        System.out.println("Received a packet");

        //Send the packet off to google
        DatagramSocket requestSocket = new DatagramSocket();
        DatagramPacket outPacket = new DatagramPacket(sourcePacket.getData(), sourcePacket.getLength(),
                InetAddress.getByName("8.8.8.8"), 53);
        requestSocket.send(outPacket);

        System.out.println("Sent off packet");

        //Wait for a response from google
        byte[] receiveData = new byte[1024];
        DatagramPacket inPacket = new DatagramPacket(receiveData, receiveData.length);
        requestSocket.receive(inPacket);

        System.out.println("Got packet back from Google");

        inPacket.setData(modifyData(inPacket.getData()));

        System.out.println("Completed data modification");

        DatagramPacket response = new DatagramPacket(inPacket.getData(), inPacket.getLength(),
                sourcePacket.getAddress(), sourcePacket.getPort());
        sourceSocket.send(response);

        System.out.println("Transaction complete");
    }

    private byte[] modifyData (byte[] data) {
        return data;
    }
}
