import java.net.*;

public class Client extends Thread {

    private int port;

    public Client(int portNumber) {
        port = portNumber;
    }

    public void setPortNumber(int portNumber) {
        port = portNumber;
    }


    public void sendSingleMessage(String message) {

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

    public void sendMultipleMessages(int numberOfMessages, String msg) {

        try {

            int i = 0;
            while (i < numberOfMessages) {

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


