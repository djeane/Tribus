package apptribus.com.tribus.activities.profile_tribu_follower.mvp;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jakewharton.rxbinding.view.RxView;

import apptribus.com.tribus.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.TRIBU_KEY;

@SuppressLint("ViewConstructor")
public class ProfileTribuFollowerView extends CoordinatorLayout {

    @BindView(R.id.sd_large_image_tribu)
    SimpleDraweeView mSdLargeImageTribu;

    @BindView(R.id.circle_image_of_admin)
    SimpleDraweeView mSdRoundImageAdmin;

    @BindView(R.id.toolbar_profile_tribu_followers)
    Toolbar mToolbarDetailTribu;

    @BindView(R.id.tv_name_of_admin)
    TextView mTvNameAdmin;

    @BindView(R.id.tv_name_tribu)
    TextView mTvTribusName;

    @BindView(R.id.tv_unique_name)
    TextView mTvUniqueName;

    @BindView(R.id.tv_description)
    TextView mTvDescription;

    @BindView(R.id.tv_thematic)
    TextView mTvThematic;

    @BindView(R.id.tv_username_admin)
    TextView mTvUsername;

    @BindView(R.id.rv_followers)
    RecyclerView mRvFollowers;

    @BindView(R.id.appbar)
    AppBarLayout mAppBar;

    @BindView(R.id.arrow_back)
    ImageButton mIbArrowBack;

    @BindView(R.id.btn_follow)
    AppCompatButton mBtnFollow;

    @BindView(R.id.collapsing)
    CollapsingToolbarLayout mCollapsing;

    @BindView(R.id.icon_folders)
    ImageView mIconFolders;

    @BindView(R.id.iv_isRestrict)
    ImageView mIvIsRestrict;

    @BindView(R.id.iv_isPublic)
    ImageView mIvIsPublic;

    @BindView(R.id.tv_participant)
    TextView mTvParticipant;

    @BindView(R.id.tv_folder_public)
    TextView mTvFolderPublic;

    @BindView(R.id.progressbar)
    ProgressBar mProgressBar;

    @BindView(R.id.progressbar_bottom)
    ProgressBar mProgressBarBottom;


    public AppCompatActivity mContext;

    public String mTribuKey;
    public LinearLayoutManager mLayoutManagerFollowers;


    public ProfileTribuFollowerView(@NonNull AppCompatActivity activity) {
        super(activity);
        inflate(activity, R.layout.activity_profile_tribu_follower, this);

        mContext = activity;

        ButterKnife.bind(this);

        initIntents();

        initViews();

    }

    public void initIntents() {
        mTribuKey = mContext.getIntent().getExtras().getString(TRIBU_KEY);

    }

    public void initViews() {
        mLayoutManagerFollowers = new LinearLayoutManager(mContext);
        mLayoutManagerFollowers.setOrientation(LinearLayoutManager.HORIZONTAL);

        mRvFollowers.setLayoutManager(mLayoutManagerFollowers);
        mRvFollowers.setHasFixedSize(true);

    }

    public Observable<Void> observableBtnArrowBack() {
        return RxView.clicks(mIbArrowBack);
    }

    public Observable<Void> observableIconFolders() {
        return RxView.clicks(mIconFolders);
    }


    public Observable<Void> observableImageAdmin() {
        return RxView.clicks(mSdRoundImageAdmin);
    }

    public Observable<Void> observableTvFolderPublic() {
        return RxView.clicks(mTvFolderPublic);
    }

    public Observable<Void> observeProfileImage() {
        return RxView.clicks(mSdLargeImageTribu);
    }

}
