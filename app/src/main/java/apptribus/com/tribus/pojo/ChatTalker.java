package apptribus.com.tribus.pojo;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by User on 11/11/2017.
 */

public class ChatTalker {

    private String currentUserId;
    private String talkerId;
    private int unreadMessages;
    private boolean talkerIsOnline;
    private Date date;
    private String message;

    public ChatTalker() {
    }

    public ChatTalker(String currentUserId, String talkerId, int unreadMessages, boolean talkerIsOnline, Date date,
                      String message) {
        this.currentUserId = currentUserId;
        this.talkerId = talkerId;
        this.unreadMessages = unreadMessages;
        this.talkerIsOnline = talkerIsOnline;
        this.date = date;
        this.message = message;
    }

    public ChatTalker(String currentUserId, String talkerId, boolean talkerIsOnline) {
        this.currentUserId = currentUserId;
        this.talkerId = talkerId;
        this.talkerIsOnline = talkerIsOnline;
    }


    public String getTalkerId() {
        return talkerId;
    }

    public int getUnreadMessages() {
        return unreadMessages;
    }

    public void setUnreadMessages(int unreadMessages) {
        this.unreadMessages = unreadMessages;
    }

    public boolean isTalkerIsOnline() {
        return talkerIsOnline;
    }

    public void setTalkerIsOnline(boolean talkerIsOnline) {
        this.talkerIsOnline = talkerIsOnline;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    public void setTalkerId(String talkerId) {
        this.talkerId = talkerId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
