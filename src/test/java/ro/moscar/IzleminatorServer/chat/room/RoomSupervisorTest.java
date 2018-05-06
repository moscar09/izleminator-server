package ro.moscar.IzleminatorServer.chat.room;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.websocket.EncodeException;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ro.moscar.IzleminatorServer.chat.IMessage;
import ro.moscar.IzleminatorServer.chat.messages.ChatMessage;
import ro.moscar.IzleminatorServer.chat.messages.HeartbeatMessage;
import ro.moscar.IzleminatorServer.chat.messages.MessageType;
import ro.moscar.IzleminatorServer.chat.messages.SystemMessage;
import ro.moscar.IzleminatorServer.chat.messages.control.NextEpisodeMessage;
import ro.moscar.IzleminatorServer.chat.messages.control.PausePlayerMessage;
import ro.moscar.IzleminatorServer.chat.messages.control.SeekAndStartPlayerMessage;
import ro.moscar.IzleminatorServer.chat.messages.control.SeekPlayerMessage;

public class RoomSupervisorTest {
	@Mock(name = "rooms")
	Map<String, Room> rooms;

	@InjectMocks
	private RoomSupervisor roomSupervisor;

	@Test
	public void shouldAddUserToRoom() throws IOException, EncodeException {
		MockitoAnnotations.initMocks(this);
		User mockUser = mock(User.class);
		Room mockRoom = mock(Room.class);

		String roomName = "testroom";
		when(rooms.containsKey(roomName)).thenReturn(true);
		when(rooms.get(roomName)).thenReturn(mockRoom);

		roomSupervisor.addUserToRoom(mockUser, "testroom");
		verify(mockRoom).addUser(mockUser);
	}

	@Test
	public void shouldSynchroniseStreamsOnLogin() throws IOException, EncodeException {
		MockitoAnnotations.initMocks(this);
		User mockUser = mock(User.class);
		Room mockRoom = mock(Room.class);

		String roomName = "testroom";
		when(rooms.containsKey(roomName)).thenReturn(true);
		when(rooms.get(roomName)).thenReturn(mockRoom);
		when(mockRoom.getPosition()).thenReturn("1234");

		roomSupervisor.addUserToRoom(mockUser, "testroom");

		verify(mockUser, times(4)).sendMessage(any());
	}

	@Test
	public void shouldSendCorrectMessages() throws IOException, EncodeException {
		MockitoAnnotations.initMocks(this);
		ArgumentCaptor<IMessage> argumentCaptor = ArgumentCaptor.forClass(IMessage.class);
		String roomName = "testroom";
		Room mockRoom = mock(Room.class);

		String username = "Testington";
		String uuid = UUID.randomUUID().toString();
		User mockUser = mock(User.class);

		when(rooms.containsKey(roomName)).thenReturn(true);
		when(rooms.get(roomName)).thenReturn(mockRoom);

		when(mockUser.getUsername()).thenReturn(username);
		when(mockUser.getUuid()).thenReturn(uuid);

		roomSupervisor.addUserToRoom(mockUser, "testroom");

		verify(mockUser, times(4)).sendMessage(argumentCaptor.capture());
		List<IMessage> messageList = argumentCaptor.getAllValues();

		assertEquals(messageList.get(0).getMessageType(), MessageType.SYSTEM);
		assertEquals(messageList.get(0).getContent(), "Welcome " + username);
		assertEquals(messageList.get(1).getMessageType(), MessageType.CONTROL);
		assertEquals(messageList.get(1).getContent(), "userid:" + uuid);
		assertEquals(messageList.get(2).getMessageType(), MessageType.CONTROL);
		assertEquals(messageList.get(2).getContent(), "seekPlayer:0");
		assertEquals(messageList.get(3).getMessageType(), MessageType.CONTROL);
		assertEquals(messageList.get(3).getContent(), "pausePlayer");

		verify(mockRoom).broadcast(argumentCaptor.capture());
		IMessage message = argumentCaptor.getValue();
		assertEquals(message.getMessageType(), MessageType.SYSTEM);
		assertEquals(message.getContent(), username + " joined.");
	}

