package tunnely.client;

public class TunnelyClient {
    public static void main(String[] args) {
        System.out.println("Starting Tunnely Client...");
        // TODO: Take in room name and password and host/join from user from args or System.in or whatever
        // TODO: Also obtain middleman server IP:port somehow (probably shouldn't be hardcoded)

        // TODO: Send appropriate packet to middleman, interpret responses, start virtual server or prepare to make virtual connections.

        // readAny should be used for getting data from the TCP app and then sending to the middleman once it is in raw data mode.
    }
}
