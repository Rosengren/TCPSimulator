import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;

public class Client {

    private static final int SCRAMBLER_PORT = 3000;
    private static final int CLIENT_PORT = 4000;

    private static final int TIMEOUT = 1000;
    private static final int PACKET_VALID_SIZE      = 1;
    private static final int PACKET_SOURCE_SIZE     = 4;
    private static final int PACKET_ID_SIZE         = 4;
    private static final int PACKET_DATA_SIZE       = 10;
    private static final int PACKET_CHECKSUM_SIZE   = 10;
    private static final int PACKET_SIZE = PACKET_ID_SIZE + PACKET_DATA_SIZE + PACKET_CHECKSUM_SIZE;

    private static final int VALID_PACKET = 1;

    private static final String DEFAULT_MESSAGE_TO_SEND = "Hello"; // TODO: split message

    private DatagramSocket clientSocket;
    private DatagramPacket clientPacket;

    public Client() {

        try {
            clientSocket = new DatagramSocket();
            clientSocket.setSoTimeout(TIMEOUT);
        } catch (SocketException e) {
            e.printStackTrace();
        }


    }


    public void sendReceive() {

        byte[] data;
        int packetID = 0;

        String[] splitMessage = splitStringEvery(DEFAULT_MESSAGE_TO_SEND, PACKET_DATA_SIZE);

        try {

            int i = 0;
            while(true) {

                data = constructPacket(splitMessage[i], packetID);

                clientPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), SCRAMBLER_PORT);
                clientSocket.send(clientPacket);

                try {
                    clientSocket.receive(clientPacket);
                    packetID++; // send next packet
                } catch (SocketTimeoutException e) {
                    // resend
                }

                if (i >= splitMessage.length) {
                    // sent all packets
                    break;
                }

                i++;
            }


        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Packet Structure: [ Valid | Source | id | data | checkSum ]
     */
    private byte[] constructPacket(String message, int packetID) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] id = ByteBuffer.allocate(4).putInt(packetID).array();

        byte[] msg = new byte[PACKET_DATA_SIZE];
        char[] msgArray = message.toCharArray();

        for (int i = 0; i < msgArray.length; i++) {
            msg[i] = (byte)msgArray[i];
        }

        byte[] checkSum = generateCRC32(message).getBytes();

        try {
            outputStream.write(VALID_PACKET);
            outputStream.write(CLIENT_PORT);
            outputStream.write(id);
            outputStream.write(msg);
            outputStream.write(checkSum);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();

    }

    private String[] splitStringEvery(String s, int interval) {
        int arrayLength = (int) Math.ceil(((s.length() / (double)interval)));
        String[] result = new String[arrayLength];

        int j = 0;
        int lastIndex = result.length - 1;
        for (int i = 0; i < lastIndex; i++) {
            result[i] = s.substring(j, j + interval);
            j += interval;
        } //Add the last bit
        result[lastIndex] = s.substring(j);

        return result;
    }

    private String generateCRC32(String message){
        CRC32 crc = new CRC32();
        crc.update(message.getBytes());
        long hashValue = crc.getValue();
        return Long.toString(hashValue);
    }


    public static void main(String args[]) {
        Client client = new Client();
        client.sendReceive();
    }
}