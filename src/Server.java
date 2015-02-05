import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Server {


    private DatagramSocket clientSocket;
    private DatagramPacket clientPacket;

    public Server() {

        try {

            // establish constant connection with client
            clientSocket = new DatagramSocket(TCPConstants.SERVER_PORT);
        } catch (SocketException e) {
            e.printStackTrace();
        }

    }

    public void sendReceive() {

        byte[] data;

        try {

            while(true) { // always listen for incoming messages

                data = new byte[TCPConstants.PACKET_SIZE];

                clientPacket = new DatagramPacket(data, data.length);
                clientSocket.receive(clientPacket);

                data = clientPacket.getData();

                if (validatePacket(data)) {

                    // send positive response

                } else {
                    // send negative response
                }

                System.out.println("Received Packet " + new String(data));

                System.out.println("Sending response");

                clientPacket = new DatagramPacket(data, data.length, clientPacket.getAddress(), clientPacket.getPort());
                clientSocket.send(clientPacket);

                System.out.println("Sent");
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