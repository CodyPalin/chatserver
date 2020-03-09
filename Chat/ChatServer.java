package Chat;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.InetAddress;
import java.lang.System;
import java.util.Date;
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
			//wait for input code?
			
			
		}catch(IOException e)
		{
			System.out.println(e);
			System.out.println("client"+userid+" has disconnected");
		}/* catch (ClassNotFoundException e2) {
			System.out.println(e2);
			System.out.println("client"+userid+" has disconnected");
		}*/
	}
}

public class ChatServer {
	private int port = 5121;
    private ServerSocket s;
    

    public static void main(String args[]) {
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
