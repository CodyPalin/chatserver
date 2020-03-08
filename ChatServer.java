import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.lang.System;
import java.util.Date;
public class ChatServer {
	private InputStream in;
	private OutputStream out;
	private int port = 5121;
    private ServerSocket s;
    

    public static void main(String args[]) {
        ChatServer server = new ChatServer();
        server.serviceClients();
    }

    public ChatServer() {
    	try {
			s = new ServerSocket(port);
			System.out.println("server up and running on port" + port + " " + InetAddress.getLocalHost());
		} catch (IOException e) {
			System.err.println(e);
		}
    }
    public void serviceClients() {
        Socket sock;
        

        while (true) {
			try {
				sock = s.accept();
				out = sock.getOutputStream();
				in = sock.getInputStream();
				
				// Note that client gets a temporary/transient port on it's side
				// to talk to the server on its well known port
				System.out.println(
				        "Received connect from " + sock.getInetAddress().getHostAddress() + ": " + sock.getPort());
                } catch (IOException e) {
                    System.err.println(e);
                }

                //server code here
            }
        }
}
