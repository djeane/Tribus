package apptribus.com.tribus.activities.feature_choice_tribus.mvp;

import android.annotation.SuppressLint;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jakewharton.rxbinding.view.RxView;

import java.util.ArrayList;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_container.FragmentContainer;
import apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_feature.FragmentFeature;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.TOPIC;
import static apptribus.com.tribus.util.Constantes.TRIBU_KEY;
import static apptribus.com.tribus.util.Constantes.TRIBU_UNIQUE_NAME;


@SuppressLint("ViewConstructor")
public class FeatureChoiceTribusView extends CoordinatorLayout {


    @BindView(R.id.toolbar_feature_choice)
    Toolbar mToolbarFeatureChoice;

    @BindView(R.id.tv_name_tribu)
    TextView mTvNameOfTribu;

    @BindView(R.id.tv_unique_name)
    TextView mTvUniqueName;

    @BindView(R.id.circle_tribu_image)
    SimpleDraweeView mCircleTribuImage;

    @BindView(R.id.frame_features)
    FrameLayout mFrameFeatures;

    @BindView(R.id.arrow_back)
    ImageView mBtnArrowBack;

    @BindView(R.id.fab_topic)
    FloatingActionButton mFabTopic;

    @BindView(R.id.fab_survey)
    FloatingActionButton mFabSurvey;

    private FragmentFeature mFragmentFeature;
    public FragmentContainer mFragmentContainer;

    public AppCompatActivity mContext;
    public String mTribuKey;
    public String mTribuUniqueName;

    public FeatureChoiceTribusView(AppCompatActivity activity) {
        super(activity);
        mContext = activity;

        inflate(activity, R.layout.activity_feature_choice_tribus, this);

        ButterKnife.bind(this);

        initIntents();

        initViews();

        setOverflowButtonColor(activity, getResources().getColor(R.color.colorIcons));
    }

    private void initIntents() {
        mTribuKey = mContext.getIntent().getStringExtra(TRIBU_KEY);
        mTribuUniqueName = mContext.getIntent().getStringExtra(TRIBU_UNIQUE_NAME);

    }

    private void initViews() {


        FragmentManager fm1 = mContext.getSupportFragmentManager();
        FragmentTransaction transaction1 = fm1.beginTransaction();
        mFragmentFeature = FragmentFeature.getInstance(mTribuKey);
        transaction1.replace(R.id.frame_features, mFragmentFeature)
                .commit();


        FragmentManager fm2 = mContext.getSupportFragmentManager();
        FragmentTransaction transaction2 = fm2.beginTransaction();

        if (mFragmentContainer == null) {
            mFragmentContainer = FragmentContainer.getInstance(TOPIC, mTribuKey);
            transaction2
                    .replace(R.id.frame_container, mFragmentContainer)
                    .commit();

            mFabSurvey.setVisibility(GONE);
            mFabTopic.setVisibility(VISIBLE);
        }

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

    public Observable<Void> observeBtnArrowBack() {
        return RxView.clicks(mBtnArrowBack);
    }

    public Observable<Void> observeFabAddTopic() {
        return RxView.clicks(mFabTopic);
    }

    public Observable<Void> observeFabAddSurvey() {
        return RxView.clicks(mFabSurvey);
    }

}
