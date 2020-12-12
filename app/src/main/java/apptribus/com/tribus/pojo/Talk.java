package apptribus.com.tribus.pojo;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by User on 6/13/2017.
 */
@IgnoreExtraProperties
public class Talk {

    private String tribuKey;
    private Date dateInvitation;
    private String talkerId;
    private boolean fromPermission;
    private Date dateAccepted;


    public Talk() {
    }


    public Date getDateInvitation() {
        return dateInvitation;
    }

    public void setDateInvitation(Date dateInvitation) {
        this.dateInvitation = dateInvitation;
    }

    public String getTalkerId() {
        return talkerId;
    }

    public void setTalkerId(String talkerId) {
        this.talkerId = talkerId;
    }

    public boolean isFromPermission() {
        return fromPermission;
    }

    public void setFromPermission(boolean fromPermission) {
        this.fromPermission = fromPermission;
    }

    public String getTribuKey() {
        return tribuKey;
    }

    public void setTribuKey(String tribuKey) {
        this.tribuKey = tribuKey;
    }

    public Date getDateAccepted() {
        return dateAccepted;
    }

    public void setDateAccepted(Date dateAccepted) {
        this.dateAccepted = dateAccepted;
    }
}
