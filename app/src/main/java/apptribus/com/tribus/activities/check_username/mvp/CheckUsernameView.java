package apptribus.com.tribus.activities.check_username.mvp;

import android.annotation.SuppressLint;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.check_username.NewCheckUsernameActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;


@SuppressLint("ViewConstructor")
public class CheckUsernameView extends FrameLayout {

    @BindView(R.id.toolbar_check_username)
    Toolbar mToolbarCheckUsername;

    @BindView(R.id.til_label_name)
    TextInputLayout mLabelName;

    @BindView(R.id.et_name)
    AppCompatEditText mEtUsername;

    @BindView(R.id.tv_username)
    TextView mTvUsername;

    @BindView(R.id.tv_info_privacy_policy)
    TextView mTvInfoPrivacyPolicy;

    @BindView(R.id.btn_check_username)
    AppCompatButton mBtnCheckUsername;

    @BindView(R.id.iv_help)
    ImageView mIvHelp;


    public AppCompatActivity mContext;


    //CONSTRUTOR
    public CheckUsernameView(NewCheckUsernameActivity activity) {
        super(activity);
        inflate(activity, R.layout.activity_check_username, this);

        mContext = activity;

        ButterKnife.bind(this);


    }


    //OBSERVABLES
    public Observable<CharSequence> observeEdittext() {
        return RxTextView.textChanges(mEtUsername);
    }

    public Observable<Void> observeIvHelp() {
        return RxView.clicks(mIvHelp);
    }

    public Observable<Void> observeButton() {
        return RxView.clicks(mBtnCheckUsername);
    }

    public Observable<Void> observeTvInfoPrivacyPolicy(){
        return RxView.clicks(mTvInfoPrivacyPolicy);
    }

}
