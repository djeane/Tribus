package apptribus.com.tribus.activities.conversation_topics.mvp;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.URLSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.conversation_topics.view_holder.ConversationTopicVH;
import apptribus.com.tribus.pojo.ConversationTopic;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.PresenceSystemAndLastSeen;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.TRIBUS_TOPICS;


public class ConversationTopicsPresenter implements Toolbar.OnMenuItemClickListener {

    private final ConversationTopicsView view;
    private final ConversationTopicsModel model;
    private CompositeSubscription subscription = new CompositeSubscription();
    private MenuItem mAddFollowers;
    private MenuItem mLeaveTribu;
    private MenuItem mChangeAdmin;
    private MenuItem mProfileTribuItem;
    private Tribu mTribu;
    private User mUser;
    private FirebaseRecyclerAdapter<ConversationTopic, ConversationTopicVH> mAdapter;
    private String mLinkPresenter;
    private String mLinkPresenterNoChanges;

    //REFERENCES - FIREBASE(KEEP SYNCED)
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mReferenceTribu = mDatabase.getReference().child(GENERAL_TRIBUS);
    private DatabaseReference mRefTribusTopics = mDatabase.getReference().child(TRIBUS_TOPICS);

    public ConversationTopicsPresenter(ConversationTopicsView view, ConversationTopicsModel model) {
        this.view = view;
        this.model = model;
    }


    public void onStart(){
        //KEEP SYNCED
        mReferenceTribu.keepSynced(true);
        mRefTribusTopics.keepSynced(true);


        PresenceSystemAndLastSeen.presenceSystem();

        //model.createConversationTopic(view.mTribuUniqueName, mTopicKey);

        subscription.add(observeTribu());
        subscription.add(hasChildren());
        subscription.add(observeUser());
        //subscription.add(observeToolbarClicks());
        subscription.add(observeTribuImage());
        subscription.add(observeBtnArrowBack());
        subscription.add(observeFab());

    }

