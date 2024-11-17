package tunnely.packet;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class EvalMemberPacket implements Packet {
    public static byte ID = 4;

    private final boolean accepted;
    private final String message;

    public EvalMemberPacket(boolean accepted, String message) {
        this.accepted = accepted;
        this.message = message;
    }

    public EvalMemberPacket(byte[] bytes) throws IllegalStateException {
        if (bytes[0] != ID) {
            throw new IllegalStateException("Invalid Packet ID for " + this.getClass().getSimpleName());
        }
        accepted = bytes[1] == 1;
        byte[] bytesMessage = new byte[bytes.length - 2];
        System.arraycopy(bytes, 2, bytesMessage, 0, bytes.length - 2);
        message = new String(bytesMessage, StandardCharsets.UTF_8);
    }

    @Override
    public byte[] toBytes() {
        byte[] bytesMessage = message.getBytes(StandardCharsets.UTF_8);
        byte[] out = new byte[2 + bytesMessage.length];
        out[0] = ID;
        if (accepted) {
            out[1] = 1;
        } else {
            out[1] = 0;
        }
        System.arraycopy(bytesMessage, 0, out, 2, bytesMessage.length);
        return out;
    }

    @Override
    public byte getId() {
        return ID;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public int hashCode() {
        return Objects.hash(accepted, message);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EvalMemberPacket)) return false;
        EvalMemberPacket that = (EvalMemberPacket) o;
        return accepted == that.accepted && Objects.equals(message, that.message);
    }

    @Override
    public String toString() {
        return "EvalMemberPacket{" +
                "accepted=" + accepted +
                ", message='" + message + '\'' +
                '}';
    }

    public boolean isAccepted() {
        return accepted;
    }
}

