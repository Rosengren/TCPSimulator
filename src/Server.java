import java.net.*;
import java.util.*;

public class Server extends Thread {

    private static final int DEFAULT_PORT_NUMBER = 1000;

    int port;

    public Server() {
        port = DEFAULT_PORT_NUMBER;
    }

    @Override
    public void run() {

        try {

            DatagramSocket mySocket = new DatagramSocket(port);

            byte[] buffer = new byte[1024];

            while(true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                mySocket.receive(packet);

                // Remove later:
                System.out.println("ip: " + packet.getAddress());
                System.out.println("port: " + packet.getPort());
                System.out.print("message: ");
                System.out.write(packet.getData(), 0, packet.getLength());
                System.out.println();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
