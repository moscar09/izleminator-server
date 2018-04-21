package ro.moscar.IzleminatorServer.chat.messages;

import com.google.gson.annotations.SerializedName;

public enum MessageAction {
	//@formatter:off
	@SerializedName("nextEpisode")        NEXT_EPISODE("nextEpisode"),
	@SerializedName("pausePlayer")        PAUSE_PLAYER("pausePlayer"),
	@SerializedName("seekPlayer")         SEEK_PLAYER("seekPlayer"),
	@SerializedName("seekAndStartPlayer") SEEK_AND_START_PLAYER("seekAndStartPlayer"),
	@SerializedName("sessionConfig")      SESSION_CONFIG("sessionConfig");
	//@formatter:on

	private final String name;

	private MessageAction(String s) {
		name = s;
	}

	public boolean equalsName(String otherName) {
		return name.equals(otherName);
	}

	public String toString() {
		return this.name;
	}
}
