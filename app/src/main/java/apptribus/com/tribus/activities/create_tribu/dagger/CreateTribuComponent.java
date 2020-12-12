package apptribus.com.tribus.activities.create_tribu.dagger;

import apptribus.com.tribus.activities.create_tribu.CreateTribuActivity;
import apptribus.com.tribus.application.dagger.AppComponent;
import dagger.Component;

/**
 * Created by User on 5/29/2017.
 */

@CreateTribuScope
@Component(modules = {CreateTribuModule.class}, dependencies = AppComponent.class)
public interface CreateTribuComponent {

    void inject(CreateTribuActivity createTribuActivity);
}
