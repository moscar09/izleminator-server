package ro.moscar.IzleminatorServer.chat.messages.control;

import ro.moscar.IzleminatorServer.chat.IMessage;
import ro.moscar.IzleminatorServer.chat.messages.ControlMessage;

public class PausePlayer extends ControlMessage implements IMessage {
	public static final String action = "pausePlayer";

	public PausePlayer() {
		super(action);
	}

}
