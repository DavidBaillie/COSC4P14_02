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

        //inPacket.setData(modifyData(inPacket.getData()));
        byte[] data = inPacket.getData();
        data = modifyData(data);

        System.out.println("Completed data modification");

        DatagramPacket response = new DatagramPacket(data, inPacket.getLength(),
                sourcePacket.getAddress(), sourcePacket.getPort());
        sourceSocket.send(response);

        sourceSocket.close();
        requestSocket.close();

        System.out.println("Transaction complete");
    }

    /**
     * Looks for the bad IP address and replaces it
     * @param data Byte array to change
     * @return Fixed array
     */
    private byte[] modifyData (byte[] data) {

        //check all bytes
        for (int i = 0; i < data.length; i++) {
            //check against all blocked IP's
            for (int[] values : blockedAddresses){
                //if the first character matches
                if (read(data[i]) == values[0]) {
                    //Check for complete match
                    if (read(data[i + 1]) == values[1] && read(data[i + 2]) == values[2]
                            && read(data[i + 3]) == values[3]){
                        //Swap all IP values
                        for (int k = 0; k < 4; k++) {
                            data[i + k] = write(redirect[k]);
                        }
                    }
                }
            }
        }

        return data;
    }

    static int read (byte b) {
        return b & 0xFF;
    }

    static byte write (int i) {
        return (byte)(i & 0xFF);
    }
}
