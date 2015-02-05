import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.zip.CRC32;

public class Client {

    private DatagramSocket clientSocket;

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
        DatagramPacket clientPacket;

        String[] splitMessage = splitStringEvery(TCPConstants.DEFAULT_MESSAGE_TO_SEND, TCPConstants.PACKET_DATA_SIZE);

        try {
            int i = 0;
            while(true) {

                data = constructPacket(splitMessage[i], packetID);

                clientPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), TCPConstants.SCRAMBLER_PORT);
                clientSocket.send(clientPacket);

                try {
                    System.out.println("Sent: " + new String(clientPacket.getData()));
                    clientPacket = new DatagramPacket(data, data.length);
                    clientSocket.receive(clientPacket);

                    if (invalidPacket(data)) {
                        continue;
                    }

                    packetID++; // send next packet


                    System.out.println("Sending Next Message");
                    // resend
                } catch (SocketTimeoutException e) {
                    System.out.println("Resending Message");
                    continue;
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
     * ConstructPacket
     *
     * creates a byte array packet of the form:
     *
     * Packet Structure: [ Valid | Source | id | data | checkSum ]
     */
    private byte[] constructPacket(String message, int packetID) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] msg = new byte[TCPConstants.PACKET_DATA_SIZE];
        char[] msgArray = message.toCharArray();

        for (int i = 0; i < msgArray.length; i++) {
            msg[i] = (byte)msgArray[i];
        }

        byte[] checkSum = String.format("%10d", generateCRC32(new String(msg))).getBytes();

        try {
            outputStream.write(Integer.toString(TCPConstants.VALID_PACKET).getBytes());
            outputStream.write(Integer.toString(TCPConstants.CLIENT_PORT).getBytes());
            outputStream.write(String.format("%04d", packetID).getBytes());
            outputStream.write(msg);
            outputStream.write(checkSum);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();

    }


    /**
     * SplitStringEvery
     *
     * Splits a given string into specific character lengths
     * @param stringToSplit: string to divide
     * @param interval: number of characters per division
     * @return String array of divided string
     */
    private String[] splitStringEvery(String stringToSplit, int interval) {
        int arrayLength = (int) Math.ceil(((stringToSplit.length() / (double)interval)));
        String[] result = new String[arrayLength];

        int j = 0;
        int lastIndex = result.length - 1;
        for (int i = 0; i < lastIndex; i++) {
            result[i] = stringToSplit.substring(j, j + interval);
            j += interval;
        } // Add the last bit
        result[lastIndex] = stringToSplit.substring(j);

        return result;
    }


    /**
     * invalidPacket
     *
     * @param data packet to verify
     * @return true if the packet is not valid
     */
    private boolean invalidPacket(byte[] data) {
        try {
            if (data[0] == TCPConstants.INVALID_PACKET) {
                return true;
            }
        } catch (Exception e) {}

        return false;
    }


    private long generateCRC32(String message){
        CRC32 crc = new CRC32();
        crc.update(message.getBytes());
        return crc.getValue();
    }


    public static void main(String args[]) {
        Client client = new Client();
        client.sendReceive();
    }
}