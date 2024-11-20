package tunnely.packet;

public class ConnectionAcceptedPacket implements Packet {
    public static byte ID = 5;

    public ConnectionAcceptedPacket() {
    }

    public ConnectionAcceptedPacket(byte[] bytes) throws IllegalStateException {
        if (bytes[0] != ID) {
            throw new IllegalStateException("Invalid Packet ID for " + this.getClass().getSimpleName());
        }
    }

    @Override
    public byte[] toBytes() {
        return new byte[]{ID};
    }

    @Override
    public byte getId() {
        return ID;
    }

    @Override
    public int hashCode() {
        return Byte.hashCode(ID);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ConnectionAcceptedPacket;
    }
}
