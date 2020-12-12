package apptribus.com.tribus.activities.invitation_request_tribu.mvp;

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
public class InvitationRequestTribuView extends RelativeLayout {

    @BindView(R.id.relative_invitation_request_tribu)
    RelativeLayout mRelativeInvitationRequestTribu;

    @BindView(R.id.coordinator_recycler)
    CoordinatorLayout mCoordinatorRecycler;

    @BindView(R.id.toolbar_invitation_request_tribu)
    public Toolbar mToolbarInvitationResquestTribu;

    @BindView(R.id.arrow_back)
    public ImageView mBtnArrowBack;

    @BindView(R.id.rv_invitation_request_tribu)
    public RecyclerView mRvInvitationRequestTribu;

    @BindView(R.id.progressbar)
    ProgressBar mProgressBar;

    @BindView(R.id.progressbar_bottom)
    ProgressBar mProgressBarBottom;

    @BindView(R.id.tv_no_request)
    TextView mTvNoRequest;

    public LinearLayoutManager mLayoutManagerInvitationsRequestTribu;

    public AppCompatActivity mContext;
    public String fromNotification;


    public InvitationRequestTribuView(@NonNull AppCompatActivity activity) {
        super(activity);
        mContext = activity;

        inflate(activity, R.layout.activity_invitation_request_tribu, this);
        ButterKnife.bind(this);

        initIntents();

        initViews();

    }

    private void initIntents() {
        fromNotification = mContext.getIntent().getStringExtra(FROM_NOTIFICATION);
    }

    private void initViews() {

        mLayoutManagerInvitationsRequestTribu = new LinearLayoutManager(mContext);
        mLayoutManagerInvitationsRequestTribu.setOrientation(LinearLayoutManager.VERTICAL);

        mRvInvitationRequestTribu.setLayoutManager(mLayoutManagerInvitationsRequestTribu);

    }


    public Observable<Void> observableBtnArrowBack() {
        return RxView.clicks(mBtnArrowBack);
    }
}
