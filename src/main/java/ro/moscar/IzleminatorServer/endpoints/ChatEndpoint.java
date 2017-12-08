package ro.moscar.IzleminatorServer.endpoints;

import java.io.IOException;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import ro.moscar.IzleminatorServer.chat.IMessage;
import ro.moscar.IzleminatorServer.chat.MessageDecoder;
import ro.moscar.IzleminatorServer.chat.MessageEncoder;
import ro.moscar.IzleminatorServer.chat.room.RoomSupervisor;
import ro.moscar.IzleminatorServer.chat.room.User;

@ServerEndpoint(value = "/chat/{room}/{username}", decoders = MessageDecoder.class, encoders = MessageEncoder.class)
public class ChatEndpoint {
	private User user;
	private String roomName;
	private static RoomSupervisor roomSupervisor = new RoomSupervisor();

	@OnOpen
	public void onOpen(Session session, @PathParam("username") String username, @PathParam("room") String roomName)
			throws IOException, EncodeException {
		this.user = new User(username, session);
		this.roomName = roomName;
		roomSupervisor.addUserToRoom(user, roomName);
	}

	@OnMessage
	public void onMessage(Session session, IMessage message) throws IOException {
		message.setFrom(user.getUsername());
		message.setFromUuid(user.getUuid());
		roomSupervisor.userMessageReceived(user, roomName, message);
	}

	@OnClose
	public void onClose(Session session) throws IOException {
		roomSupervisor.userClosedClosedConnection(user, roomName);
	}

	@OnError
	public void onError(Session session, Throwable throwable) {
		System.out.println("Error");
	}
}