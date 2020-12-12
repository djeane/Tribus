package apptribus.com.tribus.pojo;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by User on 12/26/2017.
 */

public class ConversationTopic {

    private String idParticipant;
    private Date date;
    private String topic;
    private String key;
    private boolean isEdited;
    //private HashMap<String, Object> timestampCreated;

    public ConversationTopic() {
    }

    public ConversationTopic(String idParticipant, Date date, String topic, String key) {
        this.idParticipant = idParticipant;
        this.date = date;
        this.topic = topic;
        this.key = key;
        //HashMap<String, Object> timestampNow = new HashMap<>();
        //timestampNow.put("timestamp", ServerValue.TIMESTAMP);
        //this.timestampCreated = timestampNow;

    }

    public String getIdParticipant() {
        return idParticipant;
    }

    public void setIdParticipant(String idParticipant) {
        this.idParticipant = idParticipant;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    /*public HashMap<String, Object> getTimestampCreated(){
        return timestampCreated;
    }*/

    /*@Exclude
    public long getTimestampCreatedLong(){
        return (long)timestampCreated.get("timestamp");
    }*/

    public boolean isEdited() {
        return isEdited;
    }

    public void setEdited(boolean edited) {
        isEdited = edited;
    }
}
