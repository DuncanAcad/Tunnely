package tunnely.packet;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NewRoomMemberPacket that)) return false;
        return userId == that.userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
