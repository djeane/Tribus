package apptribus.com.tribus.activities.profile_tribu_user.mvp;

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
import android.view.View;
import android.widget.FrameLayout;
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

import static apptribus.com.tribus.util.Constantes.FROM_NOTIFICATION;
import static apptribus.com.tribus.util.Constantes.TRIBU_KEY;
import static apptribus.com.tribus.util.Constantes.TRIBU_UNIQUE_NAME;

/**
 * Created by User on 7/3/2017.
 */

@SuppressLint("ViewConstructor")
public class ProfileTribuUserView extends CoordinatorLayout {

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

    @BindView(R.id.tv_folder_public)
    TextView mTvFolderPublic;

    @BindView(R.id.tv_num_participants)
    TextView mTvNumParticipants;

    @BindView(R.id.tv_username_admin)
    TextView mTvUsername;

    @BindView(R.id.tv_participant)
    TextView mTvParticipants;

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
    ImageView mIconIsRestrict;

    @BindView(R.id.iv_isPublic)
    ImageView mIconIsPublic;

    @BindView(R.id.icon_participants)
    ImageView mIconParticipantes;

    @BindView(R.id.relative_bottom)
    RelativeLayout mRelativeBottom;

    @BindView(R.id.relative_followers)
    RelativeLayout mRelativeShowFollowers;

    @BindView(R.id.progressbar)
    ProgressBar mProgressBar;

    @BindView(R.id.coordinator_recycler)
    CoordinatorLayout mCoordinatorRecycler;

    @BindView(R.id.progressbar_bottom)
    ProgressBar mProgressBarBottom;

    public AppCompatActivity mContext;

    public String mTribuKey;
    public LinearLayoutManager mLayoutManagerFollowers;

    public String fromNotification;


    public ProfileTribuUserView(@NonNull AppCompatActivity activity) {
        super(activity);
        inflate(activity, R.layout.activity_profile_tribu_user, this);

        mContext = activity;

        ButterKnife.bind(this);
        mTribuKey = activity.getIntent().getExtras().getString(TRIBU_KEY);
        fromNotification = activity.getIntent().getStringExtra(FROM_NOTIFICATION);


        //LINEAR LAYOUT MANAGER TO RECYCLER VIEW FOLLOWERS
        mLayoutManagerFollowers = new LinearLayoutManager(getContext());
        mLayoutManagerFollowers.setOrientation(LinearLayoutManager.HORIZONTAL);

        mRvFollowers.setLayoutManager(mLayoutManagerFollowers);
        //mRvFollowers.setHasFixedSize(true);

        /*mAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    mCollapsing.setTitle("Perfil da Tribu");
                    isShow = true;
                } else if(isShow) {
                    mCollapsing.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });*/

    }

    public Observable<Void> observableBtnArrowBack(){
        return RxView.clicks(mIbArrowBack);
    }

    public Observable<Void> observableIconFolders(){
        return RxView.clicks(mIconFolders);
    }

    /*public Observable<Void> observableBtnComments(){
        return RxView.clicks(mIconComments);
    }*/

    public Observable<Void> observableBtnFollow(){
        return RxView.clicks(mBtnFollow);
    }

    public Observable<Void> observableImageAdmin(){
        return RxView.clicks(mSdRoundImageAdmin);
    }

    public Observable<Void> observeIconIsPublic(){
        return RxView.clicks(mIconIsPublic);
    }

    public Observable<Void> observeIconIsRestrict(){
        return RxView.clicks(mIconIsRestrict);
    }

    public Observable<Void> observableTvFolder(){
        return RxView.clicks(mTvFolderPublic);
    }

    public Observable<Void> observeProfileImage(){
        return RxView.clicks(mSdLargeImageTribu);
    }

}
