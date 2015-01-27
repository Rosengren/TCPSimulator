import java.net.*;

public class Server extends Thread {

    int clientPort;

    public Server(int clientPort) {
        this.clientPort = clientPort;
    }

    public void listen() {
        start();
    }

    @Override
    public void run() {

        try {

            DatagramSocket mySocket = new DatagramSocket(clientPort);

            byte[] buffer = new byte[1024];

            while(true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                mySocket.receive(packet);

                // Remove later:
                System.out.println("ip: " + packet.getAddress());
                System.out.println("port: " + packet.getPort());
                System.out.print("message: ");
                System.out.write(packet.getData(), 0, packet.getLength());
                System.out.println();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
