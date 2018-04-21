package ro.moscar.IzleminatorServer.chat.messages;

import ro.moscar.IzleminatorServer.chat.IMessage;

public class HeartbeatMessage extends AbstractMessage implements IMessage {
	private static final MessageType messageType = MessageType.HEARTBEAT;
	private String position;

	public HeartbeatMessage(String content) {
		super(content);
		this.setContent(content);
	}

	public MessageType getMessageType() {
		return messageType;
	}

	public void setContent(String content) {
		this.position = content.split(":")[1];
		this.content = content;
	}

	public String getPosition() {
		return position;
	}
}
