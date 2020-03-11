package Chat;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

import Chat.Package;
class ChatListener extends Thread{
	InputStream in;
	ChatListener(InputStream in){
		this.in = in;
	}
	public void run(){
		try {
			while(true) {
				ObjectInputStream oin = new ObjectInputStream(in);
				String chatmessage = (String) oin.readObject();
				System.out.println(chatmessage);
			}
		} catch (IOException e) {
			System.out.println(e);
			System.out.println("Server has closed.");
		} catch (ClassNotFoundException e) {
			System.out.println(e);
			System.out.println("Server has closed.");
		}
	}
}
public class ChatClient
{
	private static InputStream in;
	private static OutputStream out;
	private static Package pack = new Package();
	private static boolean expectingresponse = true;
	private static Socket s;
	private static boolean notQuit = false;
	public static void main(String args[]) {
		// if (args.length != 2) {
		// 	System.err.println("Usage: java ChatClient <serverhost> <port>");
		// 	System.exit(1);
		// }
	while(true){
		while(!notQuit){
			Scanner scanner = new Scanner(System.in);
			String input = scanner.nextLine();
			String[] command = input.split(" ");
			if(command[0].equals("/connect")){
				if(command.length == 3) {
					connect(command[1], command[2]);
				}
				else {
					System.err.println("usage: /connect <server-name> port");
				}
			}
			else {
				System.err.println("usage: /connect <server-name> port");
			}
		}
        // String host = args[0];
        // int port = Integer.parseInt(args[1]);
        try {
            // Socket s = new Socket(host, port);
            // out = s.getOutputStream();
            // in = s.getInputStream();
        	ObjectInputStream oin = new ObjectInputStream(in);
        	int usernum = (int) oin.readObject();
        	pack.setName("guest"+usernum);
        	
			new ChatListener(in).start();
			//notQuit = true;
            while(notQuit){
            	//server output
            	if(expectingresponse) {
            	oin = new ObjectInputStream(in);
            	String message = (String) oin.readObject();
            	System.out.println(message);
            	expectingresponse = false;
            	}
            	//client input (put this on a thread? or have a thread that just waits for input from server?)
	            Scanner scanner = new Scanner(System.in);
	            String input = scanner.nextLine();
	            
	            if(input.charAt(0) == '/')
	            {
	            	String[] command = input.split(" ");
	                //switch statements for commands
	            	switch(command[0]) {
	            	case "/connect":
	            		if(command.length == 2) {
	            			connect(command[1], command[2]);
	            		}
	            		else {
	            			System.err.println("usage: /connect <server-name> port");
	            		}
	            		break;
	            	case "/nick":
	            		if(command.length == 2) {
	            			nick(command[1]);
	            		}
	            		else {
	            			System.err.println("usage: /nick <nickname>");
	            		}
	            		break;
	            	case "/list":

	            		if(command.length == 1)
	            		{
	            			list();
	            		}
	            		else {
	            			System.err.println("usage: /list");
	            		}
	            		break;
	            	case "/join":
	            		if(command.length == 2) {
	            			join(command[1]);
	            		}
	            		else {
	            			System.err.println("usage: /join <channel>");
	            		}
	            		break;
	            	case "/leave":
	            		if(command.length == 1) {
							leave();
	            		}
	            		else if(command.length == 2)
	            		{
	            			leave(command[1]);
	            		}
	            		else {
	            			System.err.println("usage: /leave [<channel>]");
	            		}
	            		break;
	            	case "/quit":
	            		if(command.length == 1)
	            		{
	            			quit();
	            		}
	            		else {
	            			System.err.println("usage: /quit");
	            		}
	            		break;
	            	case "/help":

	            		if(command.length == 1)
	            		{
	            			help();
	            		}
	            		else {
	            			System.err.println("usage: /help");
	            		}
	            		break;
	            	case "/stats":

	            		if(command.length == 1)
	            		{
	            			stats();
	            		}
	            		else {
	            			System.err.println("usage: /stats");
	            		}
	            		break;
	                	
	    			default:
	    				System.err.println("command not recognized, use /help to see a list of commands");
	            	}
	            }
	            else {
	            	//chat message code
	            	ObjectOutputStream oout = new ObjectOutputStream(out);
	            	//TODO check to see if in a channel before sending message
	            	pack.setMessage(input);
	    			oout.writeObject(pack);
	    			oout.flush();
	            }
				//System.out.println("your input is: "+input);
            }

		} catch (IOException e1) {
			System.out.println(e1);
		} catch (ClassNotFoundException e2) {
			System.out.println(e2);
		}
	}
    }
	
	
	/**
	 * @param servername
	 * Connect to named server
	 */
	private static void connect(String servername, String port) {
		int portnum = Integer.parseInt(port);
		notQuit = true;
		try {
		s = new Socket(servername, portnum);
        out = s.getOutputStream();
		in = s.getInputStream();
		} catch (IOException e1) {
			System.out.println(e1);
		}

	}
	/**
	 * @param name
	 * Pick a nickname (should be unique among active users)
	 */
	private static void nick(String name) {
		pack.setName(name);
		System.out.println("nickname set to "+pack.getName());
		//Either we could have the message object such as message.setNick(name). Or we could have a global variable-
		//nick present in this class and have it be set here
		// TODO Auto-generated method stub
		
	}
	/**
	 * List channels and number of users
	 */
	private static void list() {
		//System.out.println("list command not yet implemented");
		ObjectOutputStream oout;
		try {
			oout = new ObjectOutputStream(out);
			oout.writeObject("/list");
	    	oout.flush();
		} catch (IOException e1) {
			System.out.println(e1);
		}
		// TODO Auto-generated method stub
		// Not sure how many methods we want in the server.java itself.
		
	}
	/**
	 * @param string
	 * Join a channel, all text typed is sent to all users on the channel
	 */
	private static void join(String string) {
		pack.setChannel(string);
		ObjectOutputStream oout;
		try {
			oout = new ObjectOutputStream(out);
			oout.writeObject("/join " + string);
	    	oout.flush();
		} catch (IOException e1) {
			System.out.println(e1);
		}
		
	}
	
