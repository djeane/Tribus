package apptribus.com.tribus.activities.blocked_talkers.dagger;

import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.blocked_talkers.mvp.BlockedTalkersModel;
import apptribus.com.tribus.activities.blocked_talkers.mvp.BlockedTalkersPresenter;
import apptribus.com.tribus.activities.blocked_talkers.mvp.BlockedTalkersView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by User on 7/11/2017.
 */
@Module
public class BlockedTalkersModule {

    private final AppCompatActivity activity;

    public BlockedTalkersModule(AppCompatActivity activity) {
        this.activity = activity;
    }

    @BlockedTalkersScope
    @Provides
    public BlockedTalkersView provideView(){
        return new BlockedTalkersView(activity);
    }


    @BlockedTalkersScope
    @Provides
    public BlockedTalkersPresenter providePresenter(BlockedTalkersView view, BlockedTalkersModel model){
        return new BlockedTalkersPresenter(view, model);
    }


    @BlockedTalkersScope
    @Provides
    public BlockedTalkersModel provideModel(){
        return new BlockedTalkersModel(activity);
    }
}
