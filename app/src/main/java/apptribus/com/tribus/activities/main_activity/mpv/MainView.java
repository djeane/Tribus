package apptribus.com.tribus.activities.main_activity.mpv;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jakewharton.rxbinding.view.RxView;

import java.util.ArrayList;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.main_activity.fragment_talks.TalksFragment;
import apptribus.com.tribus.activities.main_activity.fragment_timeline.TimeLineFragment;
import apptribus.com.tribus.activities.main_activity.fragment_tribus.TribusFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.CONTACT_ID;
import static apptribus.com.tribus.util.Constantes.UPDATE_LAST_MESSAGE;

/**
 * Created by User on 5/25/2017.
 */

@SuppressWarnings("ConstantConditions")
@SuppressLint("ViewConstructor")
public class MainView extends CoordinatorLayout {

    public AppCompatActivity mContext;

    @BindView(R.id.toolbar_main)
    public Toolbar mToolbarMain;

    @BindView(R.id.circle_user_image)
    SimpleDraweeView mCircleUserImage;

    @BindView(R.id.tv_name_user)
    TextView mTvNameUser;

    @BindView(R.id.tv_username)
    TextView mTvUsername;

    @BindView(R.id.bottom_navigation_view)
    public BottomNavigationView mBottomNavigationItemView;

    @BindView(R.id.appbar)
    AppBarLayout mAppBar;

    @BindView(R.id.fab)
    public FloatingActionButton mFab;

    //public MenuItem mAddTalker;
    public MenuItem mProfile;
    //public MenuItem mInvitationsRequest;
    //public MenuItem mRemoveTalkers;
    public MenuItem mBlockedTalkers;
    //public MenuItem mRequestPermissionTribu;
    //public MenuItem mSearchable;
    public MenuItem mPrivacyPolicy;
    public MenuItem mShare;

    public TimeLineFragment timeLineFragment = null;
    public TribusFragment tribusFragment;
    public TalksFragment talksFragment;

    public String mUpdateLastMessage;
    public String mTalkerId;


    public MainView(@NonNull AppCompatActivity activity) {
        super(activity);
        mContext = activity;
        inflate(activity, R.layout.activity_main, this);

        ButterKnife.bind(this);

        mUpdateLastMessage = activity.getIntent().getStringExtra(UPDATE_LAST_MESSAGE);
        mTalkerId = activity.getIntent().getStringExtra(CONTACT_ID);

        mToolbarMain.inflateMenu(R.menu.menu_main_activity);

        //MENU ITEMS
        mShare = mToolbarMain.getMenu().findItem(R.id.action_share);
        mProfile = mToolbarMain.getMenu().findItem(R.id.action_profile_activity);
        //mAddTalker = mToolbarMain.getMenu().findItem(R.id.action_add_talker);
        //mInvitationsRequest = mToolbarMain.getMenu().findItem(R.id.action_invitations_request);
        //mRemoveTalkers = mToolbarMain.getMenu().findItem(R.id.action_remove_talkers);
        //mRequestPermissionTribu = mToolbarMain.getMenu().findItem(R.id.action_invitations_request_tribu);
        mPrivacyPolicy = mToolbarMain.getMenu().findItem(R.id.action_privacy_policy);


        //TO SHOW DEFAULT FRAGMENT WHEN ACTIVITY STARTS
        setOverflowButtonColor(mContext.getResources().getColor(R.color.colorIcons));

    }


