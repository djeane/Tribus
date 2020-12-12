package apptribus.com.tribus.pojo;

import java.util.Date;

public class CollectionTag {

    private String keyTag;
    private String messageKeyTag;
    private String tribuKeyTag;
    private String userIdTag;
    private String TopicKeyTag;
    private Date date;

    public CollectionTag(){

    }

    public String getKeyTag() {
        return keyTag;
    }

    public void setKeyTag(String keyTag) {
        this.keyTag = keyTag;
    }

    public String getMessageKeyTag() {
        return messageKeyTag;
    }

    public void setMessageKeyTag(String messageKeyTag) {
        this.messageKeyTag = messageKeyTag;
    }

    public String getTribuKeyTag() {
        return tribuKeyTag;
    }

    public void setTribuKeyTag(String tribuKeyTag) {
        this.tribuKeyTag = tribuKeyTag;
    }

    public String getUserIdTag() {
        return userIdTag;
    }

    public void setUserIdTag(String userIdTag) {
        this.userIdTag = userIdTag;
    }

    public String getTopicKeyTag() {
        return TopicKeyTag;
    }

    public void setTopicKeyTag(String topicKeyTag) {
        TopicKeyTag = topicKeyTag;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
