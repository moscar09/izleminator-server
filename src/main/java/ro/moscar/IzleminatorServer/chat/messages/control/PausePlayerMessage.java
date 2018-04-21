package ro.moscar.IzleminatorServer.chat.messages.control;

import ro.moscar.IzleminatorServer.chat.IMessage;
import ro.moscar.IzleminatorServer.chat.messages.AbstractControlMessage;
import ro.moscar.IzleminatorServer.chat.messages.MessageAction;

public class PausePlayerMessage extends AbstractControlMessage implements IMessage {
	public static final MessageAction action = MessageAction.PAUSE_PLAYER;

	public PausePlayerMessage() {
		super(action.toString());
	}

	public MessageAction getAction() {
		// TODO Auto-generated method stub
		return action;
	}

}
