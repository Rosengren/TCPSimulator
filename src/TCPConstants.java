
public final class TCPConstants {

    public static final int SERVER_PORT    = 2000;
    public static final int SCRAMBLER_PORT = 3000;
    public static final int CLIENT_PORT    = 4000;

    public static final int TIMEOUT = 5000;

    public static final int PACKET_VALID_SIZE      = 1;
    public static final int PACKET_SOURCE_SIZE     = 4;
    public static final int PACKET_ID_SIZE         = 4;
    public static final int PACKET_DATA_SIZE       = 10;
    public static final int PACKET_CHECKSUM_SIZE   = 10;
    public static final int NUM_OF_PACKET_COMPONENTS      = 5;
    public static final int PACKET_SIZE = PACKET_VALID_SIZE + PACKET_SOURCE_SIZE + PACKET_ID_SIZE + PACKET_DATA_SIZE + PACKET_CHECKSUM_SIZE;


    public static final int PACKET_VALIDATION   = 0;
    public static final int PACKET_SOURCE       = 1;
    public static final int PACKET_ID           = 5;
    public static final int PACKET_DATA         = 9;
    public static final int PACKET_CHECKSUM     = 19;


    public static final int VALID_PACKET = 1;
    public static final int INVALID_PACKET = 0;

    public static final String DEFAULT_MESSAGE_TO_SEND = "I Can't Believe this is not butter";

}
