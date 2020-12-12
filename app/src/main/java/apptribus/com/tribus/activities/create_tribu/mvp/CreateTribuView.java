package apptribus.com.tribus.activities.create_tribu.mvp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.Date;

import apptribus.com.tribus.R;
import apptribus.com.tribus.pojo.Admin;
import apptribus.com.tribus.pojo.ProfileTribu;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

@SuppressLint("ViewConstructor")
public class CreateTribuView extends FrameLayout implements CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.relative_create_tribu)
    RelativeLayout mRelativeLayout;

    @BindView(R.id.til_label_description)
    TextInputLayout mLabelDescription;

    @BindView(R.id.et_name_tribu)
    EditText mEtTribuName;

    @BindView(R.id.et_description_tribu)
    EditText mEtDescription;

    @BindView(R.id.tv_unique_name)
    TextView mTvUniqueName;

    @BindView(R.id.tv_info_toggleButton)
    TextView mTvInfoToggleButton;

    @BindView(R.id.sd_image_tribu)
    SimpleDraweeView mSdTribuImage;

    @BindView(R.id.sp_thematic_tribu)
    Spinner mSpThematic;

    @BindView(R.id.btn_create)
    Button mBtnCreate;

    @BindView(R.id.ci_verify_tribu_name)
    FloatingActionButton mImageVerify;

    @BindView(R.id.ci_done)
    FloatingActionButton mImageDone;

    @BindView(R.id.ci_clear_text)
    ImageView mImageClearText;

    @BindView(R.id.tb_isPublic)
    ToggleButton mToggleButton;

    @BindView(R.id.tv_character_count)
    TextView mTvCharacterCount;

    @BindView(R.id.arrow_back)
    ImageView mBtnArrowBack;

    @BindView(R.id.iv_help)
    ImageView mIvHelp;

    @BindView(R.id.tv_link_new_admin)
    TextView mTvLinkNewAdmin;

    @BindView(R.id.tv_question_isPublic)
    TextView mTvQuestion;

    public AppCompatActivity mContext;
    private String imageUrl;
    private Tribu mTribu;
    private Admin mAdmin;
    private ProfileTribu mProfileTribu;

    public CreateTribuView(@NonNull AppCompatActivity activity) {
        super(activity);
        mContext = activity;

        inflate(activity, R.layout.activity_create_tribu, this);

        ButterKnife.bind(this);

        mProfileTribu = new ProfileTribu();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.tematicas, android.R.layout.simple_spinner_dropdown_item);
        mSpThematic.setAdapter(adapter);

        mToggleButton.setChecked(true);
        mToggleButton.setOnCheckedChangeListener(this);

    }

    public Tribu getTribu(User user) {

        //SETUP PROFILE TRIBU
        mProfileTribu.setNameTribu(mEtTribuName.getText().toString().trim());
        mProfileTribu.setThematic(mSpThematic.getSelectedItem().toString().trim());
        mProfileTribu.setDescription(mEtDescription.getText().toString().trim());
        mProfileTribu.setUniqueName(mTvUniqueName.getText().toString().trim());
        if (imageUrl == null) {
            Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + getResources().getResourcePackageName(R.mipmap.ic_tribu_default_web)
                    + '/' + getResources().getResourceTypeName(R.mipmap.ic_tribu_default_web)
                    + '/' + getResources().getResourceEntryName(R.mipmap.ic_tribu_default_web));

            mProfileTribu.setImageUrl(imageUri.toString());


        } else {
            mProfileTribu.setImageUrl(imageUrl);
        }
        mProfileTribu.setNumFollowers(1);
        mProfileTribu.setNumComments(0);
        mProfileTribu.setNumRecommendations(0);
        Date date = new Date(System.currentTimeMillis());
        mProfileTribu.setCreationDate(date);

        //SETUP ADMIN
        mAdmin = new Admin(mProfileTribu.getUniqueName(), user.getId());
        mAdmin.setDate(date);

        //SETUP TRIBU
        mTribu = new Tribu(mProfileTribu, mAdmin);

        return mTribu;
    }

    public void setImageTribu(Uri image) {
        imageUrl = String.valueOf(image);
        ControllerListener listener = new BaseControllerListener() {
            @Override
            public void onFailure(String id, Throwable throwable) {
                super.onFailure(id, throwable);
                //Log.d("Valor: ", "onFailure - View: " + throwable.toString());

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
                //Log.d("Valor: ", "onSubmit");

            }
        };
        DraweeController dc = Fresco.newDraweeControllerBuilder()
                .setUri(image)
                .setControllerListener(listener)
                .setOldController(mSdTribuImage.getController())
                .build();
        mSdTribuImage.setController(dc);
        mEtDescription.requestFocus();


    }


    //OBSERVABLES
    //EDITTEXT TRIBU NAME
    public Observable<CharSequence> observeEtTribuName() {
        return RxTextView.textChanges(mEtTribuName);
    }


    //BUTTON VERIFY
    public Observable<Void> observeImageVerify() {
        return RxView.clicks(mImageVerify);
    }


    //BUTTON CLEAN TEXT
    public Observable<Void> observeImageClearText() {
        return RxView.clicks(mImageClearText);
    }


    //BUTTON CREATE
    public Observable<Void> observeCreateTribu() {
        return RxView.clicks(mBtnCreate);
    }


    //EDITEXT TRIBU NAME
    public Observable<CharSequence> observeEditText() {
        return RxTextView.textChanges(mEtTribuName);
    }

    public Observable<Void> observeIvHelp(){
        return RxView.clicks(mIvHelp);
    }

    public Observable<Void> observeRelativeLayout(){
        return RxView.clicks(mRelativeLayout);
    }

    public Observable<Void> observeLabelDescription(){
        return RxView.clicks(mLabelDescription);
    }


    public Observable<Void> observeTvLinkNewAdmin(){
        return RxView.clicks(mTvLinkNewAdmin);
    }

    //BUTTON IMAGE
    public Observable<Void> observeImage() {
        return RxView.clicks(mSdTribuImage);
    }

    public Observable<Void> observeBtnArrowBack(){
        return RxView.clicks(mBtnArrowBack);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            mProfileTribu.setPublic(true);
            mTvInfoToggleButton.setVisibility(VISIBLE);

        } else {
            mProfileTribu.setPublic(false);
            mTvInfoToggleButton.setVisibility(GONE);
        }
    }

}
