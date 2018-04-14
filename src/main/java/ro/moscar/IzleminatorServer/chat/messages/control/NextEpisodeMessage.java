package ro.moscar.IzleminatorServer.chat.messages.control;

import ro.moscar.IzleminatorServer.chat.IMessage;
import ro.moscar.IzleminatorServer.chat.messages.ControlMessage;

public class NextEpisodeMessage extends ControlMessage implements IMessage {
	public static final String action = "nextEpisode";
	private String episodeId;

	public NextEpisodeMessage(String episodeId) {
		super(String.format("%s:%s", action, episodeId));
		this.episodeId = episodeId;
	}

	public String getAction() {
		return action;
	}

	public String getEpisodeId() {
		return episodeId;
	}

}
