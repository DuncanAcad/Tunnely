package tunnely.packet;

public class JoinRoomRequestPacket extends RoomDetailsPacket {
    public static byte ID = 1;

    public JoinRoomRequestPacket(String name, String pass) {
        super(name, pass);
    }

    public JoinRoomRequestPacket(byte[] bytes) {
        super(bytes);
    }

    @Override
    public byte getId() {
        return ID;
    }
}
