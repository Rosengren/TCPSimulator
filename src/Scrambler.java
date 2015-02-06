import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Random;

/**
 * Scrambler
 *
 * simulates the loss and scrambling of packets between clients and a server
 */
public class Scrambler {

    private static final int SCRAMBLE_MESSAGE_PROBABILITY = 30; // percentage (%)
    private static final int LOST_PACKET_PROBABILITY = 20; // percentage (%)

    private DatagramSocket serverSocket;
    private DatagramSocket clientSocket;


    /**
     * Constructor
     *
     * initializes the socket between the scrambler and the client
     * as well as opens a socket for incoming clients
     */
    public Scrambler() {

        try {
            serverSocket = new DatagramSocket();
            clientSocket = new DatagramSocket(TCPConstants.SCRAMBLER_PORT);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }


    /**
     * sendReceive
     *
     * receives messages from clients and the server and randomly decides
     * to "lose", scrambler, or successfully transmit the message to the
     * correct recipient.
     *
     */
    public void sendReceive() {

        byte[] data = new byte[TCPConstants.PACKET_SIZE];
        DatagramPacket serverPacket;
        DatagramPacket clientPacket;


        while (true) {
            try {
                clientPacket = new DatagramPacket(data, data.length);
                clientSocket.receive(clientPacket);

                data = clientPacket.getData();


                if (packetNotLost()) {
                    data = scramblePacket(data);
                } else {
                    System.out.println("Packet was lost!");
                    continue; // packet lost
                }

                // send message to Server
                serverPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), TCPConstants.SERVER_PORT);
                serverSocket.send(serverPacket);


                // receive message from server
                serverSocket.receive(serverPacket);

                data = serverPacket.getData();

                if (packetNotLost()) {
                    data = scramblePacket(data);
                } else {
                    System.out.println("Packet was lost!");
                    continue; // packet lost
                }


                // send message to client
                clientPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), clientPacket.getPort());
                clientSocket.send(clientPacket);

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error: Scrambler cannot send/receive packets");
                break;
            }

        }


    }


    /**
     * packetNotLost
     *
     * Randomly decide whether a packet should be lost
     * @return true if packet is not lost, false otherwise
     */
    private boolean packetNotLost() {
        return ((int)(Math.random() * 100) + 1) > LOST_PACKET_PROBABILITY;
    }


    /**
     * scramblePacket
     *
     * Randomly decide whether to scramble a message
     *
     * @param message to potentially scramble
     * @return potentially scrambled message
     */
    private byte[] scramblePacket(byte[] message) {

        if (((int)(Math.random() * 100) + 1) < SCRAMBLE_MESSAGE_PROBABILITY)
            return shuffle(message);


        return message;
    }


    /**
     * shuffle
     *
     * randomly swap multiple characters in a given byte array
     *
     * @param message to shuffle
     * @return shuffled message
     */
    private byte[] shuffle(byte[] message) {

        System.out.println("Scrambling Data Packet: \"" + new String(message) + "\"");

        Random rnd = new Random();
        for (int i = message.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);

            byte a = message[index];
            message[index] = message[i];
            message[i] = a;
        }

        return message;
    }


    public static void main(String args[]) {
        Scrambler scrambler = new Scrambler();
        scrambler.sendReceive();
    }
}