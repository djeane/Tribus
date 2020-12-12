package apptribus.com.tribus.activities.show_image.mvp;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import static apptribus.com.tribus.util.Constantes.TRIBU_KEY;
import static apptribus.com.tribus.util.Constantes.TRIBU_UNIQUE_NAME;

/**
 * Created by User on 7/20/2017.
 */

@SuppressLint("ViewConstructor")
public class ShowImageView extends FrameLayout {

    @BindView(R.id.parent)
    RelativeLayout mRelativeParent;

    @BindView(R.id.sd_image)
    ZoomableDraweeView mSimpleDraweeViewShowImage;

    @BindView(R.id.root)
    RelativeLayout mRelativeRoot;

    @BindView(R.id.appbar)
    AppBarLayout mAppBar;

    @BindView(R.id.toolbar_show_image)
    Toolbar mToolbarShowImage;

    @BindView(R.id.arrow_back)
    ImageView mBtnArrowBack;

    @BindView(R.id.circle_tribu_image)
    SimpleDraweeView mCircleTribuImage;

    @BindView(R.id.tv_name_tribu)
    TextView mTribuName;

    @BindView(R.id.tv_unique_name)
    TextView mTribuUniqueName;

    @BindView(R.id.card_description)
    CardView mCardShowDescription;

    @BindView(R.id.tv_description_image)
    TextView mTvDescription;

    public String mTribuKey;
    public String mMessageReference;
    public Uri mImageUri;

    public AppCompatActivity mContext;

    //TO IMPLEMENT LEAN BACK
    public View mDecorView;
    public View mMainView;


    public ShowImageView(@NonNull AppCompatActivity activity) {
        super(activity);
        mContext = activity;
        inflate(activity, R.layout.activity_show_image, this);

        ButterKnife.bind(this);

        mTribuKey = activity.getIntent().getExtras().getString(TRIBU_KEY);
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
