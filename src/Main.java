
public class Main {

    private static Client client;
    private static Scrambler scrambler;
    private static Server server;

    public static void main(String args[]) {

        scrambler = new Scrambler();
        server = new Server();
        client = new Client();

        scrambler.listen();
        client.sendMessage();
    }
}

