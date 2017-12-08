package ro.moscar.IzleminatorServer.chat.room;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.EncodeException;

import ro.moscar.IzleminatorServer.chat.IMessage;
import ro.moscar.IzleminatorServer.chat.MessageType;
import ro.moscar.IzleminatorServer.chat.messages.ControlMessage;
import ro.moscar.IzleminatorServer.chat.messages.SystemMessage;

public class RoomSupervisor {
	private Map<String, Room> rooms = new ConcurrentHashMap<String, Room>();

	public void addUserToRoom(User user, String roomName) throws IOException, EncodeException {
		Room room;

		if (rooms.containsKey(roomName)) {
			room = rooms.get(roomName);
		} else {
			room = new Room(roomName);
			rooms.put(roomName, room);
		}

		user.sendMessage(new SystemMessage("Welcome " + user.getUsername()));
		user.sendMessage(new ControlMessage("userid:" + user.getUuid()));
		room.broadcast(new SystemMessage(user.getUsername() + " has joined."));
		room.addUser(user);
	}

	public void userMessageReceived(User user, String roomName, IMessage message) throws IOException {
		if (message.getMessageType() == MessageType.HEARTBEAT) {
			try {
				user.sendMessage(message);
			} catch (EncodeException e) {
				e.printStackTrace();
			}
		} else {
			rooms.get(roomName).broadcast(message);
		}
	}

	public void userClosedClosedConnection(User user, String roomName) {
		Room room = rooms.get(roomName);
		room.removeUser(user.getId());

		if (room.getUserCount() == 0) {
			rooms.remove(room.getName());
		}
	}
}