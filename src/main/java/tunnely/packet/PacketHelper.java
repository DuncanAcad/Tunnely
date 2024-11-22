package tunnely.packet;

import tunnely.util.SocketUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public final class PacketHelper {
    private PacketHelper() {
        // No constructing objects of this class!
    }

    public static void sendPacket(Socket socket, Packet packet) throws IOException {
        byte[] bytes = packet.toBytes();
        int length = bytes.length;
        OutputStream outputStream = socket.getOutputStream();
        // Send 4 bytes containing length
        byte[] lengthBytes = SocketUtil.intToBytes(length);
//        System.out.println("Sending packet length: " + SocketUtil.bytesToHexString(lengthBytes));
        outputStream.write(lengthBytes);
        // Send bytes of that length
//        System.out.println("Sending packet: " + packet + " - " + SocketUtil.bytesToHexString(bytes));
        outputStream.write(bytes);
    }

    public static byte[] receivePacketBytes(Socket socket) throws IOException {
        InputStream inputStream = socket.getInputStream();
        byte[] bytes = SocketUtil.readSpecific(inputStream, 4);
        if (bytes == null) return null;
        int length = SocketUtil.bytesToInt(bytes);
        if (length == 0) return null;
//        System.out.println("Received packet length: " + length);
        byte[] out = SocketUtil.readSpecific(inputStream, length);
//        if (out != null)
//            System.out.println("Received packet bytes: " + SocketUtil.bytesToHexString(out));
        return out;
    }
}
