package apptribus.com.tribus.application.dagger;

import android.content.Context;

import apptribus.com.tribus.application.dagger.AppModule;
import apptribus.com.tribus.application.dagger.AppScope;
import dagger.Component;

/**
 * Created by User on 5/14/2017.
 */

@AppScope
@Component(modules = {AppModule.class})
public interface AppComponent {

    Context context();

}
