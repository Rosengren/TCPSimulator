import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.zip.CRC32;

public class Server {

    private int currentClient;
    private String reconstructedMessage;

    private DatagramSocket clientSocket;


    public Server() {

        currentClient = -1; // TODO: check which client the packets are coming from

        try {

            clientSocket = new DatagramSocket(TCPConstants.SERVER_PORT);
        } catch (SocketException e) {
            e.printStackTrace();
        }

    }


    public void sendReceive() {

        byte[] data;
        int packetNum = 0;
        DatagramPacket clientPacket;

        reconstructedMessage = "";
        try {

            while(true) { // always listen for incoming messages

                data = new byte[TCPConstants.PACKET_SIZE];

                clientPacket = new DatagramPacket(data, data.length);
                clientSocket.receive(clientPacket);

                System.out.println("Received Packet " + new String(data));

                data = clientPacket.getData();

                if (validatePacket(data, packetNum)) {
                    System.out.println("Packet is good!");
                    String[] d = splitPacket(data);
                    if (Integer.parseInt(d[2]) == packetNum) {
                        reconstructedMessage += d[3];
                        packetNum++;
                    }
                    
                    data[0] = 1;
                } else {
                    System.out.println("Packet is not good!");
                    // set first bit to 0;
                    data[0] = 0;
                }

                clientPacket = new DatagramPacket(data, data.length, clientPacket.getAddress(), clientPacket.getPort());
                clientSocket.send(clientPacket);

                System.out.println("Current message: " + reconstructedMessage);
            }


        } catch (Exception e) {

        }
    }


    private boolean validatePacket(byte[] data, int packetNumber) {

        String[] elements = splitPacket(data);

        try {

            // TODO: Check if correct client

            // Check if packet is the packet we want
            if (Integer.parseInt(elements[2]) > packetNumber) {
                return false;
            }

            // verify checkSum
            if (!validateCRC32(elements[3], Long.parseLong(elements[4].replaceAll("\\s+","")))) {
                return  false;
            }

        } catch (Exception e) {
            System.out.println("Invalid Packet");
            return false;
        }
        return true;

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
        crc.update(message.getBytes());
        return hashValue == crc.getValue();
    }


    public static void main(String args[]) {
        Server server = new Server();
        server.sendReceive();
    }
}