package apptribus.com.tribus.activities.block_user.mvp;

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


@SuppressLint("ViewConstructor")
public class BlockUserView extends RelativeLayout {

    @BindView(R.id.relative_block_user)
    RelativeLayout mRelativeBlockUser;

    @BindView(R.id.coordinator_recycler)
    CoordinatorLayout mCoordinatorRecycler;

    @BindView(R.id.toolbar_block_talkers)
    Toolbar mToolbarBlockTalkers;

    @BindView(R.id.rv_block_talkers)
    RecyclerView mRvBlockTalkers;

    @BindView(R.id.arrow_back)
    ImageView mBtnArrowBack;

    @BindView(R.id.progressbar)
    ProgressBar mProgressBar;

    @BindView(R.id.progressbar_bottom)
    ProgressBar mProgressBarBottom;

    @BindView(R.id.tv_no_contacts)
    TextView mTvNoContacts;

    public LinearLayoutManager mLayoutManagerBlockTalkers;
    public AppCompatActivity mContext;


    public BlockUserView(@NonNull AppCompatActivity activity) {
        super(activity);
        mContext = activity;

        inflate(activity, R.layout.activity_block_user, this);

        ButterKnife.bind(this);

        initViews();

    }

    private void initViews(){
        mLayoutManagerBlockTalkers = new LinearLayoutManager(mContext);
        mLayoutManagerBlockTalkers.setOrientation(LinearLayoutManager.VERTICAL);

        mRvBlockTalkers.setLayoutManager(mLayoutManagerBlockTalkers);

    }

    public Observable<Void> observableBtnArrowBack(){
        return RxView.clicks(mBtnArrowBack);
    }
}
