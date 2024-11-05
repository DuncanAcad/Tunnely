package tunnely.packet;


public class MemberRawDataPacket implements Packet {

    public static byte ID = 8;
    private final byte userID;
    private final byte[] data;

    public MemberRawDataPacket(byte userID, byte[] data) {
        this.userID = userID;
        this.data = data;
    }

    public MemberRawDataPacket(byte[] bytes) throws IllegalStateException {
        if (bytes[0] != ID) {
            throw new IllegalStateException("Invalid Packet ID for " + this.getClass().getSimpleName());
        }
        userID = bytes[1];
        data = new byte[bytes.length - 2];
        for (int i = 2; i < bytes.length; i++) {
            data[i - 2] = bytes[i];
        }
    }

    @Override
    public byte[] toBytes() {
        byte[] out = new byte[2 + data.length];
        out[0] = ID;
        out[1] = userID;
        for (int i = 2; i < out.length; i++) {
            out[i] = data[i - 2];
        }
        return out;
    }

    @Override
    public byte getId() {
        return ID;
    }

    public byte getUserID() {
        return userID;
    }

    public byte[] getData() {
        return data;
    }

    public String toString() {
        //turning data into hexadecimal format
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02X ", b));
        }
        return ("user ID: " + userID + "data: " + sb);
    }
}
