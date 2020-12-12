package apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_container.dagger;

import apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_container.FragmentContainer;
import apptribus.com.tribus.application.dagger.AppComponent;
import dagger.Component;

/**
 * Created by User on 5/14/2018.
 */
@FragmentContainerScope
@Component(modules = {FragmentContainerModule.class}, dependencies = {AppComponent.class})
public interface FragmentContainerComponent {

    void inject(FragmentContainer fragment);
}
