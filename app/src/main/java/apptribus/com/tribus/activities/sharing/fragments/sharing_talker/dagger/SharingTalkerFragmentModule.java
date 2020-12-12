package apptribus.com.tribus.activities.sharing.fragments.sharing_talker.dagger;

import android.support.v4.app.Fragment;

import apptribus.com.tribus.activities.sharing.fragments.sharing_talker.mvp.SharingTalkerFragmentModel;
import apptribus.com.tribus.activities.sharing.fragments.sharing_talker.mvp.SharingTalkerFragmentPresenter;
import apptribus.com.tribus.activities.sharing.fragments.sharing_talker.mvp.SharingTalkerFragmentView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by User on 1/15/2018.
 */
@Module
public class SharingTalkerFragmentModule {

    private final Fragment fragment;

    public SharingTalkerFragmentModule(Fragment fragment) {
        this.fragment = fragment;
    }

    @SharingTalkerFragmentScope
    @Provides
    public SharingTalkerFragmentView provideView(){
        return new SharingTalkerFragmentView(fragment);
    }

    @SharingTalkerFragmentScope
    @Provides
    public SharingTalkerFragmentPresenter providePresenter(SharingTalkerFragmentView view, SharingTalkerFragmentModel model){
        return new SharingTalkerFragmentPresenter(view, model);
    }

    @SharingTalkerFragmentScope
    @Provides
    public SharingTalkerFragmentModel provideModel(){
        return new SharingTalkerFragmentModel(fragment);
    }
}
