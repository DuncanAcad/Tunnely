package tunnely.packet;

import java.nio.charset.StandardCharsets;

public class EvalMemberPacket implements Packet {
    public static byte ID = 6;
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
        if (bytes[1] == 1) {
            accepted = true;
        } else {
            accepted = false;
        }
        byte[] bytesMessage = new byte[bytes.length - 2];
        for (int i = 2; i < bytes.length; i++) {
            bytesMessage[i - 2] = bytes[i];
        }
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
        for (int i = 0; i < bytesMessage.length; i++) {
            out[i + 2] = bytesMessage[i];
        }
        return out;
    }

    @Override
    public byte getId() {
        return ID;
    }

    public String getMessage() {
        return message;
    }
}

