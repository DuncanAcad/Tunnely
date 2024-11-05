package tunnely.packet;

import java.nio.charset.StandardCharsets;

public class MemberRawDataPacket implements Packet{

    public static byte ID = 8;
    private final byte userID;
    private final String message;
    public MemberRawDataPacket(byte userID, String message){
        this.userID = userID;
        this.message = message;
    }
    public MemberRawDataPacket(byte[] bytes) throws IllegalStateException{
        if(bytes[0] != ID){
            throw new IllegalStateException("Invalid Packet ID for " + this.getClass().getSimpleName());
        }
        userID = bytes[1];
        byte[] bytesMessage = new byte[bytes.length - 2];
        for(int i = 2; i < bytes.length; i++){
            bytesMessage[i - 2] = bytes[i];
        }
        message = new String(bytesMessage, StandardCharsets.UTF_8);
    }
    @Override
    public byte[] toBytes() {
        byte[] bytesMessage = message.getBytes(StandardCharsets.UTF_8);
        byte[] out = new byte[2 + bytesMessage.length];
        out[0] = ID;
        out[1] = userID;
        for(int i = 2; i < out.length; i++){
            out[i] = bytesMessage[i - 2];
        }
        return out;
    }

    @Override
    public byte getId() {
        return ID;
    }
    public byte getUserID(){
        return userID;
    }
    public String getMessage(){
        return message;
    }
}
