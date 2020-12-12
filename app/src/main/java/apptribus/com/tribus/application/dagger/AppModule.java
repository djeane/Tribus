package apptribus.com.tribus.application.dagger;

import android.app.Application;
import android.content.Context;

import apptribus.com.tribus.pojo.User;
import dagger.Module;
import dagger.Provides;

/**
 * Created by User on 5/14/2017.
 */

@Module
public class AppModule {
    private final Context context;

    public AppModule(Application application){
        this.context = application.getApplicationContext();
    }


    @AppScope
    @Provides
    public Context provideContext(){
        return context;
    }
}
