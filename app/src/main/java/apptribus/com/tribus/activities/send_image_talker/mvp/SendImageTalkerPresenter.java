package apptribus.com.tribus.activities.send_image_talker.mvp;

import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.PresenceSystemAndLastSeen;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.TALKERS_MESSAGES;

/**
 * Created by User on 8/16/2017.
 */

public class SendImageTalkerPresenter {

    private final SendImageTalkerView view;
    private final SendImageTalkerModel model;
    private CompositeSubscription subscription = new CompositeSubscription();
    private String description;
    private boolean response;
    public static boolean isOpen;


    //REFERENCES - FIREBASE (KEEP SYNCED)
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mReferenceUser = mDatabase.getReference().child(GENERAL_USERS);
    private DatabaseReference mRefUsersTalkMessage = mDatabase.getReference().child(TALKERS_MESSAGES);



    public SendImageTalkerPresenter(SendImageTalkerView view, SendImageTalkerModel model) {
        this.view = view;
        this.model = model;
    }

    public void onStart() {

        //KEEP SYNCED
        mReferenceUser.keepSynced(true);
        mRefUsersTalkMessage.keepSynced(true);


        PresenceSystemAndLastSeen.presenceSystem();

        view.mBtnSendMessage.setEnabled(true);

        subscription.add(observeBtnSendImage());
        subscription.add(observeTalker());
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

    private Subscription observeBtnArrowBack(){
        return view.observeBtnArrowBack()
                .subscribe(__ -> {
                            if (view.mCameFrom != null) {
                                if (view.mCameFrom.equals("fromCamTalkerActivity")) {
                                    model.backToCamTalker(view.mTalkerKey);
                                } else if (view.mCameFrom.equals("fromShareActivity")) {
                                    model.backToChatTalker(view.mTalkerKey);
                                }
                            } else {
                                model.backToChatUserActivity();
                            }
                        },
                        Throwable::printStackTrace
                );
    }

    private Subscription observeTalker() {
        return model.getTalker(view.mTalkerKey)
                .subscribe(talker -> {
                    if (talker != null) {
                        setFields(talker);
                        if (talker.getThumb() != null) {
                            setImage(talker.getThumb());
                        }
                        else {
                            setImage(talker.getImageUrl());
                        }

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
                .switchMap(user -> model.getCurrentUser())
                .doOnNext(user -> {
                    if (user != null) {
                        model.uploadImageToFirebase(user, view.mUri, description, view.mFileSize);
                    }

                })
                .subscribe();
    }

    private void getDescription(){
        description = view.mEtDescription.getText().toString().trim();
    }


    //SET NAME'S TRIBU
    private void setFields(User talker) {
        view.mTalkersName.setText(talker.getName());
        view.mTalkerUsername.setText(talker.getUsername());
    }

    //SETTING IMAGE
    private void setImage(String imageUrl) {
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
                .setUri(Uri.parse(imageUrl))
                .setControllerListener(listener)
                .setOldController(view.mCircleTalkerImage.getController())
                .build();
        view.mCircleTalkerImage.setController(dc);

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

    public void onDestroy() {
        subscription.clear();
    }

}
