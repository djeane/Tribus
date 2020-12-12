package apptribus.com.tribus.activities.invitations_request_user.dagger;

import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.invitations_request_user.mvp.InvitationRequestUserModel;
import apptribus.com.tribus.activities.invitations_request_user.mvp.InvitationRequestUserPresenter;
import apptribus.com.tribus.activities.invitations_request_user.mvp.InvitationRequestUserView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by User on 7/1/2017.
 */
@Module
public class InvitationRequestUserModule {

    private final AppCompatActivity activity;

    public InvitationRequestUserModule(AppCompatActivity activity) {
        this.activity = activity;
    }


    @InvitationRequestUserScope
    @Provides
    public InvitationRequestUserView provideView(){
        return new InvitationRequestUserView(activity);
    }


    @InvitationRequestUserScope
    @Provides
    public InvitationRequestUserPresenter providePresenter(InvitationRequestUserView view, InvitationRequestUserModel model){
        return new InvitationRequestUserPresenter(view, model);
    }


    @InvitationRequestUserScope
    @Provides
    public InvitationRequestUserModel provideModel(){
        return new InvitationRequestUserModel(activity);
    }
}
