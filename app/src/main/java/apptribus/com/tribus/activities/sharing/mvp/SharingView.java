package apptribus.com.tribus.activities.sharing.mvp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.sharing.fragments.sharing_talker.SharingTalkerFragment;
import apptribus.com.tribus.activities.sharing.fragments.sharing_tribus.SharingTribusFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

/**
 * Created by User on 12/12/2017.
 */

public class SharingView extends CoordinatorLayout{

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.toolbar_sharing)
    Toolbar mToolbarSharing;

    @BindView(R.id.arrow_back)
    ImageView mBtnArrowBack;

    private SharingTribusFragment mSharingTribusFragment;
    private SharingTalkerFragment mSharingTalkerFragment;

    public AppCompatActivity mContext;
    public String mLink;


    public SharingView(AppCompatActivity activity) {
        super(activity);

        mContext = activity;
        inflate(activity, R.layout.activity_sharing, this);

        ButterKnife.bind(this);

        FragmentManager fm1 = activity.getSupportFragmentManager();
        FragmentTransaction transaction1 = fm1.beginTransaction();

        FragmentManager fm2 = activity.getSupportFragmentManager();
        FragmentTransaction transaction2 = fm2.beginTransaction();

        // Get intent, action and MIME type
        Intent intent = activity.getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                mLink = intent.getStringExtra(Intent.EXTRA_TEXT);
            }
        }


        mSharingTribusFragment = SharingTribusFragment.getInstance();
        Bundle args = new Bundle();
        args.putString("link", mLink);
        mSharingTribusFragment.setArguments(args);
        transaction1.replace(R.id.frame_tribus, mSharingTribusFragment)
                .commit();

        mSharingTalkerFragment = SharingTalkerFragment.getInstance();
        Bundle args2 = new Bundle();
        args2.putString("link", mLink);
        mSharingTalkerFragment.setArguments(args2);
        transaction2.replace(R.id.frame_talkers, mSharingTalkerFragment)
                .commit();

    }

    public Observable<Void> observableBtnArrowBack(){
        return RxView.clicks(mBtnArrowBack);
    }
}
