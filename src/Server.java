import java.net.*;

/**
 * Server
 *
 * This class simulates a TCP server, implemented using the
 * UPD protocol
 */
public class Server extends Thread {

    // Acknowledgments
    public static final byte[] invalidResponse = {0, 1, 1, 1};
    public static final byte[] validResponse = {1, 0, 0 ,0};

    private DatagramPacket sendPacket;
    private DatagramPacket receivePacket;
    private DatagramSocket sendSocket;
    private DatagramSocket receiveSocket;

    public Server(int clientPort) {

        try {
            receiveSocket = new DatagramSocket(clientPort);
            sendSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }

    }

    public void listen() {
        start();
    }

    @Override
    public void run() {

        try {

            byte[] buffer = new byte[1024];
            byte[] response = new byte[4];

            while(true) {
                receivePacket = new DatagramPacket(buffer, buffer.length);
                receiveSocket.receive(receivePacket);

                // Remove later:
                System.out.println("ip: " + receivePacket.getAddress());
                System.out.println("port: " + receivePacket.getPort());
                System.out.print("message: ");
                System.out.write(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println();

                // send receive acknowledgment

                // TODO: verify validity of packet
                response = validResponse;
                sendPacket = new DatagramPacket(response, response.length,
                        receivePacket.getAddress(), receivePacket.getPort());

                System.out.println("Sending acknowledgment");
                sendSocket.send(sendPacket);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
