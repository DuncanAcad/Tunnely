package tunnely.packet;

import java.nio.charset.StandardCharsets;
public class CloseConnectionPacket implements Packet{

    public static Byte ID = 4;// may want to change the id later

    private final String message;

    public CloseConnectionPacket(String message){
        this.message = message;
    }

    public CloseConnectionPacket(byte[] bytes) throws IllegalStateException{
        if(bytes[0] != ID){
            throw new IllegalStateException("Invalid Packet ID for " + this.getClass().getSimpleName());
        }
        byte[] bytesMessage = new byte[bytes.length - 1];
        for(int i = 1; i < bytes.length; i++){
            bytesMessage[i - 1] = bytes[i];
        }
        this.message = new String(bytesMessage, StandardCharsets.UTF_8);
    }

    @Override
    public byte[] toBytes(){
        byte[] messageBytes = this.message.getBytes(StandardCharsets.UTF_8);
        int packetLength = 1 + messageBytes.length;
        byte[] out = new byte[packetLength];
        out[0] = ID;
        for(int i = 0; i < messageBytes.length; i++) {
            out[i + 1] = messageBytes[i];
        }
        return out;
    }

    @Override
    public String toString(){
        return("message: " + this.message);//this might need to be changed later
    }
}
