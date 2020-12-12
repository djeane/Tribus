package apptribus.com.tribus.activities.comments.mvp;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Date;

import apptribus.com.tribus.activities.comments.view_pager.CommentsViewHolder;
import apptribus.com.tribus.pojo.Comment;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.PresenceSystemAndLastSeen;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static apptribus.com.tribus.util.Constantes.DISLIKES_TRIBUS_COMMENTS;
import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.LIKES_TRIBUS_COMMENTS;
import static apptribus.com.tribus.util.Constantes.TRIBUS_COMMENTS;

public class CommentsPresenter {

    private final CommentsView view;
    private final CommentsModel model;
    private CompositeSubscription subscription = new CompositeSubscription();
    private FirebaseRecyclerAdapter<Comment, CommentsViewHolder> mAdapter;
    private boolean response;
    private User mUser;
    public static boolean isOpen;



    //REFERENCES - FIREBASE (KEEP SYNCED)
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mReferenceTribu = mDatabase.getReference().child(GENERAL_TRIBUS);
    private DatabaseReference mReferenceUser = mDatabase.getReference().child(GENERAL_USERS);
    private DatabaseReference mReferenceTribus = mDatabase.getReference().child(GENERAL_TRIBUS);
    private DatabaseReference mTribusComments = mDatabase.getReference().child(TRIBUS_COMMENTS);
    private DatabaseReference mLikesTribusComments = mDatabase.getReference().child(LIKES_TRIBUS_COMMENTS);
    private DatabaseReference mDislikesTribusComments = mDatabase.getReference().child(DISLIKES_TRIBUS_COMMENTS);



    public CommentsPresenter(CommentsView view, CommentsModel model) {
        this.view = view;
        this.model = model;
    }

    public void onStart() {

        //KEEP SYNCED
        mReferenceTribu.keepSynced(true);
        mReferenceUser.keepSynced(true);
        mReferenceTribus.keepSynced(true);
        mTribusComments.keepSynced(true);
        mLikesTribusComments.keepSynced(true);
        mDislikesTribusComments.keepSynced(true);

        PresenceSystemAndLastSeen.presenceSystem();

        subscription.add(populateRecyclerView());
        subscription.add(observeBtnSend());
        subscription.add(observeBtnArrowBack());

        if (view.mRvComments != null && mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }

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

    public void onStop() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }

        isOpen = false;
    }

    private Subscription populateRecyclerView() {
        return model.getUser()
                .doOnNext(user -> {
                    mUser = user;
                })
                .switchMap(tribu -> model.getTribu(view.mTribuKey))
                .subscribe(tribu -> {
                    setImage(tribu);
                    setFields(tribu);
                    setAdapter(tribu);

                },
                        Throwable::printStackTrace
                );
    }

    private void setAdapter(Tribu tribu) {
        mAdapter = model.setAdapter(tribu);

        if (view.mRvComments != null && mAdapter != null) {
            view.mRvComments.setAdapter(model.setAdapter(tribu));
            model.verifyLastPosition(view.mRvComments, view.mLinearManagerComments);
        }
    }

    private Subscription observeBtnSend() {
        return view.observeBtnSend()
                .filter(__ -> {
                    response = this.validateFields(view.mEtWriteComment);
                    return response;
                })
                .observeOn(Schedulers.io())
                .switchMap(__ -> model.getUser())
                .map(this::getComment)
                .doOnNext(model::sendCommentToFirebase)
                .subscribe(__ -> {
                    view.mEtWriteComment.setText("");

                    //CLEAR FOCUS OF EDIT TEXT AND HIDE SOFT KEYBOARD
                    View view = model.activity.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)model.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                },
                        Throwable::printStackTrace
                );

    }

    private Subscription observeBtnArrowBack(){
        return view.observeBtnArrowBack()
                .subscribe(__ -> {
                    model.activity.finish();
                },
                        Throwable::printStackTrace
                );
    }



    private boolean validateFields(EditText etComment) {
        boolean isEmpty = false;
        String message = etComment.getText().toString().trim();
        if (!TextUtils.isEmpty(message)) {
            isEmpty = true;
        }
        return isEmpty;
    }

    //Create text message
    private Comment getComment(User user) {

        Comment comment = new Comment();
        comment.setComment(view.mEtWriteComment.getText().toString().trim());
        comment.setUidUser(user.getId());

        //TIMESTAMP - FIREBASE
        Date time = new Date(System.currentTimeMillis());
        comment.setData(time);
        comment.setNumDislikes(0);
        comment.setNumLikes(0);

        return comment;
    }

    //SETTING VIEWS
    //mLargeUserImage
    private void setImage(Tribu tribu){
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
                .setUri(Uri.parse(tribu.getProfile().getImageUrl()))
                .setControllerListener(listener)
                .setOldController(view.mSdLargeImageTribu.getController())
                .build();
        Log.d("Valor: ", "DraweeController - View: " + dc);
        view.mSdLargeImageTribu.setController(dc);

    }


    //SET FIELDS
    private void setFields(Tribu tribu){
        String append = "Comentários sobre " + tribu.getProfile().getNameTribu();
        view.mTvCommentsAboutTribu.setText(append);
        view.mTvNumComments.setText(String.valueOf(tribu.getProfile().getNumComments()));
    }

    public void onDestroy() {
        if (mAdapter != null) {
            mAdapter.cleanup();
            subscription.clear();
        }
    }

}
