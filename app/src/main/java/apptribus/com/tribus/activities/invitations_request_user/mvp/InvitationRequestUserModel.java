package apptribus.com.tribus.activities.invitations_request_user.mvp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import apptribus.com.tribus.activities.detail_talker.DetailTalkerActivity;
import apptribus.com.tribus.activities.invitations_request_user.adapter.InvitationRequestUserAdapter;
import apptribus.com.tribus.activities.invitations_request_user.repository.InvitationsRequestUserAPI;
import apptribus.com.tribus.activities.main_activity.MainActivity;
import apptribus.com.tribus.pojo.Talk;
import apptribus.com.tribus.pojo.User;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.CONTACT_ID;
import static apptribus.com.tribus.util.Constantes.TRIBU_KEY;
import static apptribus.com.tribus.util.Constantes.USER_ID;

public class InvitationRequestUserModel {

    private final AppCompatActivity activity;
    private FirebaseAuth mAuth;


    public InvitationRequestUserModel(AppCompatActivity activity) {
        this.activity = activity;
        mAuth = FirebaseAuth.getInstance();

    }


    public Observable<List<Talk>> getAllContacts(List<Talk> contacts) {
        return InvitationsRequestUserAPI.getAllContacts(contacts);
    }

    public void loadMoreContacts(List<Talk> contacts, InvitationRequestUserAdapter invitationRequestUserAdapter,
                                 ProgressBar mProgressBarBottom) {
        InvitationsRequestUserAPI.loadMoreContacts(contacts, invitationRequestUserAdapter, mProgressBarBottom);
    }


    public void openDialogToCancelInvitation(User userContact) {
        InvitationsRequestUserAPI.openDialogToCancelInvitation(userContact, activity);
    }

    public void openDetailContactActivity(String userContactId, String tribuKey) {
        Intent intent = new Intent(activity, DetailTalkerActivity.class);
        intent.putExtra(CONTACT_ID, userContactId);
        intent.putExtra(TRIBU_KEY, tribuKey);
        intent.putExtra(USER_ID, mAuth.getCurrentUser().getUid());
        activity.startActivity(intent);
    }


    public void backToMainActivity(String fromNotification) {
        if (fromNotification != null) {
            Intent intent = new Intent(activity, MainActivity.class);
            activity.startActivity(intent);
            activity.finish();

        } else {
            activity.finish();
        }

    }


}
