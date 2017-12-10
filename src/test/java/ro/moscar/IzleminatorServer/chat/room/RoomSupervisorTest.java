package ro.moscar.IzleminatorServer.chat.room;

import static org.junit.Assert.assertEquals;
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
import ro.moscar.IzleminatorServer.chat.MessageType;
import ro.moscar.IzleminatorServer.chat.messages.ChatMessage;
import ro.moscar.IzleminatorServer.chat.messages.HeartbeatMessage;

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

		verify(mockUser, times(2)).sendMessage(argumentCaptor.capture());
		List<IMessage> messageList = argumentCaptor.getAllValues();

		assertEquals(messageList.get(0).getMessageType(), MessageType.SYSTEM);
		assertEquals(messageList.get(0).getContent(), "Welcome " + username);
		assertEquals(messageList.get(1).getMessageType(), MessageType.CONTROL);
		assertEquals(messageList.get(1).getContent(), "userid:" + uuid);

		verify(mockRoom).broadcast(argumentCaptor.capture());
		IMessage message = argumentCaptor.getValue();
		assertEquals(message.getMessageType(), MessageType.SYSTEM);
		assertEquals(message.getContent(), username + " has joined.");
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
	public void shouldBroadcastOtherMessageTypes() throws IOException, EncodeException {
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

}
