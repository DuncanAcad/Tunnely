package simplechat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class SimpleChat {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            throwInvalidArgs();
        }

        String inputArg = args[0];
        try {
            int hostPort = Integer.parseInt(inputArg);
            runChatHost(hostPort);
            return;
        } catch (NumberFormatException ignored) {
            // input not a number so continue...
        }

        String[] split = inputArg.split(":");
        if (split.length < 2) {
            throwInvalidArgs();
        }

        String ip = String.join(":", Arrays.asList(Arrays.copyOfRange(split, 0, split.length - 1)));
        try {
            int joinPort = Integer.parseInt(split[split.length - 1]);
            runChatJoin(ip, joinPort);
        } catch (NumberFormatException e) {
            throwInvalidArgs();
        }
    }

    private static void runChatHost(int hostPort) throws IOException {
        ServerSocket server = new ServerSocket(hostPort);
        System.out.println("Opened server on port " + hostPort);
        List<Consumer<String>> destinations = Collections.synchronizedList(new LinkedList<>());
        while (true) {
            final Socket clientConnection = server.accept();
            System.out.println("Accepted: " + clientConnection.getInetAddress() + ":" + clientConnection.getPort());
            final AtomicReference<Consumer<String>> clientStringConsumerRef = new AtomicReference<>();
            clientStringConsumerRef.set(s -> {
                if (!clientConnection.isBound()) {
                    destinations.remove(clientStringConsumerRef.get());
                    return;
                }
                try {
                    sendStringWithLength(clientConnection.getOutputStream(), s);
                } catch (IOException e) {
                    destinations.remove(clientStringConsumerRef.get());
                }

            });
            destinations.add(clientStringConsumerRef.get());
            new Thread(() -> {
                try {
                    while (true) {
                        InputStream inputStream = clientConnection.getInputStream();
                        String toSend = readStringWithLength(inputStream);
                        if (toSend == null) break;
                        System.out.println(toSend);
                        destinations.forEach(s -> s.accept(toSend));
                    }
                } catch (IOException ignored) {
                    // Continues to closing and removing
                }
                try {
                    clientConnection.close();
                    destinations.remove(clientStringConsumerRef);
                } catch (IOException ignored) {
                }
            }, "client-" + clientConnection.getInetAddress()).start();
        }
    }

    private static void runChatJoin(String ip, int joinPort) throws IOException {
        // Constructing a socket object with the ip and port also tries to connect to it.
        Socket socket = new Socket(ip, joinPort);
        System.out.println("Connected to " + ip + ":" + joinPort);
        System.out.print("Enter Name: ");
        String name = new Scanner(System.in).nextLine().trim();
        new Thread(() -> {
            try {
                InputStream inp = socket.getInputStream();
                while (true) {
                    String s = readStringWithLength(inp);
                    if (s == null) {
                        System.exit(0);
                    }
                    System.out.println(s);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, "read-thread").start();

        OutputStream out = socket.getOutputStream();
        String toSend;
        while (!(toSend = (new Scanner(System.in).nextLine().trim())).isEmpty()) {
            sendStringWithLength(out, "<" + name + "> " + toSend);
        }
        socket.close();
        System.exit(0);
    }

    private static void sendStringWithLength(OutputStream out, String s) throws IOException {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        out.write(intToBytes(bytes.length));
        out.write(bytes);
    }

    private static String readStringWithLength(InputStream inputStream) throws IOException {
        byte[] bytes = readSpecific(inputStream, 4);
        if (bytes == null) {
            return null;
        }
        int len = bytesToInt(bytes);
        bytes = readSpecific(inputStream, len);
        if (bytes == null) return null;
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * @param max the maximum number of bytes to read.
     *
     * @return a byte array with the length of the bytes that were read, or null if the end of stream has been reached.
     */
    private static byte[] readAny(InputStream stream, int max) throws IOException {
        byte[] buf = new byte[max];
        int actuallyRead = stream.read(buf);
        if (actuallyRead == -1) return null;
        return Arrays.copyOfRange(buf, 0, actuallyRead);
    }

    private static byte[] readSpecific(InputStream stream, int total) throws IOException {
        byte[] buf = new byte[total];
        int off = 0;
        int offAdd = 0;
        while ((offAdd = stream.read(buf, off, total - off)) != -1) {
            off += offAdd;
            if (off == total) return buf;
        }
        return null;
    }

    private static byte[] intToBytes(int x) {
        // Opposite of https://stackoverflow.com/questions/2383265/convert-4-bytes-to-int
        ByteBuffer b = ByteBuffer.allocate(4);
        b.putInt(x);
        return b.array();
    }

    public static int bytesToInt(byte[] bytes) {
        if (bytes.length != 4) throw new IllegalArgumentException();
        // https://stackoverflow.com/questions/2383265/convert-4-bytes-to-int
        return ByteBuffer.wrap(bytes).getInt();
    }

    private static void throwInvalidArgs() {
        throw new IllegalArgumentException("Invalid Args! Enter a port to host a room on the specified port, or an ip address, a colon, and a port (e.g. '1.2.3.4:7777')");
    }
}

