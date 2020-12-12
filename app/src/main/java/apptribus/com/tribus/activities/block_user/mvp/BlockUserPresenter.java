package apptribus.com.tribus.activities.block_user.mvp;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import apptribus.com.tribus.activities.block_user.adapter.BlockUserAdapter;
import apptribus.com.tribus.pojo.Talk;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.PresenceSystemAndLastSeen;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static android.view.View.VISIBLE;


public class BlockUserPresenter implements BlockUserAdapter.OnBlockUserAdapterListener {
    private final BlockUserView view;

    private final BlockUserModel model;
    private CompositeSubscription subscription = new CompositeSubscription();
    public static boolean isOpen;
    private List<Talk> mListContacts;
    private BlockUserAdapter mBlockUserAdapter;


    public BlockUserPresenter(BlockUserView view, BlockUserModel model) {
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

    public void onResume() {

        PresenceSystemAndLastSeen.presenceSystem();


    }

    public void onPause() {

        PresenceSystemAndLastSeen.lastSeen();

    }


    public void onStop() {
        isOpen = false;
    }

    public void onRestart() {
        PresenceSystemAndLastSeen.presenceSystem();
    }


    private Subscription getAllContacts() {
        mListContacts.clear();
        return model.getAllContacts(mListContacts)
                .subscribe(followers -> {
                            if (followers == null || followers.isEmpty()) {
                                view.mCoordinatorRecycler.setVisibility(View.GONE);
                                view.mTvNoContacts.setVisibility(View.VISIBLE);

                            } else {
                                view.mCoordinatorRecycler.setVisibility(VISIBLE);
                                view.mTvNoContacts.setVisibility(View.GONE);
                                mListContacts = followers;

                                mBlockUserAdapter = new BlockUserAdapter(view.mContext, mListContacts, view, this);
                                view.mRvBlockTalkers.setAdapter(mBlockUserAdapter);
                                view.mProgressBar.setVisibility(View.GONE);
                                view.mProgressBarBottom.setVisibility(View.GONE);
                            }

                        },
                        Throwable::printStackTrace);

    }

    private void loadMore() {
        view.mRvBlockTalkers.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                if (reachedBottom) {
                    view.mProgressBarBottom.bringToFront();
                    view.mProgressBarBottom.setVisibility(VISIBLE);

                    model.loadMoreContacts(mListContacts, mBlockUserAdapter, view.mProgressBarBottom);
                }
            }
        });

    }


    private Subscription observeBtnArrowBack() {
        return view.observableBtnArrowBack()
                .subscribe(
                        __ -> model.backToMainActivity(),
                        Throwable::printStackTrace
                );
    }


    public void onDestroy() {
        subscription.clear();
    }

    @Override
    public void btnRemoveContactOnClickListener(User contact) {
        model.showDialogToRemoveContact(contact);
    }

    @Override
    public void openContactProfiletOnClickListener(Talk contact) {
        model.openDetailContactActivity(contact.getTalkerId(), contact.getTribuKey());
    }
}
