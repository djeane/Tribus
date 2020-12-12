package apptribus.com.tribus.activities.show_survey.mvp;

import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import apptribus.com.tribus.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.TRIBU_UNIQUE_NAME;

/**
 * Created by User on 1/19/2018.
 */

public class ShowSurveyView extends CoordinatorLayout{

    @BindView(R.id.toolbar_show_survey)
    Toolbar mToolbarShowSurvey;

    @BindView(R.id.tv_name_tribu)
    TextView mTvNameTribu;

    @BindView(R.id.rv_show_survey)
    RecyclerView mRvShowSurvey;

    @BindView(R.id.arrow_back)
    ImageView mBtnArrowBack;


    public AppCompatActivity mContext;
    public LinearLayoutManager mLayoutManagerShowSurvey;
    public String mTribuKey;

    public ShowSurveyView(AppCompatActivity activity) {
        super(activity);

        mContext = activity;

        inflate(activity, R.layout.activity_show_survey, this);

        ButterKnife.bind(this);

        mTribuKey = activity.getIntent().getStringExtra(TRIBU_UNIQUE_NAME);

        mLayoutManagerShowSurvey = new LinearLayoutManager(activity);
        mLayoutManagerShowSurvey.setOrientation(LinearLayoutManager.VERTICAL);

        mRvShowSurvey.setLayoutManager(mLayoutManagerShowSurvey);


    }

    public Observable<Void> observableBtnArrowBack(){
        return RxView.clicks(mBtnArrowBack);
    }
}