	/**
	 * Leave the current channel
	 * 
	 * @throws IOException
	 * 
	 */
	private static void leave() {
		pack.setChannel("");
		ObjectOutputStream oout;
		try {
			oout = new ObjectOutputStream(out);
			oout.writeObject("/leave");
	    	oout.flush();
		} catch (IOException e1) {
			System.out.println(e1);
		} // attempt at leave
		
	}
	/**
	 * @param channel
	 * Leave the named channel
	 */
	private static void leave(String channel) {
		pack.setMessage("/leave");
		pack.setChannel("");
		//System.out.println("leave channel command not yet implemented");
		// TODO Auto-generated method stub
		
	}
	/**
	 * Leave chat and disconnect from server
	 */
	private static void quit() {
		notQuit = false;
		pack.setChannel("");
		pack.setMessage("");
		pack.setName("");
		//System.out.println("quit command not yet implemented");
		try {
			s.close();
		} catch (IOException e1) {
			System.out.println(e1);
		}
		// TODO Auto-generated method stub
		
	}
	/**
	 * Print out help message
	 */
	private static void help() {
		System.out.println("/connect connects to the chat server.");
		System.out.println("/list posts all available channels with number of users in said channels.");
		System.out.println("/join <channelname> enters the chat of that particular channel.");
		System.out.println("/leave allows you to exit the channel.");
		System.out.println("/stats still not sure what this does.");
		System.out.println("/quit leaves the channel, exits the server, then closes the client.");
		// TODO Auto-generated method stub
		
	}
	/**
	 * Ask server for some stats
	 */
	private static void stats() {
		ObjectOutputStream oout;
		try {
			oout = new ObjectOutputStream(out);
			oout.writeObject("/stats");
	    	oout.flush();
		} catch (IOException e1) {
			System.out.println(e1);
		}
		
	}
}
