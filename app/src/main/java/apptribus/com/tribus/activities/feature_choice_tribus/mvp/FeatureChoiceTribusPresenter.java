package apptribus.com.tribus.activities.feature_choice_tribus.mvp;

import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.google.firebase.auth.FirebaseAuth;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_container.FragmentContainer;
import apptribus.com.tribus.pojo.ConversationTopic;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.util.PresenceSystemAndLastSeen;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static apptribus.com.tribus.util.Constantes.CAMPAIGN;
import static apptribus.com.tribus.util.Constantes.EVENT;
import static apptribus.com.tribus.util.Constantes.SHARED_MEDIA;
import static apptribus.com.tribus.util.Constantes.SURVEY;
import static apptribus.com.tribus.util.Constantes.TOPIC;

public class FeatureChoiceTribusPresenter implements Toolbar.OnMenuItemClickListener {

    private final FeatureChoiceTribusView view;
    private final FeatureChoiceTribusModel model;
    private MenuItem mMenuItemLeaveTribu;
    private MenuItem mMenuItemProfileTribuFollower;
    private MenuItem mMenuItemChangeAdmin;
    private MenuItem mMenuItemProfileTribuAdmin;
    private Tribu mTribu;
    private CompositeSubscription subscription = new CompositeSubscription();
    public static boolean isOpen;

    private FirebaseAuth mAuth;

    public FeatureChoiceTribusPresenter(FeatureChoiceTribusView view, FeatureChoiceTribusModel model) {
        this.view = view;
        this.model = model;
    }

    public void onStart() {

        mAuth = FirebaseAuth.getInstance();

        PresenceSystemAndLastSeen.presenceSystem();

        subscription.add(observeTribu());
        subscription.add(observeFabAddTopic());
        subscription.add(observeFabAddSurvey());
        subscription.add(observeBtnArrowBack());
        isOpen = true;

    }

    public void onStop() {
        isOpen = false;
    }

    public void onPause() {
        PresenceSystemAndLastSeen.lastSeen();
    }

    public void onRestart() {
        PresenceSystemAndLastSeen.presenceSystem();
    }

    public void onResume() {
        PresenceSystemAndLastSeen.presenceSystem();

    }

    private Subscription observeTribu() {
        return model.getTribu(view.mTribuKey)
                .subscribe(tribu -> {
                            mTribu = tribu;
                            setImage(tribu);
                            setToolbarTitleAndSubtitle(tribu);
                            view.mToolbarFeatureChoice.getMenu().clear();
                            view.mToolbarFeatureChoice.inflateMenu(R.menu.menu_conversation_topic);
                            mMenuItemLeaveTribu = view.mToolbarFeatureChoice.getMenu().findItem(R.id.leave_tribu);
                            mMenuItemChangeAdmin = view.mToolbarFeatureChoice.getMenu().findItem(R.id.change_admin);
                            mMenuItemProfileTribuAdmin = view.mToolbarFeatureChoice.getMenu().findItem(R.id.action_profile_tribu_activity);
                            mMenuItemProfileTribuFollower = view.mToolbarFeatureChoice.getMenu().findItem(R.id.profile_tribu);
                            view.mToolbarFeatureChoice.setOnMenuItemClickListener(this);
                            if (tribu.getAdmin().getUidAdmin().equals(mAuth.getCurrentUser().getUid())) {
                                mMenuItemLeaveTribu.setVisible(false);
                                mMenuItemProfileTribuFollower.setVisible(false);
                            } else {
                                mMenuItemProfileTribuFollower.setVisible(true);
                                mMenuItemChangeAdmin.setVisible(false);
                                mMenuItemProfileTribuAdmin.setVisible(false);
                            }


                        },
                        Throwable::printStackTrace
                );
    }

    private Subscription observeBtnArrowBack() {
        return view.observeBtnArrowBack()
                .subscribe(
                        __ -> model.backToMainActivity(),
                        Throwable::printStackTrace
                );
    }

    private Subscription observeFabAddTopic() {
        return view.observeFabAddTopic()
                .doOnNext(__ -> openDialogTopic(mTribu))
                .subscribe();
    }

    private Subscription observeFabAddSurvey() {
        return view.observeFabAddSurvey()
                .doOnNext(__ -> openSurveyDialog())
                .subscribe();
    }

