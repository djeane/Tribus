package apptribus.com.tribus.activities.user_profile.mvp;

import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import apptribus.com.tribus.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

public class UserProfileView extends CoordinatorLayout {

    @BindView(R.id.circle_image_of_user)
    SimpleDraweeView mCircleImageUser;

    @BindView(R.id.tv_username)
    TextView mTvProfileUserUsername;

    @BindView(R.id.tv_name)
    TextView mTvProfileUserName;

    @BindView(R.id.tv_about_me)
    TextView mTvAboutMe;

    //@BindView(R.id.et_profile_user_name)
    //EditText mEtProfileUserName;

    //@BindView(R.id.et_profile_user_username)
    //EditText mEtProfileUserUsername;

    @BindView(R.id.et_about_me)
    EditText mEtAboutMe;

    @BindView(R.id.btn_change_about_me)
    ImageButton mBtnChangeAboutMe;

    @BindView(R.id.btn_change_name)
    ImageButton mBtnChangeName;

    @BindView(R.id.btn_change_username)
    ImageButton mBtnChangeUsername;

    //@BindView(R.id.btn_save_changed_name)
    //ImageButton mBtnSaveChangedName;

    //@BindView(R.id.btn_save_changed_username)
    //ImageButton mBtnSaveChangedUsername;

    @BindView(R.id.btn_save_changed_about_me)
    ImageButton mBtnSaveChangedAboutMe;

    //@BindView(R.id.appbar)
    //AppBarLayout mAppBar;

    @BindView(R.id.arrow_back)
    ImageView mIbArrowBack;

    //@BindView(R.id.collapsing)
    //CollapsingToolbarLayout mCollapsing;

    @BindView(R.id.tb_profile_user)
    ToggleButton mToggleButton;

    //@BindView(R.id.fab)
    //FloatingActionButton mFab;

    @BindView(R.id.tv_age)
    TextView mTvAge;

    @BindView(R.id.btn_change_age)
    ImageButton mBtnChangeAge;

    @BindView(R.id.btn_save_changed_age)
    ImageButton mBtnSaveChangedAge;

    @BindView(R.id.sp_gender)
    Spinner mSpGender;

    @BindView(R.id.tv_gender)
    TextView mTvGender;

    @BindView(R.id.btn_change_gender)
    ImageButton mBtnChangeGender;

    @BindView(R.id.btn_save_changed_gender)
    ImageButton mBtnSaveChangedGender;

    @BindView(R.id.iv_help)
    ImageView mIvHelp;

    //NEW WIDGETS
    @BindView(R.id.relative_parent)
    RelativeLayout mRelativeParent;

    @BindView(R.id.relative_top)
    RelativeLayout mRelativeTop;

    @BindView(R.id.relative_info)
    RelativeLayout mRelativeInfo;

    @BindView(R.id.tv_num_tribus)
    TextView mTvNumTribus;

    @BindView(R.id.tv_num_contacts)
    TextView mTvNumContacts;

    @BindView(R.id.tv_num_admin)
    TextView mTvNumAdmin;

    @BindView(R.id.tv_num_inspiration)
    TextView mTvNumInspiration;

    @BindView(R.id.tv_num_love)
    TextView mTvNumLove;

    @BindView(R.id.tv_num_genius)
    TextView mTvNumGenius;

    @BindView(R.id.relative_middle)
    public RelativeLayout mRelativeMiddle;

    @BindView(R.id.linear_options)
    LinearLayout mLinearOptions;

    @BindView(R.id.btn_update)
    Button mBtnUpdate;

    @BindView(R.id.btn_profile)
    Button mBtnProfile;

    @BindView(R.id.iv_change_image_profile)
    ImageView mIvChangeImageProfile;

    @BindView(R.id.relative_bottom)
    public RelativeLayout mRelativeBottom;

    @BindView(R.id.rv_update)
    RecyclerView mRvUpdate;

    @BindView(R.id.relative_profile)
    RelativeLayout mRelativeProfile;

    @BindView(R.id.coordinator_updates_list)
    CoordinatorLayout mCoordinatorUpdates;

    @BindView(R.id.progressbar)
    ProgressBar mProgressBar;

    @BindView(R.id.progressbar_bottom)
    ProgressBar mProgressBarBottom;

    @BindView(R.id.tv_no_update)
    TextView mTvNoUpdates;

    public AppCompatActivity mContext;
    public LinearLayoutManager mManager;



    public UserProfileView(@NonNull AppCompatActivity activity) {
        super(activity);
        inflate(activity, R.layout.activity_user_profile, this);

        mContext = activity;

        ButterKnife.bind(this);

        initializeViews();

        configureRecyclerView();

    }

