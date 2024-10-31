package tunnely.packet;

public interface Packet {
    byte[] toBytes();

    byte getId();
}
