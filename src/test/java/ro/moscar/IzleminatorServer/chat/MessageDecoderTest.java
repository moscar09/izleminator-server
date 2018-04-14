package ro.moscar.IzleminatorServer.chat;

import static org.junit.Assert.assertEquals;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;

import org.junit.Test;

import ro.moscar.IzleminatorServer.chat.messages.ChatMessage;
import ro.moscar.IzleminatorServer.chat.messages.ControlMessage;
import ro.moscar.IzleminatorServer.chat.messages.HeartbeatMessage;

public class MessageDecoderTest {
	@Test
	public void shouldDecodeControlMessages() throws DecodeException {
		Decoder.Text<IMessage> decoder = new MessageDecoder();

		ControlMessage control = (ControlMessage) decoder.decode("{messageType: 'control', content: 'seekPlayer:123'}");
		assertEquals(control.getContent(), "seekPlayer:123");
		assertEquals(control.getMessageType(), MessageType.CONTROL);

		control = (ControlMessage) decoder.decode("{messageType: 'control', content: 'seekAndStartPlayer:123'}");
		assertEquals(control.getContent(), "seekAndStartPlayer:123");
		assertEquals(control.getMessageType(), MessageType.CONTROL);

		control = (ControlMessage) decoder.decode("{messageType: 'control', content: 'pausePlayer'}");
		assertEquals(control.getContent(), "pausePlayer");
		assertEquals(control.getMessageType(), MessageType.CONTROL);

		control = (ControlMessage) decoder.decode("{messageType: 'control', content: 'nextEpisode:00112233'}");
		assertEquals(control.getContent(), "nextEpisode:00112233");
		assertEquals(control.getMessageType(), MessageType.CONTROL);
	}

	@Test
	public void shouldDecodeHeartbeatMessages() throws DecodeException {
		Decoder.Text<IMessage> decoder = new MessageDecoder();

		HeartbeatMessage heartbeat = (HeartbeatMessage) decoder
				.decode("{messageType: 'heartbeat', content: 'HB:255643'}");
		assertEquals(heartbeat.getContent(), "HB:255643");
		assertEquals(heartbeat.getPosition(), "255643");
		assertEquals(heartbeat.getMessageType(), MessageType.HEARTBEAT);
	}

	@Test
	public void shouldDecodeChatMessages() throws DecodeException {
		Decoder.Text<IMessage> decoder = new MessageDecoder();

		ChatMessage chat = (ChatMessage) decoder.decode("{messageType: 'chat', content: 'Hello World!'}");
		assertEquals(chat.getContent(), "Hello World!");
		assertEquals(chat.getMessageType(), MessageType.CHAT);
	}
}
