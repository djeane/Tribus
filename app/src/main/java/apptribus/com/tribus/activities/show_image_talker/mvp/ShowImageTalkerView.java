package apptribus.com.tribus.activities.show_image_talker.mvp;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jakewharton.rxbinding.view.RxView;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.show_image.zoom_image.ZoomableDraweeView;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.MESSAGE_REFERENCE;
import static apptribus.com.tribus.util.Constantes.CONTACT_ID;

/**
 * Created by User on 8/16/2017.
 */

public class ShowImageTalkerView extends FrameLayout {

    @BindView(R.id.parent)
    RelativeLayout mRelativeParent;

    @BindView(R.id.sd_image)
    ZoomableDraweeView mZoomDraweeViewShowImage;

    @BindView(R.id.root)
    RelativeLayout mRelativeRoot;

    @BindView(R.id.circle_talker_image)
    SimpleDraweeView mImageTalker;

    @BindView(R.id.appbar)
    AppBarLayout mAppBar;

    @BindView(R.id.arrow_back)
    ImageView mBtnArrowBack;

    @BindView(R.id.tv_name_talker)
    TextView mTvNameTalker;

    @BindView(R.id.tv_username_talker)
    TextView mTvUsernameTalker;

    @BindView(R.id.toolbar_show_image_talker)
    Toolbar mToolbarShowImageTalker;

    @BindView(R.id.card_description)
    CardView mCardShowDescription;

    @BindView(R.id.tv_description_image)
    TextView mTvDescription;

    public String mTalkerKey;
    public String mMessageReference;
    public Uri mImageUri;

    //TO IMPLEMENT LEAN BACK
    public View mDecorView;
    public View mMainView;

    public AppCompatActivity mContext;

    public ShowImageTalkerView(@NonNull AppCompatActivity activity) {
        super(activity);
        mContext = activity;
        inflate(activity, R.layout.activity_show_image_talker, this);

        ButterKnife.bind(this);

        mTalkerKey = activity.getIntent().getStringExtra(CONTACT_ID);

        mMessageReference = activity.getIntent().getStringExtra(MESSAGE_REFERENCE);
        mImageUri = activity.getIntent().getData();

        //TO DISPLAY IMAGE
        //activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //NEW CODE TO IMPLEMENT LEAN BACK
        mDecorView = activity.getWindow().getDecorView();

    }

    public Observable<Void> observableBtnArrowBack(){
        return RxView.clicks(mBtnArrowBack);
    }

}
