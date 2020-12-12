package apptribus.com.tribus.pojo;

import java.util.Date;

public class SurveyVote {

    private String option;
    private Date date;

    public SurveyVote() {
    }

    public SurveyVote(String option, Date date) {
        this.option = option;
        this.date = date;
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
}
