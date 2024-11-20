package tunnely.client;

import tunnely.util.SocketUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A virtual server that accepts a single connection and relays the connection to another server.
 */
public class VirtualServer {
    private boolean closed = false;
    private final Socket relay;
    private final int port;
    private ServerSocket serverSocket = null;

    public VirtualServer(Socket relaySocket, int port) {
        this.relay = relaySocket;
        this.port = port;
    }

    public void run() throws IOException {
        final Socket client;
        try {
            serverSocket = new ServerSocket(port);
            // Listen for the one and only connection
            client = serverSocket.accept();
        } catch (IOException e) {
            throw new IOException("Failed to start virtual server", e);
        }

        System.out.println("Virtual server connection made.");

        new Thread(() -> {
            try {
                InputStream inputStream = client.getInputStream();
                OutputStream outputStream = relay.getOutputStream();
                while (!isClosed()) {
                    byte[] bytes = SocketUtil.readAny(inputStream, 1024);
                    if (bytes == null){
                        if (isClosed()) return;
                        close();
                    }
                    outputStream.write(bytes);
                }
            } catch (Exception e) {
                close(e);
            }
        }, "virtual-server-local-relay").start();

        try {
            InputStream inputStream = relay.getInputStream();
            OutputStream outputStream = client.getOutputStream();
            while (!isClosed()) {
                byte[] bytes = SocketUtil.readAny(inputStream, 1024);
                if (bytes == null){
                    if (isClosed()) return;
                    close();
                }
                outputStream.write(bytes);
            }
        } catch (Exception e) {
            close(e);
        }

        close();
    }


    public void close() {
        close(null);
    }

    private synchronized void close(Exception e) {
        if (isClosed()) return;
        if (e != null) {
            System.out.println("Closing due to exception:");
            e.printStackTrace();
        }
        this.closed = true;
        SocketUtil.carelesslyClose(serverSocket);
    }

    public boolean isClosed() {
        return closed;
    }
}
