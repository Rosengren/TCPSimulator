import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.zip.CRC32;

public class Server {

    private int currentClient;

    private DatagramSocket clientSocket;
    private DatagramPacket clientPacket;

    public Server() {

        currentClient = -1;

        try {

            // establish constant connection with client
            clientSocket = new DatagramSocket(TCPConstants.SERVER_PORT);
        } catch (SocketException e) {
            e.printStackTrace();
        }

    }

    public void sendReceive() {

        byte[] data;
        int packetNum = 0;
        try {

            while(true) { // always listen for incoming messages

                data = new byte[TCPConstants.PACKET_SIZE];

                clientPacket = new DatagramPacket(data, data.length);
                clientSocket.receive(clientPacket);

                data = clientPacket.getData();

                splitPacket(data);
                if (validatePacket(data, packetNum)) {
                    System.out.println("Packet is good!");
                    // send positive response
                    packetNum++;
                    data[0] = 1;
                } else {
                    System.out.println("Packet is not good!");
                    // set first bit to 0;
                    data[0] = 0;
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

    private boolean validatePacket(byte[] data, int packetNumber) {

        String[] elements = splitPacket(data);

        // TODO: Check if correct client


        // Check if packet is the packet we want
        if (Integer.parseInt(elements[2]) != packetNumber) {
            return false;
        }
        System.out.println("CheckSum " + elements[3] + " " + elements[4] + " " + elements[4].length());

        // verify checkSum
        return validateCRC32(elements[3], Long.parseLong(elements[4].replaceAll("\\s+","")));

    }


    private String[] splitPacket(byte[] data) {
        String[] result = new String[TCPConstants.NUM_OF_PACKET_COMPONENTS];

        result[0] = new String(Arrays.copyOfRange(data, 0, TCPConstants.PACKET_VALIDATION + 1));
        result[1] = new String(Arrays.copyOfRange(data, TCPConstants.PACKET_SOURCE, TCPConstants.PACKET_ID));
        result[2] = new String(Arrays.copyOfRange(data, TCPConstants.PACKET_ID, TCPConstants.PACKET_DATA));
        result[3] = new String(Arrays.copyOfRange(data, TCPConstants.PACKET_DATA, TCPConstants.PACKET_CHECKSUM));
        result[4] = new String(Arrays.copyOfRange(data, TCPConstants.PACKET_CHECKSUM, TCPConstants.PACKET_SIZE));

        return result;
    }


    private boolean validateCRC32(String message, long hashValue){
        CRC32 crc = new CRC32();
        System.out.println("Got here: " + message.length());
        crc.update(message.getBytes());
        System.out.println("hashValue " + hashValue + " crc: " + crc.getValue() + " msg: " + message);
        return hashValue == crc.getValue();
    }

    public static void main(String args[]) {
        Server server = new Server();
        server.sendReceive();
    }
}