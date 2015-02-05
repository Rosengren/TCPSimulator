import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;

public class Client {


    private static final int VALID_PACKET = 1;

    private static final String DEFAULT_MESSAGE_TO_SEND = "I Can't Believe this is not butter"; // TODO: split message

    private DatagramSocket clientSocket;
    private DatagramPacket clientPacket;

    public Client() {

        try {
            clientSocket = new DatagramSocket();
            clientSocket.setSoTimeout(TCPConstants.TIMEOUT);
        } catch (SocketException e) {
            e.printStackTrace();
        }


    }


    public void sendReceive() {

        byte[] data;
        int packetID = 0;

        String[] splitMessage = splitStringEvery(DEFAULT_MESSAGE_TO_SEND, TCPConstants.PACKET_DATA_SIZE);

        try {

            int i = 0;
            while(true) {

                data = constructPacket(splitMessage[i], packetID);

                clientPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), TCPConstants.SCRAMBLER_PORT);
                clientSocket.send(clientPacket);

                try {
                    clientPacket = new DatagramPacket(data, data.length);
                    clientSocket.receive(clientPacket);
                    packetID++; // send next packet


                    System.out.println("Sending Next Message");
                    // resend
                } catch (SocketTimeoutException e) {
                    System.out.println("Failed To Send");
                    i--;
                }

                if (i < splitMessage.length - 1)
                    i++;
                else
                    break;
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

        byte[] msg = new byte[TCPConstants.PACKET_DATA_SIZE];
        char[] msgArray = message.toCharArray();

        for (int i = 0; i < msgArray.length; i++) {
            msg[i] = (byte)msgArray[i];
        }

        byte[] checkSum = generateCRC32(message).getBytes();

        try {
            outputStream.write(VALID_PACKET);
            outputStream.write(Integer.toString(TCPConstants.CLIENT_PORT).getBytes());
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