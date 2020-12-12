package apptribus.com.tribus.activities.detail_tribu_add_followers.mvp;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import apptribus.com.tribus.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.*;

@SuppressLint("ViewConstructor")
public class DetailTribuAddFollowersView extends RelativeLayout {

    @BindView(R.id.relative_detail_tribu_add_follower)
    RelativeLayout mRelativeDetailTribuAddFollower;

    @BindView(R.id.coordinator_recycler)
    CoordinatorLayout mCoordinatorRecycler;

    @BindView(R.id.toolbar_detail_tribu_add_followers)
    Toolbar mToolbarAddFollowers;

    @BindView(R.id.rv_users_waiting_permission)
    RecyclerView mRvUserWaiting;

    @BindView(R.id.arrow_back)
    ImageView mBtnArrowBack;

    @BindView(R.id.progressbar)
    ProgressBar mProgressBar;

    @BindView(R.id.progressbar_bottom)
    ProgressBar mProgressBarBottom;

    @BindView(R.id.tv_no_followers)
    TextView mTvNoFollowers;

    public String mTribuKey;
    public LinearLayoutManager mLayoutManagerUserWaiting;

    public String fromNotification;

    public AppCompatActivity mContext;

    public DetailTribuAddFollowersView(@NonNull AppCompatActivity activity) {
        super(activity);
        mContext = activity;

        inflate(activity, R.layout.activity_detail_tribu_add_followers, this);

        ButterKnife.bind(this);

        initIntents();

        initViews();

    }

    private void initIntents() {
        mTribuKey = mContext.getIntent().getExtras().getString(TRIBU_KEY);

        fromNotification = mContext.getIntent().getStringExtra(FROM_NOTIFICATION);

    }

    private void initViews() {
        mLayoutManagerUserWaiting = new LinearLayoutManager(mContext);
        mLayoutManagerUserWaiting.setOrientation(LinearLayoutManager.VERTICAL);

        mRvUserWaiting.setLayoutManager(mLayoutManagerUserWaiting);

    }

    public Observable<Void> observableBtnArrowBack() {
        return RxView.clicks(mBtnArrowBack);
    }
}
