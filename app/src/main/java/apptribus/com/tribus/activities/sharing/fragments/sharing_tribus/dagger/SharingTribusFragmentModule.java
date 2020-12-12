package apptribus.com.tribus.activities.sharing.fragments.sharing_tribus.dagger;

import android.support.v4.app.Fragment;

import apptribus.com.tribus.activities.sharing.fragments.sharing_tribus.mvp.SharingTribusFragmentModel;
import apptribus.com.tribus.activities.sharing.fragments.sharing_tribus.mvp.SharingTribusFragmentPresenter;
import apptribus.com.tribus.activities.sharing.fragments.sharing_tribus.mvp.SharingTribusFragmentView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by User on 1/15/2018.
 */
@Module
public class SharingTribusFragmentModule {

    private final Fragment fragment;

    public SharingTribusFragmentModule(Fragment fragment) {
        this.fragment = fragment;
    }

    @SharingTribusFragmentScope
    @Provides
    public SharingTribusFragmentView provideView(){
        return new SharingTribusFragmentView(fragment);
    }

    @SharingTribusFragmentScope
    @Provides
    public SharingTribusFragmentPresenter providePresenter(SharingTribusFragmentView view, SharingTribusFragmentModel model){
        return new SharingTribusFragmentPresenter(view, model);
    }

    @SharingTribusFragmentScope
    @Provides
    public SharingTribusFragmentModel provideModel(){
        return new SharingTribusFragmentModel(fragment);
    }
}
