import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Random;

public class Scrambler {

    private DatagramSocket serverSocket;
    private DatagramSocket clientSocket;


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
        DatagramPacket serverPacket;
        DatagramPacket clientPacket;

        try {
            while (true) {

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
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private boolean packetNotLost() {
        return ((int)(Math.random() * 100) + 1) > 20; // 20% chance TODO
    }


    private byte[] scramblePacket(byte[] message) {

        if (((int)(Math.random() * 100) + 1) < 20) // 20% chance TODO
            return shuffle(message);


        return message;
    }


    private byte[] shuffle(byte[] message) {

        Random rnd = new Random();
        for (int i = message.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);

            byte a = message[index];
            message[index] = message[i];
            message[i] = a;
        }

        System.out.println("Scrambling Data " + new String(message));
        return message;
    }



    public static void main(String args[]) {
        Scrambler scrambler = new Scrambler();
        scrambler.sendReceive();
    }
}