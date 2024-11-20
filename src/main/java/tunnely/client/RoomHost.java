package tunnely.client;

import tunnely.packet.*;
import tunnely.util.SocketUtil;

import java.io.IOException;
import java.net.Socket;

public class RoomHost {
    private final Socket middlemanServer;
    private final int appPort;
    private boolean closed = false;

    public RoomHost(Socket middlemanServer, int appPort) {
        this.middlemanServer = middlemanServer;
        this.appPort = appPort;
    }

    public void run() {
        try {
            receivePacketLoop();
        } catch (Exception e) {
            close(e);
        }
    }

    // Processes incoming data, both in raw format and packet format.
    public void receivePacketLoop() throws IOException {
        while (!middlemanServer.isClosed()) {
            byte[] bytes = PacketHelper.receivePacketBytes(middlemanServer);
            if (bytes == null) {
                System.out.println("Packet stream ended, closing...");
                close();
                return;
            }
            switch (bytes[0]) {
                case 2: // Close Connection.
                    CloseConnectionPacket close = new CloseConnectionPacket(bytes);
                    System.out.println("Room was closed, ending for reason: " + close.getMessage());
                    close();
                    return; // Leave the data-processing loop.

                case 3: // New Member joining.
                    new Thread(() -> serviceJoinRequest(new NewRoomMemberPacket(bytes))).start(); // new thread to process a join request.
                    break;

                case 7: // Member Left.
                    MemberDisconnectPacket left = new MemberDisconnectPacket(bytes);
                    System.out.println("User " + left.getUserId() + " disconnected.");
                    break;

                default: // Raw data packet.
                    // TODO: Read in Raw Data Packets from Middleman.
            }
        }
    }

    // Services incoming join requests via NewRoomMemberPacket.
    // Room's join() method is synchronized meaning it orders multiple requests at once.
    public void serviceJoinRequest(NewRoomMemberPacket joinRq) {
        System.out.println("User " + joinRq.getUserId() + " is attempting to join.");

        try {
            PacketHelper.sendPacket(middlemanServer, new EvalMemberPacket(true, null));
        } catch (IOException e) {
            System.out.println("Failed to send response.");
            close();
        }
    }

    public void close() {
        close(null);
    }

    public synchronized void close(Exception e) {
        if (isClosed()) return;
        if (e != null) {
            System.out.println("Closing due to exception:");
            e.printStackTrace();
        }
        this.closed = true;
        SocketUtil.carelesslyClose(middlemanServer);
        // TODO: close all virtual connections
    }

    public boolean isClosed() {
        return closed;
    }
}
