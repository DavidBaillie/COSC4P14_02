import jdk.jfr.events.ExceptionThrownEvent;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MAIN {

    private ConcurrentLinkedQueue<DatagramPacket> requestQueue;

    public MAIN () {
        requestQueue = new ConcurrentLinkedQueue<>();

        Thread receiver = new Thread () {
            public void run () {
                while (true) {
                    try {
                        DatagramSocket socket = new DatagramSocket(2500);
                        byte[] buffer = new byte[1024];
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        socket.receive(packet);

                        String str = new String(packet.getData(), 0, packet.getLength());
                        System.out.println(str);
                        socket.close();
                    } catch (Exception e) {
                        System.out.println("Error:\n" + e);
                    }
                }
            }
        };
        receiver.setDaemon(true);
        receiver.start();
    }

    public static void main (String[] args) {
        new MAIN();

        while (true){
            System.out.println("Press q to quit");
            if (new Scanner(System.in).nextLine().equals("q")) break;
        }
    }
}
