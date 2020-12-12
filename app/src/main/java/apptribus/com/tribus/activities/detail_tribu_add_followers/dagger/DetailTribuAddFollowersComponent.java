package apptribus.com.tribus.activities.detail_tribu_add_followers.dagger;

import apptribus.com.tribus.activities.detail_tribu_add_followers.DetailTribuAddFollowersActivity;
import apptribus.com.tribus.application.dagger.AppComponent;
import dagger.Component;

/**
 * Created by User on 6/18/2017.
 */

@DetailTribuAddFollowersScope
@Component(modules = {DetailTribuAddFollowersModule.class}, dependencies = AppComponent.class)
public interface DetailTribuAddFollowersComponent {

    void inject(DetailTribuAddFollowersActivity activity);
}

