package apptribus.com.tribus.pojo;

import java.util.Date;

public class RemovedTribu {

    private String key;
    private Date date;
    private String thematic;

    public RemovedTribu(){

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

    public String getThematic() {
        return thematic;
    }

    public void setThematic(String thematic) {
        this.thematic = thematic;
    }
}
