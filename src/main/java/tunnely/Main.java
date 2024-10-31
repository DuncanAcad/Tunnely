package tunnely;

import tunnely.packet.JoinRoomRequestPacket;
import tunnely.packet.OpenRoomRequestPacket;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        testOpenRoomRequestPacket();
        testJoinRoomRequestPacket();
    }

    private static void testOpenRoomRequestPacket() {
        System.out.println("Testing OpenRoomRequestPacket");
        OpenRoomRequestPacket p1 = new OpenRoomRequestPacket("Hello", "Test");
        System.out.println("p1 = " + p1);
        byte[] p1Bytes = p1.toBytes();
        System.out.println("p1Bytes = " + Arrays.toString(p1Bytes));
        OpenRoomRequestPacket p2 = new OpenRoomRequestPacket(p1Bytes);
        System.out.println("p2 = " + p2);
        System.out.println();
    }

    private static void testJoinRoomRequestPacket() {
        System.out.println("Testing JoinRoomRequestPacket");
        JoinRoomRequestPacket p1 = new JoinRoomRequestPacket("Hello", "Test");
        System.out.println("p1 = " + p1);
        byte[] p1Bytes = p1.toBytes();
        System.out.println("p1Bytes = " + Arrays.toString(p1Bytes));
        JoinRoomRequestPacket p2 = new JoinRoomRequestPacket(p1Bytes);
        System.out.println("p2 = " + p2);
        System.out.println();
    }
}
