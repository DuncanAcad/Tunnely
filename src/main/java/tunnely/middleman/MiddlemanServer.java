package tunnely.middleman;

import tunnely.packet.CloseConnectionPacket;
import tunnely.packet.JoinRoomRequestPacket;
import tunnely.packet.OpenRoomRequestPacket;
import tunnely.packet.PacketHelper;
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

    public static void main(String[] args) {
        System.out.println("Starting Tunnely Middleman Server...");
        // Used System.in instead of Args in case of port already being in use, it will be easier
        // to check the exception in this case, if exception found, enter new port number
        int portNum;
        do {
            System.out.println("Enter a desired port for use: ");
            portNum = new Scanner(System.in).nextInt();
        } while (!SocketUtil.isPortFree(portNum));

        final ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(portNum);
        } catch (IOException e) {
            System.out.println("Failed to start middleman server!");
            e.printStackTrace();
            return;
        }

        // Client accepting loop
        while (!serverSocket.isClosed()) {
            final Socket clientSocket;
            try {
                clientSocket = serverSocket.accept();
            } catch (Throwable t) {
                // No errors needed if the server was simply closed.
                if (serverSocket.isClosed()) continue;
                System.out.println("Failed to accept connection!");
                t.printStackTrace();
                continue;
            }
            new Thread(() -> tryHandleNewConnection(clientSocket), "Connection Thread: " + clientSocket.getInetAddress()).start();
        }

        System.out.println("Server socket has been closed.");
    }

    private static void tryHandleNewConnection(Socket clientSocket) {
        try {
            handleNewConnection(clientSocket);
        } catch (Throwable t) {
            System.out.println("Failed to handle client connection: " + clientSocket + "\n Error Below:");
            t.printStackTrace();
            // Try closing to clean up, if it fails, that's alright
            SocketUtil.carelesslyClose(clientSocket);
        }
    }

    private static void handleNewConnection(Socket clientSocket) throws IOException {
        byte[] bytes = PacketHelper.receivePacketBytes(clientSocket);
        if (bytes == null) {
            SocketUtil.carelesslyClose(clientSocket);
            return;
        }
        byte packetId = bytes[0];
        try {
            if (packetId == OpenRoomRequestPacket.ID) {
                OpenRoomRequestPacket orrp = new OpenRoomRequestPacket(bytes);
                addRoom(orrp.getName(), orrp.getPass(), clientSocket);
            } else if (packetId == JoinRoomRequestPacket.ID) {
                // "The room joiner asks the middleman to connect with a name and password."
                JoinRoomRequestPacket jrrp = new JoinRoomRequestPacket(bytes);
                joinRoom(jrrp.getName(), jrrp.getPass(), clientSocket);
            } else {
                PacketHelper.sendPacket(clientSocket, new CloseConnectionPacket("Invalid packet received! Should be an OpenRoomRequestPacket or a JoinRoomRequestPacket."));
                SocketUtil.carelesslyClose(clientSocket);
            }
        } catch (IllegalStateException /*Thrown by packet constructors*/ e) {
            PacketHelper.sendPacket(clientSocket, new CloseConnectionPacket("Invalid packet received! Correct packet ID, but invalid data format."));
            SocketUtil.carelesslyClose(clientSocket);
        }
    }

    private static boolean roomExists(String name) {
        return getRoom(name) != null;
    }

    private static boolean hasCorrectRoomPassword(String name, String password) {
        return getRoom(name).getPassword().equals(password);
    }

    private static void addRoom(String name, String password, Socket hostSocket) throws IOException {
        if (roomExists(name)) {
            PacketHelper.sendPacket(hostSocket, new CloseConnectionPacket("Cannot open room: room already exists."));
            // No careless close here because we know the connection is going "normally", just a rejection for room name
            hostSocket.close();
            return;
        }
        Room room = new Room(hostSocket, name, password);
        rooms.add(room);
        room.start();
    }

    private static void joinRoom(String name, String password, Socket clientSocket) throws IOException {
        // "Returns an error message if room does not exist or if the password is wrong."
        if (!roomExists(name)) {
            PacketHelper.sendPacket(clientSocket, new CloseConnectionPacket("Cannot join room: room does not exist."));
            // No careless close here because we know the connection is going "normally", just a rejection for non-existent room
            clientSocket.close();
            return;
        }
        if (!hasCorrectRoomPassword(name, password)) {
            PacketHelper.sendPacket(clientSocket, new CloseConnectionPacket("Cannot join room: incorrect password."));
            // No careless close here because we know the connection is going "normally", just a rejection for wrong password
            clientSocket.close();
            return;
        }
        Room room = getRoom(name);
        room.join(clientSocket);
    }

    // we should have the ability for the server to close rooms should we not?
    private static void closeRoom(int portNum) {
        throw new RuntimeException("Not implemented yet!"); // TODO
    }

    private static Room getRoom(String name) {
        synchronized (rooms) {
            checkClosedRooms();
            return rooms.stream().filter(room -> room.getName().equals(name)).findFirst().orElse(null);
        }
    }

    private static void checkClosedRooms() {
        synchronized (rooms) {
            rooms.removeIf(Room::isClosed);
        }
    }
}
