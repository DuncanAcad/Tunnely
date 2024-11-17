package tunnely.middleman;

import tunnely.util.SocketUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class MiddlemanServer {
    private static final List<Room> rooms = Collections.synchronizedList(new ArrayList<>());
    private static ServerSocket serverSocket;
    public static void main(String[] args) {

        System.out.println("Starting Tunnely Middleman Server...");
        // Used System.in instead of Args in case of port already being in use, it will be easier
        // to check the exception in this case, if exception found, enter new port number
        int portNum;
        do {
            System.out.println("Enter a desired port for use: ");
            portNum = new Scanner(System.in).nextInt();
        } while (!SocketUtil.isPortFree(portNum));

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

    // we should have the ability for the server to close rooms should we not?
    private static void closeRoom(int portNum) {
        throw new RuntimeException("Not implemented yet!"); // TODO
    }

}
