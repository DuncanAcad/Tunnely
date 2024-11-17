package tunnely.packet;


import java.util.Arrays;
import java.util.Objects;

public class MemberRawDataPacket implements Packet {
    public static byte ID = 6;

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
        System.arraycopy(bytes, 2, data, 0, bytes.length - 2);
    }

    @Override
    public byte[] toBytes() {
        byte[] out = new byte[2 + data.length];
        out[0] = ID;
        out[1] = userID;
        System.arraycopy(data, 0, out, 2, out.length - 2);
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

    private String getDataAsHexString() {
        // https://stackoverflow.com/questions/2817752/how-can-i-convert-a-byte-array-to-hexadecimal-in-java
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(userID, Arrays.hashCode(data));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MemberRawDataPacket)) return false;
        MemberRawDataPacket that = (MemberRawDataPacket) o;
        return userID == that.userID && Arrays.equals(data, that.data);
    }

    @Override
    public String toString() {
        return "MemberRawDataPacket{" +
                "userID=" + userID +
                ", data=" + getDataAsHexString() +
                '}';
    }
}
