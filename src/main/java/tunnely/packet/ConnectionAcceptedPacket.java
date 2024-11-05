package tunnely.packet;

public class ConnectionAcceptedPacket implements Packet {
    public static byte ID = 7;

    public ConnectionAcceptedPacket() {
    }

    public ConnectionAcceptedPacket(byte[] bytes) throws IllegalStateException {
        if (bytes[0] != ID) {
            throw new IllegalStateException("Invalid Packet ID for " + this.getClass().getSimpleName());
        }
    }

    @Override
    public byte[] toBytes() {
        byte[] out = new byte[1];
        out[0] = ID;
        return out;
    }

    @Override
    public byte getId() {
        return ID;
    }
}
