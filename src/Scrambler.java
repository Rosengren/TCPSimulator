import java.net.*;
import java.util.*;

public class Scrambler extends Thread {


    private int serverPort;
    private int clientPort;

    private Random rand;
    private String message;

    public Scrambler(int clientPort, int serverPort) {

        this.clientPort = clientPort;
        this.serverPort = serverPort;
        message = "";
        rand = new Random();

    }

    public void setServerPort(int port) {
        serverPort = port;
    }

    public void setClientPort(int port) {
        clientPort = port;
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

            DatagramSocket mySocket = new DatagramSocket();
            byte[] msg = message.getBytes();
            InetAddress host = InetAddress.getByName("127.0.0.1");
            DatagramPacket myPacket = new DatagramPacket(msg, msg.length, host, serverPort);
            mySocket.send(myPacket);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        try {
            // TODO: add another socket to listen for messages from the server
            DatagramSocket mySocket = new DatagramSocket(clientPort);

            byte[] buffer = new byte[1024];

            while(true) {

                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                mySocket.receive(packet);

                message = new String(packet.getData());
                modifyPacket();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
