package Chat;
import java.io.Serializable;

public class Package extends Object implements Serializable {
	private String nick = "";
	private String message = "";
	private String channel = "";
	public Package() {
	}
	public void setName(String nick) {
		this.nick = nick;
	}

	public String getName() {
		return nick;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getChannel() {
		return channel;
	}
}
