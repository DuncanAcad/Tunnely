package tunnely.packet;

public class OpenRoomRequestPacket extends RoomDetailsPacket {
    public static byte ID = 0;

    public OpenRoomRequestPacket(String name, String pass) {
        super(name, pass);
    }

    public OpenRoomRequestPacket(byte[] bytes) {
        super(bytes);
    }

    @Override
    public byte getId() {
        return ID;
    }
}
