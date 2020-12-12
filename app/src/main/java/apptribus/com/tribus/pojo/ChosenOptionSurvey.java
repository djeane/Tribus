package apptribus.com.tribus.pojo;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by User on 1/22/2018.
 */

public class ChosenOptionSurvey {

    private String surveyKey;
    private String option;
    private Date date;
    //private HashMap<String, Object> timestampCreated;

    public ChosenOptionSurvey() {
        HashMap<String, Object> timestampNow = new HashMap<>();
        /*timestampNow.put("timestamp", ServerValue.TIMESTAMP);
        this.timestampCreated = timestampNow;*/
    }

    public String getSurveyKey() {
        return surveyKey;
    }

    public void setSurveyKey(String surveyKey) {
        this.surveyKey = surveyKey;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    /*public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }*/


    /*@Exclude
    public long getTimestampCreatedLong() {
        return (long) timestampCreated.get("timestamp");
    }*/
}
