package apptribus.com.tribus.activities.survey.dagger;

import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.survey.mvp.SurveyModel;
import apptribus.com.tribus.activities.survey.mvp.SurveyPresenter;
import apptribus.com.tribus.activities.survey.mvp.SurveyView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by User on 1/18/2018.
 */
@Module
public class SurveyModule {

    private final AppCompatActivity activity;

    public SurveyModule(AppCompatActivity activity) {
        this.activity = activity;
    }


    @SurveyScope
    @Provides
    public SurveyView provideView(){
        return new SurveyView(activity);
    }

    @SurveyScope
    @Provides
    public SurveyPresenter providePresenter(SurveyView view, SurveyModel model){
        return new SurveyPresenter(view, model);
    }

    @SurveyScope
    @Provides
    public SurveyModel provideModel(){
        return new SurveyModel(activity);
    }

}
