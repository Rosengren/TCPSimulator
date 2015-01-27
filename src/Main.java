
public class Main {
    private static final int CLIENT_PORT = 2015;
    private static final int SERVER_PORT = 2000;
    private static final String MESSAGE = "hello";

    private static Client client;
    private static Scrambler scrambler;
    private static Server server;

    public static void main(String args[]) {

        scrambler = new Scrambler(CLIENT_PORT, SERVER_PORT);
        server = new Server(SERVER_PORT);
        client = new Client(CLIENT_PORT);

        scrambler.listen();
        server.listen();
        client.sendSingleMessage(MESSAGE);
    }
}

