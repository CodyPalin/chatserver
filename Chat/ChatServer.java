package Chat;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.InetAddress;
import java.lang.System;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import Chat.Package;

class UserListener extends Thread{
	int userid;
	OutputStream out;
	UserListener(int userid, OutputStream out){
		this.userid = userid;
		this.out = out;
	}
	public void run(){
		try {
			while(true) {
				Package pack = ChatServer.bqueues.get(userid).poll();
				if(pack == null) {
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
					}
					continue;
				}
				String channel = pack.getChannel().toLowerCase();
				if(channel.equals("server_message")||ChatServer.channels.get(channel).contains(userid)) {
					ObjectOutputStream oout = new ObjectOutputStream(out);
					oout.writeObject(pack.getName()+": "+pack.getMessage());
					oout.flush();
				}
			}
		} catch (IOException e) {
			//System.out.println(e);
			if(ChatServer.debug)
				System.out.println("client"+userid+" has disconnected");
		}
	}
}
class UserConnection extends Thread{
	private Socket client;
	private int userid;
	private InputStream in;
	private OutputStream out;
	UserConnection(Socket client, int userid) throws SocketException{
		this.client = client;
		this.userid = userid;
		setPriority(NORM_PRIORITY - 1);
	}
	public String stats() {
		String retval = "";
		Enumeration<Set<Integer>> channels = ChatServer.channels.elements();
		int numchatters = 0;
		while(channels.hasMoreElements())
		{
			numchatters+=channels.nextElement().size();
		}
		retval +="Number of users in chat: "+numchatters+", \n";
		retval +="Total clients ever connected to this server: " +ChatServer.totalclients;
		return "here are some stats: \n"+retval;
		
	}
	public void run(){
		try {
			if(ChatServer.debug)
				System.out.println("starting chat with client "+ client.getInetAddress().getHostAddress());
			out = client.getOutputStream();
			in = client.getInputStream();
			//tell client their temp nickname
			ObjectOutputStream oout = new ObjectOutputStream(out);
			oout.writeObject(userid);
			oout.flush();
			//welcome message
			String message = "SERVER: Hello guest"+userid+" you are not yet connected to any channel, use /nick to change your name.";
			oout = new ObjectOutputStream(out);
			oout.writeObject(message);
			oout.flush();
			//create UserListener thread to listen for chats from other clients
			new UserListener(userid, out).start();
			//wait for input code
			boolean connected = true;
			while(connected) {
	        	ObjectInputStream oin = new ObjectInputStream(in);
	        	
	        	ChatServer.task.cancel();
	        	ChatServer.task = new MyTimerTask();
	        	ChatServer.timer.cancel();
	        	ChatServer.timer = new Timer();
	        	ChatServer.timer.schedule(ChatServer.task, 300000);
	        	//if chat message
	        	Object input = oin.readObject();
	        	if(input instanceof Package) {
		        	Package clientinput = (Package) input;
		        	String channel = clientinput.getChannel();
		        	if(channel.equals(""))
		        	{
		        		channel = "[default]";
		        	}
		        	//just print to server console for now
		        	String chatmessage = "{In channel: "+ channel+"} "+clientinput.getName()+": "+clientinput.getMessage();
		        	if(ChatServer.debug)
		        		System.out.println(chatmessage);
		        	
		        	Enumeration<BlockingQueue<Package>> allthreads = ChatServer.bqueues.elements();
		        	while(allthreads.hasMoreElements()) {
		        		allthreads.nextElement().add(clientinput);
		        	}
	        	}
	        	else { //else should be a command string
	        		//case statement here for commands that will be passed to server
	        		String commandinput = (String) input;
	        		String[] command = commandinput.split(" ");
	        		switch(command[0]) {
	        		case "/join":
	        			Set<Integer> clientlist = ChatServer.channels.get(command[1].toLowerCase());
	        			if(clientlist == null) {
	        				oout = new ObjectOutputStream(out);
	        				oout.writeObject("SERVER: Channel does not exist, use /list to see a list of channels");
	        				oout.flush();
	        			}
	        			else {
	        				clientlist.add(userid);
	        				oout = new ObjectOutputStream(out);
	        				oout.writeObject("SERVER: Successfully joined channel: " + command[1]);
	        				oout.flush();
	        			}
	        			break;
	        		case "/leave":
	        			Enumeration<Set<Integer>> channels = ChatServer.channels.elements();
	        			while(channels.hasMoreElements())
	        			{
	        				boolean left = channels.nextElement().remove(userid);
	        				if(left)
	        				{
		        				oout = new ObjectOutputStream(out);
		        				oout.writeObject("SERVER: You have left the channel");
		        				oout.flush();
	        				}
	        			}
	        			break;
	        		case "/list":
	        			String list = "";
	        			Enumeration<String> c = ChatServer.channels.keys();
	        			while(c.hasMoreElements())
	        			{
	        				String currentc = c.nextElement();
	        				int numchatters = ChatServer.channels.get(currentc).size();
	        				list+=currentc+" ("+numchatters+" chatters)\n";
	        			}
	        			oout = new ObjectOutputStream(out);
        				oout.writeObject(list);
        				oout.flush();
        				break;
	        		case "/stats":
	        			oout = new ObjectOutputStream(out);
        				oout.writeObject(stats());
        				oout.flush();
	        			break;
	        		default:
	        			break;
	        		}
	        	}
			}
			
		}catch(IOException e)
		{
			if(ChatServer.debug)
				System.out.println("client"+userid+" has disconnected");
		} catch (ClassNotFoundException e2) {
			if(ChatServer.debug)
				System.out.println("client"+userid+" has disconnected");
		}
	}
}
class MyTimerTask extends TimerTask{
	public void run()
	{
		if(ChatServer.debug)
    		System.out.println("Shutting down server due to inactivity");
    	Package serverpack = new Package();
    	serverpack.setChannel("SERVER_MESSAGE");
    	serverpack.setName("SERVER");
    	serverpack.setMessage("Shutting down server due to inactivity.");
    	Enumeration<BlockingQueue<Package>> allthreads = ChatServer.bqueues.elements();
    	while(allthreads.hasMoreElements()) {
    		allthreads.nextElement().add(serverpack);
    	}
    	try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	System.exit(0);
	}
}
class Shutdown extends Thread {
	public String stats() {
		String retval = "";
		Enumeration<Set<Integer>> channels = ChatServer.channels.elements();
		int numchatters = 0;
		while(channels.hasMoreElements())
		{
			numchatters+=channels.nextElement().size();
		}
		retval +="Number of users in chat: "+numchatters+", \n";
		retval +="Total clients ever connected to this server: " +ChatServer.totalclients+"\n";
		return "here are some stats: \n"+retval;
		
	}
    public void run() {
    	if(ChatServer.debug)
    		System.out.println("Shutting down server in 5 seconds...");
    	Package serverpack = new Package();
    	serverpack.setChannel("SERVER_MESSAGE");
    	serverpack.setName("SERVER");
    	serverpack.setMessage("The server will be shutting down in 5 seconds.\n"+stats());
    	Enumeration<BlockingQueue<Package>> allthreads = ChatServer.bqueues.elements();
    	while(allthreads.hasMoreElements()) {
    		allthreads.nextElement().add(serverpack);
    	}
        try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        
        System.out.println("Goodbye");
    }
 }
