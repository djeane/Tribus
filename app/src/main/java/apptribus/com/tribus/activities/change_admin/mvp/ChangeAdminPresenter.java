package apptribus.com.tribus.activities.change_admin.mvp;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import apptribus.com.tribus.activities.change_admin.adapter.ChangeAdminAdapter;
import apptribus.com.tribus.pojo.Follower;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.PresenceSystemAndLastSeen;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static android.view.View.VISIBLE;

public class ChangeAdminPresenter implements ChangeAdminAdapter.ChangeAdminOnClickListener {

    private final ChangeAdminView view;
    private final ChangeAdminModel model;
    private CompositeSubscription subscription = new CompositeSubscription();
    public static boolean isOpen;


    private List<Follower> mListFollowers;
    private ChangeAdminAdapter mChangeAdminAdapter;

    public ChangeAdminPresenter(ChangeAdminView view, ChangeAdminModel model) {
        this.view = view;
        this.model = model;
    }


    public void onStart() {

        PresenceSystemAndLastSeen.presenceSystem();

        if (mListFollowers == null) {
            mListFollowers = new ArrayList<>();
        }

        subscription.add(getAllFollowers());
        subscription.add(observeBtnArrowBack());

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

    private Subscription getAllFollowers() {
        mListFollowers.clear();
        return model.getAllFollowers(mListFollowers, view.mTribuKey)
                .subscribe(followers -> {
                            if (followers == null || followers.isEmpty()) {
                                view.mCoordinatorRecycler.setVisibility(View.GONE);
                                view.mTvNoFollowers.setVisibility(View.VISIBLE);
                                view.mTvInfo.setVisibility(View.GONE);

                            } else {
                                view.mCoordinatorRecycler.setVisibility(VISIBLE);
                                view.mTvNoFollowers.setVisibility(View.GONE);
                                view.mTvInfo.setVisibility(View.VISIBLE);
                                mListFollowers = followers;

                                mChangeAdminAdapter = new ChangeAdminAdapter(view.mContext, mListFollowers, view, this);
                                view.mRvChangeAdmin.setAdapter(mChangeAdminAdapter);
                                view.mProgressBar.setVisibility(View.GONE);
                                view.mProgressBarBottom.setVisibility(View.GONE);
                            }

                        },
                        Throwable::printStackTrace);

    }

    private void loadMore() {
        view.mRvChangeAdmin.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                if (reachedBottom) {
                    view.mProgressBarBottom.bringToFront();
                    view.mProgressBarBottom.setVisibility(VISIBLE);

                    model.loadMoreFollowers(mListFollowers, mChangeAdminAdapter, view.mProgressBarBottom, view.mTribuKey);
                }
            }
        });

    }


    private Subscription observeBtnArrowBack() {
        return view.observableBtnArrowBack()
                .subscribe(__ -> {
                            model.backToChatTribuActivity();
                        },
                        Throwable::printStackTrace
                );
    }

    public void backToChatActivity() {
        model.backToChatTribuActivity();
    }

    public void onDestroy() {
        subscription.clear();
    }

    @Override
    public void bntChangeAdminOnClick(User follower) {
        model.openDialogToChangeAdmin(follower, view.mTribuKey);
    }

    @Override
    public void openProfileActivityOnClick(String userFollowerId) {
        model.openDetailActivity(userFollowerId, view.mTribuKey);
    }
}
