package ro.moscar.IzleminatorServer.chat;

import java.util.Map;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import com.google.gson.Gson;

import ro.moscar.IzleminatorServer.chat.messages.ChatMessage;
import ro.moscar.IzleminatorServer.chat.messages.HeartbeatMessage;
import ro.moscar.IzleminatorServer.chat.messages.control.NextEpisodeMessage;
import ro.moscar.IzleminatorServer.chat.messages.control.PausePlayerMessage;
import ro.moscar.IzleminatorServer.chat.messages.control.SeekAndStartPlayerMessage;
import ro.moscar.IzleminatorServer.chat.messages.control.SeekPlayerMessage;

public class MessageDecoder implements Decoder.Text<IMessage> {
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
		case SeekPlayerMessage.action:
			return new SeekPlayerMessage(data.get("position"));
		case SeekAndStartPlayerMessage.action:
			return new SeekAndStartPlayerMessage(data.get("position"));
		case PausePlayerMessage.action:
			return new PausePlayerMessage();
		case NextEpisodeMessage.action:
			return new NextEpisodeMessage(data.get("episode_id"));
		default:
			return null;

		}
	}
}
