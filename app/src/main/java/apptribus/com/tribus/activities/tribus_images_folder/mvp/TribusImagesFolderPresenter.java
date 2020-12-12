package apptribus.com.tribus.activities.tribus_images_folder.mvp;

import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

import apptribus.com.tribus.activities.chat_tribu.adapter.ChatTribuAdapter;
import apptribus.com.tribus.activities.tribus_images_folder.adapter.TribusImagesFolderAdapter;
import apptribus.com.tribus.activities.tribus_images_folder.adapter.TribusVideosFolderAdapter;
import apptribus.com.tribus.activities.tribus_images_folder.view_holder.TribusImagesFolderViewHolder;
import apptribus.com.tribus.pojo.MessageUser;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.PresenceSystemAndLastSeen;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.TRIBUS_MESSAGES;

/**
 * Created by User on 9/23/2017.
 */

public class TribusImagesFolderPresenter {

    private final TribusImagesFolderView view;
    private final TribusImagesFolderModel model;
    private CompositeSubscription subscription = new CompositeSubscription();
    private FirebaseRecyclerAdapter<MessageUser, TribusImagesFolderViewHolder> mAdapter;
    public static boolean isOpen;
    private Tribu mTribu;

    //ADAPTER IMAGES
    private List<MessageUser> mMessagesList = new ArrayList<>();
    private TribusImagesFolderAdapter mTribusImagesFolderAdapter;

    //ADAPTER VIDEOS
    private List<MessageUser> mMessagesVideosList = new ArrayList<>();
    private TribusVideosFolderAdapter mTribusVideosFolderAdapter;

    //COMMON VARIABLES TO LOAD IMAGES AND VIDEOS
    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private int mCurrentPage = 1;



    //REFERENCES - FIREBASE (KEEP SYNCED)
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mReferenceTribu = mDatabase.getReference().child(GENERAL_TRIBUS);
    private DatabaseReference mRefTribusMessage = mDatabase.getReference().child(TRIBUS_MESSAGES);



    public TribusImagesFolderPresenter(TribusImagesFolderView view, TribusImagesFolderModel model) {
        this.view = view;
        this.model = model;
    }

    public void onStart(){

        //KEEP SYNCED
        mReferenceTribu.keepSynced(true);
        mRefTribusMessage.keepSynced(true);

        PresenceSystemAndLastSeen.presenceSystem();


        if(view.mIntentExtra.equals("image")){
            setAdapter();
        }
        else if(view.mIntentExtra.equals("video")){
            setTribusVideosFolderAdapter();
        }

        if (view.mIntentExtra.equals("image")){
            subscription.add(getTribu());
        }
        else if(view.mIntentExtra.equals("video")){
            subscription.add(getTribuToLoadVideos());
        }
        subscription.add(observeBtnArrowBack());


        isOpen = true;

    }

    public void onPause() {
        PresenceSystemAndLastSeen.lastSeen();

    }

    public void onStop() {
        model.removeListeners();
        isOpen = false;
    }

    public void onRestart() {
        PresenceSystemAndLastSeen.presenceSystem();
    }

    public void onResume() {
        PresenceSystemAndLastSeen.presenceSystem();
    }


    private Subscription getTribu(){
        return model.getTribu(view.mTribusKey)
                .doOnNext(tribu -> {
                    mTribu = tribu;
                    model.loadImages(view.mTribusKey, mTribusImagesFolderAdapter, mMessagesList,
                            view.mRvImagesFolder, TOTAL_ITEMS_TO_LOAD, mCurrentPage);
                })
                .subscribe(tribu -> {
                            setToolbarImage(mTribu);
                            setToolbarTitle(mTribu);
                            setTvNamesFolder();
                        },
                        Throwable::printStackTrace
                );
    }

    private Subscription getTribuToLoadVideos(){
        return model.getTribu(view.mTribusKey)
                .doOnNext(tribu -> {
                    mTribu = tribu;
                    model.loadVideos(view.mTribusKey, mTribusVideosFolderAdapter, mMessagesVideosList,
                            view.mRvImagesFolder, TOTAL_ITEMS_TO_LOAD, mCurrentPage);
                })
                .subscribe(tribu -> {
                            setToolbarImage(mTribu);
                            setToolbarTitle(mTribu);
                            setTvNamesFolder();
                        },
                        Throwable::printStackTrace
                );
    }


    private Subscription observeBtnArrowBack() {
        return view.observeBtnArrowBack()
                .subscribe(__ -> {

                            view.mContext.finish();
                            /*if (view.mCameFrom != null) {
                                if (view.mCameFrom.equals("fromProfileTribuUser")) {
                                    model.backToProfileTribuUserActivity();
                                }
                                else if (view.mCameFrom.equals("fromProfileTribuAdmin")) {
                                    model.backToProfileTribuAdminActivity();
                                }
                                else if (view.mCameFrom.equals("fromProfileTribuFollower")){
                                    model.backToProfileTribuFollowerActivity();
                                }
                                else {
                                    model.backToMainActivity();
                                }
                            }
                            else {
                                model.backToMainActivity();
                            }*/
                        },
                        Throwable::printStackTrace
                );
    }

    private void setAdapter() {
        mTribusImagesFolderAdapter = new TribusImagesFolderAdapter(view.mContext, mMessagesList, view.mTribusKey);

        view.mRvImagesFolder.setAdapter(mTribusImagesFolderAdapter);

    }

    private void setTribusVideosFolderAdapter() {
        mTribusVideosFolderAdapter = new TribusVideosFolderAdapter(view.mContext, mMessagesVideosList, view.mTribusKey);

        view.mRvImagesFolder.setAdapter(mTribusVideosFolderAdapter);

    }


    private void setToolbarImage(Tribu tribu){
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
                .setOldController(view.mTribuImage.getController())
                .build();
        view.mTribuImage.setController(dc);

    }

    private void setToolbarTitle(Tribu tribu){
        view.mTvTribusName.setText(tribu.getProfile().getNameTribu());
    }

    private void setTvNamesFolder(){
        if(view.mIntentExtra.equals("image")){
            view.mTvNamesFolder.setText("Imagens");
        }
        else {
            view.mTvNamesFolder.setText("Vídeos");
        }

    }

    public void onDestroy(){
        subscription.clear();
        model.removeListeners();
    }
}
