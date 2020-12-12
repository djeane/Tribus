package apptribus.com.tribus.activities.survey.dagger;

import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.survey.mvp.SurveyModel;
import apptribus.com.tribus.activities.survey.mvp.SurveyNewPresenter;
import apptribus.com.tribus.activities.survey.mvp.SurveyNewView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by User on 1/23/2018.
 */
@Module
public class SurveyNewModule {

    private final AppCompatActivity activity;

    public SurveyNewModule(AppCompatActivity activity) {
        this.activity = activity;
    }


    @SurveyScope
    @Provides
    public SurveyNewView provideView(){
        return new SurveyNewView(activity);
    }

    @SurveyScope
    @Provides
    public SurveyNewPresenter providePresenter(SurveyNewView view, SurveyModel model){
        return new SurveyNewPresenter(view, model);
    }

    @SurveyScope
    @Provides
    public SurveyModel provideModel(){
        return new SurveyModel(activity);
    }


}
