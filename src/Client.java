import java.net.*;

public class Client extends Thread {

    private static final int DEFAULT_PORT = 2015;
    private static final int DEFAULT_NUMBER_OF_MESSAGES = 10;
    private static final String DEFAULT_MESSAGE = "Hello";

    private int port;
    private int numOfMessages;
    private String message;

    public Client() {
        port = DEFAULT_PORT;
        message = DEFAULT_MESSAGE;
        numOfMessages = DEFAULT_NUMBER_OF_MESSAGES;
    }

    public void setPortNumber(int portNumber) {
        port = portNumber;
    }
    public void setMessage(String msg) {
        message = msg;
    }

    public void sendMessage() {

        try {

            DatagramSocket mySocket = new DatagramSocket();
            byte[] msg = message.getBytes();
            InetAddress host = InetAddress.getByName("127.0.0.1");
            DatagramPacket myPacket = new DatagramPacket(msg, msg.length, host, port);
            mySocket.send(myPacket);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendMultipleMessages(int numberOfMessages) {
        start();
    }

    @Override
    public void run() {

        try {

            int i = 0;
            while (i < numOfMessages) {
                String msg = "Hello";

                DatagramSocket mySocket = new DatagramSocket();
                byte[] message = msg.getBytes();
                InetAddress host = InetAddress.getByName("127.0.0.1");
                DatagramPacket myPacket = new DatagramPacket(message, message.length, host, port);
                mySocket.send(myPacket);

                sleep(1000);
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}


