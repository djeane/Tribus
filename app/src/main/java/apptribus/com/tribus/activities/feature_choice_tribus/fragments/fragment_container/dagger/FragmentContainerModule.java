package apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_container.dagger;

import android.support.v4.app.Fragment;

import apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_container.mvp.FragmentContainerModel;
import apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_container.mvp.FragmentContainerPresenter;
import apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_container.mvp.FragmentContainerView;
import dagger.Module;
import dagger.Provides;


/**
 * Created by User on 5/14/2018.
 */
@Module
public class FragmentContainerModule {

    private final Fragment fragment;
    private final String mTribusKey;
    private final String mTribusFeature;

    public FragmentContainerModule(Fragment fragment, String tribusKey, String tribusFeature) {
        this.fragment = fragment;
        this.mTribusKey = tribusKey;
        this.mTribusFeature = tribusFeature;
    }

    @FragmentContainerScope
    @Provides
    public FragmentContainerView provideView(){
        return new FragmentContainerView(fragment, mTribusKey, mTribusFeature);
    }

    @FragmentContainerScope
    @Provides
    public FragmentContainerPresenter providePresenter(FragmentContainerView view, FragmentContainerModel model){
        return new FragmentContainerPresenter(view, model);
    }

    @FragmentContainerScope
    @Provides
    public FragmentContainerModel provideModel(){
        return new FragmentContainerModel(fragment);
    }

}
