package apptribus.com.tribus.pojo;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by User on 1/18/2018.
 */

public class Survey {

    private String idUser;
    private String question;
    private String key;
    private Date date;
    private long totVotes;
    private long numVotesFirstAnswer;
    private long numVotesSecondAnswer;
    private long numVotesThirdAnswer;
    private long numVotesFourthAnswer;
    private long numVotesFifthAnswer;
    private int totAnswers;
    private String firstAnswer;
    private String secondAnswer;
    private String thirdAnswer;
    private String fourthAnswer;
    private String fifthAnswer;
    //private HashMap<String, Object> timestampCreated;

    public Survey() {
        //HashMap<String, Object> timestampNow = new HashMap<>();
        //timestampNow.put("timestamp", ServerValue.TIMESTAMP);
        //this.timestampCreated = timestampNow;
    }

    public Survey(String idUser, String question, String key, Date date, long totVotes, long numVotesFirstAnswer,
                  long numVotesSecondAnswer, int totAnswers, String firstAnswer, String secondAnswer, String thirdAnswer, String fourthAnswer, String fifthAnswer) {
        this.idUser = idUser;
        this.question = question;
        this.key = key;
        this.date = date;
        this.totVotes = totVotes;
        this.numVotesFirstAnswer = numVotesFirstAnswer;
        this.numVotesSecondAnswer = numVotesSecondAnswer;
        this.totAnswers = totAnswers;
        this.firstAnswer = firstAnswer;
        this.secondAnswer = secondAnswer;
        this.thirdAnswer = thirdAnswer;
        this.fourthAnswer = fourthAnswer;
        this.fifthAnswer = fifthAnswer;
        //HashMap<String, Object> timestampNow = new HashMap<>();
        //timestampNow.put("timestamp", ServerValue.TIMESTAMP);
        //this.timestampCreated = timestampNow;

    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
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

    public long getTotVotes() {
        return totVotes;
    }

    public void setTotVotes(long totVotes) {
        this.totVotes = totVotes;
    }

    public long getNumVotesFirstAnswer() {
        return numVotesFirstAnswer;
    }

    public void setNumVotesFirstAnswer(long numVotesFirstAnswer) {
        this.numVotesFirstAnswer = numVotesFirstAnswer;
    }

    public long getNumVotesSecondAnswer() {
        return numVotesSecondAnswer;
    }

    public void setNumVotesSecondAnswer(long numVotesSecondAnswer) {
        this.numVotesSecondAnswer = numVotesSecondAnswer;
    }

    public long getNumVotesThirdAnswer() {
        return numVotesThirdAnswer;
    }

    public void setNumVotesThirdAnswer(long numVotesThirdAnswer) {
        this.numVotesThirdAnswer = numVotesThirdAnswer;
    }

    public long getNumVotesFourthAnswer() {
        return numVotesFourthAnswer;
    }

    public void setNumVotesFourthAnswer(long numVotesFourthAnswer) {
        this.numVotesFourthAnswer = numVotesFourthAnswer;
    }

    public long getNumVotesFifthAnswer() {
        return numVotesFifthAnswer;
    }

    public void setNumVotesFifthAnswer(long numVotesFifthAnswer) {
        this.numVotesFifthAnswer = numVotesFifthAnswer;
    }

    public int getTotAnswers() {
        return totAnswers;
    }

    public void setTotAnswers(int totAnswers) {
        this.totAnswers = totAnswers;
    }

    /*public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }*/

    public String getFirstAnswer() {
        return firstAnswer;
    }

    public void setFirstAnswer(String firstAnswer) {
        this.firstAnswer = firstAnswer;
    }

    public String getSecondAnswer() {
        return secondAnswer;
    }

    public void setSecondAnswer(String secondAnswer) {
        this.secondAnswer = secondAnswer;
    }

    public String getThirdAnswer() {
        return thirdAnswer;
    }

    public void setThirdAnswer(String thirdAnswer) {
        this.thirdAnswer = thirdAnswer;
    }

    public String getFourthAnswer() {
        return fourthAnswer;
    }

    public void setFourthAnswer(String fourthAnswer) {
        this.fourthAnswer = fourthAnswer;
    }

    public String getFifthAnswer() {
        return fifthAnswer;
    }

    public void setFifthAnswer(String fifthAnswer) {
        this.fifthAnswer = fifthAnswer;
    }

    //@Exclude
    //public long getTimestampCreatedLong(){
      //  return (long)timestampCreated.get("timestamp");
   // }
}
