package apptribus.com.tribus.activities.detail_tribu_add_followers.mvp;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import apptribus.com.tribus.activities.detail_tribu_add_followers.adapter.DetailTribuAddFollowersAdapter;
import apptribus.com.tribus.activities.main_activity.MainActivity;
import apptribus.com.tribus.pojo.Follower;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.PresenceSystemAndLastSeen;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static android.view.View.VISIBLE;

public class DetailTribuAddFollowersPresenter implements DetailTribuAddFollowersAdapter.DetailTribuAddFollowersAdapterListener {

    private final DetailTribuAddFollowersView view;
    private final DetailTribuAddFollowersModel model;
    private CompositeSubscription subscription = new CompositeSubscription();
    public static boolean isOpen;

    private List<Follower> mListFollowers;
    private DetailTribuAddFollowersAdapter mDetailTribuAddFollowersAdapter;


    public DetailTribuAddFollowersPresenter(DetailTribuAddFollowersView view, DetailTribuAddFollowersModel model) {
        this.view = view;
        this.model = model;
    }


    public void onStart() {

        PresenceSystemAndLastSeen.presenceSystem();

        if (mListFollowers == null) {
            mListFollowers = new ArrayList<>();
        }

        subscription.add(observeBtnArrowBack());
        subscription.add(getAllFollowers());

        loadMore();

        isOpen = true;
        NotificationManager notificationManager = (NotificationManager) view.mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(3);

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


    private Subscription getAllFollowers() {
        mListFollowers.clear();
        return model.getAllFollowers(mListFollowers, view.mTribuKey)
                .subscribe(contacts -> {
                            if (contacts == null || contacts.isEmpty()) {
                                view.mCoordinatorRecycler.setVisibility(View.GONE);
                                view.mTvNoFollowers.setVisibility(View.VISIBLE);

                            } else {
                                view.mCoordinatorRecycler.setVisibility(VISIBLE);
                                view.mTvNoFollowers.setVisibility(View.GONE);
                                mListFollowers = contacts;

                                mDetailTribuAddFollowersAdapter = new DetailTribuAddFollowersAdapter(view.mContext,
                                        mListFollowers, view, this, view.mTribuKey);

                                view.mRvUserWaiting.setAdapter(mDetailTribuAddFollowersAdapter);
                                view.mProgressBar.setVisibility(View.GONE);
                                view.mProgressBarBottom.setVisibility(View.GONE);
                            }

                        },
                        Throwable::printStackTrace);

    }

    private void loadMore() {
        view.mRvUserWaiting.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                if (reachedBottom) {
                    view.mProgressBarBottom.bringToFront();
                    view.mProgressBarBottom.setVisibility(VISIBLE);

                    model.loadMoreFollowers(mListFollowers, mDetailTribuAddFollowersAdapter, view.mProgressBarBottom, view.mTribuKey);
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
                                model.backToMainActivity();
                            }

                        },
                        Throwable::printStackTrace
                );

    }


    public void onDestroy() {
        subscription.clear();
    }

    @Override
    public void btnAcceptFollowerOnClickListener(Follower follower, User userFollower) {
        model.showDialogToAccept(follower, userFollower, view.mTribuKey);
    }

    @Override
    public void btnDenyInvitationOnClickListener(Follower follower, User userFollower) {
        model.showDialogToDeny(follower, userFollower, view.mTribuKey);
    }

    @Override
    public void openUserProfileOnClickListener(Follower follower, String tribuKey) {
        model.openDetailFollowerActivity(follower.getUidFollower(), view.mTribuKey);
    }
}
