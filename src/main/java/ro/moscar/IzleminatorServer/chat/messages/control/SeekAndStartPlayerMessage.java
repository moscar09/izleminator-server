package ro.moscar.IzleminatorServer.chat.messages.control;

import ro.moscar.IzleminatorServer.chat.IMessage;
import ro.moscar.IzleminatorServer.chat.messages.AbstractControlMessage;
import ro.moscar.IzleminatorServer.chat.messages.MessageAction;

public class SeekAndStartPlayerMessage extends AbstractControlMessage implements IMessage {
	public static final MessageAction action = MessageAction.SEEK_AND_START_PLAYER;

	private String position;

	public SeekAndStartPlayerMessage(String position) {
		super(String.format("%s:%s", action, position));
		this.position = position;
	}

	public String getPosition() {
		return position;
	}

	public MessageAction getAction() {
		return action;
	}
}
