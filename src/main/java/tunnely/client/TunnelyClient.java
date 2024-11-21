package tunnely.client;

import tunnely.packet.*;
import tunnely.util.SocketUtil;

import java.net.Socket;
import java.util.Locale;
import java.util.Scanner;

public class TunnelyClient {
    public static void main(String[] args) {
        System.out.println("Starting Tunnely Client...");

        if (args.length < 2) {
            System.out.println("IP Address and Port not provided, closing process.");
            return;
        }

        String mmIP = args[0];
        int mmPort = Integer.parseInt(args[1]);

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter 'J' to join a room, other to create: ");
        boolean isJoining = (args.length >= 3 ? args[2] : scanner.nextLine()).trim().toUpperCase(Locale.ROOT).equals("J");
        System.out.print("Enter the room name: ");
        String name = (args.length >= 4 ? args[3] : scanner.nextLine()).trim();
        System.out.print("Enter the password: ");
        String pass = (args.length >= 5 ? args[4] : scanner.nextLine()).trim();
        System.out.println("Enter the local app port:"); // Port of the app server for room host, or the port to host the virtual server for room joiners, does not need to match on clients.
        int appPort = args.length >= 6 ? Integer.parseInt(args[5]) : scanner.nextInt();

        final Socket middleman;
        try { // Starts connection to middleman server.
            // Opens a connection to the middleman server.
            System.out.println("Connecting to Middleman Server...");
            middleman = new Socket(mmIP, mmPort);
            System.out.println("Connected to Middleman Server.");
        } catch (Exception e) {
            System.out.println("Failed to connect to Middleman.");
            e.printStackTrace();
            return; // None of the Socket exceptions are recoverable.
        }

        if (isJoining) {
            joinRoom(middleman, name, pass, appPort);
        } else {
            createRoom(middleman, name, pass, appPort);
        }
    }

    private static void joinRoom(Socket middleman, String name, String pass, int appPort) {
        try {
            PacketHelper.sendPacket(middleman, new JoinRoomRequestPacket(name, pass));
            byte[] bytes = PacketHelper.receivePacketBytes(middleman);
            if (bytes == null) {
                // Socket was closed
                System.out.println("Middleman connection was ended mid request.");
                SocketUtil.carelesslyClose(middleman);
                return;
            }
            if (bytes[0] == CloseConnectionPacket.ID) {
                CloseConnectionPacket ccp = new CloseConnectionPacket(bytes);
                System.out.println("Join request rejected! " + ccp.getMessage());
                SocketUtil.carelesslyClose(middleman);
                return;
            }
            new ConnectionAcceptedPacket(bytes);
            System.out.println("Connection successful!");
        } catch (Exception e) {
            System.out.println("Failed to communicate with Middleman!");
            e.printStackTrace();
            SocketUtil.carelesslyClose(middleman);
            return;
        }
        // Room is now joined, at this point we turn the connection into a virtual server.

        VirtualServer virtualServer = new VirtualServer(middleman, appPort);
        try {
            System.out.println("Running virtual server on port " + appPort + " (connect to `localhost:" + appPort + ")`.");
            virtualServer.run();
        } catch (Exception e) {
            if (!virtualServer.isClosed()) {
                System.out.println("Error occurred while running virtual server");
                virtualServer.close(e);
            }
            return;
        }
        virtualServer.close();
        System.out.println("Virtual server ended, shutting down...");
    }

    private static void createRoom(Socket middleman, String name, String pass, int appPort) {
        try {
            PacketHelper.sendPacket(middleman, new OpenRoomRequestPacket(name, pass));
            byte[] bytes = PacketHelper.receivePacketBytes(middleman);
            if (bytes == null) {
                // Socket was closed
                System.out.println("Middleman connection was ended mid request.");
                SocketUtil.carelesslyClose(middleman);
                return;
            }
            if (bytes[0] == CloseConnectionPacket.ID) {
                CloseConnectionPacket ccp = new CloseConnectionPacket(bytes);
                System.out.println("Join request rejected! " + ccp.getMessage());
                SocketUtil.carelesslyClose(middleman);
                return;
            }
            new ConnectionAcceptedPacket(bytes);
            System.out.println("Connection successful!");
        } catch (Exception e) {
            System.out.println("Failed to communicate with Middleman!");
            e.printStackTrace();
            SocketUtil.carelesslyClose(middleman);
            return;
        }

        // Room is now opened, turn into a room host

        RoomHost roomHost = new RoomHost(middleman, appPort);
        System.out.println("Running room host");
        roomHost.run();
        System.out.println("Room Host ended, shutting down...");
    }
}