    private void openSurveyDialog() {

        if (mTribu != null) {
            //CONFIGURATION OF DIALOG
            AlertDialog.Builder builder = new AlertDialog.Builder(view.mContext, R.style.MyDialogTheme);
            LayoutInflater inflater = view.mContext.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_survey, null);

            EditText mEtQuestion = dialogView.findViewById(R.id.et_question);
            TextView mTvCharacterCount = dialogView.findViewById(R.id.tv_character_count);
            Button mBtnCancel = dialogView.findViewById(R.id.btn_cancel);
            Button mBtnCreateQuestion = dialogView.findViewById(R.id.btn_create_question);

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

            mEtQuestion.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    String appendCount = String.valueOf(s.length()) + "/100";
                    mTvCharacterCount.setText(appendCount);

                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            //CLICK LISTENER TO BUTTONS
            mBtnCreateQuestion.setOnClickListener(v -> {
                //CHECK INTERNET CONNECTION
                if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                    ShowSnackBarInfoInternet.showToastInfoInternet(view.mContext);
                } else {
                    ShowSnackBarInfoInternet.showSnack(true, view);

                    String question = mEtQuestion.getText().toString().trim();


                    if (question.equals("")) {
                        Toast.makeText(view.mContext,
                                view.mContext.getResources().getString(R.string.toast_alert_empty_survey_question),
                                Toast.LENGTH_LONG).show();
                    } else {
                        model.openSurveyActivity(mTribu, question);

                    }

                    dialog.dismiss();

                }
            });

            mBtnCancel.setOnClickListener(v -> {
                dialog.dismiss();
            });
        }
    }


    private void openDialogTopic(Tribu tribu) {

        if (mTribu != null) {
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

                    String appendCount = String.valueOf(s.length())
                            + view.mContext.getResources().getString(R.string.topic_characters_length);
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
                    if (topic.equals("")) {
                        Toast.makeText(view.mContext,
                                view.mContext.getResources().getString(R.string.toast_alert_empty_topic_subject),
                                Toast.LENGTH_LONG).show();
                    } else {
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

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        int id = item.getItemId();

        if (id == mMenuItemLeaveTribu.getItemId()) {
            if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                ShowSnackBarInfoInternet.showToastInfoInternet(view.mContext);
            } else {
                model.leaveTribu(mTribu);
            }
        } else if (id == mMenuItemChangeAdmin.getItemId()) {
            model.openChangeAdminActivity(mTribu.getKey());

        } else if (id == mMenuItemProfileTribuAdmin.getItemId()) {
            model.openProfileTribuAdminActivity(mTribu.getKey());
        } else if (id == mMenuItemProfileTribuFollower.getItemId()) {
            model.openProfileTribuFollowerActivity(mTribu.getKey());
        }

        return true;
    }

    public void onDestroy() {
        subscription.clear();
    }

    public void getInstance(String feature) {

        FragmentManager fm = view.mContext.getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        switch (feature) {
            case TOPIC:
                view.mFabTopic.setVisibility(View.VISIBLE);
                view.mFabSurvey.setVisibility(View.GONE);
                view.mFragmentContainer = FragmentContainer.getInstance(TOPIC, view.mTribuKey);
                break;
            case SURVEY:
                view.mFabTopic.setVisibility(View.GONE);
                view.mFabSurvey.setVisibility(View.VISIBLE);
                view.mFragmentContainer = FragmentContainer.getInstance(SURVEY, view.mTribuKey);
                break;
            case EVENT:
                view.mFabTopic.setVisibility(View.GONE);
                view.mFabSurvey.setVisibility(View.GONE);
                view.mFragmentContainer = FragmentContainer.getInstance(EVENT, view.mTribuKey);
                break;
            case CAMPAIGN:
                view.mFabTopic.setVisibility(View.GONE);
                view.mFabSurvey.setVisibility(View.GONE);
                view.mFragmentContainer = FragmentContainer.getInstance(CAMPAIGN, view.mTribuKey);
                break;
            case SHARED_MEDIA:
                view.mFabTopic.setVisibility(View.GONE);
                view.mFabSurvey.setVisibility(View.GONE);
                view.mFragmentContainer = FragmentContainer.getInstance(SHARED_MEDIA, view.mTribuKey);
                break;
        }
        transaction.replace(R.id.frame_container, view.mFragmentContainer).commit();

    }

    public void openDetailTribuAddFollowers(String mTribusKey) {
        model.openDetailTribuAddFollowers(mTribusKey);
    }

    public void startChatActivity(String tribuUniqueName, String topicKey, String tribuKey, String tribuName, String topicName) {
        model.openChatTribuActivity(tribuUniqueName, topicKey, tribuKey, tribuName, topicName);
    }
}
