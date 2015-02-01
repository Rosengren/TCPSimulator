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

    public void simulatePacketTransfer() {
        int value = rand.nextInt(8);

        if (value < 2) {
            scramblePacket();
//            sendMessage();
        } else if (value > 7) {
//            sendMessage();
        }

        System.out.println("Sending message");
        sendMessage();

        // if the value is greater than 2 and
        // less than 7, the message is "lost"
    }

    public void scramblePacket() {
        // TODO: scramble Packet
    }


    public void sendMessage() {

        try {

            byte[] msg = message.getBytes();
            InetAddress host = InetAddress.getLocalHost();
            sendPacket = new DatagramPacket(msg, msg.length, host, serverPort);
            serverSocket.send(sendPacket);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        try {

            byte[] buffer = new byte[100];

            while(true) {

                receivePacket = new DatagramPacket(buffer, buffer.length);
                clientSocket.receive(receivePacket);

                // TODO: determine if the message is coming from client or server
                message = new String(receivePacket.getData());
                simulatePacketTransfer();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
