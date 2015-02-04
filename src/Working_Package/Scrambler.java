package Working_Package;

import java.net.*;
import java.util.*;

/**
 * Working_Package.Scrambler
 *
 * This class causes lost and scrambled packets, simulating the
 * process of sending data packets from one computer to another
 *
 */
public class Scrambler {

    private static final int CLIENT_RCV_PORT = 2015;
    private static final int SERVER_PORT = 2014;
    private static final int BYTE_BUFF_SIZE = 256;

    private int clientPort;

    private DatagramPacket serverPacket;
    private DatagramPacket clientPacket;
    private DatagramSocket clientSocket;
    private DatagramSocket serverSocket;

    private Random rand;
    // private String message;

    public static void main (String[] args){
        Scrambler scrambler = new Scrambler();
        System.out.println("Started Working_Package.Scrambler!");
        scrambler.listen();

    } //end main

    public Scrambler() {

        try {
            clientSocket = new DatagramSocket(CLIENT_RCV_PORT);
            serverSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
            System.exit(1);
        }
        rand = new Random();
    }

    private void listen(){
        try {

            byte[] buffer = new byte[BYTE_BUFF_SIZE];

            while(true) {

                clientPacket = new DatagramPacket(buffer, buffer.length);
                clientSocket.receive(clientPacket);
                clientPort = clientPacket.getPort();
                System.out.println("Received client message, client port is: " + clientPort);

                // TODO: determine if the message is coming from client or server
                String message = new String(clientPacket.getData());
                //simulatePacketTransfer(message);

                //sendMessage stuff
                byte[] msg = message.getBytes();
                InetAddress host = InetAddress.getLocalHost(); //local host is 127.0.0.1
                serverPacket = new DatagramPacket(msg, msg.length, host, SERVER_PORT);
                serverSocket.send(serverPacket);
                System.out.println("Sent message off to Server");

                serverSocket.receive(serverPacket);
                System.out.println("Received Response from Server: " + serverPacket.getData().toString());


                byte[] newMsg = serverPacket.getData();
                clientPacket = new DatagramPacket(newMsg, newMsg.length, host, clientPort);
                clientSocket.send(clientPacket);
                System.out.println("Sent information to Client now");

                clientSocket.close();
                serverSocket.close();
                break;

                //   serverPacket = new DatagramPacket();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void simulatePacketTransfer(String message) {
        int value = rand.nextInt(8);

//        if (value < 2) {
//            scramblePacket(message);
////            sendMessage();
//        } else if (value > 7) {
////            sendMessage();
//        }

        System.out.println("Sending message");
        sendMessage(message); // TODO: remove later

        // if the value is greater than 2 and
        // less than 7, the message is "lost"
    }

    private byte[] scramblePacket(String message) {
        byte[] byteMessage = message.getBytes();

        Random rnd = new Random();
        for (int i = byteMessage.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            byte a = byteMessage[index];
            byteMessage[index] = byteMessage[i];
            byteMessage[i] = a;
        }
        return byteMessage;
    }


    private void sendMessage(String message) {

        try {

            byte[] msg = message.getBytes();
            InetAddress host = InetAddress.getLocalHost(); //local host is 127.0.0.1
            serverPacket = new DatagramPacket(msg, msg.length, host, SERVER_PORT);
            serverSocket.send(serverPacket);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
