package tunnely.middleman;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MiddlemanServer {
    private static List<Room> rooms = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {
        System.out.println("Starting Tunnely Middleman Server...");
    }

    private static boolean roomExists(String name) {
        throw new RuntimeException("Not implemented yet!"); // TODO
    }

    private static boolean hasCorrectRoomPassword(String name, String password) {
        throw new RuntimeException("Not implemented yet!"); // TODO
    }

    private static void addRoom(String name, String password, Socket roomHost) {
        throw new RuntimeException("Not implemented yet!"); // TODO
    }

    private static void joinRoom(String name, String password, Socket roomMember) {
        throw new RuntimeException("Not implemented yet!"); // TODO
    }
}
