package apptribus.com.tribus.pojo;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;

/**
 * Created by User on 5/21/2017.
 */
@IgnoreExtraProperties
public class User {

    private String id;
    private String username;
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private String imageUrl;
    private String token;
    private Date date;
    private boolean isAccepted;
    private String aboutMe;
    private boolean online;
    private boolean onlineInChat;
    private Date lastSeen;
    private int year;
    private int month;
    private int day;
    private String gender;
    private String thumb;

    public User() {
    }

    public User(String username, String name, String email, String password, String phoneNumber, String imageUrl) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.imageUrl = imageUrl;
    }


    //CONSTRUCTOR WITH IS ACCEPTED WITHOUT TOKEN
    public User(String id, String username, String name, String email, String password, String phoneNumber, String imageUrl,
                boolean isAccepted, Date date) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.imageUrl = imageUrl;
        this.isAccepted = isAccepted;
        this.date = date;
    }


    //CONSTRUCTOR WITH IS ACCEPTED AND TOKEN
    public User(String id, String username, String name, String email, String password, String phoneNumber, String imageUrl,
                String token, boolean isAccepted, Date date) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.imageUrl = imageUrl;
        this.token = token;
        this.isAccepted = isAccepted;
        this.date = date;

    }


    //CONSTRUCTOR WITH TOKEN, IS ACCEPTED, DATE AND ABOUT ME
    public User(String id, String username, String name, String email, String password, String phoneNumber, String imageUrl,
                String token, boolean isAccepted, Date date, String aboutMe) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.imageUrl = imageUrl;
        this.token = token;
        this.isAccepted = isAccepted;
        this.date = date;
        this.aboutMe = aboutMe;

    }

    //CONSTRUCTOR WITH TOKEN, IS ACCEPTED, DATE, ABOUT ME AND ONLINE
    public User(String id, String username, String name, String email, String password, String phoneNumber, String imageUrl,
                String token, boolean isAccepted, Date date, String aboutMe, boolean online) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.imageUrl = imageUrl;
        this.token = token;
        this.isAccepted = isAccepted;
        this.date = date;
        this.aboutMe = aboutMe;
        this.online = online;

    }

    //CONSTRUCTOR WITH TOKEN, IS ACCEPTED, DATE, ABOUT ME, ONLINE AND LAST SEEN
    public User(String id, String username, String name, String email, String password, String phoneNumber, String imageUrl,
                String token, boolean isAccepted, Date date, String aboutMe, boolean online, Date lastSeen) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.imageUrl = imageUrl;
        this.token = token;
        this.isAccepted = isAccepted;
        this.date = date;
        this.aboutMe = aboutMe;
        this.online = online;
        this.lastSeen = lastSeen;
    }

    //CONSTRUCTOR WITH TOKEN, IS ACCEPTED, DATE, ABOUT ME, ONLINE, LAST SEEN, AGE AND GENDER
    public User(String id, String username, String name, String email, String password, String phoneNumber, String imageUrl,
                String token, boolean isAccepted, Date date, String aboutMe, boolean online, Date lastSeen,
                int year, int month, int day, String gender) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.imageUrl = imageUrl;
        this.token = token;
        this.isAccepted = isAccepted;
        this.date = date;
        this.aboutMe = aboutMe;
        this.online = online;
        this.lastSeen = lastSeen;
        this.year = year;
        this.month = month;
        this.day = day;
        this.gender = gender;
    }

    //CONSTRUCTOR WITH TOKEN, IS ACCEPTED, DATE, ABOUT ME, ONLINE, LAST SEEN, AGE, GENDER AND IS ONLINE IN CHAT
    public User(String id, String username, String name, String email, String password, String phoneNumber, String imageUrl,
                String token, boolean isAccepted, Date date, String aboutMe, boolean online, Date lastSeen,
                int year, int month, int day, String gender, boolean onlineInChat) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.imageUrl = imageUrl;
        this.token = token;
        this.isAccepted = isAccepted;
        this.date = date;
        this.aboutMe = aboutMe;
        this.online = online;
        this.lastSeen = lastSeen;
        this.year = year;
        this.month = month;
        this.day = day;
        this.gender = gender;
        this.onlineInChat = onlineInChat;
    }


    //CONSTRUCTOR WITH TOKEN, IS ACCEPTED, DATE, ABOUT ME, ONLINE, LAST SEEN, AGE AND GENDER WITHOUT EMAIL AND PASSWORD
    public User(String id, String username, String name, String phoneNumber, String imageUrl,
                String token, boolean isAccepted, Date date, String aboutMe, boolean online, Date lastSeen,
                int year, int month, int day, String gender) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.imageUrl = imageUrl;
        this.token = token;
        this.isAccepted = isAccepted;
        this.date = date;
        this.aboutMe = aboutMe;
        this.online = online;
        this.lastSeen = lastSeen;
        this.year = year;
        this.month = month;
        this.day = day;
        this.gender = gender;
    }


    public User(String id, String username, String name, String email, String password, String phoneNumber, String imageUrl,
                String token) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.imageUrl = imageUrl;
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public Date getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Date lastSeen) {
        this.lastSeen = lastSeen;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isOnlineInChat() {
        return onlineInChat;
    }

    public void setOnlineInChat(boolean onlineInChat) {
        this.onlineInChat = onlineInChat;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }
}
