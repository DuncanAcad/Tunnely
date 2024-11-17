package tunnely.middleman;

import tunnely.packet.*;
import tunnely.util.SocketUtil;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Room {
    private boolean closed = false;

    private final String name;
    private final String password;

    private final Socket roomHostConnection;
    private final Map<Byte, Socket> roomMemberConnections;

    private EvalMemberPacket latestEvalMemberPacket;

    public Room(Socket roomHostConnection, String name, String password) {
        this.roomHostConnection = roomHostConnection;
        this.roomMemberConnections = new ConcurrentHashMap<>();
        this.name = name;
        this.password = password;
        this.latestEvalMemberPacket = null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, password);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Room)) return false;
        Room room = (Room) o;
        return Objects.equals(name, room.name) && Objects.equals(password, room.password);
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public synchronized void join(Socket roomMember) throws IOException {
        // "The middleman assigns a user ID to the room joiner"
        byte newId = -1;
        Set<Byte> currentBytes = roomMemberConnections.keySet();
        for (int i = 0; i <= Byte.MAX_VALUE; i++) {
            if (currentBytes.contains((byte) i)) continue;
            newId = (byte) i;
            break;
        }
        if (newId == -1) {
            PacketHelper.sendPacket(roomMember, new CloseConnectionPacket("Failed to join room: no user IDs available."));
            SocketUtil.carelesslyClose(roomMember);
            return;
        }

        // "and will tell the room host of the new user."
        PacketHelper.sendPacket(roomHostConnection, new NewRoomMemberPacket(newId));

        // "The room host accepts the new user and stores any info about it (user id), and then tells the middleman that it will accept the user."

        // TODO: let main loop receive the bytes and add to evalMemberPackets
        byte[] bytes = PacketHelper.receivePacketBytes(roomHostConnection);
        if (bytes == null) {
            // Uh oh, unexpected response from room host, that's real bad
            closeRoom("Closing room: improper/invalid/no response.");
            SocketUtil.carelesslyClose(roomMember);
            return;
        }
        if (bytes[0] != EvalMemberPacket.ID) {
            // Uh oh, unexpected response from room host, that's real bad
            return;
        }

        // Wait for a latestEvalMemberPacket
        long startWaitTime = System.currentTimeMillis();
        while (latestEvalMemberPacket == null) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (System.currentTimeMillis() - startWaitTime > 10000) {
                // Uh oh, no eval member response from room host for 10 seconds straight, that's real bad
                if (isClosed()) return;
                closeRoom("Closing room: improper/invalid/no response.");
                SocketUtil.carelesslyClose(roomMember);
                return;
            }
        }
        EvalMemberPacket emp = latestEvalMemberPacket;
        latestEvalMemberPacket = null;

        // "If, for whatever reason, the room host doesn’t want to accept that (such as too many users in room) the room host tells the middleman “nope”"
        if (!emp.isAccepted()) {
            PacketHelper.sendPacket(roomMember, new CloseConnectionPacket("Failed to join room: room host rejected join request."
                    + (emp.getMessage().isEmpty() ? "" : (" Room host gave the following reason: " + emp.getMessage()))));
            SocketUtil.carelesslyClose(roomMember);
            return;
        }

        // "The middleman tells the room joiner that they have been accepted"
        PacketHelper.sendPacket(roomMember, new ConnectionAcceptedPacket());

        // Finally, add the room member to our connections map
        roomMemberConnections.put(newId, roomMember);

        // "the packet system is then abandoned for this connection, and it is now just sending raw data for whatever application is in use."

        byte userId = newId;
        new Thread(() -> {
            try {
                this.openRawDataLineReceiver(userId, roomMember);
            } catch (Throwable t) {
            }
        }).start();
    }

    /**
     * This method runs receives all bytes from a room member, and sends them to the room host with the MemberRawDataPacket.
     * Sending data to the member is done after receiving data from the room host, so it is done in the main loop.
     */
    private void openRawDataLineReceiver(byte userId, Socket roomMember) throws IOException {
        // inputStream -> raw packet to room host with id
        // raw packet from room host with id -> output stream
        byte[] bytes;
        while ((bytes = SocketUtil.readAny(roomMember.getInputStream(), 4096)) != null) {
            sendToRoomHost(userId, bytes);
        }
    }

    private synchronized void sendToRoomHost(byte userId, byte[] bytes) throws IOException {
        PacketHelper.sendPacket(roomHostConnection, new MemberRawDataPacket(userId, bytes));
    }

    private synchronized void closeRoom(String message) {
        try {
            PacketHelper.sendPacket(roomHostConnection, new CloseConnectionPacket(message));
        } catch (Throwable ignored) {
        }
        this.close();
    }

    private synchronized void close() {
        // Kill it all and mark as closed!
        // Marking as closed will let the middleman server check isClosed and see it can remove it from its rooms list.
        closed = true;
        SocketUtil.carelesslyClose(roomHostConnection);
        roomMemberConnections.forEach((aByte, socket) -> SocketUtil.carelesslyClose(socket));
    }

    public boolean isClosed() {
        return closed; // TODO use this method in MiddlemanServer to remove removes, make a checkClosedRooms method to remove them and use checkClosedRooms at the start of every join/create request
    }

    public void start() {
        try {
            runHostReceiveLoop();
        } catch (Throwable t) {
            System.out.println("Error during host receive loop.");
            t.printStackTrace();
        }
        this.closeRoom("Error during host receive loop.");
    }

    private void runHostReceiveLoop() throws IOException {
        while (true) {
            byte[] bytes = PacketHelper.receivePacketBytes(roomHostConnection);
            if (bytes == null) {
                this.closeRoom("No packet received.");
                return;
            }

            switch (bytes[0]) {
                case /*CloseConnectionPacket.ID*/2:
                    CloseConnectionPacket ccp = new CloseConnectionPacket(bytes);
                    this.closeRoom("Room closing due to given reason: " + ccp.getMessage());
                    return;
                case /*MemberRawDataPacket.ID*/6:
                    MemberRawDataPacket mrdp = new MemberRawDataPacket(bytes);
                    this.sendRawToMember(mrdp.getUserID(), mrdp.getData());
                    break;
                case /*EvalMemberPacket.ID*/4:
                    // To be processed by the thread expecting this
                    // Only one thread should be expecting this at a time, so only a single non-collection field is needed for storing this
                    assert this.latestEvalMemberPacket == null;
                    this.latestEvalMemberPacket = new EvalMemberPacket(bytes);
                    break;
            }
        }
    }

    private void sendRawToMember(byte userID, byte[] data) {

    }
}