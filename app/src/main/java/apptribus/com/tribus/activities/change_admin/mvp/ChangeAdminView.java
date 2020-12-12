package apptribus.com.tribus.activities.change_admin.mvp;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import apptribus.com.tribus.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.TRIBU_KEY;

@SuppressLint("ViewConstructor")
public class ChangeAdminView extends FrameLayout {

    @BindView(R.id.toolbar_change_admin)
    Toolbar mToolbarChangeAdmin;

    @BindView(R.id.relative_change_admin)
    RelativeLayout mRelativeChangeAdmin;

    @BindView(R.id.coordinator_recycler)
    CoordinatorLayout mCoordinatorRecycler;

    @BindView(R.id.rv_change_admin)
    RecyclerView mRvChangeAdmin;

    @BindView(R.id.tv_info)
    TextView mTvInfo;

    @BindView(R.id.arrow_back)
    ImageView mBtnArrowBack;

    @BindView(R.id.tv_no_followers)
    TextView mTvNoFollowers;

    @BindView(R.id.progressbar)
    ProgressBar mProgressBar;

    @BindView(R.id.progressbar_bottom)
    ProgressBar mProgressBarBottom;

    public String mTribuKey;
    public LinearLayoutManager mLayoutChangeAdmin;
    public AppCompatActivity mContext;


    public ChangeAdminView(@NonNull AppCompatActivity activity) {
        super(activity);
        mContext = activity;
        inflate(activity, R.layout.activity_change_admin, this);
        ButterKnife.bind(this);

        initIntents();
        initViews();

    }

    private void initIntents(){
        mTribuKey = mContext.getIntent().getExtras().getString(TRIBU_KEY);
    }

    private void initViews(){
        mLayoutChangeAdmin = new LinearLayoutManager(mContext);
        mLayoutChangeAdmin.setOrientation(LinearLayoutManager.VERTICAL);

        mRvChangeAdmin.setLayoutManager(mLayoutChangeAdmin);
        mRvChangeAdmin.setHasFixedSize(true);
    }

    public Observable<Void> observableBtnArrowBack(){
        return RxView.clicks(mBtnArrowBack);
    }
}
