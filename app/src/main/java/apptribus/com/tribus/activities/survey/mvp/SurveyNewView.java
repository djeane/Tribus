package apptribus.com.tribus.activities.survey.mvp;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import apptribus.com.tribus.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.SURVEY_QUESTION;
import static apptribus.com.tribus.util.Constantes.TRIBU_KEY;
import static apptribus.com.tribus.util.Constantes.TRIBU_UNIQUE_NAME;

/**
 * Created by User on 1/23/2018.
 */

public class SurveyNewView extends CoordinatorLayout {

    @BindView(R.id.arrow_back)
    ImageView mBtnArrowBack;

    @BindView(R.id.tv_question)
    TextView mTvQuestion;

    @BindView(R.id.tv_survey)
    TextView mTvSurvey;

    @BindView(R.id.toolbar_survey)
    Toolbar mToolbarSurvey;

    @BindView(R.id.et_question)
    EditText mEtQuestionOne;

    @BindView(R.id.et_question_two)
    EditText mEtQuestionTwo;

    @BindView(R.id.et_question_three)
    EditText mEtQuestionThree;

    @BindView(R.id.et_question_four)
    EditText mEtQuestionFour;

    @BindView(R.id.et_question_five)
    EditText mEtQuestionFive;

    @BindView(R.id.tv_add_options)
    TextView mTvAddOptions;

    @BindView(R.id.tv_limit_date)
    TextView mTvLimitDate;

    @BindView(R.id.til_label_answer_three)
    TextInputLayout mTilLabelAnswerThree;

    @BindView(R.id.til_label_answer_four)
    TextInputLayout mTilLabelAnswerFour;

    @BindView(R.id.til_label_answer_five)
    TextInputLayout mTilLabelAnswerFive;

    @BindView(R.id.btn_create_survey)
    AppCompatButton mBtnCreateSurvey;

    public AppCompatActivity mContext;
    public String mSurveyQuestion;
    public String mTribuKey;

    public SurveyNewView(AppCompatActivity activity) {
        super(activity);
        mContext = activity;

        inflate(activity, R.layout.activity_survey, this);

        ButterKnife.bind(this);

        mSurveyQuestion = activity.getIntent().getStringExtra(SURVEY_QUESTION);
        mTribuKey = activity.getIntent().getStringExtra(TRIBU_KEY);
    }

    public Observable<Void> observeBtnArrowBack(){
        return RxView.clicks(mBtnArrowBack);
    }

    public Observable<Void> observeTvAddOptions(){
        return RxView.clicks(mTvAddOptions);
    }

    public Observable<Void> observeBtnCreateSurvey(){
        return RxView.clicks(mBtnCreateSurvey);
    }

    public Observable<Void> observeTvLimitDate(){
        return RxView.clicks(mTvLimitDate);
    }

}
