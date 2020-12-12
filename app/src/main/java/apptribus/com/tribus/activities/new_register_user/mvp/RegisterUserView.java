package apptribus.com.tribus.activities.new_register_user.mvp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jakewharton.rxbinding.view.RxView;

import apptribus.com.tribus.R;
import apptribus.com.tribus.util.SharedPreferenceManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

/**
 * Created by User on 11/19/2017.
 */

public class RegisterUserView extends FrameLayout{

    @BindView(R.id.toolbar_register_user)
    Toolbar mToolbarRegisterUser;

    @BindView(R.id.tv_name)
    TextView mTvName;

    @BindView(R.id.tv_username)
    TextView mTvUserName;

    @BindView(R.id.arrow_back)
    ImageView mBtnArrowBack;

    @BindView(R.id.sd_image_user)
    SimpleDraweeView mSdImageUser;

    @BindView(R.id.btn_next)
    Button mBtnNext;

    AppCompatActivity mContext;
    public String mName, mUsername, mImageUser;
    public MenuItem mPrivacyPolicy;



    public RegisterUserView(@NonNull AppCompatActivity activity) {
        super(activity);
        inflate(activity, R.layout.activity_register_user, this);
        mContext = activity;

        ButterKnife.bind(this);

        mName = SharedPreferenceManager.getInstance(activity).getNome();
        mUsername = SharedPreferenceManager.getInstance(activity).getUsername();

        //SET TOOLBAR AS ACTION BAR
        //mToolbarRegisterUser.inflateMenu(R.menu.menu_register_user);
        //mPrivacyPolicy = mToolbarRegisterUser.getMenu().findItem(R.id.action_privacy_policy_register_user);

    }

    //OBSERVABLES
    public Observable<Void> observeImage(){
        return RxView.clicks(mSdImageUser);
    }

    public Observable<Void> observeBtnArrowBack(){
        return RxView.clicks(mBtnArrowBack);
    }

    public Observable<Void> observeBtnNext(){
        return RxView.clicks(mBtnNext);
    }

}
