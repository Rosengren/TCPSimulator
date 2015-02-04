import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

public class Scrambler {

    private static final int SERVER_PORT = 2000; // TODO: refactor
    private static final int SCRAMBLER_PORT = 3000;

    private static final int PACKET_VALID_SIZE      = 1;
    private static final int PACKET_SOURCE_SIZE     = 8;
    private static final int PACKET_ID_SIZE         = 4;
    private static final int PACKET_DATA_SIZE       = 10;
    private static final int PACKET_CHECKSUM_SIZE   = 10;
    private static final int PACKET_SIZE = PACKET_ID_SIZE + PACKET_DATA_SIZE + PACKET_CHECKSUM_SIZE;

    private DatagramSocket serverSocket;
    private DatagramPacket serverPacket;

    private DatagramSocket clientSocket;
    private DatagramPacket clientPacket;



    public Scrambler() {

        try {
            serverSocket = new DatagramSocket(SERVER_PORT);
            clientSocket = new DatagramSocket(SCRAMBLER_PORT);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }



    public void sendReceive() {

        byte[] data = new byte[PACKET_SIZE];

        try {
            System.out.println("Waiting for Client");
            clientPacket = new DatagramPacket(data, data.length);
            clientSocket.receive(clientPacket);


            System.out.println("Received: " + new String(clientPacket.getData()));

            data = clientPacket.getData();

            // send message to Server
            serverPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), SCRAMBLER_PORT);
            serverSocket.send(serverPacket);

            // receive message from server
            serverSocket.receive(serverPacket);

            System.out.println("Received from Server: " + new String(serverPacket.getData()));

            // send message to client
            int clientPort = getClientPort(data);

            clientPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), SCRAMBLER_PORT);
            clientSocket.send(clientPacket);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private int getClientPort(byte[] data) {
        return Integer.parseInt(new String(Arrays.copyOfRange(data, 1, 1 + PACKET_SOURCE_SIZE)));
    }

    public static void main(String args[]) {
        Scrambler scrambler = new Scrambler();
        scrambler.sendReceive();
    }
}