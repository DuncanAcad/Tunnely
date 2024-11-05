package tunnely.packet;

public class NewRoomMemberPacket implements Packet {

    public static byte ID = 3;//might want to change this later
    private final byte userId;

    public NewRoomMemberPacket(byte userId) {
        this.userId = userId;
    }

    public NewRoomMemberPacket(byte[] bytes) throws IllegalStateException {
        if (bytes[0] != ID) {
            throw new IllegalStateException("Invalid Packet ID for " + this.getClass().getSimpleName());
        }
        userId = bytes[1];
    }

    @Override
    public byte[] toBytes() {
        byte[] out = new byte[2];
        out[0] = ID;
        out[1] = userId;
        return out;
    }

    @Override
    public byte getId() {
        return ID;
    }

    @Override
    public String toString() {
        return "NewRoomMemberPacket{" +
                "userId=" + userId +
                '}';
    }

    public byte getUserId() {
        return userId;
    }
}
