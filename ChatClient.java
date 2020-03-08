import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;
public class ChatClient
{
	private static InputStream in;
	private static OutputStream out;
	public static void main(String args[]) {
		if (args.length != 2) {
			System.err.println("Usage: java TimeClient <serverhost> <port>");
			System.exit(1);
        }
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        try {
            Socket s = new Socket(host, port);
            out = s.getOutputStream();
            in = s.getInputStream();
            //client code here
            while(true){
	            Scanner scanner = new Scanner(System. in);
	            String input = scanner.nextLine();
	            if(input.charAt(0) == '/')
	            {
	            	String[] command = input.split(" ");
	                //switch statements for commands
	            	switch(command[0]) {
	            	case "/connect":
	            		if(command.length == 2) {
	            			connect(command[1]);
	            		}
	            		else {
	            			System.err.println("usage: /connect <server-name>");
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
				//System.out.println("your input is: "+input);
            }

		} catch (IOException e1) {
			System.out.println(e1);
		} /*catch (ClassNotFoundException e2) {
			System.out.println(e2);
		}*/
    }
	
	
	/**
	 * @param servername
	 * Connect to named server
	 */
	private static void connect(String servername) {
		System.out.println("connect command not yet implemented");
		
	}
	/**
	 * @param name
	 * Pick a nickname (should be unique among active users)
	 */
	private static void nick(String name) {
		System.out.println("nick command not yet implemented");
		// TODO Auto-generated method stub
		
	}
	/**
	 * List channels and number of users
	 */
	private static void list() {
		System.out.println("list command not yet implemented");
		// TODO Auto-generated method stub
		
	}
	/**
	 * @param string
	 * Join a channel, all text typed is sent to all users on the channel
	 */
	private static void join(String string) {
		System.out.println("join command not yet implemented");
		// TODO Auto-generated method stub
		
	}
	/**
	 * Leave the current channel

	 */
	private static void leave() {
		System.out.println("leave command not yet implemented");
		// TODO Auto-generated method stub
		
	}
	/**
	 * @param channel
	 * Leave the named channel
	 */
	private static void leave(String channel) {
		System.out.println("leave channel command not yet implemented");
		// TODO Auto-generated method stub
		
	}
	/**
	 * Leave chat and disconnect from server
	 */
	private static void quit() {
		System.out.println("quit command not yet implemented");
		// TODO Auto-generated method stub
		
	}
	/**
	 * Print out help message
	 */
	private static void help() {
		System.out.println("help command not yet implemented");
		// TODO Auto-generated method stub
		
	}
	/**
	 * Ask server for some stats
	 */
	private static void stats() {
		System.out.println("stats command not yet implemented");
		// TODO Auto-generated method stub
		
	}
}