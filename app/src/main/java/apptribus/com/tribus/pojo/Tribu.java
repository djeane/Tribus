package apptribus.com.tribus.pojo;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by User on 5/26/2017.
 */
@IgnoreExtraProperties
public class Tribu {

    private ProfileTribu profile;
    private Admin admin;
    private Date date;
    private String uidUser;
    private String uniqueNameTribu;
    private String key;
    private String thematic;
    //private HashMap<String, Object> timestampCreated;


    public Tribu() {
    }

    public Tribu(ProfileTribu profile, Admin admin) {
        this.profile = profile;
        this.admin = admin;
        /*HashMap<String, Object> timestampNow = new HashMap<>();
        timestampNow.put("timestamp", ServerValue.TIMESTAMP);
        this.timestampCreated = timestampNow;*/

    }

    //TO HANDLE INDIVIDUAL FOLLOWERS - DATE JUST TO HANDLE ADMIN FOLLOWER TOO
    public Tribu(String uidUser, String uniqueNameTribu, Date date){
        this.uidUser = uidUser;
        this.uniqueNameTribu = uniqueNameTribu;
        this.date = date;
        /*HashMap<String, Object> timestampNow = new HashMap<>();
        timestampNow.put("timestamp", ServerValue.TIMESTAMP);
        this.timestampCreated = timestampNow;*/

    }

    //TO HANDLE INDIVIDUAL FOLLOWERS - DATE JUST TO HANDLE ADMIN FOLLOWER TOO
    public Tribu(String uidUser, String uniqueNameTribu){
        this.uidUser = uidUser;
        this.uniqueNameTribu = uniqueNameTribu;
        this.date = date;
        /*HashMap<String, Object> timestampNow = new HashMap<>();
        timestampNow.put("timestamp", ServerValue.TIMESTAMP);
        this.timestampCreated = timestampNow;*/

    }

    public ProfileTribu getProfile() {
        return profile;
    }

    public void setProfile(ProfileTribu profile) {
        this.profile = profile;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUidUser() {
        return uidUser;
    }

    public void setUidUser(String uidUser) {
        this.uidUser = uidUser;
    }

    public String getUniqueNameTribu() {
        return uniqueNameTribu;
    }

    public void setUniqueNameTribu(String uniqueNameTribu) {
        this.uniqueNameTribu = uniqueNameTribu;
    }

    /*public HashMap<String, Object> getTimestampCreated(){
        return timestampCreated;
    }*/

    /*@Exclude
    public long getTimestampCreatedLong(){
        return (long)timestampCreated.get("timestamp");
    }*/

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getThematic() {
        return thematic;
    }

    public void setThematic(String thematic) {
        this.thematic = thematic;
    }
}
