package apptribus.com.tribus.activities.show_profile_image.mvp;

import android.content.Context;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jakewharton.rxbinding.view.RxView;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.show_image.zoom_image.ZoomableDraweeView;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

/**
 * Created by User on 12/25/2017.
 */

public class ShowProfileImageView extends RelativeLayout {


    @BindView(R.id.parent)
    RelativeLayout mRelativeParent;

    @BindView(R.id.sd_image)
    ZoomableDraweeView mZoomDraweeViewShowImage;

    @BindView(R.id.root)
    RelativeLayout mRelativeRoot;

    @BindView(R.id.appbar)
    AppBarLayout mAppBar;

    @BindView(R.id.arrow_back)
    ImageView mBtnArrowBack;

    @BindView(R.id.toolbar_show_profile_image)
    Toolbar mToolbarShowImage;

    public MenuItem mDownloadImage;
    public MenuItem mShare;

    public AppCompatActivity mContext;
    public Uri mImageUri;
    public View mDecorView;
    public View mMainView;

    public ShowProfileImageView(AppCompatActivity activity) {
        super(activity);
        mContext = activity;

        inflate(activity, R.layout.activity_show_profile_image, this);

        ButterKnife.bind(this);

        mImageUri = activity.getIntent().getData();

        mToolbarShowImage.inflateMenu(R.menu.menu_show_image_profile);

        //mDownloadImage = mToolbarShowImage.getMenu().findItem(R.id.action_download);
        mShare = mToolbarShowImage.getMenu().findItem(R.id.action_share);


        //NEW CODE TO IMPLEMENT LEAN BACK
        mDecorView = activity.getWindow().getDecorView();

    }

    public Observable<Void> observableBtnArrowBack(){
        return RxView.clicks(mBtnArrowBack);
    }
}
