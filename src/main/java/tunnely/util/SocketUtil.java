package tunnely.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class SocketUtil {
    /**
     * @param max the maximum number of bytes to read.
     *
     * @return a byte array with the length of the bytes that were read, or null if the end of stream has been reached.
     */
    public static byte[] readAny(InputStream stream, int max) throws IOException {
        byte[] buf = new byte[max];
        int actuallyRead = stream.read(buf);
        if (actuallyRead == -1) return null;
        return Arrays.copyOfRange(buf, 0, actuallyRead);
    }


    /**
     * @param stream the input stream, expected to be from a socket
     * @param total  the total amount of bits to receive
     *
     * @return a byte array of length `total`, or null
     */
    public static byte[] readSpecific(InputStream stream, int total) throws IOException {
        byte[] buf = new byte[total];
        int off = 0;
        int offAdd;
        while ((offAdd = stream.read(buf, off, total - off)) != -1) {
            off += offAdd;
            if (off == total) return buf;
        }
        return null;
    }

    /**
     * @param x the integer to convert
     *
     * @return a byte array of length 4 representing the integer x
     */
    public static byte[] intToBytes(int x) {
        // Opposite of https://stackoverflow.com/questions/2383265/convert-4-bytes-to-int
        ByteBuffer b = ByteBuffer.allocate(4);
        b.putInt(x);
        return b.array();
    }

    /**
     * @param bytes a byte array of length 4 representing an integer
     *
     * @return the integer represented by the bytes
     */
    public static int bytesToInt(byte[] bytes) {
        if (bytes.length != 4) throw new IllegalArgumentException();
        // https://stackoverflow.com/questions/2383265/convert-4-bytes-to-int
        return ByteBuffer.wrap(bytes).getInt();
    }

    /**
     * Used to check if port is in use to avoid errors due to overlapping ports.
     * Will attempt to create an empty unused socket with the port, and immediately close it.
     *
     * @param portNum the port to test
     *
     * @return true if the socket is free, otherwise false
     */
    public static boolean isPortFree(int portNum) {
        try (ServerSocket test = new ServerSocket(portNum)) {
            // if no exception thrown, port is open
            test.close(); // currently closes socket as I'm not 100% sure that this will allow a new socket in the same
            SocketUtil.carelesslyClose(test);
            // place as the test
            return true;
        } catch (IOException testExc) {
            //if exception is thrown by creating a socket, it means the port is busy
            return false;
        }
    }

    public static void carelesslyClose(Closeable socket) {
        if (socket == null) return;
        try {
            socket.close();
        } catch (Throwable ignored) {
        }
    }
}
