package apptribus.com.tribus.pojo;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by User on 8/20/2017.
 */

public class RequestTribu {

    private String mTribuKey;
    private Date date;
    private String thematic;
    //private HashMap<String, Object> timestampCreated;

    public RequestTribu() {
    }

    public RequestTribu(String tribuKey, Date date) {
        this.mTribuKey = tribuKey;
        this.date = date;
        /*HashMap<String, Object> timestampNow = new HashMap<>();
        timestampNow.put("timestamp", ServerValue.TIMESTAMP);
        this.timestampCreated = timestampNow;*/

    }

    public RequestTribu(String tribuKey) {
        this.mTribuKey = tribuKey;
        /*HashMap<String, Object> timestampNow = new HashMap<>();
        timestampNow.put("timestamp", ServerValue.TIMESTAMP);
        this.timestampCreated = timestampNow;*/

    }

    public String getTribuKey() {
        return mTribuKey;
    }

    public void setTribuKey(String tribuKey) {
        this.mTribuKey = tribuKey;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getThematic() {
        return thematic;
    }

    public void setThematic(String thematic) {
        this.thematic = thematic;
    }

    /*public HashMap<String, Object> getTimestampCreated(){
        return timestampCreated;
    }

    @Exclude
    public long getTimestampCreatedLong(){
        return (long)timestampCreated.get("timestamp");
    }*/
}
