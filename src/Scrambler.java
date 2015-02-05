import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Scrambler {


    private DatagramSocket serverSocket;
    private DatagramPacket serverPacket;

    private DatagramSocket clientSocket;
    private DatagramPacket clientPacket;



    public Scrambler() {

        try {
            serverSocket = new DatagramSocket();
            clientSocket = new DatagramSocket(TCPConstants.SCRAMBLER_PORT);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }



    public void sendReceive() {

        byte[] data = new byte[TCPConstants.PACKET_SIZE];

        try {
            while (true) {
                System.out.println("Waiting for Client");
                clientPacket = new DatagramPacket(data, data.length);
                clientSocket.receive(clientPacket);


                System.out.println("Received: " + new String(clientPacket.getData()));

                data = clientPacket.getData();

                // send message to Server
                serverPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), TCPConstants.SERVER_PORT);
                serverSocket.send(serverPacket);


                System.out.println("Sending to Server...");
                // receive message from server
                serverSocket.receive(serverPacket);

                System.out.println("Received from Server: " + new String(serverPacket.getData()));

                // send message to client
                clientPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), clientPacket.getPort());
                clientSocket.send(clientPacket);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String args[]) {
        Scrambler scrambler = new Scrambler();
        scrambler.sendReceive();
    }
}