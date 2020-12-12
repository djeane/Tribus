package apptribus.com.tribus.activities.invitation_request_tribu.mvp;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import apptribus.com.tribus.activities.invitation_request_tribu.adapter.InvitationRequestTribuAdapter;
import apptribus.com.tribus.pojo.RequestTribu;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.util.PresenceSystemAndLastSeen;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static android.view.View.VISIBLE;

public class InvitationRequestTribuPresenter implements InvitationRequestTribuAdapter.InvitationRequestTribuListener {

    private final InvitationRequestTribuView view;
    private final InvitationRequestTribuModel model;
    private CompositeSubscription subscription = new CompositeSubscription();
    public static boolean isOpen;

    private List<RequestTribu> mListRequest;
    private InvitationRequestTribuAdapter mInvitationRequestTribuAdapter;


    public InvitationRequestTribuPresenter(InvitationRequestTribuView view, InvitationRequestTribuModel model) {
        this.view = view;
        this.model = model;
    }

    public void onStart() {

        PresenceSystemAndLastSeen.presenceSystem();

        if (mListRequest == null) {
            mListRequest = new ArrayList<>();
        }

        subscription.add(observeBtnArrowBack());
        subscription.add(getAllRequestTribu());

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


    private Subscription getAllRequestTribu() {
        mListRequest.clear();
        return model.getAllRequestTribu(mListRequest)
                .subscribe(contacts -> {
                            if (contacts == null || contacts.isEmpty()) {
                                view.mCoordinatorRecycler.setVisibility(View.GONE);
                                view.mTvNoRequest.setVisibility(View.VISIBLE);

                            } else {
                                view.mCoordinatorRecycler.setVisibility(VISIBLE);
                                view.mTvNoRequest.setVisibility(View.GONE);
                                mListRequest = contacts;

                                mInvitationRequestTribuAdapter = new InvitationRequestTribuAdapter(view.mContext, mListRequest,
                                        view, this);
                                view.mRvInvitationRequestTribu.setAdapter(mInvitationRequestTribuAdapter);
                                view.mProgressBar.setVisibility(View.GONE);
                                view.mProgressBarBottom.setVisibility(View.GONE);
                            }

                        },
                        Throwable::printStackTrace);

    }

    private void loadMore() {
        view.mRvInvitationRequestTribu.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                if (reachedBottom) {
                    view.mProgressBarBottom.bringToFront();
                    view.mProgressBarBottom.setVisibility(VISIBLE);

                    model.loadMoreRequestTribu(mListRequest, mInvitationRequestTribuAdapter, view.mProgressBarBottom);
                }
            }
        });

    }


    private Subscription observeBtnArrowBack() {
        return view.observableBtnArrowBack()
                .subscribe(
                        __ -> model.backMainActivity(view.fromNotification),
                        Throwable::printStackTrace
                );
    }

    public void backToMainActivity() {
        model.backMainActivity(view.fromNotification);
    }

    public void onDestroy() {
        subscription.clear();

    }

    @Override
    public void btnCancelRequestOnClikListener(Tribu tribu) {
        model.openDialogToCancelRequestToTribu(tribu);
    }

    @Override
    public void openProfileTribuOnClickListener(String tribuKey) {
        model.openProfileTribuUser(tribuKey);
    }
}
