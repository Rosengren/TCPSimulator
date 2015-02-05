import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Server
 *
 * Server-side of TCP simulator using UDP
 */
public class Server extends PacketValidation {

    private static final String FILENAME = "log_file.txt";

    private int currentClient;

    private DatagramSocket clientSocket;


    /**
     * Constructor
     *
     * initializes the client socket to listen for incoming
     * packets
     */
    public Server() {

        currentClient = -1; // TODO: check which client the packets are coming from

        try {

            clientSocket = new DatagramSocket(TCPConstants.SERVER_PORT);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }


    /**
     * sendReceive
     *
     * listens for incoming data and sends an acknowledgment when
     * the packet has not been scrambled or modified during
     * transmission. If a packet has been changed, a message
     * is sent, notifying the client that the packet needs
     * to be re-sent
     */
    public void sendReceive() {

        byte[] data;
        int packetNum = 1; //
        DatagramPacket clientPacket;

        try {

            while(true) { // always listen for incoming messages

                data = new byte[TCPConstants.PACKET_SIZE];

                clientPacket = new DatagramPacket(data, data.length);
                clientSocket.receive(clientPacket);

                System.out.println("Received Packet " + new String(data));

                data = clientPacket.getData();

                if (validatePacket(data, packetNum)) {
                    System.out.println("Packet is good!");

                    // Determine if the packet was the previously received packet or a new packet
                    Packet packet = splitPacket(data);
                    if (packet.getId() == packetNum) {
                        saveToFile(packet.getData());
                        packetNum++;
                    }

                    data[0] = 1;
                } else {
                    System.out.println("Packet is not good!");
                    // set first bit to 0;
                    data[0] = 0;
                }

                clientPacket = new DatagramPacket(data, data.length, clientPacket.getAddress(), clientPacket.getPort());
                clientSocket.send(clientPacket);

            }


        } catch (Exception e) {

        }
    }

    /**
     * saveToFile
     *
     * saves incoming packets to a file
     * @param message to append to a file
     */
    private void saveToFile(String message) {
        // TODO: add a delimiter between complete messages
        try {
            FileWriter fw = new FileWriter(FILENAME, true);
            fw.write(message);
            fw.close();
        } catch(IOException ioe) {
            System.err.println("IOException: " + ioe.getMessage());
        }
    }


    public static void main(String args[]) {
        Server server = new Server();
        server.sendReceive();
    }
}