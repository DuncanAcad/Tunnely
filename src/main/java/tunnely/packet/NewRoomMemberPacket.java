package tunnely.packet;

public class NewRoomMemberPacket extends UserIdPacket {
    public static byte ID = 3;

    public NewRoomMemberPacket(byte userId) {
        super(userId);
    }

    public NewRoomMemberPacket(byte[] bytes) throws IllegalStateException {
        super(bytes);
    }

    @Override
    public byte getId() {
        return ID;
    }
}
