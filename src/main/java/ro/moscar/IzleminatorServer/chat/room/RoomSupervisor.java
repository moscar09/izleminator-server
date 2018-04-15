package ro.moscar.IzleminatorServer.chat.room;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.EncodeException;

import ro.moscar.IzleminatorServer.chat.IMessage;
import ro.moscar.IzleminatorServer.chat.MessageType;
import ro.moscar.IzleminatorServer.chat.messages.ControlMessage;
import ro.moscar.IzleminatorServer.chat.messages.HeartbeatMessage;
import ro.moscar.IzleminatorServer.chat.messages.SystemMessage;
import ro.moscar.IzleminatorServer.chat.messages.control.NextEpisodeMessage;

public class RoomSupervisor {
	private Map<String, Room> rooms = new ConcurrentHashMap<String, Room>();
	private User roomLeader;

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

		if (room.getIsPaused()) {
			user.sendMessage(new ControlMessage("pausePlayer"));
			if (room.getPosition() != null) {
				user.sendMessage(new ControlMessage("seekPlayer:" + room.getPosition()));
			}
		} else {
			String position = room.getPosition() != null ? room.getPosition() : "0";
			user.sendMessage(new ControlMessage("seekAndStartPlayer:" + position));
		}

		room.broadcast(new SystemMessage(user.getUsername() + " has joined."));
		room.addUser(user);
	}

	public void userMessageReceived(User user, String roomName, IMessage message) throws IOException {
		Room room = rooms.get(roomName);

		if (message.getMessageType() == MessageType.HEARTBEAT) {
			try {
				user.sendMessage(message);
				if (room.getPosition() == null || roomLeader.getUuid().equals(user.getUuid())) {
					roomLeader = user;
					String position = ((HeartbeatMessage) message).getPosition();
					room.setPosition(position);
				}
			} catch (EncodeException e) {
				e.printStackTrace();
			}
		} else if (message.getMessageType() == MessageType.CHAT) {
			room.broadcast(message);
		} else {
			if (message.getMessageType() == MessageType.CONTROL && message.getContent().equals("pausePlayer")) {
				room.setIsPaused(true);
				room.broadcast(message);
			} else if (message.getMessageType() == MessageType.CONTROL) {
				String[] components = message.getContent().split(":");

				if (components[0].equals("seekAndStartPlayer") || components[0].equals("seekPlayer")) {
					roomLeader = user;
					room.setPosition(components[1]);

					if (components[0].equals("seekAndStartPlayer")) {
						room.setIsPaused(false);
					}

					room.broadcast(message);
				} else if (components[0].equals(NextEpisodeMessage.action)) {
					room.enqueueForNextEpisode(user);

					if (room.getReadyForNextEpisode()) {
						room.switchToNextEpisode();
						room.broadcast(new SystemMessage("Next episode is starting."));
						room.broadcast(new ControlMessage("seekAndStartPlayer:0"));
					} else {
						try {
							room.broadcast(new SystemMessage(
									String.format("%s moved to the next episode.", user.getUsername())));
							user.sendMessage(new ControlMessage("pausePlayer"));
						} catch (EncodeException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	public void userClosedClosedConnection(User user, String roomName) {
		Room room = rooms.get(roomName);
		if (roomLeader != null && roomLeader.getUuid().equals(user.getUuid())) {
			roomLeader = null;
		}
		room.removeUser(user.getId());

		if (room.getUserCount() == 0) {
			rooms.remove(room.getName());
		}
	}
}
