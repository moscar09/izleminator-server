package ro.moscar.IzleminatorServer.chat;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import javax.websocket.EncodeException;

import org.junit.Test;

import com.google.gson.JsonObject;

import ro.moscar.IzleminatorServer.chat.messages.ChatMessage;
import ro.moscar.IzleminatorServer.chat.messages.HeartbeatMessage;
import ro.moscar.IzleminatorServer.chat.messages.SystemMessage;
import ro.moscar.IzleminatorServer.chat.messages.control.SessionConfigMessage;

public class MessageEncoderTest {

	@Test
	public void shouldEncodeSystemMessages() {
		IMessage message = new SystemMessage("System message");
		MessageEncoder encoder = new MessageEncoder();

		try {
			String encoded = encoder.encode(message);
			assertEquals("{\"messageType\":\"system\",\"content\":\"System message\",\"from\":\"System\"}", encoded);
		} catch (EncodeException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void shouldEncodeControlMessages() {
		IMessage message = new SessionConfigMessage("123");
		MessageEncoder encoder = new MessageEncoder();

		try {
			String encoded = encoder.encode(message);
			System.out.println(encoded);

			assertEquals(
					"{\"action\":\"sessionConfig\",\"userid\":\"123\",\"messageType\":\"control\",\"content\":\"userid:123\"}",
					encoded);
		} catch (EncodeException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void shouldEncodeChatMessages() {
		IMessage message = new ChatMessage("Chat message");
		message.setFrom("user");
		message.setFromUuid(UUID.randomUUID().toString());
		MessageEncoder encoder = new MessageEncoder();

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
		MessageEncoder encoder = new MessageEncoder();

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
