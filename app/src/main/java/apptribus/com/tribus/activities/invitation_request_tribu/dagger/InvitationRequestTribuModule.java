package apptribus.com.tribus.activities.invitation_request_tribu.dagger;

import android.support.v7.app.AppCompatActivity;

import javax.inject.Scope;

import apptribus.com.tribus.activities.invitation_request_tribu.mvp.InvitationRequestTribuModel;
import apptribus.com.tribus.activities.invitation_request_tribu.mvp.InvitationRequestTribuPresenter;
import apptribus.com.tribus.activities.invitation_request_tribu.mvp.InvitationRequestTribuView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by User on 8/20/2017.
 */
@Module
public class InvitationRequestTribuModule {

    private final AppCompatActivity activity;

    public InvitationRequestTribuModule(AppCompatActivity activity) {
        this.activity = activity;
    }

    @InvitationRequestTribuScope
    @Provides
    public InvitationRequestTribuView provideView(){
        return new InvitationRequestTribuView(activity);
    }

    @InvitationRequestTribuScope
    @Provides
    public InvitationRequestTribuPresenter providePresenter(InvitationRequestTribuView view, InvitationRequestTribuModel model){
        return new InvitationRequestTribuPresenter(view, model);
    }

    @InvitationRequestTribuScope
    @Provides
    public InvitationRequestTribuModel provideModel(){
        return new InvitationRequestTribuModel(activity);
    }
}
