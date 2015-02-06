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

        currentClient = TCPConstants.NO_CLIENT;

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
        int packetNum = TCPConstants.INITIAL_PACKET;
        DatagramPacket clientPacket;

        int previousClient = -2;

        while(true) { // always listen for incoming messages

            try {

                data = new byte[TCPConstants.PACKET_SIZE];

                clientPacket = new DatagramPacket(data, data.length);
                clientSocket.receive(clientPacket);

                System.out.println("Received Packet");

                data = clientPacket.getData();

                if (validatePacket(data, packetNum, currentClient)) {
                    System.out.println("Valid Packet Received");

                    // Determine if the packet was previously received or is a new one
                    Packet packet = splitPacket(data);

                    currentClient = packet.getSource();
                    if (packet.getId() == packetNum) {
                        saveToFile(packet.getData());
                        packetNum++;

                    } else if (finalPacket(packet.getId()) && currentClient != previousClient) {
                        System.out.println("Final Packet Received");
                        packetNum = 1;
                        saveToFile(packet.getData() + "<endOfMessage>\n");
                        previousClient = currentClient;
                        currentClient = TCPConstants.NO_CLIENT;

                    }

                    data[0] = TCPConstants.VALID_PACKET;
                } else {
                    System.out.println("Invalid Packet Received: " + new String(data));

                    data[0] = TCPConstants.INVALID_PACKET;
                }

                clientPacket = new DatagramPacket(data, data.length, clientPacket.getAddress(), clientPacket.getPort());
                clientSocket.send(clientPacket);


            } catch (Exception e) {
                System.out.println("Error: failed to send/receive packets");
                break;
            }
        }

        clientSocket.close();


    }


    private boolean finalPacket(int packetID) {
        return packetID == TCPConstants.FINAL_PACKET;
    }


    /**
     * saveToFile
     *
     * saves incoming packets to a file
     * @param message to append to a file
     */
    private void saveToFile(String message) {

        try {
            FileWriter fw = new FileWriter(FILENAME, true);
            fw.write(message);
            fw.close();
        } catch(IOException ioe) {
            System.err.println("IOException: " + ioe.getMessage());
        } catch (Exception e) {
            System.out.println("Error: cannot save to file");
        }
    }


    public static void main(String args[]) {
        Server server = new Server();
        server.sendReceive();
    }
}