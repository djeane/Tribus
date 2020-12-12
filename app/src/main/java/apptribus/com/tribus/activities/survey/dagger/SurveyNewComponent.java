package apptribus.com.tribus.activities.survey.dagger;

import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.survey.SurveyActivity;
import apptribus.com.tribus.application.dagger.AppComponent;
import dagger.Component;

/**
 * Created by User on 1/23/2018.
 */
@SurveyScope
@Component(modules = {SurveyNewModule.class}, dependencies = {AppComponent.class})
public interface SurveyNewComponent {

    void inject(SurveyActivity activity);
}
