import java.net.*;
import java.util.*;

public class Scrambler extends Thread {

    private  static final int DEFAULT_PORT = 2015;

    private ArrayList<Server> servers;
    private ArrayList<Client> clients;

    private Random rand;

    private int port;
    private String message;

    public Scrambler() {
        servers = new ArrayList<Server>();
        clients = new ArrayList<Client>();

        port = DEFAULT_PORT;
        message = "";
        rand = new Random();

    }

    public void addServer(Server server) {
        servers.add(server);
    }

    public void setPortNumber(int portNumber) {
        port = portNumber;
    }

    public void listen() {
        start();
    }

    public void s() {
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
            DatagramPacket myPacket = new DatagramPacket(msg, msg.length, host, port);
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

                // Remove later:
                System.out.println("ip: " + packet.getAddress());
                System.out.println("port: " + packet.getPort());
                System.out.print("message: ");
                System.out.write(packet.getData(), 0, packet.getLength());
                System.out.println();

                message = new String(packet.getData());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
