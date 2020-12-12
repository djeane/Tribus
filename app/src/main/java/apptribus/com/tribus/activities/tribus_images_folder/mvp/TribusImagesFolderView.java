package apptribus.com.tribus.activities.tribus_images_folder.mvp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jakewharton.rxbinding.view.RxView;

import apptribus.com.tribus.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.TRIBU_UNIQUE_NAME;

/**
 * Created by User on 9/23/2017.
 */

@SuppressLint("ViewConstructor")
public class TribusImagesFolderView extends FrameLayout{

    @BindView(R.id.toolbar_images_folder)
    Toolbar mToolbarImagesFolder;

    @BindView(R.id.rv_images_folder)
    RecyclerView mRvImagesFolder;

    @BindView(R.id.circle_tribu_image)
    SimpleDraweeView mTribuImage;

    @BindView(R.id.tv_name_tribu)
    TextView mTvTribusName;

    @BindView(R.id.tv_name_folder)
    TextView mTvNamesFolder;

    @BindView(R.id.arrow_back)
    ImageView mBtnArrowBack;

    public AppCompatActivity mContext;

    private GridLayoutManager mGridLayoutManager;

    public String mTribusKey;
    public String mIntentExtra;
    public String mCameFrom;

    public TribusImagesFolderView(@NonNull AppCompatActivity activity) {
        super(activity);
        inflate(activity, R.layout.activity_tribus_image_folder, this);

        mContext = activity;

        ButterKnife.bind(this);

        mTribusKey = activity.getIntent().getStringExtra(TRIBU_UNIQUE_NAME);
        mIntentExtra = activity.getIntent().getStringExtra("intentExtra");
        mCameFrom = activity.getIntent().getStringExtra("cameFrom");

        mGridLayoutManager = new GridLayoutManager(getContext(), 2);
        mRvImagesFolder.setLayoutManager(mGridLayoutManager);
    }

    public Observable<Void> observeBtnArrowBack(){
        return RxView.clicks(mBtnArrowBack);
    }
}
