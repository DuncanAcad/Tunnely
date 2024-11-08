package tunnely.packet;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class CloseConnectionPacket implements Packet {

    public static byte ID = 2;// may want to change the id later

    private final String message;

    public CloseConnectionPacket(String message) {
        this.message = message;
    }

    public CloseConnectionPacket(byte[] bytes) throws IllegalStateException {
        if (bytes[0] != ID) {
            throw new IllegalStateException("Invalid Packet ID for " + this.getClass().getSimpleName());
        }
        byte[] bytesMessage = new byte[bytes.length - 1];
        System.arraycopy(bytes, 1, bytesMessage, 0, bytes.length - 1);
        this.message = new String(bytesMessage, StandardCharsets.UTF_8);
    }

    @Override
    public byte[] toBytes() {
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        int packetLength = 1 + messageBytes.length;
        byte[] out = new byte[packetLength];
        out[0] = ID;
        System.arraycopy(messageBytes, 0, out, 1, messageBytes.length);
        return out;
    }

    public byte getId() {
        return ID;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public int hashCode() {
        return Objects.hash(message);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CloseConnectionPacket)) return false;
        CloseConnectionPacket that = (CloseConnectionPacket) o;
        return Objects.equals(message, that.message);
    }

    @Override
    public String toString() {
        return "CloseConnectionPacket{" +
                "message='" + message + '\'' +
                '}';
    }
}
