package apptribus.com.tribus.activities.survey.mvp;

import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.conversation_topics.repository.ConversationTopicAPI;
import apptribus.com.tribus.activities.survey.repository.SurveyAPI;
import apptribus.com.tribus.pojo.Survey;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.util.firestore.FirestoreService;
import rx.Observable;

/**
 * Created by User on 1/18/2018.
 */

public class SurveyModel {

    private final AppCompatActivity activity;
    private FirestoreService mFirestoreService;

    public SurveyModel(AppCompatActivity activity) {
        this.activity = activity;
        mFirestoreService = new FirestoreService(activity);
    }

    public Observable<Tribu> getTribu(String tribuKey) {
        return mFirestoreService.getTribu(tribuKey);
        //return SurveyAPI.getTribu(tribuKey);
    }

    public void sendSurveyToFirebase(Tribu tribu, Survey survey){
        SurveyAPI.sendSurveyToFirebase(tribu, survey, activity);
    }

}
