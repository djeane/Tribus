package apptribus.com.tribus.activities.show_survey.mvp;

import android.support.v7.app.AppCompatActivity;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import apptribus.com.tribus.activities.conversation_topics.repository.ConversationTopicAPI;
import apptribus.com.tribus.activities.show_survey.repository.ShowSurveyAPI;
import apptribus.com.tribus.activities.show_survey.view_holder.ShowSurveyViewHolder;
import apptribus.com.tribus.pojo.Survey;
import apptribus.com.tribus.pojo.Tribu;
import rx.Observable;

/**
 * Created by User on 1/19/2018.
 */

public class ShowSurveyModel {

    private final AppCompatActivity activity;

    public ShowSurveyModel(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void backToMainActivity(){
        activity.finish();
    }

    public Observable<Tribu> getTribu(String uniqueName) {
        return ShowSurveyAPI.getTribu(uniqueName);
    }


    //SET ADAPTER TO SHOW LIST OF TALKERS
    public FirebaseRecyclerAdapter<Survey, ShowSurveyViewHolder> setRecyclerViewSurveys(Tribu tribu){
        return ShowSurveyAPI.getSurvey(tribu, activity);
    }

}
