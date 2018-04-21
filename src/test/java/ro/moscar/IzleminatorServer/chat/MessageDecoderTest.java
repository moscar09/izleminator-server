package ro.moscar.IzleminatorServer.chat;

import static org.junit.Assert.assertEquals;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;

import org.junit.Test;

import ro.moscar.IzleminatorServer.chat.messages.ChatMessage;
import ro.moscar.IzleminatorServer.chat.messages.HeartbeatMessage;
import ro.moscar.IzleminatorServer.chat.messages.control.NextEpisodeMessage;
import ro.moscar.IzleminatorServer.chat.messages.control.PausePlayerMessage;
import ro.moscar.IzleminatorServer.chat.messages.control.SeekAndStartPlayerMessage;
import ro.moscar.IzleminatorServer.chat.messages.control.SeekPlayerMessage;

public class MessageDecoderTest {
	@Test
	public void shouldDecodeSeekPlayerMessage() throws DecodeException {
		Decoder.Text<IMessage> decoder = new MessageDecoder();

		SeekPlayerMessage seekPlayer = (SeekPlayerMessage) decoder.decode(
				"{messageType: 'control', content: 'seekPlayer:123', action: 'seekPlayer', position: '123', version:'1'}");
		assertEquals(seekPlayer.getContent(), "seekPlayer:123");
		assertEquals(seekPlayer.getAction(), "seekPlayer");
		assertEquals(seekPlayer.getPosition(), "123");
		assertEquals(seekPlayer.getMessageType(), MessageType.CONTROL);
	}

	@Test
	public void shouldDecodeSeekAndStartPlayerMessage() throws DecodeException {
		Decoder.Text<IMessage> decoder = new MessageDecoder();

		SeekAndStartPlayerMessage seekAndStartPlayer = (SeekAndStartPlayerMessage) decoder.decode(
				"{messageType: 'control', content: 'seekAndStartPlayer:123', action: 'seekAndStartPlayer', position: '123', version:'1'}");
		assertEquals(seekAndStartPlayer.getContent(), "seekAndStartPlayer:123");
		assertEquals(seekAndStartPlayer.getAction(), "seekAndStartPlayer");
		assertEquals(seekAndStartPlayer.getPosition(), "123");
		assertEquals(seekAndStartPlayer.getMessageType(), MessageType.CONTROL);
	}

	@Test
	public void shouldDecodePausePlayerMessage() throws DecodeException {
		Decoder.Text<IMessage> decoder = new MessageDecoder();

		PausePlayerMessage pausePlayer = (PausePlayerMessage) decoder
				.decode("{messageType: 'control', content: 'pausePlayer', action: 'pausePlayer', version:'1'}");
		assertEquals(pausePlayer.getContent(), "pausePlayer");
		assertEquals(pausePlayer.getAction(), "pausePlayer");
		assertEquals(pausePlayer.getMessageType(), MessageType.CONTROL);
	}

	@Test
	public void shouldDecodeNextEpisodeMessage() throws DecodeException {
		Decoder.Text<IMessage> decoder = new MessageDecoder();

		NextEpisodeMessage nextEpisode = (NextEpisodeMessage) decoder.decode(
				"{messageType: 'control', content: 'nextEpisode:00112233', action: 'nextEpisode', episode_id: '00112233', version:'1'}");

		assertEquals(nextEpisode.getContent(), "nextEpisode:00112233");
		assertEquals(nextEpisode.getAction(), "nextEpisode");
		assertEquals(nextEpisode.getEpisodeId(), "00112233");
		assertEquals(nextEpisode.getMessageType(), MessageType.CONTROL);
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
