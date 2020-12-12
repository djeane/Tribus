package apptribus.com.tribus.activities.register_user.mvp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.jakewharton.rxbinding.view.RxView;

import java.util.Date;

import apptribus.com.tribus.R;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.SharedPreferenceManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

/**
 * Created by User on 5/20/2017.
 */
@SuppressLint("ViewConstructor")
public class UserRegisterView extends FrameLayout implements CompoundButton.OnCheckedChangeListener {

    //@BindView(R.id.arrow_back)
    //ImageView mBtnArrowBack;

    @BindView(R.id.tv_name)
    TextView mtvNome;

    @BindView(R.id.tv_username)
    TextView mTvUsername;

    @BindView(R.id.sd_image_user)
    SimpleDraweeView mImageUser;

    //@BindView(R.id.tv_age)
    //TextView mTvAge;

    //@BindView(R.id.et_about_me)
    //EditText mEtAboutMe;

    @BindView(R.id.et_password)
    EditText mEtSenha;

    @BindView(R.id.et_telefone)
    EditText mEtTelefone;

    @BindView(R.id.et_email)
    EditText mEtEmail;

    //@BindView(R.id.sp_gender)
    //Spinner mSpGender;

    @BindView(R.id.tv_information)
    TextView mTvInformation;

    @BindView(R.id.tb_accept_invitation)
    ToggleButton mToggleButton;

    @BindView(R.id.btn_cadastrar)
    Button mBtnCadastrar;

    private String name, username, imageUser;
    private User mUser = new User();
    private ProgressDialog progress;
    public AppCompatActivity mContext;



    //CONSTRUCTOR
    public UserRegisterView(AppCompatActivity activity) {
        super(activity);
        mContext = activity;

        inflate(activity, R.layout.activity_user_register, this);

        ButterKnife.bind(this);

        progress = new ProgressDialog(activity);

        mToggleButton.setOnCheckedChangeListener(this);

        name = SharedPreferenceManager.getInstance(activity).getNome();
        username = SharedPreferenceManager.getInstance(activity).getUsername();
        setViews();


    }


    //GET DATA
    public User getUser(){
        Log.d("Valor: ", "entrou em getAdmin - View");

        //String aboutMe = mEtAboutMe.getText().toString().trim();
        String phoneNumber = mEtTelefone.getText().toString().trim();
        String password = mEtSenha.getText().toString().trim();
        String email = mEtEmail.getText().toString().trim();

        mUser.setUsername(username);
        mUser.setName(name);
        //mUser.setAboutMe(aboutMe);
        mUser.setEmail(email);
        mUser.setPassword(password);
        mUser.setPhoneNumber(phoneNumber);
        if(imageUser == null){
            Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + getResources().getResourcePackageName(R.mipmap.ic_user_default_web)
                    + '/' + getResources().getResourceTypeName(R.mipmap.ic_user_default_web)
                    + '/' + getResources().getResourceEntryName(R.mipmap.ic_user_default_web));

            mUser.setImageUrl(imageUri.toString());
        }
        else {
            mUser.setImageUrl(imageUser);
        }

        //TIMESTAMP - FIREBASE
        Date date = new Date(System.currentTimeMillis());
        mUser.setDate(date);
        mUser.setOnline(true);

        return mUser;
    }


    //SET DATA AND IMAGE
    public void setViews(){
        mtvNome.setText(name);
        mTvUsername.setText(username);
    }

    public void setImageUser(Uri image){
        Log.d("Valor: ", "image - View: " + image);
        imageUser = String.valueOf(image);
        ControllerListener listener = new BaseControllerListener() {
            @Override
            public void onFailure(String id, Throwable throwable) {
                super.onFailure(id, throwable);
                Log.d("Valor: ", "onFailure - View: " + throwable.toString());

            }

            @Override
            public void onIntermediateImageFailed(String id, Throwable throwable) {
                super.onIntermediateImageFailed(id, throwable);
            }

            @Override
            public void onFinalImageSet(String id, Object imageInfo, Animatable animatable) {
                super.onFinalImageSet(id, imageInfo, animatable);
            }

            @Override
            public void onIntermediateImageSet(String id, Object imageInfo) {
                super.onIntermediateImageSet(id, imageInfo);
            }

            @Override
            public void onRelease(String id) {
                super.onRelease(id);
            }

            @Override
            public void onSubmit(String id, Object callerContext) {
                super.onSubmit(id, callerContext);
                Log.d("Valor: ", "onSubmit");

            }
        };
        DraweeController dc = Fresco.newDraweeControllerBuilder()
                .setUri(image)
                .setControllerListener(listener)
                .setOldController(mImageUser.getController())
                .build();
        Log.d("Valor: ", "DraweeController - View: " + dc);
        mImageUser.setController(dc);
    }


    //SHOW MESSAGE AND LOADING
    public void showMessage(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    public void showLoading(boolean loading){
        progress.setMessage("Aguarde, enquanto seu cadastro é finalizado...");
        progress.setCancelable(false);

        if(loading){
            progress.show();
        }
        else {
            progress.dismiss();
        }
    }


    //OBSERVABLES
    public Observable<Void> observeImage(){
        return RxView.clicks(mImageUser);
    }

    public Observable<Void> observeBtnCadastrar(){
        return RxView.clicks(mBtnCadastrar);
    }

    /*public Observable<Void> observeBtnAge(){
        return RxView.clicks(mTvAge);
    }*/

    /*public Observable<Void> observableBtnArrowBack(){
        return RxView.clicks(mBtnArrowBack);
    }*/

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked){
            mUser.setAccepted(true);
            mTvInformation.setVisibility(VISIBLE);

        }
        else {
            mUser.setAccepted(false);
            mTvInformation.setVisibility(GONE);
        }
    }
}
