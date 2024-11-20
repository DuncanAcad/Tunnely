package tunnely.packet;

import java.util.Objects;

public abstract class UserIdPacket implements Packet {
    private final byte userId;

    public UserIdPacket(byte userId) {
        this.userId = userId;
    }

    public UserIdPacket(byte[] bytes) throws IllegalStateException {
        if (bytes[0] != getId()) {
            throw new IllegalStateException("Invalid Packet ID for " + this.getClass().getSimpleName());
        }
        userId = bytes[1];
    }

    @Override
    public byte[] toBytes() {
        return new byte[]{getId(), userId};
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
        if (this.getClass() != o.getClass()) return false;
        UserIdPacket that = (UserIdPacket) o;
        return userId == that.userId;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "userId=" + userId +
                '}';
    }
}