    private void setOverflowButtonColor(final int color) {
        final String overflowDescription = mContext.getString(R.string.abc_action_menu_overflow_description);
        final String overflowDescriptionUserProfileEdit = mContext.getString(R.string.icon_user_profile_edit);
        final ViewGroup decorView = (ViewGroup) mContext.getWindow().getDecorView();
        final ViewTreeObserver viewTreeObserver = decorView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(() -> {
            final ArrayList<View> outViews = new ArrayList<>();
            decorView.findViewsWithText(outViews, overflowDescription, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
            decorView.findViewsWithText(outViews, overflowDescriptionUserProfileEdit, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
            if (outViews.isEmpty()) {
                return;
            }
            AppCompatImageView overflow = (AppCompatImageView) outViews.get(0);
            //AppCompatImageView overflowUserProfileEdit = (AppCompatImageView) outViews.get(1);
            overflow.setColorFilter(color);
            //overflowUserProfileEdit.setColorFilter(color);

        });
    }

    //OBSERVABLES
    //mFab
    public Observable<Void> observeFAB() {
        return RxView.clicks(mFab);
    }

    //mToolbarMain
    /*public Observable<Void> observeToolbarMain() {
        return RxView.clicks(mToolbarMain);
    }*/


















    //OLD CODE

    /*@BindView(R.id.toolbar_main)
    public Toolbar mToolbarMain;

    //@BindView(R.id.circle_user_image)
    //SimpleDraweeView mCircleUserImage;

    //@BindView(R.id.tv_name_user)
    //TextView mTvNameUser;

    //@BindView(R.id.tv_username)
    //TextView mTvUsername;

    @BindView(R.id.bottom_navigation_view)
    public BottomNavigationView mBottomNavigationItemView;

    @BindView(R.id.appbar)
    AppBarLayout mAppBar;

    @BindView(R.id.fab)
    public FloatingActionButton mFab;

    public MenuItem mProfileItem;
    public AppCompatActivity mContext;

    public MenuItem mAddTalker;
    public MenuItem mInvitationsRequest;
    public MenuItem mRemoveTalkers;
    public MenuItem mBlockedTalkers;
    public MenuItem mRequestPermissionTribu;
    public MenuItem mSearchable;
    public MenuItem mPrivacyPolicy;
    public MenuItem mShare;

    private TimeLineFragment timeLineFragment = null;
    private TribusFragment tribusFragment;
    private TalksFragment talksFragment;

    public String mUpdateLastMessage;
    public String mTalkerId;


    public MainView(@NonNull AppCompatActivity activity) {
        super(activity);
        mContext = activity;
        inflate(activity, R.layout.activity_main, this);

        ButterKnife.bind(this);

        mUpdateLastMessage = activity.getIntent().getStringExtra(UPDATE_LAST_MESSAGE);
        mTalkerId = activity.getIntent().getStringExtra(CONTACT_ID);



        FragmentManager fm1 = activity.getSupportFragmentManager();
        FragmentTransaction transaction1 = fm1.beginTransaction();

        mToolbarMain.inflateMenu(R.menu.menu_main_activity);
        SearchManager searchManager = (SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);

        //MENU ITEMS
        mSearchable = mToolbarMain.getMenu().findItem(R.id.action_searchable_activity);
        mShare = mToolbarMain.getMenu().findItem(R.id.action_share);
        //mProfileItem = mToolbarMain.getMenu().findItem(R.id.action_profile_activity);
        mAddTalker = mToolbarMain.getMenu().findItem(R.id.action_add_talker);
        mInvitationsRequest = mToolbarMain.getMenu().findItem(R.id.action_invitations_request);
        mRemoveTalkers = mToolbarMain.getMenu().findItem(R.id.action_remove_talkers);
        //mBlockedTalkers = mToolbarMain.getMenu().findItem(R.id.action_blocked_talkers);
        mRequestPermissionTribu = mToolbarMain.getMenu().findItem(R.id.action_invitations_request_tribu);
        mPrivacyPolicy = mToolbarMain.getMenu().findItem(R.id.action_privacy_policy);


        //SearchView searchView = (SearchView) mSearchable.getActionView();
        //searchView.setMaxWidth(Integer.MAX_VALUE);
        //searchView.setSearchableInfo(searchManager.getSearchableInfo(activity.getComponentName()));
        //searchView.setQueryHint(getResources().getString(R.string.search_hint));

        //TO SHOW DEFAULT FRAGMENT WHEN ACTIVITY STARTS
        if (timeLineFragment == null) {
            timeLineFragment = TimeLineFragment.getInstance(0);
            transaction1.replace(R.id.frame_list, timeLineFragment).commit();
            tribusFragment = null;
            talksFragment = null;

            if (mAddTalker != null) {
                mAddTalker.setVisible(false);
                mInvitationsRequest.setVisible(false);
                mRemoveTalkers.setVisible(false);
                mRequestPermissionTribu.setVisible(false);
            }
        }
        setOverflowButtonColor(mContext.getResources().getColor(R.color.colorIcons));

    }

    private void setOverflowButtonColor(final int color) {
        final String overflowDescription = mContext.getString(R.string.abc_action_menu_overflow_description);
        final ViewGroup decorView = (ViewGroup) mContext.getWindow().getDecorView();
        final ViewTreeObserver viewTreeObserver = decorView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(() -> {
            final ArrayList<View> outViews = new ArrayList<>();
            decorView.findViewsWithText(outViews, overflowDescription, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
            if (outViews.isEmpty()) {
                return;
            }
            AppCompatImageView overflow = (AppCompatImageView) outViews.get(0);
            overflow.setColorFilter(color);
        });
    }

    //OBSERVABLES
    //mFab
    public Observable<Void> observeFAB() {
        return RxView.clicks(mFab);
    }

    //mToolbarMain
    public Observable<Void> observeToolbarMain() {
        return RxView.clicks(mToolbarMain);
    }*/
}
