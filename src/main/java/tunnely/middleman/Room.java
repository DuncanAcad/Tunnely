package tunnely.middleman;

import tunnely.packet.CloseConnectionPacket;
import tunnely.packet.EvalMemberPacket;
import tunnely.packet.NewRoomMemberPacket;
import tunnely.packet.PacketHelper;

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

    public Room(Socket roomHostConnection, String name, String password) {
        this.roomHostConnection = roomHostConnection;
        this.roomMemberConnections = new ConcurrentHashMap<>();
        this.name = name;
        this.password = password;
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
        byte newId = -1;
        Set<Byte> currentBytes = roomMemberConnections.keySet();
        for (int i = 0; i <= Byte.MAX_VALUE; i++) {
            if (currentBytes.contains((byte) i)) continue;
            newId = (byte) i;
            break;
        }
        if (newId == -1) {
            PacketHelper.sendPacket(roomMember, new CloseConnectionPacket("Failed to join room: no user IDs available."));
            roomMember.close();
            return;
        }
        PacketHelper.sendPacket(roomHostConnection, new NewRoomMemberPacket(newId));
        byte[] bytes = PacketHelper.receivePacketBytes(roomHostConnection);
        if (bytes == null) {
            // Uh oh, unexpected response
            closeRoom("Closing room: improper/invalid/no response.");
            return;
        }
        if (bytes[0] != EvalMemberPacket.ID) {
            // Uh oh, unexpected response
            return;
        }
        EvalMemberPacket emp;
        try {
            emp = new EvalMemberPacket(bytes);
        } catch (Throwable t) {
            // Uh oh
            closeRoom("Closing room: invalid packet as response.");
            return;
        }
        if (!emp.isAccepted()) {
            PacketHelper.sendPacket(roomMember, new CloseConnectionPacket("Failed to join room: room host rejected join request."
                    + (emp.getMessage().isEmpty() ? "" : (" Room host gave the following reason: " + emp.getMessage()))));
            roomMember.close();
            return;
        }
        // Finally, add the room member to our connections map
        roomMemberConnections.put(newId, roomMember);
    }

    private void closeRoom(String message) {
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
        try {
            roomHostConnection.close();
        } catch (Throwable ignored) {
        }
        roomMemberConnections.forEach((aByte, socket) -> {
            try {
                socket.close();
            } catch (Throwable ingored) {
            }
        });
    }

    public boolean isClosed() {
        return closed; // TODO use this method in MiddlemanServer to remove removes, make a checkClosedRooms method to remove them and use checkClosedRooms at the start of every join/create request
    }
}