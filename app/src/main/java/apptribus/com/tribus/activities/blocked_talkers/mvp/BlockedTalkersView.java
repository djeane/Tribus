package apptribus.com.tribus.activities.blocked_talkers.mvp;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jakewharton.rxbinding.view.RxView;

import apptribus.com.tribus.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

@SuppressLint("ViewConstructor")
public class BlockedTalkersView extends RelativeLayout {

    @BindView(R.id.relative_blocked_user)
    RelativeLayout mRelativeBlockedUser;

    @BindView(R.id.coordinator_recycler)
    CoordinatorLayout mCoordinatorRecycler;

    @BindView(R.id.toolbar_blocked_talkers)
    Toolbar mToolbarBlockTalkers;

    @BindView(R.id.rv_blocked_talkers)
    RecyclerView mRvBlockedTalkers;

    @BindView(R.id.arrow_back)
    ImageView mBtnArrowBack;

    public AppCompatActivity mContext;

    public LinearLayoutManager mLayoutManagerBlockedTalkers;


    public BlockedTalkersView(@NonNull AppCompatActivity activity) {
        super(activity);
        mContext = activity;
        inflate(activity, R.layout.activity_blocked_talkers, this);

        ButterKnife.bind(this);

        mLayoutManagerBlockedTalkers = new LinearLayoutManager(activity);
        mLayoutManagerBlockedTalkers.setOrientation(LinearLayoutManager.VERTICAL);

        mRvBlockedTalkers.setLayoutManager(mLayoutManagerBlockedTalkers);
        mRvBlockedTalkers.setHasFixedSize(true);

    }


    public Observable<Void> observableBtnArrowBack(){
        return RxView.clicks(mBtnArrowBack);
    }
}
