package apptribus.com.tribus.activities.change_admin.dagger;

import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.change_admin.mvp.ChangeAdminModel;
import apptribus.com.tribus.activities.change_admin.mvp.ChangeAdminPresenter;
import apptribus.com.tribus.activities.change_admin.mvp.ChangeAdminView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by User on 7/12/2017.
 */
@Module
public class ChangeAdminModule {

    private final AppCompatActivity activity;

    public ChangeAdminModule(AppCompatActivity activity) {
        this.activity = activity;
    }

    @ChangeAdminScope
    @Provides
    public ChangeAdminView provideView(){
        return new ChangeAdminView(activity);
    }

    @ChangeAdminScope
    @Provides
    public ChangeAdminPresenter providePresenter(ChangeAdminView view, ChangeAdminModel model){
        return new ChangeAdminPresenter(view, model);
    }

    @ChangeAdminScope
    @Provides
    public ChangeAdminModel provideModel(){
        return new ChangeAdminModel(activity);
    }

}
