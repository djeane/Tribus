package apptribus.com.tribus.activities.comments.mvp;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jakewharton.rxbinding.view.RxView;

import apptribus.com.tribus.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.TRIBU_UNIQUE_NAME;

@SuppressLint("ViewConstructor")
public class CommentsView extends FrameLayout {

    @BindView(R.id.toolbar_comments_tribu)
    Toolbar mToolbarCommentsTribu;

    @BindView(R.id.sd_large_image_tribu)
    SimpleDraweeView mSdLargeImageTribu;

    @BindView(R.id.tv_comments_about_tribu)
    TextView mTvCommentsAboutTribu;

    @BindView(R.id.tv_num_comments)
    TextView mTvNumComments;

    @BindView(R.id.rv_comments)
    RecyclerView mRvComments;

    @BindView(R.id.et_write_comment)
    EditText mEtWriteComment;

    @BindView(R.id.btn_send_comment)
    FloatingActionButton mBtnSendComment;

    @BindView(R.id.appbar)
    AppBarLayout mAppBar;

    @BindView(R.id.collapsing)
    CollapsingToolbarLayout mCollapsing;

    @BindView(R.id.arrow_back)
    ImageButton mBtnArrowBack;

    //INTENT
    public String mTribuKey;

    public LinearLayoutManager mLinearManagerComments;

    public CommentsView(@NonNull AppCompatActivity activity) {
        super(activity);
        inflate(activity, R.layout.activity_comments, this);

        ButterKnife.bind(this);
        mTribuKey = activity.getIntent().getExtras().getString(TRIBU_UNIQUE_NAME);

        mLinearManagerComments = new LinearLayoutManager(activity);
        mLinearManagerComments.setOrientation(LinearLayoutManager.VERTICAL);
        mRvComments.setLayoutManager(mLinearManagerComments);

        mAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    mCollapsing.setTitle("Comentários");
                    isShow = true;
                } else if(isShow) {
                    mCollapsing.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });


    }


    public Observable<Void> observeBtnSend(){
        return RxView.clicks(mBtnSendComment);
    }

    public Observable<Void> observeBtnArrowBack(){
        return RxView.clicks(mBtnArrowBack);
    }
}
