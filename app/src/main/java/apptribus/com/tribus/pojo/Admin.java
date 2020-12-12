package apptribus.com.tribus.pojo;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by User on 6/3/2017.
 */
@IgnoreExtraProperties
public class Admin {

    //NEW PARAMETERS TO CREATE AN ADMIN - Since 25/07/2017
    private String tribuUniqueName;
    private String uidAdmin;
    private String tribuKey;
    private Date date;


    public Admin() {
    }

    public Admin(String tribuUniqueName, String uidAdmin, Date date) {
        this.tribuUniqueName = tribuUniqueName;
        this.uidAdmin = uidAdmin;
        this.date = date;

    }

    public Admin(String tribuUniqueName, String uidAdmin) {
        this.tribuUniqueName = tribuUniqueName;
        this.uidAdmin = uidAdmin;

    }

    public String getTribuUniqueName() {
        return tribuUniqueName;
    }

    public void setTribuUniqueName(String tribuUniqueName) {
        this.tribuUniqueName = tribuUniqueName;
    }

    public String getUidAdmin() {
        return uidAdmin;
    }

    public void setUidAdmin(String uidAdmin) {
        this.uidAdmin = uidAdmin;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTribuKey() {
        return tribuKey;
    }

    public void setTribuKey(String tribuKey) {
        this.tribuKey = tribuKey;
    }

}
