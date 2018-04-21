package ro.moscar.IzleminatorServer.chat;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import javax.websocket.EncodeException;

import org.junit.Test;

import com.google.gson.JsonObject;

import ro.moscar.IzleminatorServer.chat.messages.ChatMessage;
import ro.moscar.IzleminatorServer.chat.messages.HeartbeatMessage;
import ro.moscar.IzleminatorServer.chat.messages.SystemMessage;
import ro.moscar.IzleminatorServer.chat.messages.control.NextEpisodeMessage;
import ro.moscar.IzleminatorServer.chat.messages.control.PausePlayerMessage;
import ro.moscar.IzleminatorServer.chat.messages.control.SeekAndStartPlayerMessage;
import ro.moscar.IzleminatorServer.chat.messages.control.SeekPlayerMessage;
import ro.moscar.IzleminatorServer.chat.messages.control.SessionConfigMessage;

public class MessageEncoderTest {
	private MessageEncoder encoder = new MessageEncoder();

	@Test
	public void shouldEncodeSystemMessages() {
		IMessage message = new SystemMessage("System message");

		try {
			String encoded = encoder.encode(message);
			assertEquals("{\"messageType\":\"system\",\"content\":\"System message\",\"from\":\"System\"}", encoded);
		} catch (EncodeException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void shouldEncodeNextEpisodeMessages() throws EncodeException {
		IMessage message = new NextEpisodeMessage("123");

		assertEquals(
				"{\"action\":\"nextEpisode\",\"episodeId\":\"123\",\"messageType\":\"control\",\"content\":\"nextEpisode:123\"}",
				encoder.encode(message));
	}

	@Test
	public void shouldEncodePausePlayerMessages() throws EncodeException {
		IMessage message = new PausePlayerMessage();

		assertEquals("{\"action\":\"pausePlayer\",\"messageType\":\"control\",\"content\":\"pausePlayer\"}",
				encoder.encode(message));
	}

	@Test
	public void shouldEncodeSeekAndStartPlayerMessages() throws EncodeException {
		IMessage message = new SeekAndStartPlayerMessage("123");

		assertEquals(
				"{\"action\":\"seekAndStartPlayer\",\"position\":\"123\",\"messageType\":\"control\",\"content\":\"seekAndStartPlayer:123\"}",
				encoder.encode(message));
	}

	@Test
	public void shouldEncodeSeekPlayerMessages() throws EncodeException {
		IMessage message = new SeekPlayerMessage("123");

		assertEquals(
				"{\"action\":\"seekPlayer\",\"position\":\"123\",\"messageType\":\"control\",\"content\":\"seekPlayer:123\"}",
				encoder.encode(message));
	}

	@Test
	public void shouldEncodeSessionConfigMessages() throws EncodeException {
		IMessage message = new SessionConfigMessage("123");

		assertEquals(
				"{\"action\":\"sessionConfig\",\"userid\":\"123\",\"messageType\":\"control\",\"content\":\"userid:123\"}",
				encoder.encode(message));
	}

	@Test
	public void shouldEncodeChatMessages() {
		IMessage message = new ChatMessage("Chat message");
		message.setFrom("user");
		message.setFromUuid(UUID.randomUUID().toString());

		JsonObject json = new JsonObject();
		json.addProperty("messageType", "chat");
		json.addProperty("content", message.getContent());
		json.addProperty("from", message.getFrom());
		json.addProperty("fromUuid", message.getFromUuid());
		try {
			String encoded = encoder.encode(message);
			assertEquals(json.toString(), encoded);
		} catch (EncodeException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void shouldEncodeHeartbeatMessages() {
		IMessage message = new HeartbeatMessage("HB:12231241");

		JsonObject json = new JsonObject();
		json.addProperty("messageType", "heartbeat");
		json.addProperty("position", "12231241");
		json.addProperty("content", message.getContent());

		try {
			String encoded = encoder.encode(message);

			assertEquals(json.toString(), encoded);
		} catch (EncodeException e) {
			e.printStackTrace();
		}

	}

}
