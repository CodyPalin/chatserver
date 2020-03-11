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
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
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
				String message = ChatServer.bqueues.get(userid).poll();
				if(message == null) {
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
					}
					continue;
				}
				ObjectOutputStream oout = new ObjectOutputStream(out);
				oout.writeObject(message);
				oout.flush();
			}
		} catch (IOException e) {
			System.out.println(e);
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
	public void run(){
		try {
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
		        	String chatmessage = "{In channel: "+ channel+"}"+clientinput.getName()+": "+clientinput.getMessage();
		        	System.out.println(message);
		        	
		        	Enumeration<BlockingQueue<String>> allthreads = ChatServer.bqueues.elements();
		        	while(allthreads.hasMoreElements()) {
		        		allthreads.nextElement().add(chatmessage);
		        	}
	        	}
	        	else { //else should be a command string
	        		//case statement here for commands that will be passed to server
	        		String command = (String) input;
	        	}
			}
			
		}catch(IOException e)
		{
			System.out.println(e);
			System.out.println("server"+userid+" has closed");
		} catch (ClassNotFoundException e2) {
			System.out.println(e2);
			System.out.println("server"+userid+" has closed");
		}
	}
}

public class ChatServer {
	private int port = 5121;
    private ServerSocket s;
    public volatile static Dictionary<Integer, BlockingQueue<String>> bqueues = new Hashtable<Integer, BlockingQueue<String>>();

    public static void main(String args[]) {
        ChatServer server = new ChatServer();
    }

    public ChatServer() {
    	try {
			s = new ServerSocket(port);
			System.out.println("server up and running on port " + port + " " + InetAddress.getLocalHost());
		} catch (IOException e) {
			System.err.println(e);
		}
    	int userid = 0;
    	Socket client;
    	while(true)
    	{
    		try {
    			//connect
    			client = s.accept();
    			userid++;
    			System.out.println(
				        "Received connect from " + client.getInetAddress().getHostAddress() + ": " + client.getPort());
    			//create thread's specific blocking queue, this will store chats sent from other clients to this client
    			bqueues.put(userid, new LinkedBlockingQueue<>(500));
    			new UserConnection(client, userid).start();
    		}catch (IOException e) {
				System.out.println(e);
			}
    	}
    	
    }
}
