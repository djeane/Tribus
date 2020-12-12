package apptribus.com.tribus.activities.invitations_request_user.mvp;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import apptribus.com.tribus.activities.invitations_request_user.adapter.InvitationRequestUserAdapter;
import apptribus.com.tribus.activities.main_activity.MainActivity;
import apptribus.com.tribus.pojo.Talk;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.PresenceSystemAndLastSeen;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static android.view.View.VISIBLE;

public class InvitationRequestUserPresenter implements InvitationRequestUserAdapter.InvitationRequestUserListener {

    private final InvitationRequestUserView view;
    private final InvitationRequestUserModel model;
    private CompositeSubscription subscription = new CompositeSubscription();
    public static boolean isOpen;

    private List<Talk> mListContacts;
    private InvitationRequestUserAdapter mInvitationRequestUserAdapter;


    public InvitationRequestUserPresenter(InvitationRequestUserView view, InvitationRequestUserModel model) {
        this.view = view;
        this.model = model;
    }


    public void onStart() {

        PresenceSystemAndLastSeen.presenceSystem();
        if (mListContacts == null) {
            mListContacts = new ArrayList<>();
        }

        subscription.add(observeBtnArrowBack());
        subscription.add(getAllContacts());

        loadMore();

        isOpen = true;
    }

    public void onPause() {
        PresenceSystemAndLastSeen.lastSeen();

    }

    public void onRestart() {
        PresenceSystemAndLastSeen.presenceSystem();
    }

    public void onResume() {
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

                                mInvitationRequestUserAdapter = new InvitationRequestUserAdapter(view.mContext,
                                        mListContacts, view, this);
                                view.mRvInvitationRequestUser.setAdapter(mInvitationRequestUserAdapter);
                                view.mProgressBar.setVisibility(View.GONE);
                                view.mProgressBarBottom.setVisibility(View.GONE);
                            }

                        },
                        Throwable::printStackTrace);

    }

    private void loadMore() {
        view.mRvInvitationRequestUser.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                if (reachedBottom) {
                    view.mProgressBarBottom.bringToFront();
                    view.mProgressBarBottom.setVisibility(VISIBLE);

                    model.loadMoreContacts(mListContacts, mInvitationRequestUserAdapter, view.mProgressBarBottom);
                }
            }
        });

    }


    private Subscription observeBtnArrowBack() {
        return view.observableBtnArrowBack()
                .subscribe(__ -> {
                            if (view.fromNotification != null) {
                                Intent intent = new Intent(view.mContext, MainActivity.class);
                                view.mContext.startActivity(intent);
                                view.mContext.finish();

                            } else {
                                view.mContext.finish();
                            }

                        },
                        Throwable::printStackTrace
                );
    }

    public void backToMainActivity() {
        model.backToMainActivity(view.fromNotification);
    }


    public void onDestroy() {
        subscription.clear();
    }

    @Override
    public void btnCancelInvitation(User userContactId) {
        model.openDialogToCancelInvitation(userContactId);
    }

    @Override
    public void openUserProfile(String userContactId, String tribuKey) {
        model.openDetailContactActivity(userContactId, tribuKey);
    }
}
