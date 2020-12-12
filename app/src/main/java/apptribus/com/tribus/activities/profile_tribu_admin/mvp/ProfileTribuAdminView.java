package apptribus.com.tribus.activities.profile_tribu_admin.mvp;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jakewharton.rxbinding.view.RxView;

import apptribus.com.tribus.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.TRIBU_KEY;

@SuppressLint("ViewConstructor")
public class ProfileTribuAdminView extends CoordinatorLayout {

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
    public TextView mTvDescription;

    @BindView(R.id.tv_thematic)
    TextView mTvThematic;

    @BindView(R.id.tv_creation_date)
    TextView mTvCreationDate;

    @BindView(R.id.tv_followers)
    TextView mTvFollowers;

    @BindView(R.id.tv_num_participants)
    TextView mTvNumRecommendations;

    @BindView(R.id.tv_comments)
    TextView mTvComments;

    @BindView(R.id.tv_isRestrict)
    TextView mTvIsRestrict;

    @BindView(R.id.tv_isPublic)
    TextView mTvIsPublic;

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

    @BindView(R.id.icon_participants)
    ImageView mBtnRecommendation;

    @BindView(R.id.icon_folders)
    ImageView mIconFolders;

    @BindView(R.id.btn_change_description)
    public ImageButton mBtnChangeDescription;

    @BindView(R.id.btn_save_changed_description)
    public ImageButton mBtnSaveChangedDescription;

    @BindView(R.id.tv_change_is_public)
    TextView mTvChangeIsPublic;

    @BindView(R.id.et_profile_tribu_edit_description)
    public EditText mEtProfileTribuEditDescription;

    @BindView(R.id.fab)
    FloatingActionButton mFab;

    @BindView(R.id.iv_isRestrict)
    ImageView mIvIsRestrict;

    @BindView(R.id.iv_isPublic)
    ImageView mIvIsPublic;

    @BindView(R.id.relative_edit_tribu)
    RelativeLayout mRelativeEditTribu;

    @BindView(R.id.nestedScrollView)
    NestedScrollView mNestedScrollView;

    @BindView(R.id.tv_folder_public)
    TextView mTvFolderPublic;

    @BindView(R.id.tv_participant)
    TextView mTvParticipant;

    @BindView(R.id.progressbar)
    ProgressBar mProgressBar;

    @BindView(R.id.progressbar_bottom)
    ProgressBar mProgressBarBottom;


    public AppCompatActivity mContext;

    public String mTribuKey;
    public LinearLayoutManager mLayoutManagerFollowers;


    public ProfileTribuAdminView(@NonNull AppCompatActivity activity) {
        super(activity);
        inflate(activity, R.layout.activity_profile_tribu_admin, this);

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

    }

    public Observable<Void> observableBtnArrowBack() {
        return RxView.clicks(mIbArrowBack);
    }

    public Observable<Void> observableIconFolders() {
        return RxView.clicks(mIconFolders);
    }


    public Observable<Void> observableBtnChangeDescription() {
        return RxView.clicks(mBtnChangeDescription);
    }


    public Observable<Void> observableBtnSaveChangedDescription() {
        return RxView.clicks(mBtnSaveChangedDescription);
    }


    public Observable<Void> observableTvChangeIsPublic() {
        return RxView.clicks(mTvChangeIsPublic);
    }

    public Observable<Void> observableFab() {
        return RxView.clicks(mFab);
    }

    public Observable<Void> observableImageAdmin() {
        return RxView.clicks(mSdRoundImageAdmin);
    }

    public Observable<Void> observableRelativeEditTribu() {
        return RxView.clicks(mRelativeEditTribu);
    }

    public Observable<Void> observableTvFolderPublic() {
        return RxView.clicks(mTvFolderPublic);
    }

    public Observable<Void> observeProfileImage() {
        return RxView.clicks(mSdLargeImageTribu);
    }

}
