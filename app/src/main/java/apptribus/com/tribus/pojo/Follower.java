package apptribus.com.tribus.pojo;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by User on 6/14/2017.
 */
@IgnoreExtraProperties
public class Follower {

    //NEW PARAMETERS TO CREATE AN FOLLOWER - Since 25/07/2017
    private String uidFollower;
    private Boolean isAdmin;
    private Date date;
    //private HashMap<String, Object> timestampCreated;

    public Follower() {
    }

    public Follower(String uidFollower, Boolean isAdmin, Date date) {
        this.uidFollower = uidFollower;
        this.isAdmin = isAdmin;
        this.date = date;
        /*HashMap<String, Object> timestampNow = new HashMap<>();
        timestampNow.put("timestamp", ServerValue.TIMESTAMP);
        this.timestampCreated = timestampNow;*/

    }

    public Follower(Boolean isAdmin, Date date) {
        this.isAdmin = isAdmin;
        this.date = date;
        /*HashMap<String, Object> timestampNow = new HashMap<>();
        timestampNow.put("timestamp", ServerValue.TIMESTAMP);
        this.timestampCreated = timestampNow;*/

    }

    public Follower(String uidFollower, Boolean isAdmin) {
        this.uidFollower = uidFollower;
        this.isAdmin = isAdmin;
        /*HashMap<String, Object> timestampNow = new HashMap<>();
        timestampNow.put("timestamp", ServerValue.TIMESTAMP);
        this.timestampCreated = timestampNow;*/

    }



    public String getUidFollower() {
        return uidFollower;
    }

    public void setUidFollower(String uidFollower) {
        this.uidFollower = uidFollower;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    /*public HashMap<String, Object> getTimestampCreated(){
        return timestampCreated;
    }

    @Exclude
    public long getTimestampCreatedLong(){
        return (long)timestampCreated.get("timestamp");
    }*/
}
