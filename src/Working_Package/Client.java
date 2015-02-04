package Working_Package;
import java.net.*;
import java.util.zip.CRC32;

/**
 * Client
 *
 * This class simulates the TCP protocol using UDP
 */
public class Client {

    private static final int CLIENT_RCV_PORT = 2015;
    private static final int BYTE_BUFF_SIZE = 256;
    private static final String DEFAULT_REQUEST_MESSAGE = "Request File Information";

    private DatagramPacket sendPacket;
    private DatagramSocket clientSocket;

    public static void main (String[] args) throws InterruptedException {

        Client client = new Client();
        System.out.println("Started Client!");
        // TODO do this and also make a String argument so that the user might be able to change the request messages --> it will show that the server rejects incorrect messages
        //client.sendMultipleMessages(Integer.parseInt(args[0]));

        client.sendMultipleMessages(1);
    }// end main

    public Client() {

        try {
            clientSocket = new DatagramSocket();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void sendMultipleMessages(int numberOfMessages) {

        try {
            String fullMessage = generateCRC32(DEFAULT_REQUEST_MESSAGE) + DEFAULT_REQUEST_MESSAGE;
            byte[] message = new byte[BYTE_BUFF_SIZE];

            char[] charMessage = fullMessage.toCharArray();

            //load the byte array with the checksum hash and the message.
            for (int i = 0; i < charMessage.length; i++) {
                    message[i] = (byte)charMessage[i];
            }

            //testing stuff
            System.out.println("This is message: " + message.toString());
            System.out.println("This is message with new String: " + new String(message));
            System.out.println("This is the 24th byte: " + message[24]);
            System.out.println("This is the 25th byte: " + message[25]);
            System.out.println("This is the 255th byte: " + message[255]);
            System.out.println("This is the 47th byte: " + message[47]);
            System.out.println("This is the 109th byte: " + message[109]);

            InetAddress host = InetAddress.getLocalHost();

            sendPacket = new DatagramPacket(message, message.length, host, CLIENT_RCV_PORT);

            for(int i = 0; i < numberOfMessages; i++){

                //send packet
                clientSocket.send(sendPacket);
                System.out.println("Sent request packet!");


                // wait for packet
                clientSocket.receive(sendPacket);

                validateCRC32(new String(sendPacket.getData()));
                System.out.println("Received information from end server: " + new String(sendPacket.getData()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        clientSocket.close();
    }


    // TODO move these into a static class (same with the exact same ones in server.java)
    private String generateCRC32(String message){
        CRC32 crc = new CRC32();
        crc.update(message.getBytes());
        long hashValue = crc.getValue();
        return Long.toString(hashValue);
    }

    private boolean validateCRC32(String messageWithLeadingHash){
        String hash = messageWithLeadingHash.substring(0, 10);
        long hashValue = Long.parseLong(hash);
        String message = messageWithLeadingHash.substring(10, messageWithLeadingHash.length());
        CRC32 crc = new CRC32();
        crc.update(message.getBytes());
        if(hashValue == crc.getValue()){return true;}
        return false;
    }
}