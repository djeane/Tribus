package apptribus.com.tribus.activities.feature_choice_tribus.dagger;

import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.feature_choice_tribus.mvp.FeatureChoiceTribusModel;
import apptribus.com.tribus.activities.feature_choice_tribus.mvp.FeatureChoiceTribusPresenter;
import apptribus.com.tribus.activities.feature_choice_tribus.mvp.FeatureChoiceTribusView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by User on 1/17/2018.
 */

@Module
public class FeatureChoiceTribusModule {

    private final AppCompatActivity activity;

    public FeatureChoiceTribusModule(AppCompatActivity activity) {
        this.activity = activity;
    }


    @FeatureChoiceTribusScope
    @Provides
    public FeatureChoiceTribusView provideView(){
        return new FeatureChoiceTribusView(activity);
    }

    @FeatureChoiceTribusScope
    @Provides
    public FeatureChoiceTribusPresenter providePresenter(FeatureChoiceTribusView view, FeatureChoiceTribusModel model){
        return new FeatureChoiceTribusPresenter(view, model);
    }

    @FeatureChoiceTribusScope
    @Provides
    public FeatureChoiceTribusModel provideModel(){
        return new FeatureChoiceTribusModel(activity);
    }
}
