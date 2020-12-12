package apptribus.com.tribus.activities.conversation_topics.mvp;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jakewharton.rxbinding.view.RxView;

import java.util.ArrayList;

import apptribus.com.tribus.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.LINK_INTO_MESSAGE;
import static apptribus.com.tribus.util.Constantes.TRIBU_UNIQUE_NAME;

/**
 * Created by User on 12/25/2017.
 */

public class ConversationTopicsView extends CoordinatorLayout {

    @BindView(R.id.coordinator_topic)
    CoordinatorLayout mCoordinatorTopic;

    @BindView(R.id.relative_recycler)
    RelativeLayout mRelativeRecyler;

    @BindView(R.id.toolbar_conversation_topics)
    Toolbar mToolbarConversationTopic;

    @BindView(R.id.rv_topic)
    RecyclerView mRvTopic;

    @BindView(R.id.tv_name_tribu)
    TextView mTvNameOfTribu;

    @BindView(R.id.tv_unique_name)
    TextView mTvUniqueName;

    @BindView(R.id.circle_tribu_image)
    SimpleDraweeView mCircleTribuImage;

    @BindView(R.id.arrow_back)
    ImageView mBtnArrowBack;

    @BindView(R.id.fab)
    public FloatingActionButton mFab;

    public String mTribuKey;
    public String mLink;

    public AppCompatActivity mContext;
    public LinearLayoutManager mLayoutManager;

    public ConversationTopicsView(AppCompatActivity activity) {
        super(activity);
        mContext = activity;

        inflate(activity, R.layout.activity_conversation_topics, this);

        ButterKnife.bind(this);
        mTribuKey = activity.getIntent().getStringExtra(TRIBU_UNIQUE_NAME);
        mLink = activity.getIntent().getStringExtra(LINK_INTO_MESSAGE);

        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRvTopic.setLayoutManager(mLayoutManager);
        setOverflowButtonColor(activity, getResources().getColor(R.color.colorIcons));
    }

    public static void setOverflowButtonColor(final AppCompatActivity activity, final int color) {
        final String overflowDescription = activity.getString(R.string.abc_action_menu_overflow_description);
        final ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
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

    public Observable<Void> observeToolbarClicks(){
        return RxView.clicks(mToolbarConversationTopic);
    }

    public Observable<Void> observeBtnArrowBack(){
        return RxView.clicks(mBtnArrowBack);
    }

    public Observable<Void> observableTribuImage(){
        return RxView.clicks(mCircleTribuImage);
    }

    //mFab
    public Observable<Void> observeFAB() {
        return RxView.clicks(mFab);
    }


}
