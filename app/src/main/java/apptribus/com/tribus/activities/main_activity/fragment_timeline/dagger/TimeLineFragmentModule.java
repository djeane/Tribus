package apptribus.com.tribus.activities.main_activity.fragment_timeline.dagger;

import android.support.v4.app.Fragment;

import apptribus.com.tribus.activities.main_activity.fragment_timeline.mvp.TimeLineModel;
import apptribus.com.tribus.activities.main_activity.fragment_timeline.mvp.TimeLinePresenter;
import apptribus.com.tribus.activities.main_activity.fragment_timeline.mvp.TimeLineView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by User on 5/27/2017.
 */

@Module
public class TimeLineFragmentModule {

    private final Fragment fragment;

    public TimeLineFragmentModule(Fragment fragment) {
        this.fragment = fragment;
    }

    @TimeLineFragmentScope
    @Provides
    public TimeLineView provideView(){
        return new TimeLineView(fragment);
    }

    @TimeLineFragmentScope
    @Provides
    public TimeLinePresenter providePresenter(TimeLineView view, TimeLineModel model){
        return new TimeLinePresenter(view, model);
    }

    @TimeLineFragmentScope
    @Provides
    public TimeLineModel provideModel(){
        return new TimeLineModel(fragment);
    }
}
