package apptribus.com.tribus.activities.invitation_request_tribu.dagger;

import apptribus.com.tribus.activities.invitation_request_tribu.InvitationRequestTribuActivity;
import apptribus.com.tribus.application.dagger.AppComponent;
import dagger.Component;

/**
 * Created by User on 8/20/2017.
 */
@InvitationRequestTribuScope
@Component(modules = {InvitationRequestTribuModule.class}, dependencies = {AppComponent.class})
public interface InvitationRequestTribuComponent {

    void inject(InvitationRequestTribuActivity activity);
}
