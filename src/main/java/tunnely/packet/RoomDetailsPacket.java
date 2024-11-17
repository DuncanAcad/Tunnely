package tunnely.packet;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public abstract class RoomDetailsPacket implements Packet {
    private final String name;
    private final String pass;

    public RoomDetailsPacket(String name, String pass) {
        if (name.isEmpty()) {
            throw new IllegalStateException("Room name cannot be empty!");
        }
        this.name = name;
        this.pass = pass;
    }

    public RoomDetailsPacket(byte[] bytes) {
        int i = 0;

        if (bytes[i++] != getId()) {
            throw new IllegalStateException("Invalid Packet ID for " + this.getClass().getSimpleName());
        }

        int nameLength = bytes[i++];
        int passLength = bytes.length - nameLength - 2; // total - id byte - name length byte - name length
        byte[] nameBytes = new byte[nameLength];
        byte[] passBytes = new byte[passLength];
        for (int j = 0; j < nameLength; j++) {
            nameBytes[j] = bytes[i++];
        }
        for (int j = 0; j < passLength; j++) {
            passBytes[j] = bytes[i++];
        }

        this.name = new String(nameBytes, StandardCharsets.UTF_8);
        if (this.name.isEmpty()) {
            throw new IllegalStateException("Room name cannot be empty!");
        }
        this.pass = new String(passBytes, StandardCharsets.UTF_8);
    }

    // [name length (1)] [pass length (1)] [name [nl]] [pass [pl]]
    @Override
    public byte[] toBytes() throws IllegalStateException {
        // Convert strings to bytes
        byte[] nameBytes = this.name.getBytes(StandardCharsets.UTF_8);
        byte[] passBytes = this.pass.getBytes(StandardCharsets.UTF_8);

        // Get lengths
        int nameLength = nameBytes.length;
        int passLength = passBytes.length;
        if (nameLength > Byte.MAX_VALUE || passLength > Byte.MAX_VALUE) {
            throw new IllegalStateException("Name and Password are too long! Cannot construct a valid packet.");
        }

        // We need to pass a byte containing the length of the first string (the second will be calculated from total)
        // so our total packet size the id byte, plus a length byte, plus the length of our two strings
        int packetLength = 2 + nameLength + passLength;

        // Make our output byte array
        byte[] out = new byte[packetLength];
        int i = 0;

        out[i++] = getId();

        out[i++] = (byte) nameLength;
        for (byte b : nameBytes) {
            out[i++] = b;
        }
        for (byte b : passBytes) {
            out[i++] = b;
        }

        return out;
    }

    public String getName() {
        return name;
    }

    public String getPass() {
        return pass;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, pass);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoomDetailsPacket)) return false;
        RoomDetailsPacket that = (RoomDetailsPacket) o;
        return Objects.equals(name, that.name) && Objects.equals(pass, that.pass);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "name='" + name + '\'' +
                ", pass='" + pass + '\'' +
                '}';
    }
}
