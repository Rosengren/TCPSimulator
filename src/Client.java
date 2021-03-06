import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.Random;

/**
 * Client
 *
 * Client-side of TCP simulator using UDP
 */
public class Client extends PacketValidation {

    private DatagramSocket clientSocket;
    private int clientPort;
    private String message;

    /**
     * Constructor
     *
     * initializes socket connection with Server and sets a
     * time limit before a packet is considered "lost"
     * Also, set a randomly generated port number
     * between 1 and 9999
     */
    public Client() {

        try {
            message = TCPConstants.DEFAULT_MESSAGE_TO_SEND;
            clientSocket = new DatagramSocket();
            clientSocket.setSoTimeout(TCPConstants.TIMEOUT);
            clientPort = generateClientPort();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }


    /**
     * sendReceive
     *
     * sends (uploads) messages to a server, simulating a TCP transfer using UDP.
     * When a packet is lost or scrambled, the packet is sent again until it is
     * acknowledged. Once acknowledged, this client sends the next packet until
     * all packets have been successfully received.
     */
    public void sendReceive() {

        byte[] data;
        int packetID = TCPConstants.INITIAL_PACKET;
        DatagramPacket clientPacket;

        String[] splitMessage = splitStringEvery(message, TCPConstants.PACKET_DATA_SIZE);


        int i = 0;
        while(true) {

            try {

                if (i == splitMessage.length - 1) {// final packet
                    data = constructPacket(splitMessage[i], TCPConstants.FINAL_PACKET); // send end message
                    System.out.println("Sending Final Packet");
                } else
                    data = constructPacket(splitMessage[i], packetID);


                clientPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), TCPConstants.SCRAMBLER_PORT);
                clientSocket.send(clientPacket);

                try {

                    System.out.println("Sending Packet: " + new String(data));
                    clientPacket = new DatagramPacket(data, data.length);
                    clientSocket.receive(clientPacket);

                    if (!validatePacket(data, packetID, clientPort) || data[0] == TCPConstants.INVALID_PACKET) {
                        System.out.println("Invalid Packet Received: Resending Packet");
                        continue;
                    }

                    packetID++; // send next packet


                    System.out.println("Valid Packet Received: Sending Next Packet: " + new String(data));
                    // resend
                } catch (SocketTimeoutException e) {
                    System.out.println("Response Timed out: Resending packet");
                    continue;
                }

                if (i < splitMessage.length - 1)
                    i++;
                else
                    break;

            } catch (java.io.IOException e) {
                System.out.println("Error: IO Exception");
                e.printStackTrace();
                break;

            }
        }

        clientSocket.close();
    }

    private int generateClientPort() {
        return new Random().nextInt(9999 - 1000) + 1000;
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
            outputStream.write((byte) TCPConstants.VALID_PACKET);
            outputStream.write(Integer.toString(clientPort).getBytes());
            outputStream.write(String.format("%04d", packetID).getBytes());
            outputStream.write(msg);
            outputStream.write(checkSum);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();

    }

    public void setMessageToDeliver(String message) {
        this.message = message;
    }

    /**
     * splitStringEvery
     *
     * split a given string into equal interval divisions (with the exception of the last
     * string which may be less than the interval)
     * @param stringToSplit: string to be divided
     * @param interval: length of each divided string
     * @return array of divided strings
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


    public static void main(String args[]) {
        Client client = new Client();

        if (args.length != 0) {
            client.setMessageToDeliver(args[0]);
        }

        client.sendReceive();
    }
}