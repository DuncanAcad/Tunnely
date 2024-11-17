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
    private static ServerSocket serverSocket;

    public static void main(String[] args) throws IOException {
        System.out.println("Starting Tunnely Middleman Server...");
        // Used System.in instead of Args in case of port already being in use, it will be easier
        // to check the exception in this case, if exception found, enter new port number
        int portNum;
        do {
            System.out.println("Enter a desired port for use: ");
            portNum = new Scanner(System.in).nextInt();
        } while (!SocketUtil.isPortFree(portNum));

        serverSocket = new ServerSocket(portNum);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(() -> tryHandleNewConnection(clientSocket), "Connection Thread: " + clientSocket.getInetAddress()).start();
        }
    }

    private static void tryHandleNewConnection(Socket clientSocket) {
        try {
            handleNewConnection(clientSocket);
        } catch (Throwable t) {
            System.out.println("Failed to handle client connection: " + clientSocket + "\n Error Below:");
            t.printStackTrace();
            // Try closing to clean up, if it fails, that's alright
            if (!clientSocket.isClosed()) try {
                clientSocket.close();
            } catch (IOException ignored) {
            }
        }
    }

    private static void handleNewConnection(Socket clientSocket) throws IOException {
        byte[] bytes = PacketHelper.receivePacketBytes(clientSocket);
        if (bytes == null) {
            clientSocket.close();
            return;
        }
        byte packetId = bytes[0];
        try {
            if (packetId == OpenRoomRequestPacket.ID) {
                OpenRoomRequestPacket orrp = new OpenRoomRequestPacket(bytes);
                addRoom(orrp.getName(), orrp.getPass(), clientSocket);
            } else if (packetId == JoinRoomRequestPacket.ID) {
                JoinRoomRequestPacket jrrp = new JoinRoomRequestPacket(bytes);
                joinRoom(jrrp.getName(), jrrp.getPass(), clientSocket);
            } else {
                PacketHelper.sendPacket(clientSocket, new CloseConnectionPacket("Invalid packet received! Should be an OpenRoomRequestPacket or a JoinRoomRequestPacket."));
                clientSocket.close();
            }
        } catch (IllegalStateException /*Thrown by packet constructors*/ e) {
            PacketHelper.sendPacket(clientSocket, new CloseConnectionPacket("Invalid packet received! Correct packet ID, but invalid data format."));
            clientSocket.close();
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
            hostSocket.close();
            return;
        }
        Room room = new Room(hostSocket, name, password);
        rooms.add(room);
    }

    private static void joinRoom(String name, String password, Socket clientSocket) throws IOException {
        if (!roomExists(name)) {
            PacketHelper.sendPacket(clientSocket, new CloseConnectionPacket("Cannot join room: room does not exist."));
            clientSocket.close();
            return;
        }
        if (!hasCorrectRoomPassword(name, password)) {
            PacketHelper.sendPacket(clientSocket, new CloseConnectionPacket("Cannot join room: incorrect password."));
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
            return rooms.stream().filter(room -> room.getName().equals(name)).findFirst().orElse(null);
        }
    }
}
