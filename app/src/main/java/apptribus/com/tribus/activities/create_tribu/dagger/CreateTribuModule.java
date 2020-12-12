package apptribus.com.tribus.activities.create_tribu.dagger;

import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.create_tribu.mvp.CreateTribuModel;
import apptribus.com.tribus.activities.create_tribu.mvp.CreateTribuPresenter;
import apptribus.com.tribus.activities.create_tribu.mvp.CreateTribuView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by User on 5/29/2017.
 */

@Module
public class CreateTribuModule {

    private final AppCompatActivity activity;

    public CreateTribuModule(AppCompatActivity activity) {
        this.activity = activity;
    }


    @CreateTribuScope
    @Provides
    public CreateTribuView provideView(){
        return new CreateTribuView(activity);
    }


    @CreateTribuScope
    @Provides
    public CreateTribuPresenter providePresenter(CreateTribuView view, CreateTribuModel model){
        return new CreateTribuPresenter(view, model);
    }


    @CreateTribuScope
    @Provides
    public CreateTribuModel provideModel(){
        return new CreateTribuModel(activity);
    }
}
