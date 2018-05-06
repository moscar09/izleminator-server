package ro.moscar.IzleminatorServer.chat.room;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.EncodeException;

import ro.moscar.IzleminatorServer.chat.IMessage;
import ro.moscar.IzleminatorServer.chat.messages.AbstractControlMessage;
import ro.moscar.IzleminatorServer.chat.messages.HeartbeatMessage;
import ro.moscar.IzleminatorServer.chat.messages.MessageAction;
import ro.moscar.IzleminatorServer.chat.messages.MessageType;
import ro.moscar.IzleminatorServer.chat.messages.SystemMessage;
import ro.moscar.IzleminatorServer.chat.messages.control.PausePlayerMessage;
import ro.moscar.IzleminatorServer.chat.messages.control.SeekAndStartPlayerMessage;
import ro.moscar.IzleminatorServer.chat.messages.control.SeekPlayerMessage;
import ro.moscar.IzleminatorServer.chat.messages.control.SessionConfigMessage;

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
		user.sendMessage(new SessionConfigMessage(user.getUuid()));

		if (room.getIsPaused()) {
			user.sendMessage(new PausePlayerMessage());
			if (room.getPosition() != null) {
				user.sendMessage(new SeekPlayerMessage(room.getPosition()));
			}
		} else {
			String position = room.getPosition() != null ? room.getPosition() : "0";
			user.sendMessage(new SeekPlayerMessage(position));
			user.sendMessage(new PausePlayerMessage());
			room.setIsPaused(true);
		}

		room.broadcast(new SystemMessage(String.format("%s joined.", user.getUsername())));
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
		} else if (message.getMessageType() == MessageType.CONTROL) {
			processControlMessage((AbstractControlMessage) message, room, user);
		} else {
			// throw some silent exception
		}
	}

	private void processControlMessage(AbstractControlMessage message, Room room, User user) {
		if (message.getAction() == MessageAction.PAUSE_PLAYER) {
			room.setIsPaused(true);
			room.broadcast(message);
		} else if (message.getAction() == MessageAction.SEEK_PLAYER) {
			roomLeader = user;
			room.setPosition(((SeekPlayerMessage) message).getPosition());
			room.broadcast(message);
		} else if (message.getAction() == MessageAction.SEEK_AND_START_PLAYER) {
			roomLeader = user;
			room.setPosition(((SeekAndStartPlayerMessage) message).getPosition());
			room.setIsPaused(false);
			room.broadcast(message);

		} else if (message.getAction() == MessageAction.NEXT_EPISODE) {
			room.enqueueForNextEpisode(user);

			if (room.getReadyForNextEpisode()) {
				room.switchToNextEpisode();
				room.broadcast(new SystemMessage("Next episode is starting."));
				room.broadcast(new SeekAndStartPlayerMessage("0"));
			} else {
				try {
					room.broadcast(
							new SystemMessage(String.format("%s moved to the next episode.", user.getUsername())));
					user.sendMessage(new PausePlayerMessage());
				} catch (EncodeException | IOException e) {
					e.printStackTrace();
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
		} else {
			room.broadcast(new SystemMessage(String.format("%s left.", user.getUsername())));
		}
	}
}
