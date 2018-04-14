package ro.moscar.IzleminatorServer.chat.messages.control;

import ro.moscar.IzleminatorServer.chat.IMessage;
import ro.moscar.IzleminatorServer.chat.messages.ControlMessage;

public class PausePlayerMessage extends ControlMessage implements IMessage {
	public static final String action = "pausePlayer";

	public PausePlayerMessage() {
		super(action);
	}

	public Object getAction() {
		// TODO Auto-generated method stub
		return action;
	}

}
