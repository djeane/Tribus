package apptribus.com.tribus.activities.detail_add_talker.mvp;

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

import static apptribus.com.tribus.util.Constantes.FROM_NOTIFICATION;

@SuppressLint("ViewConstructor")
public class DetailAddTalkerView extends RelativeLayout{

    @BindView(R.id.relative_detail_add_talker)
    RelativeLayout mRelativeDetailAddTalker;

    @BindView(R.id.coordinator_recycler)
    CoordinatorLayout mCoordinatorRecycler;

    @BindView(R.id.toolbar_talkers_waiting_permission)
    Toolbar mToolbarAddTalkers;

    @BindView(R.id.rv_talkers_waiting_permission)
    RecyclerView mRvTalkersWaiting;

    @BindView(R.id.arrow_back)
    ImageView mBtnArrowBack;

    @BindView(R.id.progressbar)
    ProgressBar mProgressBar;

    @BindView(R.id.progressbar_bottom)
    ProgressBar mProgressBarBottom;

    @BindView(R.id.tv_no_contacts)
    TextView mTvNoContacts;

    public LinearLayoutManager mLayoutManagerTalkersWaiting;
    public AppCompatActivity mContext;

    public String fromNotification;

    public DetailAddTalkerView(@NonNull AppCompatActivity activity) {
        super(activity);
        mContext = activity;

        inflate(activity, R.layout.activity_detail_add_talker, this);
        ButterKnife.bind(this);

        initIntents();
        initViews();

    }

    private void initIntents(){
        fromNotification = mContext.getIntent().getStringExtra(FROM_NOTIFICATION);

    }
    private void initViews(){
        mLayoutManagerTalkersWaiting = new LinearLayoutManager(mContext);
        mLayoutManagerTalkersWaiting.setOrientation(LinearLayoutManager.VERTICAL);

        mRvTalkersWaiting.setLayoutManager(mLayoutManagerTalkersWaiting);

    }

    public Observable<Void> observableBtnArrowBack(){
        return RxView.clicks(mBtnArrowBack);
    }
}
