package apptribus.com.tribus.activities.show_survey.dagger;

import apptribus.com.tribus.activities.show_survey.ShowSurveyActivity;
import apptribus.com.tribus.application.dagger.AppComponent;
import dagger.Component;

/**
 * Created by User on 1/19/2018.
 */
@ShowSurveyScope
@Component(modules = {ShowSurveyModule.class}, dependencies = {AppComponent.class})
public interface ShowSurveyComponent {

    void inject(ShowSurveyActivity activity);
}
