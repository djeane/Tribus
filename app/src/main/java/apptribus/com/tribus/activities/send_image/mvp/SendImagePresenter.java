package apptribus.com.tribus.activities.send_image.mvp;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;

import apptribus.com.tribus.R;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.PresenceSystemAndLastSeen;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.IMAGES_USERS;
import static apptribus.com.tribus.util.Constantes.TRIBUS_MESSAGES;

/**
 * Created by User on 7/12/2017.
 */

public class SendImagePresenter {

    private final SendImageView view;
    private final SendImageModel model;
    private CompositeSubscription subscription = new CompositeSubscription();
    private String description;
    private boolean response;
    public static boolean isOpen;


    //REFERENCES - FIREBASE (KEEP SYNCED)
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mReferenceTribu = mDatabase.getReference().child(GENERAL_TRIBUS);
    private DatabaseReference mReferenceUser = mDatabase.getReference().child(GENERAL_USERS);
    private DatabaseReference mRefTribusMessage = mDatabase.getReference().child(TRIBUS_MESSAGES);


    public SendImagePresenter(SendImageView view, SendImageModel model) {
        this.view = view;
        this.model = model;
    }

    public void onStart() {
        //KEEP SYNCED
        mReferenceTribu.keepSynced(true);
        mReferenceUser.keepSynced(true);
        mRefTribusMessage.keepSynced(true);

        PresenceSystemAndLastSeen.presenceSystem();
        view.mBtnSendMessage.setEnabled(true);

        subscription.add(observeBtnSendImage());
        subscription.add(observeTribu());
        subscription.add(observeBtnArrowBack());

        setSimpleDraweeView();

        isOpen = true;


    }

    public void onPause(){
        PresenceSystemAndLastSeen.lastSeen();
    }

    public void onRestart(){
        PresenceSystemAndLastSeen.presenceSystem();
    }

    public void onResume(){
        PresenceSystemAndLastSeen.presenceSystem();
    }

    public void onStop(){
        isOpen = false;
    }

    private Subscription observeTribu() {
        return model.getTribu(view.mTribuKey)
                .subscribe(tribu -> {
                    setFields(tribu);
                    setImage(tribu);
                },
                        Throwable::printStackTrace
                );
    }


    private Subscription observeBtnArrowBack(){
        return view.observeBtnArrowBack()
                .subscribe(__ -> {
                            if (view.mCameFrom != null) {
                                if (view.mCameFrom.equals("fromCamActivityChatTribu")) {
                                    model.backToCam(view.mTribuKey, view.mTopicKey);
                                } else if (view.mCameFrom.equals("fromShareActivity")) {
                                    model.backToChatTribu(view.mTribuKey, view.mTopicKey);
                                }
                            } else {
                                model.backToChatActivity();
                            }
                        },
                        Throwable::printStackTrace
                );
    }

    private Subscription observeBtnSendImage() {
        return view.observeBtnSendImage()
                .filter(__ -> {
                    if(!ShowSnackBarInfoInternet.checkConnectionAnother()){
                        ShowSnackBarInfoInternet.showToastInfoInternet(view.mContext);
                        return false;
                    }
                    else {
                        ShowSnackBarInfoInternet.showSnack(true, view);
                        return true;
                    }
                })
                .doOnNext(__ -> view.mBtnSendMessage.setEnabled(false))
                .doOnNext(__ -> getDescription())
                .switchMap(user -> model.getUser())
                .doOnNext(user -> model.uploadImageToFirebase(user, view.mUri, description, view.mFileSize, view.mTopicKey, view.mTribuKey))
                .subscribe();
    }

    private void getDescription(){
        description = view.mEtDescription.getText().toString().trim();
    }

    //SET NAME'S TRIBU
    private void setFields(Tribu tribu) {
        view.mTribuName.setText(tribu.getProfile().getNameTribu());
        view.mTribuUniqueName.setText(tribu.getProfile().getUniqueName());
    }

    //SETTING IMAGE
    private void setImage(Tribu tribu) {
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
                .setUri(Uri.parse(tribu.getProfile().getImageUrl()))
                .setControllerListener(listener)
                .setOldController(view.mCircleTribuImage.getController())
                .build();
        view.mCircleTribuImage.setController(dc);

    }

    private void setSimpleDraweeView() {
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
                .setUri(view.mUri)
                .setControllerListener(listener)
                .setOldController(view.mSimpleDraweeViewShowImage.getController())
                .build();
        view.mSimpleDraweeViewShowImage.setController(dc);

    }


    public void onDestroy(){
        subscription.clear();
    }
}
