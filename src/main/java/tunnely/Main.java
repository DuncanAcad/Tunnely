package tunnely;

import tunnely.packet.*;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        testOpenRoomRequestPacket();
        testJoinRoomRequestPacket();
        testCloseConnectionPacket();
        testNewRoomMemberPacket();
        testEvalMemberPacket();
        testConnectionAcceptedPacket();
        testMemberRawDataPacket();
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

    private static void testCloseConnectionPacket() {
        System.out.println("Testing CloseConnectionPacket");
        CloseConnectionPacket p1 = new CloseConnectionPacket("Hello, I am closing the connection.");
        System.out.println("p1 = " + p1);
        byte[] p1Bytes = p1.toBytes();
        System.out.println("p1Bytes = " + Arrays.toString(p1Bytes));
        CloseConnectionPacket p2 = new CloseConnectionPacket(p1Bytes);
        System.out.println("p2 = " + p2);
        System.out.println();
    }

    private static void testNewRoomMemberPacket() {
        System.out.println("Testing NewRoomMemberPacket");
        NewRoomMemberPacket p1 = new NewRoomMemberPacket((byte) 21);
        System.out.println("p1 = " + p1);
        byte[] p1Bytes = p1.toBytes();
        System.out.println("p1Bytes = " + Arrays.toString(p1Bytes));
        NewRoomMemberPacket p2 = new NewRoomMemberPacket(p1Bytes);
        System.out.println("p2 = " + p2);
        System.out.println();
    }

    private static void testEvalMemberPacket() {
        System.out.println("Testing EvalMemberPacket");
        EvalMemberPacket p1 = new EvalMemberPacket(true, "Welcome!");
        System.out.println("p1 = " + p1);
        byte[] p1Bytes = p1.toBytes();
        System.out.println("p1Bytes = " + Arrays.toString(p1Bytes));
        EvalMemberPacket p2 = new EvalMemberPacket(p1Bytes);
        System.out.println("p2 = " + p2);
        System.out.println();
    }

    private static void testConnectionAcceptedPacket() {
        System.out.println("Testing ConnectionAcceptedPacket");
        ConnectionAcceptedPacket p1 = new ConnectionAcceptedPacket();
        System.out.println("p1 = " + p1);
        byte[] p1Bytes = p1.toBytes();
        System.out.println("p1Bytes = " + Arrays.toString(p1Bytes));
        ConnectionAcceptedPacket p2 = new ConnectionAcceptedPacket(p1Bytes);
        System.out.println("p2 = " + p2);
        System.out.println();
    }

    private static void testMemberRawDataPacket() {
        System.out.println("Testing MemberRawDataPacket");
        MemberRawDataPacket p1 = new MemberRawDataPacket((byte) 21, new byte[]{1, 2, 3, 4, 5});
        System.out.println("p1 = " + p1);
        byte[] p1Bytes = p1.toBytes();
        System.out.println("p1Bytes = " + Arrays.toString(p1Bytes));
        MemberRawDataPacket p2 = new MemberRawDataPacket(p1Bytes);
        System.out.println("p2 = " + p2);
        System.out.println();
    }
}
