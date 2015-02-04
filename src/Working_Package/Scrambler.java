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

    private DatagramSocket clientSocket;
    private DatagramSocket serverSocket;


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

    }


    private void listen(){
        try {

            byte[] buffer = new byte[BYTE_BUFF_SIZE];
            int clientPort;
            DatagramPacket serverPacket;
            DatagramPacket clientPacket;

            while(true) {

                // wait for message from client(s)
                clientPacket = new DatagramPacket(buffer, buffer.length);
                clientSocket.receive(clientPacket);
                clientPort = clientPacket.getPort();
                System.out.println("Received client message, client port is: " + clientPort);

                String message = new String(clientPacket.getData());

                byte[] msg;
                if (packetNotLost())
                    msg = scramblePacket(message);
                else
                    continue; // packet is lost

                // send message to server
                InetAddress host = InetAddress.getLocalHost(); //local host is 127.0.0.1
                serverPacket = new DatagramPacket(msg, msg.length, host, SERVER_PORT);
                serverSocket.send(serverPacket);
                System.out.println("Sent message off to Server");


                // wait for message from server
                serverSocket.receive(serverPacket);
                System.out.println("Received Response from Server: " + serverPacket.getData().toString());


                // send message to client
                byte[] newMsg = serverPacket.getData();
                clientPacket = new DatagramPacket(newMsg, newMsg.length, host, clientPort);
                clientSocket.send(clientPacket);
                System.out.println("Sent information to Client now");

                clientSocket.close();
                serverSocket.close();
                break;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean packetNotLost() {
        return ((int)(Math.random() * 100) + 1) < 80; // 20% chance
    }


    private byte[] scramblePacket(String message) {

        if (((int)(Math.random() * 100) + 1) < 60) // 40% chance
            return shuffle(message);

        return message.getBytes();
    }


    private byte[] shuffle(String message) {
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

}
