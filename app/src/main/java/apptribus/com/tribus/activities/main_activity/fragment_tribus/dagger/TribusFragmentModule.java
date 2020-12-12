package apptribus.com.tribus.activities.main_activity.fragment_tribus.dagger;

import android.support.v4.app.Fragment;

import apptribus.com.tribus.activities.main_activity.fragment_tribus.mvp.TribusFragmentModel;
import apptribus.com.tribus.activities.main_activity.fragment_tribus.mvp.TribusFragmentPresenter;
import apptribus.com.tribus.activities.main_activity.fragment_tribus.mvp.TribusFragmentView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by User on 6/7/2017.
 */

@Module
public class TribusFragmentModule {

    private final Fragment fragment;


    public TribusFragmentModule(Fragment fragment) {
        this.fragment = fragment;
    }

    @TribusFragmentScope
    @Provides
    public TribusFragmentView provideView(){
        return new TribusFragmentView(fragment);
    }

    @TribusFragmentScope
    @Provides
    public TribusFragmentPresenter prividePresenter(TribusFragmentView view, TribusFragmentModel model){
        return new TribusFragmentPresenter(view, model);
    }


    @TribusFragmentScope
    @Provides
    public TribusFragmentModel provideModel(){
        return new TribusFragmentModel(fragment);
    }

}