    public void onResume() {

        PresenceSystemAndLastSeen.presenceSystem();

        if (view.mRvTopic != null && mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }


    public void onPause() {
        PresenceSystemAndLastSeen.lastSeen();

    }

    public void onRestart() {
        PresenceSystemAndLastSeen.presenceSystem();
    }


    public void onStop(){
        model.removeListeners();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private Subscription observeTribu() {
        return model.getTribu(view.mTribuKey)
                .subscribe(tribu -> {
                            mTribu = tribu;
                            setImage(tribu);
                            setToolbarTitleAndSubtitle(tribu);
                            view.mToolbarConversationTopic.getMenu().clear();
                            view.mToolbarConversationTopic.inflateMenu(R.menu.menu_conversation_topic);
                            mLeaveTribu = view.mToolbarConversationTopic.getMenu().findItem(R.id.leave_tribu);
                            mChangeAdmin = view.mToolbarConversationTopic.getMenu().findItem(R.id.change_admin);
                            mAddFollowers = view.mToolbarConversationTopic.getMenu().findItem(R.id.add_followers);
                            mProfileTribuItem = view.mToolbarConversationTopic.getMenu().findItem(R.id.action_profile_tribu_activity);
                            view.mToolbarConversationTopic.setOnMenuItemClickListener(this);
                            if (tribu.getAdmin().getUidAdmin().equals(mAuth.getCurrentUser().getUid())) {
                                mLeaveTribu.setVisible(false);
                            } else {
                                mAddFollowers.setVisible(false);
                                mChangeAdmin.setVisible(false);
                                mProfileTribuItem.setVisible(false);
                            }

                        },
                        Throwable::printStackTrace
                );
    }

    private Subscription hasChildren() {
        return model.hasChildren(view.mTribuKey)
                .subscribe(hasChildren -> {
                            if (!hasChildren) {
                                view.mRvTopic.setVisibility(View.GONE);
                                TextView tv = new TextView(view.getContext());
                                tv.setText(R.string.hasChildrenConversationTopic);
                                tv.setId(R.id.text);
                                tv.setTextSize(16);
                                tv.setTextColor(view.getResources().getColor(R.color.colorPrimaryDark));

                                tv.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                                        RelativeLayout.LayoutParams.WRAP_CONTENT));
                                tv.setGravity(Gravity.CENTER);

                                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) view.mCoordinatorTopic.getLayoutParams();

                                setViewMargins(view.getContext(), params, 0, 0, 0, 0, tv);

                                view.mRelativeRecyler.addView(tv);
                            } else {
                                if (view.mRelativeRecyler.findViewById(R.id.text) != null) {
                                    view.mRvTopic.setVisibility(View.VISIBLE);
                                    view.mRelativeRecyler.removeView(view.mRelativeRecyler.findViewById(R.id.text));
                                } else {
                                    view.mRvTopic.setVisibility(View.VISIBLE);
                                }

                            }
                        },

                        Throwable::printStackTrace
                );
    }

    private void setViewMargins(Context con, ViewGroup.LayoutParams params,
                                int left, int top, int right, int bottom, View view) {

        final float scale = con.getResources().getDisplayMetrics().density;
        // convert the DP into pixel
        int pixel_left = (int) (left * scale + 0.5f);
        int pixel_top = (int) (top * scale + 0.5f);
        int pixel_right = (int) (right * scale + 0.5f);
        int pixel_bottom = (int) (bottom * scale + 0.5f);

        ViewGroup.MarginLayoutParams s = (ViewGroup.MarginLayoutParams) params;
        s.setMargins(pixel_left, pixel_top, pixel_right, pixel_bottom);

        view.setLayoutParams(params);
    }


    private Subscription observeTribuImage() {
        return view.observableTribuImage()
                .subscribe(__ -> {
                            view.mContext.finish();
                        },
                        Throwable::printStackTrace
                );
    }

    private Subscription observeToolbarClicks() {
        return view.observeToolbarClicks()
                .subscribe(__ -> {
                            if (mTribu.getAdmin().getUidAdmin().equals(mAuth.getCurrentUser().getUid())) {
                                model.openProfileTribuAdminActivity(mTribu);
                            } else {
                                model.openDetailTribu(mTribu);
                            }

                        },
                        Throwable::printStackTrace
                );
    }

    private Subscription observeBtnArrowBack() {
        return view.observeBtnArrowBack()
                .subscribe(__ -> {
                            view.mContext.finish();
                        },
                        Throwable::printStackTrace
                );
    }

    private Subscription observeFab(){
        return view.observeFAB()
                .doOnNext(__ -> openDialogTopic(mTribu))
                .subscribe();
    }

    private Subscription observeUser() {
        return model.getUser()
                .subscribe(
                        this::setAdapter,
                        Throwable::printStackTrace
                );
    }


    private void openDialogTopic(Tribu tribu) {

        if(mTribu != null) {
            //CONFIGURATION OF DIALOG
            AlertDialog.Builder builder = new AlertDialog.Builder(view.mContext, R.style.MyDialogTheme);
            LayoutInflater inflater = view.mContext.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_topic, null);

            EditText mEtTopic = dialogView.findViewById(R.id.et_topic);
            TextView mTvCharacterCount = dialogView.findViewById(R.id.tv_character_count);
            Button mBtnCancel = dialogView.findViewById(R.id.btn_cancel);
            Button mBtnCreateTopic = dialogView.findViewById(R.id.btn_create_topic);

            builder.setView(dialogView);

            AlertDialog dialog = builder.create();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            WindowManager.LayoutParams wmlp = dialog.getWindow()
                    .getAttributes();
            wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
            wmlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
            wmlp.gravity = WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;
            dialog.getWindow().setGravity(wmlp.gravity);

            dialog.show();

            mEtTopic.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    String appendCount = String.valueOf(s.length()) + "/50";
                    mTvCharacterCount.setText(appendCount);

                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            //CLICK LISTENER TO BUTTONS
            mBtnCreateTopic.setOnClickListener(v -> {
                //CHECK INTERNET CONNECTION
                if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                    ShowSnackBarInfoInternet.showToastInfoInternet(view.mContext);
                } else {
                    ShowSnackBarInfoInternet.showSnack(true, view);

                    String topic = mEtTopic.getText().toString().trim();
                    if (topic.equals("")){
                        Toast.makeText(view.mContext, "Por favor, informe o assunto do tópico.", Toast.LENGTH_LONG).show();
                    }
                    else {
                        ConversationTopic conversationTopic = new ConversationTopic();
                        conversationTopic.setTopic(topic);

                        model.sendTopicToFirebase(tribu, conversationTopic);
                    }

                    dialog.dismiss();

                }
            });

            mBtnCancel.setOnClickListener(v -> {
                dialog.dismiss();
            });

        }
    }



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

    private void setToolbarTitleAndSubtitle(Tribu tribu) {

        view.mTvNameOfTribu.setText(tribu.getProfile().getNameTribu());
        view.mTvUniqueName.setText(tribu.getProfile().getUniqueName());

    }

    private void setAdapter(User user) {
        mAdapter = model.setRecyclerView(mTribu, view);

        if (view.mRvTopic != null && mAdapter != null) {
            view.mRvTopic.setAdapter(model.setRecyclerView(mTribu, view));
        }
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {

        int id = item.getItemId();

        if (id == mAddFollowers.getItemId()) {
            model.openDetailTribuAddFollowers(mTribu);

        }
        else if (id == mLeaveTribu.getItemId()) {
            if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                ShowSnackBarInfoInternet.showToastInfoInternet(view.mContext);
            }
            else {
                model.leaveTribu();
            }
        }
        else if (id == mChangeAdmin.getItemId()) {
            model.openChangeAdminActivity(mTribu);

        }
        else if (id == mProfileTribuItem.getItemId()) {
            model.openProfileTribuAdminActivity(mTribu);
        }

        return true;
    }


    public void onDestroy() {
        if (mAdapter != null) {
            mAdapter.cleanup();
            subscription.clear();
            model.removeListeners();
        }
    }
}
