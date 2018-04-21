package ro.moscar.IzleminatorServer.chat.room;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.EncodeException;

import ro.moscar.IzleminatorServer.chat.IMessage;
import ro.moscar.IzleminatorServer.chat.messages.MessageType;

public class Room {
	private String name;
	private String position;
	private Boolean isPaused = false;
	private Map<String, User> users = new ConcurrentHashMap<String, User>();
	private Map<String, Boolean> usersQueuingForNextEpisode = new ConcurrentHashMap<String, Boolean>();

	public Room(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void addUser(User user) {
		users.put(user.getId(), user);
	}

	public void removeUser(String userId) {
		users.remove(userId);
	}

	public int getUserCount() {
		return users.size();
	}

	public void broadcast(IMessage message) {
		users.values().forEach(user -> {
			if (message.getMessageType().equals(MessageType.CONTROL)
					&& usersQueuingForNextEpisode.containsKey(user.getId())) {
				return;
			}

			try {
				user.sendMessage(message);
			} catch (IOException | EncodeException e) {
				e.printStackTrace();
			}
		});
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public Boolean getIsPaused() {
		return this.isPaused;
	}

	public void setIsPaused(Boolean isPaused) {
		this.isPaused = isPaused;
	}

	public void enqueueForNextEpisode(User user) {
		usersQueuingForNextEpisode.put(user.getId(), true);
	}

	public Boolean getReadyForNextEpisode() {
		return usersQueuingForNextEpisode.size() == users.size();
	}

	public void switchToNextEpisode() {
		usersQueuingForNextEpisode = new ConcurrentHashMap<String, Boolean>();
	}
}
