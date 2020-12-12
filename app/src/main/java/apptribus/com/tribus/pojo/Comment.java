package apptribus.com.tribus.pojo;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by User on 7/3/2017.
 */

public class Comment {

    private String comment;
    private Date data;
    private int numLikes;
    private int numDislikes;
    private String uidUser;
    /*private HashMap<String, Object> timestampCreated;*/


    public Comment() {
    }

    public Comment(String comment, Date Date, int numLikes, int numDislikes, String uidUser) {
        this.comment = comment;
        this.data = data;
        this.numLikes = numLikes;
        this.numDislikes = numDislikes;
        this.uidUser = uidUser;
        /*HashMap<String, Object> timestampNow = new HashMap<>();
        timestampNow.put("timestamp", ServerValue.TIMESTAMP);
        this.timestampCreated = timestampNow;*/

    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public int getNumLikes() {
        return numLikes;
    }

    public void setNumLikes(int numLikes) {
        this.numLikes = numLikes;
    }

    public int getNumDislikes() {
        return numDislikes;
    }

    public void setNumDislikes(int numDislikes) {
        this.numDislikes = numDislikes;
    }

    public String getUidUser() {
        return uidUser;
    }

    public void setUidUser(String uidUser) {
        this.uidUser = uidUser;
    }

    /*public HashMap<String, Object> getTimestampCreated(){
        return timestampCreated;
    }

    @Exclude
    public long getTimestampCreatedLong(){
        return (long)timestampCreated.get("timestamp");
    }*/
}
