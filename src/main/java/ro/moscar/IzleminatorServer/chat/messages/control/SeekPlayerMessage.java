package ro.moscar.IzleminatorServer.chat.messages.control;

import ro.moscar.IzleminatorServer.chat.IMessage;
import ro.moscar.IzleminatorServer.chat.messages.ControlMessage;

public class SeekPlayerMessage extends ControlMessage implements IMessage {
	public static final String action = "seekPlayer";

	private String position;

	public SeekPlayerMessage(String position) {
		super(String.format("%s:%s", action, position));
		this.position = position;
	}

	public String getPosition() {
		return position;
	}

	public String getAction() {
		return action;
	}
}