public class ChatServer {
	private static int port = 5121;
	volatile static boolean debug = false;
	volatile static Timer timer;
	volatile static TimerTask task;
	volatile static int totalclients = 0;
    private ServerSocket s;
    public volatile static Dictionary<Integer, BlockingQueue<Package>> bqueues = new Hashtable<Integer, BlockingQueue<Package>>();
    public volatile static Dictionary<String, Set<Integer>> channels = new Hashtable<String, Set<Integer>>();
    public static void main(String args[]) {
		if(args.length != 2 && args.length != 4)
		{
			System.err.println("Usage: java ChatServer -p <port#> -d <debug-level>");
			System.exit(1);
		}
		String portinput = "";
		String debuginput = "";
		if(args[0].equals("-p")) 
			portinput = args[1];
		if(args.length > 2)
		{
			debuginput = args[3];
			if(args[2].equals("-p")) {
				portinput = args[3];
				debuginput = args[1];
			}
		}
    	if(portinput != "") {
    		int p = 0;
    		try {
    		p = Integer.parseInt(portinput);
    		}catch(NumberFormatException e1) {
    			System.err.println("Usage: java ChatServer -p <port#> -d <debug-level>");
    			System.exit(1);
    		}
    		if(p!=0)
    			port = p;
    	}
    	if(debuginput != "") {
    		int d = 0;
    		try {
    		d = Integer.parseInt(debuginput);
    		}catch(NumberFormatException e1) {
    			System.err.println("Usage: java ChatServer -p <port#> -d <debug-level>");
    			System.exit(1);
    		}
    		if(d!=0)
    		{
    			debug = true;
    		}
    	}
    	Runtime.getRuntime().addShutdownHook(new Shutdown());
    	task = new MyTimerTask();
		timer = new Timer();
        timer.schedule(task, 300000);
        ChatServer server = new ChatServer();
    }

    public ChatServer() {
    	try {
			s = new ServerSocket(port);
			if(debug)
				System.out.println("server up and running on port " + port + " " + InetAddress.getLocalHost());
		} catch (IOException e) {
			System.err.println(e);
		}
    	ArrayList<String> chanls = new ArrayList<String>(Arrays.asList(
    			"general",
    			"help",
    			"potatoes"
    			)); //add more to this list if you want to add more channels
    	
    	for(String chan: chanls) {
    	channels.put(chan, new HashSet<Integer>());
    	}
    	int userid = 0;
    	Socket client;
    	while(true)
    	{
    		try {
    			//connect
    			client = s.accept();
    			userid++;
    			totalclients++;
    			if(debug)
	    			System.out.println(
					        "Received connect from " + client.getInetAddress().getHostAddress() + ": " + client.getPort());
    			//create thread's specific blocking queue, this will store chats sent from other clients to this client
    			bqueues.put(userid, new LinkedBlockingQueue<>(500));
    			new UserConnection(client, userid).start();
    		}catch (IOException e) {
				System.err.println(e);
			}
    	}
    	
    }
}
