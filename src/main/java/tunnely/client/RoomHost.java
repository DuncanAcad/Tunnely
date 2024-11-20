package tunnely.client;

import tunnely.packet.*;
import tunnely.util.SocketUtil;

import java.io.IOException;
import java.net.Socket;

public class RoomHost {
    private final Socket middlemanServer;
    private final int appPort;

    public RoomHost(Socket middlemanServer, int appPort) {
        this.middlemanServer = middlemanServer;
        this.appPort = appPort;
    }

    public void run() {

    }

    // Processes incoming data, both in raw format and packet format.
    public void processData() throws IOException {
        while (!middlemanServer.isClosed()) {
            byte[] bytes = PacketHelper.receivePacketBytes(middlemanServer);
            switch (bytes[0]) {
                case 2: // Close Connection.
                    CloseConnectionPacket close = new CloseConnectionPacket(bytes);
                    System.out.println(close.getMessage());
                    SocketUtil.carelesslyClose(middlemanServer);
                    // TODO: close all connections to app server
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
        }
    }
}
