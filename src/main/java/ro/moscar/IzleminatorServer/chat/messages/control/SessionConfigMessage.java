package ro.moscar.IzleminatorServer.chat.messages.control;

import ro.moscar.IzleminatorServer.chat.messages.AbstractControlMessage;
import ro.moscar.IzleminatorServer.chat.messages.MessageAction;

public class SessionConfigMessage extends AbstractControlMessage {
	private static final MessageAction action = MessageAction.SESSION_CONFIG;
	private String userid;

	public SessionConfigMessage(String userid) {
		super(String.format("userid:%s", userid));
		this.userid = userid;
	}

	@Override
	public MessageAction getAction() {
		return action;
	}

	public String getUserid() {
		return userid;
	}
}