	@Test
	public void shouldReflectHeartbeatMessage() throws IOException, EncodeException {
		MockitoAnnotations.initMocks(this);

		String roomName = "testroom";
		User mockUser = mock(User.class);
		Room mockRoom = mock(Room.class);
		when(mockRoom.getPosition()).thenReturn(null);
		when(rooms.get(roomName)).thenReturn(mockRoom);

		IMessage message = new HeartbeatMessage("HB:1234");

		roomSupervisor.userMessageReceived(mockUser, roomName, message);
		verify(mockUser).sendMessage(message);
		verify(mockRoom).setPosition("1234");
	}

	@Test
	public void shouldSetPositionOnHeartbeat() throws IOException, EncodeException {
		MockitoAnnotations.initMocks(this);

		String roomName = "testroom";
		User mockUser = mock(User.class);
		Room mockRoom = mock(Room.class);
		when(mockRoom.getPosition()).thenReturn(null);
		when(rooms.get(roomName)).thenReturn(mockRoom);

		roomSupervisor.userMessageReceived(mockUser, roomName, new HeartbeatMessage("HB:1234"));

		verify(mockRoom).setPosition("1234");
	}

	@Test
	public void shouldIgnoreHeartbeatIfNotRoomLeader() throws IOException, EncodeException {
		MockitoAnnotations.initMocks(this);

		String roomName = "testroom";
		User mockRoomLeader = mock(User.class);
		User mockOtherUser = mock(User.class);
		Room room = new Room(roomName);

		when(mockRoomLeader.getUuid()).thenReturn(UUID.randomUUID().toString());
		when(mockOtherUser.getUuid()).thenReturn(UUID.randomUUID().toString());
		when(rooms.get(roomName)).thenReturn(room);

		roomSupervisor.userMessageReceived(mockRoomLeader, roomName, new HeartbeatMessage("HB:1234"));
		assertEquals(room.getPosition(), "1234");

		roomSupervisor.userMessageReceived(mockOtherUser, roomName, new HeartbeatMessage("HB:5678"));
		assertEquals(room.getPosition(), "1234");

		roomSupervisor.userMessageReceived(mockRoomLeader, roomName, new HeartbeatMessage("HB:9999"));
		assertEquals(room.getPosition(), "9999");
	}

	@Test
	public void shouldBroadcastChatMessages() throws IOException, EncodeException {
		MockitoAnnotations.initMocks(this);

		String roomName = "testroom";
		User mockUser = mock(User.class);
		IMessage message = new ChatMessage("Hello World");
		Room mockRoom = mock(Room.class);

		when(rooms.get(roomName)).thenReturn(mockRoom);

		roomSupervisor.userMessageReceived(mockUser, roomName, message);
		verify(mockRoom).broadcast(message);
	}

	@Test
	public void shouldUpdateRoomStateOnControlMessages() throws IOException, EncodeException {
		MockitoAnnotations.initMocks(this);

		String roomName = "testroom";
		User mockUser = mock(User.class);
		Room mockRoom = mock(Room.class);

		when(rooms.get(roomName)).thenReturn(mockRoom);

		IMessage message = new SeekPlayerMessage("23456");
		roomSupervisor.userMessageReceived(mockUser, roomName, message);
		verify(mockRoom).setPosition("23456");
		verify(mockRoom, times(0)).setIsPaused(false);
		verify(mockRoom).broadcast(message);

		message = new PausePlayerMessage();
		roomSupervisor.userMessageReceived(mockUser, roomName, message);
		verify(mockRoom).broadcast(message);
		verify(mockRoom).setIsPaused(true);

		message = new SeekAndStartPlayerMessage("12345");
		roomSupervisor.userMessageReceived(mockUser, roomName, message);
		verify(mockRoom).broadcast(message);
		verify(mockRoom).setIsPaused(false);
		verify(mockRoom).setPosition("12345");
	}

