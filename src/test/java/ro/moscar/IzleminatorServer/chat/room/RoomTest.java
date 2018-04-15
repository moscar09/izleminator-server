package ro.moscar.IzleminatorServer.chat.room;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.websocket.EncodeException;

import org.junit.Assert;
import org.junit.Test;

import ro.moscar.IzleminatorServer.chat.IMessage;
import ro.moscar.IzleminatorServer.chat.messages.ChatMessage;
import ro.moscar.IzleminatorServer.chat.messages.ControlMessage;

public class RoomTest {
	private String roomName = "testRoom";

	@Test
	public void roomShouldHaveCorrectName() {
		Room room = new Room(roomName);

		assertEquals(room.getName(), roomName);
	}

	@Test
	public void roomShouldHaveCorrectNumberOfUsers() {
		Room room = new Room(roomName);

		for (Integer i = 0; i < 5; i++) {
			User mockUser = mock(User.class);
			when(mockUser.getId()).thenReturn(i.toString());
			room.addUser(mockUser);
		}
		assertEquals(room.getUserCount(), 5);

		room.removeUser("0");
		assertEquals(room.getUserCount(), 4);
	}

	@Test
	public void roomShouldBroadcastMessageToAllUsersInEpisode() {
		Room room = new Room(roomName);
		List<User> users = new ArrayList<User>();
		IMessage message = new ChatMessage("heyo");

		for (Integer i = 0; i < 5; i++) {
			User mockUser = mock(User.class);
			when(mockUser.getId()).thenReturn(i.toString());
			users.add(mockUser);
			room.addUser(mockUser);
		}

		room.broadcast(message);

		users.forEach(user -> {
			try {
				verify(user).sendMessage(message);
			} catch (IOException | EncodeException e) {
				Assert.fail();
			}
		});
	}

	@Test
	public void roomGetReadyForNextEpisode() {
		Room room = new Room(roomName);
		List<User> users = new ArrayList<User>();
		for (Integer i = 0; i < 2; i++) {
			User mockUser = mock(User.class);
			when(mockUser.getId()).thenReturn(i.toString());
			users.add(mockUser);
			room.addUser(mockUser);
		}

		assertEquals(false, room.getReadyForNextEpisode());
		room.enqueueForNextEpisode(users.get(0));
		assertEquals(false, room.getReadyForNextEpisode());
		room.enqueueForNextEpisode(users.get(1));
		assertEquals(true, room.getReadyForNextEpisode());
	}

	@Test
	public void roomDoesntBroadcastControlToEnqueuedUsers() throws IOException, EncodeException {
		Room room = new Room(roomName);
		List<User> users = new ArrayList<User>();

		for (Integer i = 0; i < 2; i++) {
			User mockUser = mock(User.class);
			when(mockUser.getId()).thenReturn(i.toString());
			users.add(mockUser);
			room.addUser(mockUser);
		}

		room.enqueueForNextEpisode(users.get(0));
		IMessage controlMessage = new ControlMessage("pausePlayer");
		room.broadcast(controlMessage);
		verify(users.get(1)).sendMessage(controlMessage);
		verify(users.get(0), times(0)).sendMessage(controlMessage);
	}

	@Test
	public void roomBroadcastsChatToEnqueuedUsers() throws IOException, EncodeException {
		Room room = new Room(roomName);
		List<User> users = new ArrayList<User>();

		for (Integer i = 0; i < 2; i++) {
			User mockUser = mock(User.class);
			when(mockUser.getId()).thenReturn(i.toString());
			users.add(mockUser);
			room.addUser(mockUser);
		}

		room.enqueueForNextEpisode(users.get(0));
		IMessage chatMessage = new ChatMessage("Hello World!");
		room.broadcast(chatMessage);
		verify(users.get(1)).sendMessage(chatMessage);
		verify(users.get(0)).sendMessage(chatMessage);
	}

	@Test
	public void usersGetControlMessagesAfterSwitchingEpisode() throws IOException, EncodeException {
		Room room = new Room(roomName);
		List<User> users = new ArrayList<User>();

		for (Integer i = 0; i < 2; i++) {
			User mockUser = mock(User.class);
			when(mockUser.getId()).thenReturn(i.toString());
			users.add(mockUser);
			room.addUser(mockUser);
			room.enqueueForNextEpisode(mockUser);
		}

		room.switchToNextEpisode();

		IMessage controlMessage = new ControlMessage("pausePlayer");
		room.broadcast(controlMessage);
		verify(users.get(1)).sendMessage(controlMessage);
		verify(users.get(0)).sendMessage(controlMessage);

	}

}
