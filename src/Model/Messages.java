package Model;

import java.util.Date;


public class Messages {

	private int userId;
	private int messageId;
	private String messageText;
	private Date messageDate;
	private boolean messageReaded;

	public long getUserID() {
		return userId;
	}

	public String getMessageText() {
		return messageText;
	}

	public Date getMessageDate() {
		return messageDate;
	}

	public boolean isMessageReaded() {
		return messageReaded;
	}
	public int messageId(){
		return messageId;
	}
	

}
