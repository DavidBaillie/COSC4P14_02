import com.sun.org.apache.xml.internal.serializer.Encodings;
import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import jdk.jfr.events.ExceptionThrownEvent;

import javax.xml.crypto.Data;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MAIN {

    private ConcurrentLinkedQueue<DatagramPacket> requestQueue;
    static int[][] blockedAddresses = {{50, 28, 51, 184}, {208, 80, 154, 224}};
    static int[] redirect = {139, 157, 100, 6};

    /**
     * Runnable Main static method for class
     * @param args
     */
    public static void main (String[] args) {

        System.out.println("Press q to quit");

        Thread ender = new Thread () {
            public void run () {
                if (new Scanner(System.in).nextLine().equals("q")) System.exit(0);
            }
        }; ender.setDaemon(true); ender.start();

        while (true){
            try {
                new PacketHandler();
            } catch (Exception e) {
                System.out.println("Error occurred while processing request:\n" + e);
            }
        }
    }
}
