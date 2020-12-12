package apptribus.com.tribus.activities.main_activity.fragment_talks.dagger;

import android.support.v4.app.Fragment;

import apptribus.com.tribus.activities.main_activity.fragment_talks.mvp.TalksFragmentModel;
import apptribus.com.tribus.activities.main_activity.fragment_talks.mvp.TalksFragmentPresenter;
import apptribus.com.tribus.activities.main_activity.fragment_talks.mvp.TalksFragmentView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by User on 6/13/2017.
 */
@Module
public class TalksFragmentModule {

    private final Fragment fragment;

    public TalksFragmentModule(Fragment fragment) {
        this.fragment = fragment;
    }


    @TalksFragmentScope
    @Provides
    public TalksFragmentView provideView(){
        return new TalksFragmentView(fragment);
    }


    @TalksFragmentScope
    @Provides
    public TalksFragmentPresenter providePresenter(TalksFragmentView view, TalksFragmentModel model){
        return new TalksFragmentPresenter(view, model);
    }


    @TalksFragmentScope
    @Provides
    public TalksFragmentModel provideModel(){
        return new TalksFragmentModel(fragment);
    }

}
