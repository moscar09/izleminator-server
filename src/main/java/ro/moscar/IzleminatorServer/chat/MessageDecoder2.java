package ro.moscar.IzleminatorServer.chat;

import java.util.Map;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import com.google.gson.Gson;

import ro.moscar.IzleminatorServer.chat.messages.ChatMessage;
import ro.moscar.IzleminatorServer.chat.messages.HeartbeatMessage;
import ro.moscar.IzleminatorServer.chat.messages.control.PausePlayer;
import ro.moscar.IzleminatorServer.chat.messages.control.SeekAndStartPlayer;
import ro.moscar.IzleminatorServer.chat.messages.control.SeekPlayer;

public class MessageDecoder2 implements Decoder.Text<IMessage> {
	private Gson gson = new Gson();

	@Override
	public void init(EndpointConfig config) {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public IMessage decode(String s) throws DecodeException {
		@SuppressWarnings("unchecked")
		Map<String, String> data = gson.fromJson(s, Map.class);

		IMessage decodedMessage;

		switch (data.get("messageType").toString()) {
		case "control":
			decodedMessage = decodeControlMessages(data);
			break;

		case "heartbeat":
			return new HeartbeatMessage(data.get("content"));
		default:
			return new ChatMessage(data.get("content"));
		}

		if (decodedMessage == null) {
			throw new DecodeException(s, "Message has invalid schema!");
		}

		return decodedMessage;
	}

	@Override
	public boolean willDecode(String s) {
		return s != null;
	}

	private IMessage decodeControlMessages(Map<String, String> data) {

		switch (data.get("action")) {
		case SeekPlayer.action:
			return new SeekPlayer(data.get("position"));
		case SeekAndStartPlayer.action:
			return new SeekAndStartPlayer(data.get("position"));
		case PausePlayer.action:
			return new PausePlayer();
		default:
			return null;

		}
	}
}
