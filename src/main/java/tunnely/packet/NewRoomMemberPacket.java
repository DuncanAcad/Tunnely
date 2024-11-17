package tunnely.packet;

import java.util.Objects;

public class NewRoomMemberPacket implements Packet {
    public static byte ID = 3;

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
        return new byte[]{ID, userId};
    }

    @Override
    public byte getId() {
        return ID;
    }

    public byte getUserId() {
        return userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NewRoomMemberPacket)) return false;
        NewRoomMemberPacket that = (NewRoomMemberPacket) o;
        return userId == that.userId;
    }

    @Override
    public String toString() {
        return "NewRoomMemberPacket{" +
                "userId=" + userId +
                '}';
    }
}
