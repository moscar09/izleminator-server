package ro.moscar.IzleminatorServer.chat.messages;

import com.google.gson.annotations.SerializedName;

public enum MessageType {
	@SerializedName("chat")
	CHAT, @SerializedName("control")
	CONTROL, @SerializedName("heartbeat")
	HEARTBEAT, @SerializedName("system")
	SYSTEM;
}
