package apptribus.com.tribus.activities.invitations_request_user.mvp;

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
public class InvitationRequestUserView extends RelativeLayout {

    @BindView(R.id.relative_invitation_request_user)
    RelativeLayout mRelativeInvitationRequestUser;

    @BindView(R.id.coordinator_recycler)
    CoordinatorLayout mCoordinatorRecycler;

    @BindView(R.id.toolbar_invitations_request_user)
    Toolbar mToolbarAddTalkers;

    @BindView(R.id.rv_invitations_request)
    RecyclerView mRvInvitationRequestUser;

    @BindView(R.id.arrow_back)
    ImageView mBtnArrowBack;

    @BindView(R.id.progressbar)
    ProgressBar mProgressBar;

    @BindView(R.id.progressbar_bottom)
    ProgressBar mProgressBarBottom;

    @BindView(R.id.tv_no_request_user)
    TextView mTvNoContacts;

    public LinearLayoutManager mLayoutManagerInvitationsRequestUser;
    public AppCompatActivity mContext;
    public String fromNotification;


    public InvitationRequestUserView(@NonNull AppCompatActivity activity) {
        super(activity);
        mContext = activity;

        inflate(activity, R.layout.activity_invitation_request_user, this);
        ButterKnife.bind(this);

        iniIntents();

        initViews();


    }

    private void iniIntents(){
        fromNotification = mContext.getIntent().getStringExtra(FROM_NOTIFICATION);

    }

    private void initViews(){
        mLayoutManagerInvitationsRequestUser = new LinearLayoutManager(mContext);
        mLayoutManagerInvitationsRequestUser.setOrientation(LinearLayoutManager.VERTICAL);

        mRvInvitationRequestUser.setLayoutManager(mLayoutManagerInvitationsRequestUser);
    }

    public Observable<Void> observableBtnArrowBack(){
        return RxView.clicks(mBtnArrowBack);
    }
}
