import java.net.*;

/**
 * Client
 *
 * This class simulates the TCP protocol using UDP
 */
public class Client extends Thread {

    private static final int PACKET_SIZE = 100;

    private int port;

    private DatagramPacket sendPacket;
    private DatagramPacket receivePacket;
    private DatagramSocket sharedSocket;

    public Client(int portNumber) {
        port = portNumber;

        try {
            sharedSocket = new DatagramSocket();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void setPortNumber(int portNumber) {
        port = portNumber;
    }


    /**
     * Send a single packet to the client
     * Packet format:
     *      Message - message we want to send
     *      Length  - size of the packet
     *      Host    - host name (localhost)
     *      Port    - Port number (destination)
     */
    public void sendSingleMessage(String message) {

        byte[] packet = new byte[PACKET_SIZE];
        byte[] msg = message.getBytes();

        // Need to add header information at the start of the packet
//        packet[0] =

        try {

            InetAddress host = InetAddress.getLocalHost();
            sendPacket = new DatagramPacket(msg, msg.length, host, port);
            sharedSocket.send(sendPacket);

        } catch (Exception e) {
            e.printStackTrace();
        }

        sharedSocket.close();
    }

    public void sendMultipleMessages(int numberOfMessages, String msg) {

        try {

            byte[] message = msg.getBytes();
            InetAddress host = InetAddress.getLocalHost();
            sendPacket = new DatagramPacket(message, message.length, host, port);

            int i = 0;
            while (i < numberOfMessages) {

                sharedSocket.send(sendPacket);
                sleep(1000);
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        sharedSocket.close();
    }

}


