package ro.moscar.IzleminatorServer.chat.messages;

public abstract class AbstractMessage {
	protected String content;
	private String from;
	private String fromUuid;

	public AbstractMessage(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getFromUuid() {
		return fromUuid;
	}

	public void setFromUuid(String uuid) {
		this.fromUuid = uuid;
	}

	public abstract MessageType getMessageType();
}
