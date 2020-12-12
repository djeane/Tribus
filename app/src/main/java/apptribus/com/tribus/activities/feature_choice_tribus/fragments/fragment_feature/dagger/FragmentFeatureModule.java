package apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_feature.dagger;

import android.support.v4.app.Fragment;

import apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_feature.FragmentFeature;
import apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_feature.mvp.FragmentFeatureModel;
import apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_feature.mvp.FragmentFeaturePresenter;
import apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_feature.mvp.FragmentFeatureView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by User on 1/18/2018.
 */
@Module
public class FragmentFeatureModule {

    private final Fragment fragment;
    private final String mTribusKey;

    public FragmentFeatureModule(Fragment fragment, String tribusKey) {
        this.fragment = fragment;
        mTribusKey = tribusKey;
    }

    @FragmentFeatureScope
    @Provides
    public FragmentFeatureView provideView(){
        return new FragmentFeatureView(fragment, mTribusKey);
    }

    @FragmentFeatureScope
    @Provides
    public FragmentFeaturePresenter providePresenter(FragmentFeatureView view, FragmentFeatureModel model){
        return new FragmentFeaturePresenter(view, model);
    }

    @FragmentFeatureScope
    @Provides
    public FragmentFeatureModel provideModel(){
        return new FragmentFeatureModel(fragment);
    }
}
