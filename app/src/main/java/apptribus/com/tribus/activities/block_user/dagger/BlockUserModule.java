package apptribus.com.tribus.activities.block_user.dagger;

import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.block_user.mvp.BlockUserModel;
import apptribus.com.tribus.activities.block_user.mvp.BlockUserPresenter;
import apptribus.com.tribus.activities.block_user.mvp.BlockUserView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by User on 7/4/2017.
 */
@Module
public class BlockUserModule {

    private final AppCompatActivity activity;

    public BlockUserModule(AppCompatActivity activity) {
        this.activity = activity;
    }


    @BlockUserScope
    @Provides
    public BlockUserView provideView(){
        return new BlockUserView(activity);
    }

    @BlockUserScope
    @Provides
    public BlockUserPresenter providePresenter(BlockUserView view, BlockUserModel model){
        return new BlockUserPresenter(view, model);
    }

    @BlockUserScope
    @Provides
    public BlockUserModel provideModel(){
        return new BlockUserModel(activity);
    }
}
