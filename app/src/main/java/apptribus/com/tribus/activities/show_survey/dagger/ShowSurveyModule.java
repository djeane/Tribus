package apptribus.com.tribus.activities.show_survey.dagger;

import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.show_survey.mvp.ShowSurveyModel;
import apptribus.com.tribus.activities.show_survey.mvp.ShowSurveyPresenter;
import apptribus.com.tribus.activities.show_survey.mvp.ShowSurveyView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by User on 1/19/2018.
 */
@Module
public class ShowSurveyModule {

    private final AppCompatActivity activity;

    public ShowSurveyModule(AppCompatActivity activity) {
        this.activity = activity;
    }

    @ShowSurveyScope
    @Provides
    public ShowSurveyView provideView(){
        return new ShowSurveyView(activity);
    }

    @ShowSurveyScope
    @Provides
    public ShowSurveyPresenter providePresenter(ShowSurveyView view, ShowSurveyModel model){
        return new ShowSurveyPresenter(view, model);
    }

    @ShowSurveyScope
    @Provides
    public ShowSurveyModel provideModel(){
        return new ShowSurveyModel(activity);
    }
}
