import java.net.*;
import java.util.*;

public class Scrambler extends Thread {

    private  static final int DEFAULT_PORT = 2015;
    private  static final int DEFAULT_SERVER_PORT = 2000;

    private int serverPort;

    private Random rand;

    private int port;
    private String message;

    public Scrambler() {
        serverPort = DEFAULT_SERVER_PORT;
        port = DEFAULT_PORT;
        message = "";
        rand = new Random();

    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public void setPortNumber(int portNumber) {
        port = portNumber;
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

            DatagramSocket mySocket = new DatagramSocket(port);

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
