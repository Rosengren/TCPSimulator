import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Server {

    private static final int SERVER_PORT = 2000; // Todo: refactor
    private static final int SCRAMBLER_PORT = 3000;

    private static final int PACKET_VALID_SIZE      = 1;
    private static final int PACKET_SOURCE_SIZE     = 8;
    private static final int PACKET_ID_SIZE         = 4;
    private static final int PACKET_DATA_SIZE       = 10;
    private static final int PACKET_CHECKSUM_SIZE   = 10;
    private static final int PACKET_SIZE = PACKET_ID_SIZE + PACKET_DATA_SIZE + PACKET_CHECKSUM_SIZE;

    private DatagramSocket clientSocket;
    private DatagramPacket clientPacket;

    public Server() {

        try {

            // establish constant connection with client
            clientSocket = new DatagramSocket(SCRAMBLER_PORT);
        } catch (SocketException e) {
            e.printStackTrace();
        }

    }

    public void sendReceive() {

        byte[] data;

        try {

            while(true) { // always listen for incoming messages

                data = new byte[PACKET_SIZE];

                clientPacket = new DatagramPacket(data, data.length);
                clientSocket.receive(clientPacket);

                data = clientPacket.getData();

                if (validatePacket(data)) {

                    // send positive response

                } else {
                    // send negative response
                }

                System.out.println("Received Packet " + new String(data));
            }


        } catch (Exception e) {

        }
    }

    private boolean validatePacket(byte[] data) {

        // TODO: check packet
        return true;
    }

    public static void main(String args[]) {
        Server server = new Server();
        server.sendReceive();
    }
}