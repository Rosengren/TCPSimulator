
public final class TCPConstants {

    public static final int SERVER_PORT    = 2000;
    public static final int SCRAMBLER_PORT = 3000;
    public static final int CLIENT_PORT    = 4000;

    public static final int TIMEOUT = 1000;

    public static final int PACKET_VALID_SIZE      = 1;
    public static final int PACKET_SOURCE_SIZE     = 4;
    public static final int PACKET_ID_SIZE         = 4;
    public static final int PACKET_DATA_SIZE       = 10;
    public static final int PACKET_CHECKSUM_SIZE   = 5;
    public static final int PACKET_SIZE = PACKET_ID_SIZE + PACKET_DATA_SIZE + PACKET_CHECKSUM_SIZE;
}
