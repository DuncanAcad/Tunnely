package tunnely.client;

import tunnely.packet.*;
import tunnely.util.SocketUtil;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RoomHost {
    private final Socket middlemanServer;
    private final int appPort;
    private boolean closed = false;
    private final Map<Byte, Socket> virtualConnections;

    public RoomHost(Socket middlemanServer, int appPort) {
        this.middlemanServer = middlemanServer;
        this.appPort = appPort;
        this.virtualConnections = new ConcurrentHashMap<>();
    }

    public void run() {
        try {
            receivePacketLoop();
        } catch (Exception e) {
            close(e, "Error while running room host!");
        }
    }

    // Processes incoming data, both in raw format and packet format.
    public void receivePacketLoop() throws IOException {
        while (!middlemanServer.isClosed()) {
            byte[] bytes = PacketHelper.receivePacketBytes(middlemanServer);
            if (bytes == null) {
                close(null, "Packet stream ended, closing...");
                return;
            }
            System.out.println("Data received from middleman: " + SocketUtil.bytesToHexString(bytes)); // Todo: comment out
            switch (bytes[0]) {
                case 2: // Close Connection.
                    CloseConnectionPacket close = new CloseConnectionPacket(bytes);
                    close(null, "Room was closed, ending for reason: " + close.getMessage());
                    return; // Leave the data-processing loop.

                case 3: // New Member joining.
                    serviceJoinRequest(new NewRoomMemberPacket(bytes));
                    break;

                case 7: // Member Left.
                    MemberDisconnectPacket mdp = new MemberDisconnectPacket(bytes);
                    System.out.println("User " + mdp.getUserId() + " disconnected.");
                    removeMember(mdp.getUserId(), false);
                    break;

                case 6: // Raw data packet.
                    MemberRawDataPacket mrdp = new MemberRawDataPacket(bytes);
                    handleRawData(mrdp.getUserId(), mrdp.getData());
                default:
                    close(null, "Invalid packet ID (" + bytes[0] + ")!");
            }
        }
    }

    private void handleRawData(byte userId, byte[] data) {
        Socket socket = virtualConnections.getOrDefault(userId, null);
        if (socket == null) {
            removeMember(userId, true);
            return;
        }
        try {
            socket.getOutputStream().write(data);
        } catch (Exception e) {
            if (virtualUserExists(userId))
                System.out.println("Error sending data to " + userId + ", removing user...");
            removeMember(userId, true);
        }
    }

    private void removeMember(byte userId, boolean notifyMM) {
        Socket socket = virtualConnections.remove(userId);
        if (socket != null) SocketUtil.carelesslyClose(socket);
        if (!notifyMM) return;
        trySendToMiddleman(new MemberDisconnectPacket(userId));
    }

    // Services incoming join requests via NewRoomMemberPacket.
    // Room's join() method is synchronized meaning it orders multiple requests at once.
    public void serviceJoinRequest(NewRoomMemberPacket nrmp) {
        byte userId = nrmp.getUserId();
        if (virtualUserExists(userId)) {
            close(null, "");
        }
        System.out.println("Creating new virtual connection...");
        Socket socket = tryCreateVirtualConnection();
        if (socket == null) {
            trySendToMiddleman(new EvalMemberPacket(false, ""));
            return;
        }
        System.out.println("Connection created for user " + userId);
        virtualConnections.put(userId, socket);
        if (!trySendToMiddleman(new EvalMemberPacket(true, ""))) return;
        System.out.println("User " + userId + " has successfuly joined");
        new Thread(() -> openVirtualConnectionReceiver(userId, socket));
    }

    private void openVirtualConnectionReceiver(byte userId, Socket socket) {
        try {
            while (true) {
                byte[] bytes;
                while (!this.isClosed() && (bytes = SocketUtil.readAny(socket.getInputStream(), 1024)) != null) {
                    System.out.println("Data received from " + userId + ": " + SocketUtil.bytesToHexString(bytes));
                    if (!trySendToMiddleman(new MemberRawDataPacket(userId, bytes))) return;
                }
            }
        } catch (Exception e) {
            if (!virtualUserExists(userId)) {
                SocketUtil.carelesslyClose(socket);
                return;
            }
            removeMember(userId, true);
        }
    }

    private boolean virtualUserExists(byte userId) {
        return virtualConnections.containsKey(userId);
    }

    /**
     * Creates and returns a socket connecting to localhost:appPort or null if connection fails.
     */
    private Socket tryCreateVirtualConnection() {
        try {
            return new Socket("localhost", appPort);
        } catch (Exception e) {
            System.out.println("Failed to create virtual connection! App server might not be open...");
            return null;
        }
    }

    private synchronized boolean trySendToMiddleman(Packet packet) {
        try {
            PacketHelper.sendPacket(middlemanServer, packet);
            return true;
        } catch (IOException e) {
            close(e, "Failed to send packet to middleman (" + packet + "):");
        }
        return false;
    }

    public synchronized void close(Exception e, String message) {
        if (isClosed()) return;
        if (e != null) {
            System.out.println(message == null ? "Closing due to exception:" : message);
            e.printStackTrace();
        }
        this.closed = true;
        SocketUtil.carelesslyClose(middlemanServer);
        virtualConnections.forEach((aByte, socket) -> SocketUtil.carelesslyClose(middlemanServer));
    }

    public boolean isClosed() {
        return closed;
    }
}
