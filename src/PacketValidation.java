import java.util.Arrays;
import java.util.zip.CRC32;

public abstract class PacketValidation {

    protected long generateCRC32(String message){
        CRC32 crc = new CRC32();
        crc.update(message.getBytes());
        return crc.getValue();
    }

    protected boolean validateCRC32(String message, long hashValue){
        CRC32 crc = new CRC32();
        crc.update(message.getBytes());
        return hashValue == crc.getValue();
    }

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

    protected boolean validatePacket(byte[] data, int packetId) {

        Packet packet = splitPacket(data);

        try {

            // TODO: Check if correct client

            // Check if packet is the packet we want
            if (packet.getId() > packetId) {
                return false;
            }

            // verify checkSum
            if (!validateCRC32(packet.getData(), packet.getChecksum())) {
                return  false;
            }

        } catch (Exception e) {
            System.out.println("Invalid Packet");
            return false;
        }
        return true;

    }


    protected boolean invalidPacket(byte[] data) {
        Packet packet;
        try {
            packet = splitPacket(data);
            // verify checkSum
            if (!validateCRC32(packet.getData(), packet.getChecksum())) {
                return  true;
            }

            if (packet.getValidation() == TCPConstants.INVALID_PACKET) {
                return true;
            }

        } catch (Exception e) {
            return true;
        }

        return false;
    }


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
