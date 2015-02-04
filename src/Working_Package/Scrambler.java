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


                buffer = clientPacket.getData();

                if (packetNotLost())
                    buffer = scramblePacket(buffer);
                else
                    continue; // packet lost


                // send message to server
                InetAddress host = InetAddress.getLocalHost(); //local host is 127.0.0.1
                serverPacket = new DatagramPacket(buffer, buffer.length, host, SERVER_PORT);
                serverSocket.send(serverPacket);
                System.out.println("Sent message off to Server");


                // wait for message from server
                serverSocket.receive(serverPacket);
                System.out.println("Received Response from Server: " + serverPacket.getData().toString());


                buffer = serverPacket.getData();

                if (packetNotLost())
                    buffer = scramblePacket(buffer);
                else
                    continue; // packet lost

                // send message to client

                clientPacket = new DatagramPacket(buffer, buffer.length, host, clientPort);
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


    private byte[] scramblePacket(byte[] message) {

        if (((int)(Math.random() * 100) + 1) < 60) // 40% chance
            return shuffle(message);

        return message;
    }


    private byte[] shuffle(byte[] message) {
        
        Random rnd = new Random();
        for (int i = message.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);

            byte a = message[index];
            message[index] = message[i];
            message[i] = a;
        }
        return message;
    }

}
