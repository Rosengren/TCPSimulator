import java.util.Arrays;
import java.util.zip.CRC32;

/**
 * PacketValidation
 *
 * Contains all of the common methods used by Client-side and Server-side
 * packet handling
 */
public abstract class PacketValidation {


    /**
     * generateCRC32
     *
     * generates a hash number (long) based on a given string
     * @param message to generate hash for
     * @return long hash number
     */
    protected long generateCRC32(String message){
        CRC32 crc = new CRC32();
        crc.update(message.getBytes());
        return crc.getValue();
    }


    /**
     * validateCRC32
     *
     * compares a given string with a given hash number to determine
     * if the string correctly matches the number
     * @param message to compare with the hash
     * @param hashValue to compare with the message
     * @return true if they match, false otherwise
     */
    protected boolean validateCRC32(String message, long hashValue){
        CRC32 crc = new CRC32();
        crc.update(message.getBytes());
        return hashValue == crc.getValue();
    }


    /**
     * splitPacket
     *
     * breaks up a packet into its individual components and returns a
     * Packet object containing all the information from the packet
     * @param data packet to split
     * @return Packet object
     */
    protected Packet splitPacket(byte[] data) {
        Packet packet = new Packet();

        try {
            packet.setValidation(Integer.parseInt(new String(Arrays.copyOfRange(data, 0, TCPConstants.PACKET_VALIDATION + 1))));
        } catch (Exception e) {
            packet.setValidation(-1);
        }

        try {
            packet.setSource(Integer.parseInt(new String(Arrays.copyOfRange(data, TCPConstants.PACKET_SOURCE, TCPConstants.PACKET_ID))));
        } catch (Exception e) {
            packet.setSource(-1);
        }

        try {
            packet.setId(Integer.parseInt(new String(Arrays.copyOfRange(data, TCPConstants.PACKET_ID, TCPConstants.PACKET_DATA))));
        } catch (Exception e) {
            packet.setId(-1);
        }

        packet.setData(new String(Arrays.copyOfRange(data, TCPConstants.PACKET_DATA, TCPConstants.PACKET_CHECKSUM)));

        try {
            packet.setChecksum(Long.parseLong(new String(Arrays.copyOfRange(data, TCPConstants.PACKET_CHECKSUM, TCPConstants.PACKET_SIZE)).replaceAll("\\s+", "")));
        } catch (Exception e) {
            packet.setChecksum(-1);
        }


        return packet;
    }


    /**
     * validatePacket
     *
     * determines whether a packet is valid or has been modified
     * while being sent. This method checks the packet id, checkSum,
     * and that the packet has been received by the correct client
     * @param data packet to validate
     * @param packetId expected
     * @return true if the packet is valid, false otherwise
     */
    protected boolean validatePacket(byte[] data, int packetId, int currentClient) {

        Packet packet = splitPacket(data);

        try {

            // Check if packet is coming from the correct client
            if (currentClient != packet.getSource() && currentClient != TCPConstants.NO_CLIENT) {
                return false;
            }

            // Check if packet is the packet we want
            if (packet.getId() > packetId && packetId != TCPConstants.FINAL_PACKET) {
                return false;
            }

            // verify checkSum
            if (!validateCRC32(packet.getData(), packet.getChecksum())) {
                return  false;
            }


            if (packet.getValidation() == TCPConstants.INVALID_PACKET) {
                return false;
            }

        } catch (Exception e) {
            System.out.println("Invalid Packet");
            return false;
        }
        return true;

    }


    /**
     * Packet
     *
     * object representing a packet
     */
    protected class Packet {

        private int validation;
        private int source;
        private int id;
        private String data;
        private long checksum;

        public void setId(int id) {
                this.id = id;
        }

        public void setData(String data) {
            this.data = data;
        }

        public void setChecksum(long checksum) {
            this.checksum = checksum;
        }

        public void setValidation(int validation) {
                this.validation = validation;
        }

        public int getId() {
            return id;
        }

        public int getSource() {
            return source;
        }

        public String getData() {
            return data;
        }

        public long getChecksum() {
            return checksum;
        }

        public int getValidation() {
            return validation;
        }

        public void setSource(int source) {
            this.source = source;
        }

    }
}
