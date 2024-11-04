package tunnely.packet;

public class NewRoomMemberPacket implements Packet{

    public static byte ID = 5;//might want to change this later
    private final int userId;
    public NewRoomMemberPacket(int userId){
        this.userId = userId;
    }
    public NewRoomMemberPacket(byte[] bytes) throws IllegalStateException{
        if(bytes[0] != ID){
            throw new IllegalStateException("Invalid Packet ID for " + this.getClass().getSimpleName());
        }
        userId = bytes[1];
    }
    @Override
    public byte[] toBytes() throws IllegalStateException{
        if((int)(byte)userId != userId){
            throw new IllegalStateException("user ID is too large");//there should be more info in this error
        }
        byte[] out = new byte[2];
        out[0] = ID;
        out[1] = (byte) userId;
        return out;
    }
    public byte getId() {
        return ID;
    }
}
