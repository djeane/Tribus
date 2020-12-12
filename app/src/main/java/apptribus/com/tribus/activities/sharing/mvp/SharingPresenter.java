package apptribus.com.tribus.activities.sharing.mvp;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.sharing.view_holder.SharingTalkersViewHolder;
import apptribus.com.tribus.activities.sharing.view_holder.SharingTribusViewHolder;
import apptribus.com.tribus.pojo.Talk;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.PresenceSystemAndLastSeen;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static apptribus.com.tribus.util.Constantes.BLOCKED_TALKERS;
import static apptribus.com.tribus.util.Constantes.FOLLOWERS;
import static apptribus.com.tribus.util.Constantes.FOLLOWER_ID;
import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.USERS_TALKS;

/**
 * Created by User on 12/12/2017.
 */

public class SharingPresenter {

    private final SharingView view;
    private final SharingModel model;
    private CompositeSubscription subscription = new CompositeSubscription();
    public static boolean isOpen;

    public SharingPresenter(SharingView view, SharingModel model) {
        this.view = view;
        this.model = model;
    }

    public void onStart(){

        PresenceSystemAndLastSeen.presenceSystem();

        subscription.add(observeBtnArrowBack());

        isOpen = true;

    }

    public void onResume(){
        PresenceSystemAndLastSeen.presenceSystem();
    }

    public void onPause(){
        PresenceSystemAndLastSeen.lastSeen();

    }

    public void onRestart(){
        PresenceSystemAndLastSeen.presenceSystem();
    }

    public void onStop(){

        isOpen = false;

    }


    private Subscription observeBtnArrowBack() {
        return view.observableBtnArrowBack()
                .subscribe(
                        __ -> model.backToMainActivity(),
                        Throwable::printStackTrace
                );
    }


    public void onDestroy(){

            subscription.clear();

    }
}
