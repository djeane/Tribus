package apptribus.com.tribus.activities.send_image.dagger;

import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.send_image.mvp.SendImageModel;
import apptribus.com.tribus.activities.send_image.mvp.SendImagePresenter;
import apptribus.com.tribus.activities.send_image.mvp.SendImageView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by User on 7/12/2017.
 */
@Module
public class SendImageModule {

    private final AppCompatActivity activity;

    public SendImageModule(AppCompatActivity activity) {
        this.activity = activity;
    }

    @SendImageScope
    @Provides
    public SendImageView provideView(){
        return new SendImageView(activity);
    }

    @SendImageScope
    @Provides
    public SendImagePresenter providePresenter(SendImageView view, SendImageModel model){
        return new SendImagePresenter(view, model);
    }

    @SendImageScope
    @Provides
    public SendImageModel provideModel(){
        return new SendImageModel(activity);
    }
}
