package apptribus.com.tribus.activities.detail_add_talker.mvp;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import apptribus.com.tribus.activities.detail_add_talker.adapter.DetailAddTalkerAdapter;
import apptribus.com.tribus.activities.main_activity.MainActivity;
import apptribus.com.tribus.pojo.Talk;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.PresenceSystemAndLastSeen;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static android.view.View.VISIBLE;


public class DetailAddTalkerPresenter implements DetailAddTalkerAdapter.DetailAddContactAdapterListener {

    private final DetailAddTalkerView view;
    private final DetailAddTalkerModel model;
    private CompositeSubscription subscription = new CompositeSubscription();
    public static boolean isOpen;

    private List<Talk> mListContacts;
    private DetailAddTalkerAdapter mDetailAddTalkerAdapter;



    public DetailAddTalkerPresenter(DetailAddTalkerView view, DetailAddTalkerModel model) {
        this.view = view;
        this.model = model;
    }

    public void onStart(){

        PresenceSystemAndLastSeen.presenceSystem();

        if (mListContacts == null) {
            mListContacts = new ArrayList<>();
        }

        subscription.add(observeBtnArrowBack());
        subscription.add(getAllContacts());

        loadMore();


        isOpen = true;


        //to cancel all notifications if user opens it from app icon
        NotificationManager notificationManager = (NotificationManager) view.mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.cancel(1);

    }

    public void onPause(){
        PresenceSystemAndLastSeen.lastSeen();
    }

    public void onRestart(){
        PresenceSystemAndLastSeen.presenceSystem();
    }

    public void onResume(){
        PresenceSystemAndLastSeen.presenceSystem();
    }

    public void onStop() {

        isOpen = false;
    }


    private Subscription getAllContacts() {
        mListContacts.clear();
        return model.getAllContacts(mListContacts)
                .subscribe(contacts -> {
                            if (contacts == null || contacts.isEmpty()) {
                                view.mCoordinatorRecycler.setVisibility(View.GONE);
                                view.mTvNoContacts.setVisibility(View.VISIBLE);

                            } else {
                                view.mCoordinatorRecycler.setVisibility(VISIBLE);
                                view.mTvNoContacts.setVisibility(View.GONE);
                                mListContacts = contacts;

                                mDetailAddTalkerAdapter = new DetailAddTalkerAdapter(view.mContext, mListContacts, view, this);
                                view.mRvTalkersWaiting.setAdapter(mDetailAddTalkerAdapter);
                                view.mProgressBar.setVisibility(View.GONE);
                                view.mProgressBarBottom.setVisibility(View.GONE);
                            }

                        },
                        Throwable::printStackTrace);

    }

    private void loadMore() {
        view.mRvTalkersWaiting.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                if (reachedBottom) {
                    view.mProgressBarBottom.bringToFront();
                    view.mProgressBarBottom.setVisibility(VISIBLE);

                    model.loadMoreContacts(mListContacts, mDetailAddTalkerAdapter, view.mProgressBarBottom);
                }
            }
        });

    }




    private Subscription observeBtnArrowBack(){
        return view.observableBtnArrowBack()
                .subscribe(__ -> {
                            if (view.fromNotification != null) {
                                Intent intent = new Intent(view.mContext, MainActivity.class);
                                view.mContext.startActivity(intent);
                                view.mContext.finish();

                            } else {
                                model.backToMainActivity();
                            }

                        },
                        Throwable::printStackTrace
                );
    }

    public void onDestroy(){
        subscription.clear();

    }

    @Override
    public void btnAcceptContactOnClickListener(Talk contact, User userContact) {
        model.showDialogToAcceptContact(contact, userContact);
    }

    @Override
    public void btnDenyInvitationOnClickListener(Talk contact, User userContact) {
        model.showDialogToDenyInvitation(contact, userContact);
    }

    @Override
    public void openUserProfileOnClickListener(String contactId, String tribuKey) {
        model.openDetailContactActivity(contactId, tribuKey);
    }
}
