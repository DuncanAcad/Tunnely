package tunnely.middleman;

import java.net.Socket;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class Room {
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
}
