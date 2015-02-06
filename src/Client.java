import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;

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

        try {
            int i = 0;
            while(true) {

                if (i == splitMessage.length - 1) {// final packet
                    data = constructPacket(splitMessage[i], TCPConstants.FINAL_PACKET); // send end message
                    System.out.println("FINAL MESSAGE SENT");
                } else
                    data = constructPacket(splitMessage[i], packetID);


                clientPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), TCPConstants.SCRAMBLER_PORT);
                clientSocket.send(clientPacket);

                try {
                    System.out.println("Sent: " + new String(clientPacket.getData()));
                    clientPacket = new DatagramPacket(data, data.length);
                    clientSocket.receive(clientPacket);

                    System.out.println("Received packet: "  + new String(data));
                    if (!validatePacket(data, packetID, clientPort)) {
                        System.out.println("Invalid Packet: Resending Message");
                        continue;
                    }

                    packetID++; // send next packet


                    System.out.println("Sending Next Message");
                    // resend
                } catch (SocketTimeoutException e) {
                    System.out.println("Response Timed out: Resending Message");
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

    private int generateClientPort() {
        return (int)(Math.random() * 9999) + 1;
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