	@Test
	public void shouldHandleRoomCloseCorrectly() throws IOException, EncodeException {
		MockitoAnnotations.initMocks(this);

		String userId = "42";
		String roomName = "testRoom";
		User mockUser = mock(User.class);
		Room mockRoom = mock(Room.class);

		when(mockUser.getId()).thenReturn(userId);
		when(rooms.get(roomName)).thenReturn(mockRoom);
		when(mockRoom.getName()).thenReturn(roomName);

		roomSupervisor.addUserToRoom(mockUser, roomName);
		roomSupervisor.userClosedClosedConnection(mockUser, roomName);

		verify(mockRoom).removeUser(userId);
		verify(rooms).remove(roomName);
	}

	@Test
	public void shouldAnnounceThatUserLeft() {
		MockitoAnnotations.initMocks(this);

		String userName = "Testington";
		String roomName = "testRoom";
		Room mockRoom = mock(Room.class);
		User mockUser = mock(User.class);

		when(rooms.get(roomName)).thenReturn(mockRoom);
		when(mockRoom.getUserCount()).thenReturn(1);
		when(mockRoom.getName()).thenReturn(roomName);
		when(mockUser.getUsername()).thenReturn(userName);

		ArgumentCaptor<IMessage> argumentCaptor = ArgumentCaptor.forClass(IMessage.class);

		roomSupervisor.userClosedClosedConnection(mockUser, roomName);

		verify(mockRoom, times(1)).broadcast(argumentCaptor.capture());

		SystemMessage message = (SystemMessage) argumentCaptor.getValue();
		assertEquals(String.format("%s left.", userName), message.getContent());

	}

	@Test
	public void shouldPauseUsersWhenNotNextEpisode() throws IOException, EncodeException {
		MockitoAnnotations.initMocks(this);

		String roomName = "testRoom";
		User mockUser = mock(User.class);
		Room mockRoom = mock(Room.class);

		when(mockUser.getId()).thenReturn("42");
		when(mockUser.getUsername()).thenReturn("Testington");
		when(rooms.get(roomName)).thenReturn(mockRoom);
		when(mockRoom.getName()).thenReturn(roomName);
		when(mockRoom.getReadyForNextEpisode()).thenReturn(false);

		IMessage message;
		ArgumentCaptor<IMessage> argumentCaptor = ArgumentCaptor.forClass(IMessage.class);

		roomSupervisor.userMessageReceived(mockUser, roomName, new NextEpisodeMessage("0123"));

		verify(mockRoom, times(1)).broadcast(argumentCaptor.capture());
		message = argumentCaptor.getValue();
		assertEquals(MessageType.SYSTEM, message.getMessageType());
		assertEquals("Testington moved to the next episode.", message.getContent());

		verify(mockUser, times(1)).sendMessage(argumentCaptor.capture());
		message = argumentCaptor.getValue();
		assertEquals(MessageType.CONTROL, message.getMessageType());
		assertEquals("pausePlayer", message.getContent());
	}

	@Test
	public void shouldResetUsersWhenNextEpisode() throws IOException, EncodeException {
		MockitoAnnotations.initMocks(this);

		String roomName = "testRoom";
		User mockUser = mock(User.class);
		Room mockRoom = mock(Room.class);

		when(mockUser.getId()).thenReturn("42");
		when(mockUser.getUsername()).thenReturn("Testington");
		when(rooms.get(roomName)).thenReturn(mockRoom);
		when(mockRoom.getName()).thenReturn(roomName);
		when(mockRoom.getReadyForNextEpisode()).thenReturn(true);

		ArgumentCaptor<IMessage> argumentCaptor = ArgumentCaptor.forClass(IMessage.class);

		roomSupervisor.userMessageReceived(mockUser, roomName, new NextEpisodeMessage("0123"));

		verify(mockRoom, times(2)).broadcast(argumentCaptor.capture());
		List<IMessage> messages = argumentCaptor.getAllValues();

		assertEquals(MessageType.SYSTEM, messages.get(0).getMessageType());
		assertEquals("Next episode is starting.", messages.get(0).getContent());
		assertEquals(MessageType.CONTROL, messages.get(1).getMessageType());
		assertEquals("seekAndStartPlayer:0", messages.get(1).getContent());
	}

}
