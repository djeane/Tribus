package apptribus.com.tribus.activities.invitation_request_tribu.mvp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;

import java.util.List;

import apptribus.com.tribus.activities.invitation_request_tribu.adapter.InvitationRequestTribuAdapter;
import apptribus.com.tribus.activities.invitation_request_tribu.repository.InvitationRequestTribuAPI;
import apptribus.com.tribus.activities.main_activity.MainActivity;
import apptribus.com.tribus.activities.profile_tribu_user.ProfileTribuUserActivity;
import apptribus.com.tribus.pojo.RequestTribu;
import apptribus.com.tribus.pojo.Tribu;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.TRIBU_KEY;

public class InvitationRequestTribuModel {

    private final AppCompatActivity activity;

    public InvitationRequestTribuModel(AppCompatActivity activity) {
        this.activity = activity;
    }


    public Observable<List<RequestTribu>> getAllRequestTribu(List<RequestTribu> requests) {
        return InvitationRequestTribuAPI.getAllRequestTribu(requests);
    }

    public void loadMoreRequestTribu(List<RequestTribu> requests, InvitationRequestTribuAdapter invitationRequestTribuAdapter,
                                     ProgressBar mProgressBarBottom) {
        InvitationRequestTribuAPI.loadMoreRequestTribu(requests, invitationRequestTribuAdapter, mProgressBarBottom);
    }


    public void openDialogToCancelRequestToTribu(Tribu tribu) {

        InvitationRequestTribuAPI.openDialogToCancelRequestToTribu(tribu, activity);
    }

    public void openProfileTribuUser(String tribuKey) {
        Intent intent = new Intent(activity, ProfileTribuUserActivity.class);
        intent.putExtra(TRIBU_KEY, tribuKey);
        activity.startActivity(intent);
    }


    public void backMainActivity(String fromNotification) {
        if (fromNotification != null) {
            Intent intent = new Intent(activity, MainActivity.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(intent);
            activity.finish();

        } else {
            activity.finish();
        }

    }

}
