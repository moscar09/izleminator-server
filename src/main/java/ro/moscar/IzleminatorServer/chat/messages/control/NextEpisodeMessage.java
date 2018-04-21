package ro.moscar.IzleminatorServer.chat.messages.control;

import ro.moscar.IzleminatorServer.chat.IMessage;
import ro.moscar.IzleminatorServer.chat.messages.AbstractControlMessage;
import ro.moscar.IzleminatorServer.chat.messages.MessageAction;

public class NextEpisodeMessage extends AbstractControlMessage implements IMessage {
	private static final MessageAction action = MessageAction.NEXT_EPISODE;
	private String episodeId;

	public NextEpisodeMessage(String episodeId) {
		super(String.format("%s:%s", action, episodeId));
		this.episodeId = episodeId;
	}

	public MessageAction getAction() {
		return action;
	}

	public String getEpisodeId() {
		return episodeId;
	}

}
