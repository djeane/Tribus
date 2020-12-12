package apptribus.com.tribus.activities.invitations_request_user.dagger;

import apptribus.com.tribus.activities.invitations_request_user.InvitationRequestUserActivity;
import apptribus.com.tribus.application.dagger.AppComponent;
import dagger.Component;

/**
 * Created by User on 7/1/2017.
 */

@InvitationRequestUserScope
@Component(modules = {InvitationRequestUserModule.class}, dependencies = {AppComponent.class})
public interface InvitationRequestUserComponent {

    void inject(InvitationRequestUserActivity activity);
}
