package tunnely.client;

import java.net.Socket;
import tunnely.packet.*;
import tunnely.util.SocketUtil;
import java.util.Scanner;

public class TunnelyClient {
	private static Socket middleman;
	//private static String lastInput;
	
	public static void main(String[] args) {
		System.out.println("Starting Tunnely Client...");
		if(args.length >= 2) {
			String mmIP = args[0]; // args are used to enter the middleman server IP/Port.
			int mmPort = Integer.parseInt(args[1]);
		} else {
			System.out.println("IP Address and Port not provided, closing process.");
			return;
		}
		
		try { // Starts connection to middleman server.
			startConnection(new Socket(InetAddress.getByName(mmIP), mmPort)); // Opens a connection to the middleman server.
			System.out.println("Connected to Middleman Server.");
		} catch(Exception e) {
			System.out.println("Failed to connect to Middleman.");
			e.printStackTrace();
			return; // None of the Socket exceptions are recoverable.
		}
		
		while(requstRoom().getId() == 2); // Prompts user for input and searches for a room.
		
		// new thread(() -> sendRawData()).start();
		// processData();
		
		System.out.prinln("Middleman connection closed. Ending process.");
	}
	
	// Sets the Socket connected to the middleman (cannot be changed after start).
	public static startConnection(Socket middleman) {
		if(this.middleman == null) {
			this.middleman = middleman;
		}
	}
	
	// Attempts to join a room. If no room by that name exists, attempts to create one.
	public static Packet requestRoom() {
		Scanner rqscn = new Scanner(System.in);
		
		System.out.print("Enter \'J\' to join a room, other to create: ");
		String rqtype = rmscn.nextLine();
		System.out.print("Enter the room name: ");
		String name = rmscn.nextLine();
		System.out.print("Enter the password: ");
		String pass = rmscn.nextLine();
		
		try {
			if(rqtype.toUpperCase().equals("J")) {
				PacketHelper.sendPacket(middleman, new JoinRoomRequestPacket(name, pass));
			} else {
				PacketHelper.sendPacket(middleman, new OpenRoomRequestPacket(name, pass));
			}
		} catch(IOException e) {
			System.out.println("Failed to send packet.");
			return null; // null return indicates requestRoom was unsuccessful.
		}
		
		// rqscn.close();
		
		byte[] bytes = PacketHelper.receivePacketBytes(middleman); // Listen for middleman response.
		if(bytes[0] == 2) { // Connection was refused/closed.
			CloseConnectionPacket fPacket = new CloseConnectionPacket(bytes);
			System.out.println(fPacket.getMessage());
			return null;
		}
		
		return new ConnectionAcceptedPacket(bytes);
	}
	
	public static void sendRawData() {
		while(!middleman.isClosed()) {
			// TODO: Send data from our TCP app to the middleman in raw data mode.
			// readAny should be used for getting data from the TCP app and then sending to the middleman once it is in raw data mode
			// middleman.getOutputStream().write( );
		}
	}
	
	// Processes incoming data, both in raw format and packet format.
	public static void processData() {
		while(!middleman.isClosed()) {
			byte[] bytes = PacketHelper.receivePacketBytes(middleman);
			switch(bytes[0]) {
				case 2: // Close Connection.
					CloseConnectionPacket close = new CloseConnectionPacket(bytes);
					System.out.println(close.getMessage());
					return; // Leave the data-processing loop.
					
				case 3: // New Member joining.
					new thread(() -> serviceJoinRequest(new NewRoomMemberPacket(bytes))).start(); // new thread to process a join request.
					break;
					
				case 7: // Member Left.
					MemberLeftPacket left = new MemberLeftPacket(bytes);
					System.out.println("User " + left.getUserId() + " disconnected.");
					break;
					
				default: // Raw data packet.
					// TODO: Read in Raw Data Packets from Middleman.
			}
		}
	}
	
	// Services incoming join requests via NewRoomMemberPacket.
	// Room's join() method is synchronized meaning it orders multiple requests at once.
	public static void serviceJoinRequest(NewRoomMemberPacket joinRq) {
		System.out.prinln("User " + joinRq.getUserId() + " is attempting to join.");
		System.out.print("Enter \'Y\' to accept, or provide a reason for rejection:");
		String hostMessage = "N"; // "No" by default.
		
		hostMessage = new Scanner(System.in).nextLine();
		// TODO: Add a timelimit for response using another thread.
		// new thread(() -> inputRequest()).start();
		// sleep(9000);
		// Check thread return value somehow. Force close if still running.
		
		try {
			if(hostMessage.toUpper().equals("Y")) {
				PacketHelper.sendPacket(middleman, new EvalMemberPacket(true, null));
			} else {
				PacketHelper.sendPacket(middleman, new EvalMemberPacket(false, hostMessage));
			}
		} catch(IOException e) {
			System.out.println("Failed to send response.");
		}
	}
	
	/* Thread for scanning user inputs. Designed to be interrupted after a given timeslice.
	public static synchronized void inputRequest() {
		Scanner rqscn = new Scanner(System.in);
		lastInput = rqscn.nextLine();
	}
	*/
}