    private void configureRecyclerView(){

        mManager = new LinearLayoutManager(getContext());
        mManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRvUpdate.setLayoutManager(mManager);

    }


    private void initializeViews(){
        mCircleImageUser.bringToFront();
        mIvChangeImageProfile.bringToFront();
        mIbArrowBack.bringToFront();
        mRelativeInfo.invalidate();

        mBtnUpdate.setBackground(getResources().getDrawable(R.drawable.button_options_talker_update_pressed));
        mBtnUpdate.setTextColor(getResources().getColor(R.color.accent));

        mBtnProfile.setBackground(getResources().getDrawable(R.drawable.button_options_talker_profile));
        mBtnProfile.setTextColor(getResources().getColor(R.color.primary_text));

        mCoordinatorUpdates.bringToFront();
        mCoordinatorUpdates.setVisibility(View.VISIBLE);
        mRelativeProfile.setVisibility(GONE);
        //mBtnProfile.setBackgroundColor(getResources().getColor(R.color.primary_light));
        //mBtnProfile.setTextColor(getResources().getColor(R.color.primary_dark));
        //mBtnUpdate.setBackgroundColor(getResources().getColor(R.color.colorIcons));
        //mBtnUpdate.setTextColor(getResources().getColor(R.color.primary_text));
        //mRelativePrivate.setVisibility(View.GONE);
        //mRelativeProfile.setVisibility(View.VISIBLE);
        //mRvUpdate.setVisibility(View.GONE);

    }



    //OBSERVABLES
    //EditText's
    /*public Observable<CharSequence> observeEtUserUsername() {
        return RxTextView.textChanges(mEtProfileUserUsername);
    }*/

    //TO CHANGE NAME
    public Observable<Void> observableBtnChangeName() {
        return RxView.clicks(mBtnChangeName);
    }

    /*public Observable<Void> observableBtnSaveChangedName() {
        return RxView.clicks(mBtnSaveChangedName);
    }*/


    //TO CHANGE USERNAME
    public Observable<Void> observableBtnChangeUsername() {
        return RxView.clicks(mBtnChangeUsername);
    }

    /*public Observable<Void> observeBtnSaveChangedUsername() {
        return RxView.clicks(mBtnSaveChangedUsername);
    }*/


    //TO CHANGE ABOUT ME
    public Observable<Void> observableBtnChangeAboutMe() {
        return RxView.clicks(mBtnChangeAboutMe);
    }

    public Observable<Void> observableBtnSaveChangedAboutMe() {
        return RxView.clicks(mBtnSaveChangedAboutMe);
    }


    //TO CHANGE AGE
    public Observable<Void> observableBtnChangeAge() {
        return RxView.clicks(mBtnChangeAge);
    }

    public Observable<Void> observableBtnSaveChangedAge() {
        return RxView.clicks(mBtnSaveChangedAge);
    }

    //TO CHANGE GENDER
    public Observable<Void> observableBtnChangeGender() {
        return RxView.clicks(mBtnChangeGender);
    }

    public Observable<Void> observableBtnSaveChangedGender() {
        return RxView.clicks(mBtnSaveChangedGender);
    }


    public Observable<Void> observableIbArrowBack() {
        return RxView.clicks(mIbArrowBack);
    }

    public Observable<Void> observableIvChangeImageProfile() {
        return RxView.clicks(mIvChangeImageProfile);
    }

    public Observable<Void> observeFrameProfile(){
        return RxView.clicks(mRelativeProfile);
    }

    public Observable<Void> observeIvHelp(){
        return RxView.clicks(mIvHelp);
    }

    public Observable<Void> observeProfileImage(){
        return RxView.clicks(mCircleImageUser);
    }

    public Observable<Void> observableBtnUpdate(){
        return RxView.clicks(mBtnUpdate);
    }

    public Observable<Void> observableBtnProfile(){
        return RxView.clicks(mBtnProfile);
    }


















