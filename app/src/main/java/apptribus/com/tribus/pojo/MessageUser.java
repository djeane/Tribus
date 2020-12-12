package apptribus.com.tribus.pojo;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;

/**
 * Created by User on 6/11/2017.
 */
@IgnoreExtraProperties
public class MessageUser {

    private String message;
    private String contentType;
    private String uidUser;
    private Boolean isAccepetd;
    private Video video;
    private Image image;
    private Audio audio;
    private String key;
    private Date date;
    private String link;
    private String topicKey;
    private Boolean isVisualized;

    //TO REPLY MESSAGE
    private String replyUserId;
    private String replyMessage;
    private String replyMessageKey;



    public MessageUser() {
    }

    public MessageUser(String message, String contentType, String uidUser) {
        this.message = message;
        this.contentType = contentType;
        this.uidUser = uidUser;
    }

    //CORRECT MESSAGE
    public MessageUser(String message, String contentType, String uidUser, Boolean isAccepetd) {
        this.message = message;
        this.contentType = contentType;
        this.uidUser = uidUser;
        this.isAccepetd = isAccepetd;
    }

    //ANOTHER CORRECT MESSAGE - WITH KEY
    public MessageUser(String message, String contentType, String uidUser, Boolean isAccepetd, String key) {
        this.message = message;
        this.contentType = contentType;
        this.uidUser = uidUser;
        this.isAccepetd = isAccepetd;
        this.key = key;
    }

    //ANOTHER CORRECT MESSAGE - WITH KEY ANDO TOPIC KEY
    public MessageUser(String message, String contentType, String uidUser, Boolean isAccepetd, String key, String topicKey) {
        this.message = message;
        this.contentType = contentType;
        this.uidUser = uidUser;
        this.isAccepetd = isAccepetd;
        this.key = key;
        this.topicKey = topicKey;
    }


    //ANOTHER CORRECT MESSAGE - WITH KEY AND DATE AND LINK_INTO_MESSAGE
    public MessageUser(String message, String link, String contentType, String uidUser, Boolean isAccepetd) {
        this.message = message;
        this.link = link;
        this.contentType = contentType;
        this.uidUser = uidUser;
        this.isAccepetd = isAccepetd;
        this.key = key;
        this.date = date;
    }


    //AUDIO MESSAGE
    public MessageUser(String message, String contentType, String uidUser, Boolean isAccepetd, Audio audio) {
        this.message = message;
        this.contentType = contentType;
        this.uidUser = uidUser;
        this.isAccepetd = isAccepetd;
        this.audio = audio;
    }


    //VIDEO MESSAGE
    public MessageUser(String message, String contentType, String uidUser, Boolean isAccepetd, Video video) {
        this.message = message;
        this.contentType = contentType;
        this.uidUser = uidUser;
        this.isAccepetd = isAccepetd;
        this.video = video;
    }

    //IMAGE MESSAGE
    public MessageUser(String message, String contentType, String uidUser, Boolean isAccepetd, Image image) {
        this.message = message;
        this.contentType = contentType;
        this.uidUser = uidUser;
        this.isAccepetd = isAccepetd;
        this.image = image;
    }

    //ANOTHER CORRECT MESSAGE - WITH KEY, DATE AND TOPIC KEY
    public MessageUser(String message, String contentType, Boolean isAccepetd, String uidUser, String topicKey) {
        this.message = message;
        this.contentType = contentType;
        this.uidUser = uidUser;
        this.isAccepetd = isAccepetd;
        this.key = key;
        this.date = date;
        this.topicKey = topicKey;
    }

    //REPLY MESSAGE TEXT
    public MessageUser(String message, String contentType, String uidUser, Boolean isAccepetd,
                       String topicKey, String replyUserId, String replyMessage, String replyMessageKey) {
        this.message = message;
        this.contentType = contentType;
        this.uidUser = uidUser;
        this.isAccepetd = isAccepetd;
        this.key = key;
        this.topicKey = topicKey;
        this.replyUserId = replyUserId;
        this.replyMessage = replyMessage;
        this.replyMessageKey = replyMessageKey;
    }

    //ANOTHER CORRECT MESSAGE - WITH KEY AND DATE, LINK_INTO_MESSAGE AND TOPIC
    public MessageUser(String message, String link, String contentType, String uidUser, Boolean isAccepetd, String topicKey) {
        this.message = message;
        this.link = link;
        this.contentType = contentType;
        this.uidUser = uidUser;
        this.isAccepetd = isAccepetd;
        this.key = key;
        this.date = date;
        this.topicKey = topicKey;
    }


    //AUDIO MESSAGE
    public MessageUser(String message, String contentType, String uidUser, Boolean isAccepetd, Audio audio, String topicKey) {
        this.message = message;
        this.contentType = contentType;
        this.uidUser = uidUser;
        this.isAccepetd = isAccepetd;
        this.audio = audio;
        this.topicKey = topicKey;
    }


    //VIDEO MESSAGE
    public MessageUser(String message, String contentType, String uidUser, Boolean isAccepetd, Video video, String topicKey) {
        this.message = message;
        this.contentType = contentType;
        this.uidUser = uidUser;
        this.isAccepetd = isAccepetd;
        this.video = video;
        this.topicKey = topicKey;
    }

    //IMAGE MESSAGE
    public MessageUser(String message, String contentType, String uidUser, Boolean isAccepetd, Image image, String topicKey) {
        this.message = message;
        this.contentType = contentType;
        this.uidUser = uidUser;
        this.isAccepetd = isAccepetd;
        this.image = image;
        this.topicKey = topicKey;
    }



    public String getUidUser() {
        return uidUser;
    }

    public void setUidUser(String uidUser) {
        this.uidUser = uidUser;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean isAccepetd() {
        return isAccepetd;
    }

    public void setAccepetd(Boolean accepetd) {
        isAccepetd = accepetd;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Audio getAudio() {
        return audio;
    }

    public void setAudio(Audio audio) {
        this.audio = audio;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTopicKey() {
        return topicKey;
    }

    public void setTopicKey(String topicKey) {
        this.topicKey = topicKey;
    }

    public String getReplyUserId() {
        return replyUserId;
    }

    public String getReplyMessage() {
        return replyMessage;
    }

    public String getReplyMessageKey() {
        return replyMessageKey;
    }

    public Boolean getVisualized() {
        return isVisualized;
    }

    public void setVisualized(Boolean visualized) {
        isVisualized = visualized;
    }
}
