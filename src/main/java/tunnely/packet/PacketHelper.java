package tunnely.packet;

import java.io.IOException;
import java.net.Socket;

public final class PacketHelper {
    private PacketHelper() {
        // No constructing objects of this class!
    }

    // Tip: socket.getInputStream() can be used to get an InputStream, which can be used with readSpecific to get data from the socket connection
    //      socket.getOutputStream() can be used to get an OutputStream, which can send data to the socket connection

    public static void sendPacket(Socket socket, Packet packet) throws IOException {
        throw new RuntimeException("Not implemented yet!");
        // TODO: Convert packet to bytes using its method and get length

        // TODO: Send packet length as 4 bytes, then send packet bytes
    }

    public static byte[] receivePacketBytes(Socket socket) throws IOException {
        throw new RuntimeException("Not implemented yet!");
        // TODO: Receive 4 bytes, convert to an int to get length

        // TODO: Receive bytes of found length and return
    }
}
