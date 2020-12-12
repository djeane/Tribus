package apptribus.com.tribus.pojo;

import java.util.Date;

public class UserUpdate {

    private String updateType;
    private String updateContent;
    private Date date;
    private CollectionTag tag;
    private String updateKey;

    public UserUpdate(){

    }

    public String getUpdateType() {
        return updateType;
    }

    public void setUpdateType(String updateType) {
        this.updateType = updateType;
    }

    public String getUpdateContent() {
        return updateContent;
    }

    public void setUpdateContent(String updateContent) {
        this.updateContent = updateContent;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public CollectionTag getTag() {
        return tag;
    }

    public void setTag(CollectionTag tag) {
        this.tag = tag;
    }

    public String getUpdateKey() {
        return updateKey;
    }

    public void setUpdateKey(String updateKey) {
        this.updateKey = updateKey;
    }
}
