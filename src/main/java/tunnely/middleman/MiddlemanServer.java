package tunnely.middleman;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import static java.lang.Integer.parseInt;

public class MiddlemanServer {
    private static final List<Room> rooms = Collections.synchronizedList(new ArrayList<>());
    private static ServerSocket serverSocket;
    private static Scanner input = new Scanner(System.in);
    public static void main(String[] args) {
        System.out.println("Starting Tunnely Middleman Server...");
        // TODO: Take in port for hosting middleman server through args or System.in or whatever & test
        // Used System.in instead of Args in case of port already being in use, it will be easier
        // to check the exception in this case, if exception found, enter new port number
        int portNum;
        do {
            System.out.println("Enter a desired port for use: ");
            portNum = input.nextInt();
        }while(portFree(portNum) == false);
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

    // we should have the ability for the server to close rooms should we not?
    private static void closeRoom(int portNum){
        throw new RuntimeException("Not implemented yet!"); // TODO
    }

    // used to check if port is in use to avoid errors due to overlapping ports
    // will attempt to create an empty unused socket, if the port is in use
    // returns boolean of whether socket is free or in use
    private static boolean portFree(int portNum){
        try(ServerSocket test = new ServerSocket(portNum)){
            // if no exception thrown, port is open
            test.close(); // currently closes socket as I'm not 100% sure that this will allow a new socket in the same
            // place as the test
            return true;
        }catch (IOException testExc){
            //if exception is thrown by creating a socket, it means the port is busy
            return false;
        }
    }
}
