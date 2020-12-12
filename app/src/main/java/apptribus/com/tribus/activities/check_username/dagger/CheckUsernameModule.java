package apptribus.com.tribus.activities.check_username.dagger;

import apptribus.com.tribus.activities.check_username.NewCheckUsernameActivity;
import apptribus.com.tribus.activities.check_username.mvp.CheckUsernameModel;
import apptribus.com.tribus.activities.check_username.mvp.CheckUsernamePresenter;
import apptribus.com.tribus.activities.check_username.mvp.CheckUsernameView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by User on 5/14/2017.
 */

@Module
public class CheckUsernameModule {

    private final NewCheckUsernameActivity activity;

    public CheckUsernameModule(NewCheckUsernameActivity activity) {
        this.activity = activity;
    }

    @CheckUsernameScope
    @Provides
    public CheckUsernameView view(){
        return new CheckUsernameView(activity);
    }

    @CheckUsernameScope
    @Provides
    public CheckUsernamePresenter presenter(CheckUsernameView view, CheckUsernameModel model){
        return new CheckUsernamePresenter(view, model);
    }

    @CheckUsernameScope
    @Provides
    public CheckUsernameModel model(){
        return new CheckUsernameModel(activity);
    }




}
