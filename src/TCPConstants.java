
public final class TCPConstants {

    public static final int SERVER_PORT    = 2000;
    public static final int SCRAMBLER_PORT = 3000;

    public static final int TIMEOUT = 1000;

    public static final int PACKET_VALID_SIZE    = 1;
    public static final int PACKET_SOURCE_SIZE   = 4;
    public static final int PACKET_ID_SIZE       = 4;
    public static final int PACKET_DATA_SIZE     = 10;
    public static final int PACKET_CHECKSUM_SIZE = 10;

    public static final int PACKET_SIZE = PACKET_VALID_SIZE + PACKET_SOURCE_SIZE + PACKET_ID_SIZE + PACKET_DATA_SIZE + PACKET_CHECKSUM_SIZE;

    public static final int INITIAL_PACKET = 1; // packets start at 1
    public static final int FINAL_PACKET   = 0; // last packet to send

    public static final int NO_CLIENT = -1; // Nobody is sending data. Waiting for new client

    public static final int PACKET_VALIDATION = 0;
    public static final int PACKET_SOURCE     = 1;
    public static final int PACKET_ID         = 5;
    public static final int PACKET_DATA       = 9;
    public static final int PACKET_CHECKSUM   = 19;


    public static final char VALID_PACKET = '1';
    public static final char INVALID_PACKET = '0';

    public static final String DEFAULT_MESSAGE_TO_SEND = "I Can't Believe this is not butter";

}