        //OLD CODE
    /*@BindView(R.id.sd_large_image_user)
    SimpleDraweeView mCircleImageUser;

    @BindView(R.id.toolbar_profile_user)
    Toolbar mToolbarProfileUser;

    @BindView(R.id.tv_date)
    TextView mTvDate;

    @BindView(R.id.tv_profile_user_username)
    TextView mTvProfileUserUsername;

    @BindView(R.id.tv_profile_user_name)
    TextView mTvProfileUserName;

    @BindView(R.id.tv_about_me)
    TextView mTvAboutMe;

    @BindView(R.id.et_profile_user_name)
    EditText mEtProfileUserName;

    @BindView(R.id.et_profile_user_username)
    EditText mEtProfileUserUsername;

    @BindView(R.id.et_about_me)
    EditText mEtAboutMe;

    @BindView(R.id.btn_change_about_me)
    ImageButton mBtnChangeAboutMe;

    @BindView(R.id.btn_change_name)
    ImageButton mBtnChangeName;

    @BindView(R.id.btn_change_username)
    ImageButton mBtnChangeUsername;

    @BindView(R.id.btn_save_changed_name)
    ImageButton mBtnSaveChangedName;

    @BindView(R.id.btn_save_changed_username)
    ImageButton mBtnSaveChangedUsername;

    @BindView(R.id.btn_save_changed_about_me)
    ImageButton mBtnSaveChangedAboutMe;

    @BindView(R.id.appbar)
    AppBarLayout mAppBar;

    @BindView(R.id.arrow_back)
    ImageButton mIbArrowBack;

    @BindView(R.id.collapsing)
    CollapsingToolbarLayout mCollapsing;

    @BindView(R.id.tb_profile_user)
    ToggleButton mToggleButton;

    @BindView(R.id.fab)
    FloatingActionButton mFab;

    @BindView(R.id.frame_profile)
    FrameLayout mRelativeProfile;

    @BindView(R.id.tv_age)
    TextView mTvAge;

    @BindView(R.id.btn_change_age)
    ImageButton mBtnChangeAge;

    @BindView(R.id.btn_save_changed_age)
    ImageButton mBtnSaveChangedAge;

    @BindView(R.id.sp_gender)
    Spinner mSpGender;

    @BindView(R.id.tv_gender)
    TextView mTvGender;

    @BindView(R.id.btn_change_gender)
    ImageButton mBtnChangeGender;

    @BindView(R.id.btn_save_changed_gender)
    ImageButton mBtnSaveChangedGender;

    @BindView(R.id.iv_help)
    ImageView mIvHelp;

    public AppCompatActivity mContext;

    public UserProfileView(@NonNull AppCompatActivity activity) {
        super(activity);
        inflate(activity, R.layout.activity_user_profile, this);

        mContext = activity;

        ButterKnife.bind(this);

        mAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    mCollapsing.setTitle("Seu perfil");
                    isShow = true;
                } else if (isShow) {
                    mCollapsing.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });

    }

    //OBSERVABLES
    //EditText's
    public Observable<CharSequence> observeEtUserUsername() {
        return RxTextView.textChanges(mEtProfileUserUsername);
    }

    //TO CHANGE NAME
    public Observable<Void> observableBtnChangeName() {
        return RxView.clicks(mBtnChangeName);
    }

    public Observable<Void> observableBtnSaveChangedName() {
        return RxView.clicks(mBtnSaveChangedName);
    }


    //TO CHANGE USERNAME
    public Observable<Void> observableBtnChangeUsername() {
        return RxView.clicks(mBtnChangeUsername);
    }

    public Observable<Void> observeBtnSaveChangedUsername() {
        return RxView.clicks(mBtnSaveChangedUsername);
    }


    //TO CHANGE ABOUT ME
    public Observable<Void> observableBtnChangeAboutMe() {
        return RxView.clicks(mBtnChangeAboutMe);
    }

    public Observable<Void> observableBtnSaveChangedAboutMe() {
        return RxView.clicks(mBtnSaveChangedAboutMe);
    }


    //TO CHANGE AGE
    public Observable<Void> observableBtnChangeAge() {
        return RxView.clicks(mBtnChangeAge);
    }

    public Observable<Void> observableBtnSaveChangedAge() {
        return RxView.clicks(mBtnSaveChangedAge);
    }

    //TO CHANGE GENDER
    public Observable<Void> observableBtnChangeGender() {
        return RxView.clicks(mBtnChangeGender);
    }

    public Observable<Void> observableBtnSaveChangedGender() {
        return RxView.clicks(mBtnSaveChangedGender);
    }


    public Observable<Void> observableIbArrowBack() {
        return RxView.clicks(mIbArrowBack);
    }

    public Observable<Void> observableIvChangeImageProfile() {
        return RxView.clicks(mFab);
    }

    public Observable<Void> observeFrameProfile(){
        return RxView.clicks(mRelativeProfile);
    }

    public Observable<Void> observeIvHelp(){
        return RxView.clicks(mIvHelp);
    }

    public Observable<Void> observeProfileImage(){
        return RxView.clicks(mCircleImageUser);
    }*/

}
