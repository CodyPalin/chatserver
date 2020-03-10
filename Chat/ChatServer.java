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
import Chat.Package;
class UserConnection extends Thread{
	private Socket client;
	private int userid;
	private InputStream in;
	private OutputStream out;
	private Thread[] channels; //a list of threads with a thread being associated with a chat channel. Will attempt a 2 channel chatroom.
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
			//wait for input code
			boolean connected = true;
			while(connected) {
	        	ObjectInputStream oin = new ObjectInputStream(in);
	        	//if chat message
	        	Object input = oin.readObject();
	        	if(input instanceof Package) {
		        	Package clientinput = (Package) input;
		        	String channel = clientinput.getChannel();
				if(channel.equals(channels[0]){
					//join the thread. for channel 0.
				}
				if(channel.equals(channels[1]){
					//join the thread for channel 1.
				}
		        	//just print to server console for now
		        	System.out.println(clientinput.getName()+": "+clientinput.getMessage());
	        	}
	        	else { //else should be a command string
	        		//case statement here for commands that will be passed to server
	        		String command = (String) input;
	        	}
			}
			
		}catch(IOException e)
		{
			System.out.println(e);
			System.out.println("client"+userid+" has disconnected");
		} catch (ClassNotFoundException e2) {
			System.out.println(e2);
			System.out.println("client"+userid+" has disconnected");
		}
	}
}

public class ChatServer {
	private int port = 5121;
    private ServerSocket s;
    

    public static void main(String args[]) {
	channels = new thread[2];                  // Creates a 2 threads that respond to a different channel. The for loop is my attempt at instantiating these two threads.
	for(int i = 0; i < channels.size -1; i++){
		channels[i] = new Thread(this+i);    //attempt at making two different thread names.
		channels[i].start();
	}
        ChatServer server = new ChatServer();
    }

    public ChatServer() {
    	try {
			s = new ServerSocket(port);
			System.out.println("server up and running on port" + port + " " + InetAddress.getLocalHost());
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
    			new UserConnection(client, userid).start();
    		}catch (IOException e) {
				System.out.println(e);
			}
    	}
    	
    }
}
