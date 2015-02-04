package Working_Package;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.zip.CRC32;

/**
 * Server
 *
 * This class simulates a TCP server, implemented using the
 * UPD protocol
 */
public class Server{

    private static final int SCRAMBLER_PORT = 2015;
    private static final int SERVER_PORT = 2014;
    private static final int BYTE_BUFF_SIZE = 256;
    private static final String DEFAULT_ONE_LINER = "Two nuns walk into a bar!";
    private static final String DEFAULT_REQUEST_MESSAGE = "Request File Information";

    private DatagramPacket sendPacket;
    private DatagramPacket receivePacket;
    private DatagramSocket serverSocket;

    public static void main (String[] args) {
        System.out.println("Start Server");
        Server server = new Server();
        server.receiveRequest();
        server.closeSockets();
    }// end main

    public Server() {
        try {
            serverSocket = new DatagramSocket(SERVER_PORT);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private void receiveRequest(){


        try {
            byte[] message = new byte[BYTE_BUFF_SIZE];
            InetAddress host = InetAddress.getLocalHost();

            // TODO change so that we have 256 bytes (attempting to conform to protocol)
            receivePacket = new DatagramPacket(message, message.length, host, SCRAMBLER_PORT);

            //wait for packet
            serverSocket.receive(receivePacket);
            System.out.println("Received: " + new String(receivePacket.getData()));
            // TODO
            if(validateCRC32(new String(receivePacket.getData()))){

                sendFileInformation();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    private String getFileInformation(String filename) {

        String fileInformation = DEFAULT_ONE_LINER;

        try {
            BufferedReader in = new BufferedReader(new FileReader(filename));
            //arbitrarily choosing to read the second line of the file
            fileInformation = in.readLine();
            fileInformation = in.readLine();
            in.close();
        } catch (FileNotFoundException e){
            System.err.println("Couldn't open quote file.  Serving time instead.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileInformation;
    }


    private void sendFileInformation(){

        String fileInformation = getFileInformation("C:\\Users\\Nikola\\workspace\\TCP_Assignment\\bin\\Working_Package\\one-liners.txt");

        try {
            String fullMessage = generateCRC32(fileInformation) + fileInformation;
            byte[] message = new byte[BYTE_BUFF_SIZE];

            char[] charMessage = fullMessage.toCharArray();

            //load the byte array with the checksum hash and the message.
            for (int i = 0; i < BYTE_BUFF_SIZE; i++) {
                if (i < charMessage.length) {
                    message[i] = (byte)charMessage[i];
                } else {
                    message[i] = 0; // padding
                }
            }

            InetAddress host = InetAddress.getLocalHost();

            sendPacket = new DatagramPacket(message, message.length, host, SCRAMBLER_PORT);

            //send packet
            System.out.println("Sent: " + new String(sendPacket.getData()));
            serverSocket.send(sendPacket);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeSockets(){
        this.serverSocket.close();
    }

}
