package apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_feature.dagger;

import apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_feature.FragmentFeature;
import apptribus.com.tribus.application.dagger.AppComponent;
import dagger.Component;

/**
 * Created by User on 1/18/2018.
 */
@FragmentFeatureScope
@Component(modules = {FragmentFeatureModule.class}, dependencies = {AppComponent.class})
public interface FragmentFeatureComponent {

    void inject(FragmentFeature fragment);
}
