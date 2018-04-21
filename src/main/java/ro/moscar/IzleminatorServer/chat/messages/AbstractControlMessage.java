package ro.moscar.IzleminatorServer.chat.messages;

import ro.moscar.IzleminatorServer.chat.IMessage;

public abstract class AbstractControlMessage extends AbstractMessage implements IMessage {
	private static final MessageType messageType = MessageType.CONTROL;

	public AbstractControlMessage(String content) {
		super(content);
	}

	public MessageType getMessageType() {
		return messageType;
	}

	public abstract MessageAction getAction();
}
