package apptribus.com.tribus.activities.comments.dagger;

import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.comments.mvp.CommentsModel;
import apptribus.com.tribus.activities.comments.mvp.CommentsPresenter;
import apptribus.com.tribus.activities.comments.mvp.CommentsView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by User on 7/24/2017.
 */
@Module
public class CommentsModule {

    private final AppCompatActivity activity;

    public CommentsModule(AppCompatActivity activity) {
        this.activity = activity;
    }

    @CommentsScope
    @Provides
    public CommentsView provideView(){
        return new CommentsView(activity);
    }

    @CommentsScope
    @Provides
    public CommentsPresenter providePresenter(CommentsView view, CommentsModel model){
        return new CommentsPresenter(view, model);
    }

    @CommentsScope
    @Provides
    public CommentsModel provideModel(){
        return new CommentsModel(activity);
    }
}
