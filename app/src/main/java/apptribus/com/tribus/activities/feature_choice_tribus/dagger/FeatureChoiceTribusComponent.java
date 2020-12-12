package apptribus.com.tribus.activities.feature_choice_tribus.dagger;

import apptribus.com.tribus.activities.feature_choice_tribus.FeatureChoiceTribusActivity;
import apptribus.com.tribus.application.dagger.AppComponent;
import dagger.Component;

/**
 * Created by User on 1/17/2018.
 */

@FeatureChoiceTribusScope
@Component(modules = {FeatureChoiceTribusModule.class}, dependencies = {AppComponent.class})
public interface FeatureChoiceTribusComponent {

    void inject(FeatureChoiceTribusActivity activity);
}
