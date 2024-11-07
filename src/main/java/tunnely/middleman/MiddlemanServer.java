package tunnely.middleman;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MiddlemanServer {
    private static List<Room> rooms = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {
        System.out.println("Starting Tunnely Middleman Server...");
    }
}
