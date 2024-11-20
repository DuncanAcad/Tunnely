package tunnely.packet;

public class MemberDisconnectPacket extends UserIdPacket {
    public static byte ID = 7;

    public MemberDisconnectPacket(byte userId) {
        super(userId);
    }

    public MemberDisconnectPacket(byte[] bytes) throws IllegalStateException {
        super(bytes);
    }

    @Override
    public byte getId() {
        return ID;
    }
}
