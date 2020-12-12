package apptribus.com.tribus.activities.survey.dagger;

import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.application.dagger.AppComponent;
import dagger.Component;

/**
 * Created by User on 1/18/2018.
 */
@SurveyScope
@Component(modules = {SurveyModule.class}, dependencies = {AppComponent.class})
public interface SurveyComponent {

    void inject(AppCompatActivity activity);
}
