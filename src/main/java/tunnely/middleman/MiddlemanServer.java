package tunnely.middleman;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MiddlemanServer {
    private static List<Room> rooms = Collections.synchronizedList(new ArrayList<>());

    private static ServerSocket serverSocket;

    public static void main(String[] args) {
        System.out.println("Starting Tunnely Middleman Server...");
        // TODO: Take in port for hosting middleman server through args or System.in or whatever

        // TODO: Construct the server socket

        // TODO: Accept connections in a loop and start a thread for each accepted connection running addRoom or joinRoom
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
