package apptribus.com.tribus.activities.send_image.mvp;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
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

import static apptribus.com.tribus.util.Constantes.TOPIC_KEY;
import static apptribus.com.tribus.util.Constantes.TRIBU_UNIQUE_NAME;

/**
 * Created by User on 7/12/2017.
 */

public class SendImageView extends FrameLayout {

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

    @BindView(R.id.et_description)
    EditText mEtDescription;

    @BindView(R.id.btn_send_message)
    FloatingActionButton mBtnSendMessage;


    public String mTribuKey;
    public String mTopicKey;
    public Uri mUri;
    public int mFileSize;
    public String mCameFrom;

    public AppCompatActivity mContext;


    public SendImageView(@NonNull AppCompatActivity activity) {
        super(activity);
        mContext = activity;
        inflate(activity, R.layout.activity_send_image, this);
        ButterKnife.bind(this);

        mTribuKey = activity.getIntent().getExtras().getString(TRIBU_UNIQUE_NAME);
        mTopicKey = activity.getIntent().getExtras().getString(TOPIC_KEY);
        mUri = activity.getIntent().getData();
        mFileSize = activity.getIntent().getExtras().getInt("image_size");
        mCameFrom = activity.getIntent().getStringExtra("cameFrom");

        //activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    public Observable<Void> observeBtnSendImage(){
        return RxView.clicks(mBtnSendMessage);
    }

    public Observable<Void> observeBtnArrowBack(){
        return RxView.clicks(mBtnArrowBack);
    }
}
