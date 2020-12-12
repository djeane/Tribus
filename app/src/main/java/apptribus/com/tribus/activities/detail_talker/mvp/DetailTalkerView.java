package apptribus.com.tribus.activities.detail_talker.mvp;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jakewharton.rxbinding.view.RxView;

import apptribus.com.tribus.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.FROM_CHAT_TRIBUS;
import static apptribus.com.tribus.util.Constantes.FROM_NOTIFICATION;
import static apptribus.com.tribus.util.Constantes.CONTACT_ID;
import static apptribus.com.tribus.util.Constantes.TRIBU_KEY;
import static apptribus.com.tribus.util.Constantes.UPDATE_LAST_MESSAGE;
import static apptribus.com.tribus.util.Constantes.USER_ID;

@SuppressLint("ViewConstructor")
public class DetailTalkerView extends CoordinatorLayout {

    @BindView(R.id.relative_parent)
    RelativeLayout mRelativeParent;

    @BindView(R.id.arrow_back)
    ImageView mBtnArrowBack;

    @BindView(R.id.relative_top)
    RelativeLayout mRelativeTop;

    @BindView(R.id.circle_image_of_talker)
    SimpleDraweeView mSdCircleImageTalker;

    @BindView(R.id.relative_info)
    RelativeLayout mRelativeInfo;

    @BindView(R.id.tv_name)
    TextView mTvName;

    @BindView(R.id.tv_username)
    TextView mTvUsername;

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

    @BindView(R.id.relative_private)
    public RelativeLayout mRelativePrivate;

    @BindView(R.id.relative_bottom)
    public RelativeLayout mRelativeBottom;

    @BindView(R.id.rv_update)
    RecyclerView mRvUpdate;

    @BindView(R.id.relative_profile)
    RelativeLayout mRelativeProfile;

    @BindView(R.id.tv_age)
    TextView mTvAge;

    @BindView(R.id.tv_gender)
    TextView mTvGender;

    @BindView(R.id.tv_location)
    TextView mTvLocation;

    @BindView(R.id.tv_status)
    TextView mTvStatus;

    //BUTTONS AND LINEAR LAYOUT
    //private layout
        //accept
    @BindView(R.id.linear_buttons_private_accept)
    public LinearLayout mLinearButtonsPrivateAccept;

    @BindView(R.id.btn_talker_private_accept)
    Button mBtnTalkerPrivateAccept;

    @BindView(R.id.btn_denied_private)
    Button mBtnDeniedPrivate;

        //added
    @BindView(R.id.linear_buttons_private_added)
    public LinearLayout mLinearButtonsPrivateAdded;

    @BindView(R.id.btn_private_added)
    public Button mBtnPrivateAdded;

    @BindView(R.id.btn_cancel_private_added)
    public Button mBtnCancelPrivateAdded;

    //public layout
        //accept
    @BindView(R.id.linear_buttons_public_accept)
    public LinearLayout mLinearButtonsPublicAccept;

    @BindView(R.id.btn_accept_talker_public)
    Button mBtnAcceptTalkerPublic;

    @BindView(R.id.btn_denied_public)
    Button mBtnDeniedPublic;

        //added
    @BindView(R.id.linear_buttons_public_add)
    public LinearLayout mLinearButtonsPublicAdd;

    @BindView(R.id.btn_add_talker)
    public Button mBtnAddTalker;

    @BindView(R.id.btn_remove_talker)
    public Button mBtnRemoveTalker;

    @BindView(R.id.relative_progress)
    RelativeLayout mRelativeProgress;

    public AppCompatActivity mContext;

    public String mUpdateLastMessage;
    public String mContactId;
    public String mUserId;
    public LinearLayoutManager mManager;
    public String mFromInvitations;
    public String mCameFrom;
    public String fromNotification;
    public String mTribusKey;
    public String mFromChatTribus;

    public DetailTalkerView(@NonNull AppCompatActivity activity) {
        super(activity);

        mContext = activity;

        inflate(activity, R.layout.activity_detail_talker, this);

        ButterKnife.bind(this);

        initIntents(activity);

        initializeViews();

        configureRecyclerView();

    }

    private void initIntents(AppCompatActivity activity){

        mContactId = activity.getIntent().getExtras().getString(CONTACT_ID);
        mUpdateLastMessage = activity.getIntent().getStringExtra(UPDATE_LAST_MESSAGE);
        mUserId = activity.getIntent().getExtras().getString(USER_ID);
        mTribusKey = activity.getIntent().getExtras().getString(TRIBU_KEY);
        mFromChatTribus = activity.getIntent().getExtras().getString(FROM_CHAT_TRIBUS);
        mFromInvitations = activity.getIntent().getStringExtra("fromInvitationsRequestUser");
        mCameFrom = activity.getIntent().getStringExtra("cameFrom");
        fromNotification = activity.getIntent().getStringExtra(FROM_NOTIFICATION);

    }

    private void configureRecyclerView(){

        mManager = new LinearLayoutManager(getContext());
        mManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRvUpdate.setLayoutManager(mManager);

    }


    private void initializeViews(){
        mSdCircleImageTalker.bringToFront();
        mBtnArrowBack.bringToFront();
        mRelativeInfo.invalidate();

        mBtnProfile.setBackground(getResources().getDrawable(R.drawable.button_options_talker_profile_pressed));
        mBtnProfile.setTextColor(getResources().getColor(R.color.primary_dark));

        mBtnUpdate.setBackground(getResources().getDrawable(R.drawable.button_options_talker_update));
        mBtnUpdate.setTextColor(getResources().getColor(R.color.primary_text));

        mRelativePrivate.setVisibility(View.GONE);
        mRelativeProfile.setVisibility(View.VISIBLE);
        mRvUpdate.setVisibility(View.GONE);

    }


    public Observable<Void> observableBtnArrowBabk(){
        return RxView.clicks(mBtnArrowBack);
    }

    public Observable<Void> observeProfileImage(){
        return RxView.clicks(mSdCircleImageTalker);
    }

    public Observable<Void> observeBtnUpdate(){
        return RxView.clicks(mBtnUpdate);
    }

    public Observable<Void> observeBtnProfile(){
        return RxView.clicks(mBtnProfile);
    }

    public Observable<Void> observableBtnRemoveTalker(){
        return RxView.clicks(mBtnRemoveTalker);
    }

    public Observable<Void> observableBtnAddContact(){
        return RxView.clicks(mBtnAddTalker);
    }

    public Observable<Void> observableBtnTalkerPrivateAccept() {
        return RxView.clicks(mBtnTalkerPrivateAccept);
    }

    public Observable<Void> observableBtnDeniedPrivate() {
        return RxView.clicks(mBtnDeniedPrivate);
    }



    public Observable<Void> observableBtnAcceptTalkerPublic() {
        return RxView.clicks(mBtnAcceptTalkerPublic);
    }

    public Observable<Void> observableBtnDeniedPublic() {
        return RxView.clicks(mBtnDeniedPublic);
    }

    public Observable<Void> observableBtnPrivateAdded() {
        return RxView.clicks(mBtnPrivateAdded);
    }

    public Observable<Void> observableBtnCancelPrivateAdded() {
        return RxView.clicks(mBtnCancelPrivateAdded);
    }

}
