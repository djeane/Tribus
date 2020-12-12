package apptribus.com.tribus.activities.new_register_user.mvp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.enums.EPickType;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.main_activity.MainActivity;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by User on 11/19/2017.
 */

public class RegisterUserPresenter{

    private final RegisterUserView view;
    private final RegisterUserModel model;
    private CompositeSubscription subscription = new CompositeSubscription();
    private boolean response;
    private Uri mImageUserUri;
    public PickSetup setup = new PickSetup();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();



    public RegisterUserPresenter(RegisterUserView view, RegisterUserModel model) {
        this.view = view;
        this.model = model;
    }

    public void onStart(){

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            openMainActivity();
        }
        else {
            setViews();
            subscription.add(observerImage());
            subscription.add(observeBtnArrowBack());
            subscription.add(observeBtnNext());

        }
    }

    private void setViews(){
        view.mTvName.setText(view.mName);
        view.mTvUserName.setText(view.mUsername);
    }

    private void openMainActivity(){
        Intent intent = new Intent(view.mContext, MainActivity.class);
        view.mContext.startActivity(intent);
        view.mContext.finish();
    }


    private Subscription observerImage() {
        return view.observeImage()
                .doOnNext(__ -> model.requirePermission())
                .filter(__ -> true)
                //.doOnNext(__ -> showDialogToChooseImage())
                .subscribe(__ -> {
                            showDialogToChooseImage();
                            //model.getImage();
                        },
                        Throwable::printStackTrace);
    }

    public void showDialogToChooseImage() {

        setup.setTitle("Escolha")
                .setTitleColor(view.getResources().getColor(R.color.primary_text))
                .setBackgroundColor(Color.WHITE)
                .setCancelText("Cancelar")
                .setFlip(true)
                .setMaxSize(500)
                .setPickTypes(EPickType.GALLERY, EPickType.CAMERA)
                .setCameraButtonText("Câmera")
                .setGalleryButtonText("Galeria de Imagens")
                .setIconGravity(Gravity.LEFT)
                .setButtonOrientation(LinearLayoutCompat.VERTICAL)
                .setSystemDialog(false)
                .setCameraIcon(R.mipmap.camera_colored)
                .setGalleryIcon(R.mipmap.gallery_colored);

        PickImageDialog.build(setup).show(view.mContext);
    }

    public void getImage() {
        model.getImage();
    }

    public void setImageUser(Uri image) {
        mImageUserUri = image;
        view.mImageUser = String.valueOf(image);
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
                .setOldController(view.mSdImageUser.getController())
                .build();
        view.mSdImageUser.setController(dc);
    }

    private Subscription observeBtnArrowBack() {
        return view.observeBtnArrowBack()
                .subscribe(
                        __ -> view.mContext.finish(),
                        Throwable::printStackTrace);
    }

    private Subscription observeBtnNext(){
        return view.observeBtnNext()
                .subscribe(
                        __ -> model.openPhoneNumberAuthentication(mImageUserUri, view.mName, view.mUsername),
                        Throwable::printStackTrace);
    }

    public void onDestroy(){
        subscription.clear();
    }

}

