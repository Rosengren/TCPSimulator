import java.net.*;
import java.util.*;

/**
 * Scrambler
 *
 * This class causes lost and scrambled packets, simulating the
 * process of sending data packets from one computer to another
 *
 */
public class Scrambler extends Thread {

    private int serverPort;
    private int clientPort;

    private DatagramPacket sendPacket;
    private DatagramPacket receivePacket;
    private DatagramSocket clientSocket;
    private DatagramSocket serverSocket;
    private DatagramSocket sharedSocket;

    private Random rand;
    private String message;

    public Scrambler(int clientPort, int serverPort) {

        this.clientPort = clientPort;
        this.serverPort = serverPort;
        message = "";

        try {
            clientSocket = new DatagramSocket(clientPort);
            serverSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
            System.exit(1);
        }

        rand = new Random();

    }

    public void listen() {
        start();
    }

    public void modifyPacket() {
        int value = rand.nextInt(3);

        if (value < 2) {
            scramblePacket();
            sendMessage();
        }

        // if the value is greater than 2, the message is "lost"
    }

    public void scramblePacket() {
        // TODO: scramble Packet
    }


    public void sendMessage() {

        try {

            byte[] msg = message.getBytes();
            InetAddress host = InetAddress.getByName("127.0.0.1");
            DatagramPacket myPacket = new DatagramPacket(msg, msg.length, host, serverPort);
            serverSocket.send(myPacket);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        try {

            byte[] buffer = new byte[1024];

            while(true) {

                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                clientSocket.receive(packet);

                message = new String(packet.getData());
                modifyPacket();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
