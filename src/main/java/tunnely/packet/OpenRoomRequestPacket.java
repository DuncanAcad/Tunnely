package tunnely.packet;

import java.nio.charset.StandardCharsets;

public class OpenRoomRequestPacket implements Packet {
    public static Byte ID = 0;

    private final String name;
    private final String pass;

    public OpenRoomRequestPacket(String name, String pass) {
        this.name = name;
        this.pass = pass;
    }

    public OpenRoomRequestPacket(byte[] bytes) {
        int i = 0;

        if (bytes[i++] != ID) {
            throw new IllegalStateException("Invalid Packet ID for " + this.getClass().getSimpleName());
        }

        int nameLength = bytes[i++];
        int passLength = bytes[i++];
        byte[] nameBytes = new byte[nameLength];
        byte[] passBytes = new byte[passLength];
        for (int j = 0; j < nameLength; j++) {
            nameBytes[j] = bytes[i++];
        }
        for (int j = 0; j < passLength; j++) {
            passBytes[j] = bytes[i++];
        }

        this.name = new String(nameBytes, StandardCharsets.UTF_8);
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

        // We need to pass integers containing the length of the strings, so our total packet size includes two integers,
        // or the amount of bytes two integers takes, plus the length of the strings' bytes.
        // We also pass the ID as a byte so that increases the size again by 1.
        int packetLength = 1 + 2 + nameLength + passLength;

        // Make our output byte array
        byte[] out = new byte[packetLength];
        int i = 0;

        out[i++] = ID;

        out[i++] = (byte) nameLength;
        out[i++] = (byte) passLength;
        for (byte b : nameBytes) {
            out[i++] = b;
        }
        for (byte b : passBytes) {
            out[i++] = b;
        }

        return out;
    }

    @Override
    public String toString() {
        return "OpenRoomRequestPacket{" + "name='" + name + '\'' + ", pass='" + pass + '\'' + '}';
    }
}
