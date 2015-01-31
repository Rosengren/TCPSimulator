import java.net.*;

/**
 * Server
 *
 * This class simulates a TCP server, implemented using the
 * UPD protocol
 */
public class Server extends Thread {

    private DatagramPacket sendPacket;
    private DatagramPacket receivePacket;
    private DatagramSocket sendSocket;
    private DatagramSocket receiveSocket;

    public Server(int clientPort) {

        try {
            receiveSocket = new DatagramSocket(clientPort);

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

            while(true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                receiveSocket.receive(packet);

                // Remove later:
                System.out.println("ip: " + packet.getAddress());
                System.out.println("port: " + packet.getPort());
                System.out.print("message: ");
                System.out.write(packet.getData(), 0, packet.getLength());
                System.out.println();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
