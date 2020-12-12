package apptribus.com.tribus.activities.main_activity.fragment_timeline.view_holder;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.feature_choice_tribus.FeatureChoiceTribusActivity;
import apptribus.com.tribus.activities.main_activity.fragment_timeline.mvp.TimeLineView;
import apptribus.com.tribus.activities.profile_tribu_admin.ProfileTribuAdminActivity;
import apptribus.com.tribus.activities.profile_tribu_user.ProfileTribuUserActivity;
import apptribus.com.tribus.pojo.Follower;
import apptribus.com.tribus.pojo.RequestTribu;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.firestore.FirestoreService;
import apptribus.com.tribus.util.GetTimeAgo;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.*;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static apptribus.com.tribus.util.Constantes.ADMINS;
import static apptribus.com.tribus.util.Constantes.ADMINS_INVITATIONS;
import static apptribus.com.tribus.util.Constantes.ADMINS_PERMISSIONS;
import static apptribus.com.tribus.util.Constantes.FOLLOWERS;
import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.TRIBUS_FOLLOWERS;
import static apptribus.com.tribus.util.Constantes.TRIBUS_SURVEY;
import static apptribus.com.tribus.util.Constantes.TRIBUS_TOPICS;
import static apptribus.com.tribus.util.Constantes.TRIBU_KEY;
import static apptribus.com.tribus.util.Constantes.TRIBU_UNIQUE_NAME;

/**
 * Created by User on 1/1/2018.
 */

public class TimeLineViewHolder extends RecyclerView.ViewHolder {

    private Context mContext;

    @BindView(R.id.item_tribu_card)
    CardView mCardView;

    @BindView(R.id.relative_main)
    RelativeLayout mRelativeMain;

    @BindView(R.id.relative_top)
    RelativeLayout mRelativeTop;

    @BindView(R.id.iv_topic)
    ImageView mIvTopic;

    @BindView(R.id.tv_topic_number)
    TextView mTvTopicNumber;

    @BindView(R.id.iv_survey)
    ImageView mIvSurvey;

    @BindView(R.id.tv_survey_number)
    TextView mTvSurveyNumber;

    @BindView(R.id.tv_num_participants)
    TextView mTvNumParticipants;

    @BindView(R.id.iv_share)
    ImageView mIvShare;

    @BindView(R.id.linear_middle)
    LinearLayout mLinearMiddle;

    @BindView(R.id.relative_sdv)
    RelativeLayout mRelativeSdv;

    @BindView(R.id.coordinator)
    CoordinatorLayout mCoordinator;

    @BindView(R.id.image_tribu)
    SimpleDraweeView mImageTribu;

    @BindView(R.id.btn_participar)
    Button mBtnParticipar;

    @BindView(R.id.iv_locked)
    ImageView mIvLocked;

    @BindView(R.id.circle_image_of_admin)
    SimpleDraweeView mCircleImageAdmin;

    @BindView(R.id.tv_name)
    TextView mTvName;

    @BindView(R.id.tv_username)
    TextView mTvUsername;

    @BindView(R.id.tv_tribus_name)
    TextView mTvTribusName;

    @BindView(R.id.tv_tribus_thematic)
    TextView mTvTribusThematic;

    @BindView(R.id.tv_tribus_description)
    TextView mTvTribusDescription;

    @BindView(R.id.view1)
    View mView1;

    @BindView(R.id.relative_button)
    RelativeLayout mRelativeButton;

    @BindView(R.id.linearLayout)
    LinearLayout mLinearLayout;

    @BindView(R.id.view2)
    View mView2;


    //FIRESTORE INSTACE
    private FirestoreService mFirestoreService;

    //FIREBASE INSTANCES
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;

    //FIREBASE REFERENCES
    private DatabaseReference mReferenceTribus;
    public DatabaseReference mReferenceUsers;
    public DatabaseReference mReferenceAdmin;
    private DatabaseReference mReferenceFollowers;
    private DatabaseReference mTribusFollowers;
    private DatabaseReference mRefAdminPermissions;
    private DatabaseReference mRefAdminInvitations;
    private DatabaseReference mRefTribus;
    private DatabaseReference mReferenceTopics;
    private DatabaseReference mReferenceSurvey;


    //SHOW
    private static ProgressDialog progress;

    private String creationDate = "Criada ";

    public TimeLineViewHolder(View itemView) {
        super(itemView);
        mContext = itemView.getContext();
        ButterKnife.bind(this, itemView);
        mFirestoreService = new FirestoreService(itemView.getContext());

        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mReferenceTribus = mDatabase.getReference().child(GENERAL_TRIBUS);
        mReferenceUsers = mDatabase.getReference().child(GENERAL_USERS);
        mReferenceFollowers = mDatabase.getReference().child(FOLLOWERS);
        mTribusFollowers = mDatabase.getReference().child(TRIBUS_FOLLOWERS);
        mRefAdminPermissions = mDatabase.getReference().child(ADMINS_PERMISSIONS);
        mRefAdminInvitations = mDatabase.getReference().child(ADMINS_INVITATIONS);
        mRefTribus = mDatabase.getReference().child(GENERAL_TRIBUS);
        mReferenceAdmin = mDatabase.getReference().child(ADMINS);
        mReferenceTopics = mDatabase.getReference().child(TRIBUS_TOPICS);
        mReferenceSurvey = mDatabase.getReference().child(TRIBUS_SURVEY);

    }


    //SET IMAGE OF TRIBU
    private void setTribusImage(String url){

            ControllerListener listener = new BaseControllerListener() {
            @Override
            public void onFailure(String id, Throwable throwable) {
                super.onFailure(id, throwable);
                //Log.d("Valor: ", "onFailure - id: " + id + "throwable: " + throwable);
            }

            @Override
            public void onIntermediateImageFailed(String id, Throwable throwable) {
                super.onIntermediateImageFailed(id, throwable);
                //Log.d("Valor: ", "onIntermediateImageFailed - id: " + id + "throwable: " + throwable);
            }

            @Override
            public void onFinalImageSet(String id, Object imageInfo, Animatable animatable) {
                super.onFinalImageSet(id, imageInfo, animatable);
                Log.d("Valor: ", "onFinalImageSet - id: " + id + "imageInfo: " + imageInfo + "animatable: " + animatable);
            }

            @Override
            public void onIntermediateImageSet(String id, Object imageInfo) {
                super.onIntermediateImageSet(id, imageInfo);
                Log.d("Valor: ", "onIntermediateImageSet - id: " + id + "imageInfo: " + imageInfo);
            }

            @Override
            public void onRelease(String id) {
                super.onRelease(id);
                Log.d("Valor: ", "onRelease - id: " + id);
            }

            @Override
            public void onSubmit(String id, Object callerContext) {
                super.onSubmit(id, callerContext);
                Log.d("Valor: ", "onSubmit - id: " + id + "callerContext: " + callerContext);
                mCardView.setVisibility(VISIBLE);
            }
        };

        //SCRIPT - LARGURA DA IMAGEM
        //int w = 0;
        /*if (holder.mImageTribu.getLayoutParams().width == FrameLayout.LayoutParams.MATCH_PARENT
                || holder.mImageTribu.getLayoutParams().width == FrameLayout.LayoutParams.WRAP_CONTENT) {

            Display display = ((MainActivity) mContext).getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            try {
                w = size.x;
                Log.d("Valor: ", "Valor da largura(w) em onStart(FragmentPesquisarTribu): " + w);

            } catch (Exception e) {
                w = display.getWidth();
                e.printStackTrace();
            }
        }*/

        Uri uri = Uri.parse(url);
        DraweeController dc = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setControllerListener(listener)
                .setOldController(mImageTribu.getController())
                .build();
        mImageTribu.setController(dc);

    }


    //SET DESCRIPTION
    private void setTribusDescription(String description){
        mTvTribusDescription.setText(description);
    }


    public void setAdminsName(String name) {
        if (name != null) {
            String[] firstName = name.split(" ");
            //String appendNameAndUsername = "por " + firstName[0] + " (" + username + ")";
            mTvName.setText(firstName[0]);
        }
    }


    //SET USERNAME
    private void setAdminsUsername(String username){
        mTvUsername.setText(username);
    }



    //SET NAME
    private void setTribusName(String name){
        mTvTribusName.setText(name);
    }

    //SET THEMATIC
    private void setTribusThematic(String thematic){
        mTvTribusThematic.setText(thematic);
    }


    //SET NUM FOLLOWERS
    private void setNumFollowers(long numParticipants){

        int index = String.valueOf(numParticipants).length();

        SpannableString styledNumParticipantes = new SpannableString(String.valueOf(numParticipants));

        styledNumParticipantes.setSpan(
                new RelativeSizeSpan(2f),
                0,
                index,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );


        if (numParticipants > 1){

            String appendNumParticipants = styledNumParticipantes + " participantes";
            mTvNumParticipants.setText(appendNumParticipants);

            //String append = String.valueOf(numParticipants) ;
            //mTvNumParticipants.setText(append);
        }
        else {
            String appendNumParticipants = styledNumParticipantes + " participante";
            mTvNumParticipants.setText(appendNumParticipants);

            //String append = String.valueOf(numParticipants) + " participante";
            //mTvNumParticipants.setText(append);
        }

    }

    //SET IMAGE OF ADMIN
    private void setImageAdmin(String url){


            ControllerListener listener = new BaseControllerListener() {
                @Override
                public void onFailure(String id, Throwable throwable) {
                    super.onFailure(id, throwable);
                    Log.d("Valor: ", "onFailure - id: " + id + "throwable: " + throwable);
                }

                @Override
                public void onIntermediateImageFailed(String id, Throwable throwable) {
                    super.onIntermediateImageFailed(id, throwable);
                    Log.d("Valor: ", "onIntermediateImageFailed - id: " + id + "throwable: " + throwable);
                }

                @Override
                public void onFinalImageSet(String id, Object imageInfo, Animatable animatable) {
                    super.onFinalImageSet(id, imageInfo, animatable);
                    Log.d("Valor: ", "onFinalImageSet - id: " + id + "imageInfo: " + imageInfo + "animatable: " + animatable);
                }

                @Override
                public void onIntermediateImageSet(String id, Object imageInfo) {
                    super.onIntermediateImageSet(id, imageInfo);
                    Log.d("Valor: ", "onIntermediateImageSet - id: " + id + "imageInfo: " + imageInfo);
                }

                @Override
                public void onRelease(String id) {
                    super.onRelease(id);
                    Log.d("Valor: ", "onRelease - id: " + id);
                }

                @Override
                public void onSubmit(String id, Object callerContext) {
                    super.onSubmit(id, callerContext);
                    Log.d("Valor: ", "onSubmit - id: " + id + "callerContext: " + callerContext);
                }
            };

            //SCRIPT - LARGURA DA IMAGEM
            //int w = 0;
        /*if (holder.mImageTribu.getLayoutParams().width == FrameLayout.LayoutParams.MATCH_PARENT
                || holder.mImageTribu.getLayoutParams().width == FrameLayout.LayoutParams.WRAP_CONTENT) {

            Display display = ((MainActivity) mContext).getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            try {
                w = size.x;
                Log.d("Valor: ", "Valor da largura(w) em onStart(FragmentPesquisarTribu): " + w);

            } catch (Exception e) {
                w = display.getWidth();
                e.printStackTrace();
            }
        }*/

            Uri uri = Uri.parse(url);
            DraweeController dc = Fresco.newDraweeControllerBuilder()
                    .setUri(uri)
                    .setControllerListener(listener)
                    .setOldController(mCircleImageAdmin.getController())
                    .build();
            mCircleImageAdmin.setController(dc);

    }


    public void initTimeLineVH(Tribu tribu, TimeLineView view,
                               Fragment fragmentContext) {


        mFirestoreService.addAdminIntoFirestore(tribu);


        //get Tribus's Admin to looking for admin's data - NOT CURRENT USER
        mReferenceUsers
                .child(tribu.getAdmin().getUidAdmin())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        mReferenceTopics
                                .child(tribu.getProfile().getUniqueName())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshotTopics) {

                                        mReferenceSurvey
                                                .child(tribu.getProfile().getUniqueName())
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshotSurvey) {

                                                        long numSurvey;

                                                        long numTopic;

                                                        if (dataSnapshotSurvey.hasChildren()) {
                                                            numSurvey = dataSnapshotSurvey.getChildrenCount();
                                                            setNumSurvey(numSurvey);
                                                        }
                                                        else {
                                                            numSurvey = 0;
                                                            setNumSurvey(numSurvey);
                                                        }


                                                        if (dataSnapshotTopics.hasChildren()) {
                                                            numTopic = dataSnapshotTopics.getChildrenCount();
                                                            setNumTopics(numTopic);
                                                        }
                                                        else {
                                                            numTopic = 0;
                                                            setNumTopics(numTopic);
                                                        }

                                                        User admin = dataSnapshot.getValue(User.class);

                                                        if (admin.getThumb() != null) {
                                                            setImageAdmin(admin.getThumb());
                                                        } else {
                                                            setImageAdmin(admin.getImageUrl());
                                                        }

                                                        setAdminsName(admin.getName());
                                                        setAdminsUsername(admin.getUsername());

                                                        setTribusImage(tribu.getProfile().getImageUrl());

                                                        setTribusName(tribu.getProfile().getNameTribu());
                                                        //setTribusUniqueName(mTribu.getProfile().getUniqueName());
                                                        setTribusDescription(tribu.getProfile().getDescription());
                                                        setTribusThematic(tribu.getProfile().getThematic());
                                                        //setTribusImage(mTribu.getProfile().getImageUrl());

                                                        //setCreationDate(mTribu.getProfile().getCreationDate());
                                                        //mTvNumParticipants.setEnabled(false);
                                                        setNumFollowers(tribu.getProfile().getNumFollowers());

                                                        //SET isPUBLIC
                                                        if (tribu.getProfile().isPublic()) {
                                                            mIvLocked.setImageResource(R.drawable.ic_public_tribu);
                                                        } else {
                                                            mIvLocked.setImageResource(R.drawable.ic_restricted_tribu);
                                                        }

                                        /*mTvNumParticipants.setOnClickListener(v -> {
                                            showNumParticipantes(fragmentContext, mTribu);
                                        });*/

                                        /*mTvNumParticipants.setOnClickListener(v -> {
                                            showNumParticipantes(fragmentContext, mTribu);
                                        });*/
                                                        mIvLocked.setOnClickListener(v -> {
                                                            showLocked(fragmentContext, tribu);
                                                        });


                                                        mIvTopic.setOnClickListener(v -> {
                                                            showNumTopics(fragmentContext, numTopic, tribu);
                                                        });

                                                        mIvSurvey.setOnClickListener(v -> {
                                                            showNumSurveys(fragmentContext, numSurvey, tribu);
                                                        });

                                        /*mTvNumTopic.setOnClickListener(v -> {
                                            showNumTopics(fragmentContext, numTopic, mTribu);
                                        });*/

                                                        //LISTENER FOR BtnFOLLOW
                                                        mBtnParticipar.setOnClickListener(v -> {
                                                            //CHECK INTERNET CONNECTION
                                                            if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                                                                ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
                                                            } else {
                                                                ShowSnackBarInfoInternet.showSnack(true, view);

                                                                showDialog(tribu);
                                                /*mReferenceAdmin
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshotAdmin) {

                                                                if (dataSnapshotAdmin.hasChild(mAuth.getCurrentUser().getUid())) {
                                                                    showDialogToRemoveContact(mTribu);

                                                                } else {
                                                                    showDialogToCreatTribu();
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {

                                                            }
                                                        });*/


                                                            }
                                                        });

                                                        //SET WIDGETS BY DEFAULT
                                                        verifyIfFollower(mAuth.getCurrentUser().getUid(), tribu);

                                                        verifyIfFollowerWaitingPermissions(mAuth.getCurrentUser().getUid(), tribu);


                                                        //LISTENER FOR ICON SHARE
                                                        mIvShare.setOnClickListener(v -> {
                                                            //CHECK INTERNET CONNECTION
                                                            if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                                                                ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
                                                            } else {
                                                                ShowSnackBarInfoInternet.showSnack(true, view);
                                                                openShareFragmentToCard(tribu);
                                                            }

                                                            //openCommentActivity(fragment.getContext(), tribusKey, mTribu);
                                                        });


                                                        //LISTENER FOR CARDVIEW
                                                        mTvTribusName.setOnClickListener(v -> {
                                                            mTribusFollowers
                                                                    .child(tribu.getProfile().getUniqueName())
                                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                                            Follower follower = dataSnapshot.getValue(Follower.class);

                                                                            //FOR ADMIN, FOLLOWER AND USER
                                                                            if (tribu.getAdmin().getUidAdmin().equals(mAuth.getCurrentUser().getUid())) {
                                                                                openProfileTribuAdmin(tribu);
                                                                            } else if (dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())) {
                                                                                //openProfileTribuFollower(fragment, mTribu);
                                                                                openFeatureChoiceActivity(tribu);
                                                                            } else {
                                                                                openProfileTribuUser(tribu);
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(DatabaseError databaseError) {
                                                                            databaseError.toException().printStackTrace();

                                                                        }
                                                                    });

                                                        });

                                                        //LISTENER FOR IMAGE
                                                        mImageTribu.setOnClickListener(v -> {
                                                            mTribusFollowers
                                                                    .child(tribu.getProfile().getUniqueName())
                                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                                            Follower follower = dataSnapshot.getValue(Follower.class);

                                                                            //FOR ADMIN, FOLLOWER AND USER
                                                                            if (tribu.getAdmin().getUidAdmin().equals(mAuth.getCurrentUser().getUid())) {
                                                                                openProfileTribuAdmin(tribu);

                                                                            } else if (dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())) {
                                                                                //openProfileTribuFollower(fragment, mTribu);
                                                                                openFeatureChoiceActivity(tribu);
                                                                            } else {
                                                                                openProfileTribuUser(tribu);
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(DatabaseError databaseError) {
                                                                            databaseError.toException().printStackTrace();

                                                                        }
                                                                    });

                                                        });

                                                        mCircleImageAdmin.setOnClickListener(v -> {
                                                            openImageAdmin(admin, tribu, fragmentContext);
                                                        });

                                                        mTvName.setOnClickListener(v -> {
                                                            openImageAdmin(admin, tribu, fragmentContext);
                                                        });

                                                        mCardView.setVisibility(VISIBLE);


                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                        databaseError.toException().printStackTrace();
                                                    }
                                                });

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        databaseError.toException().printStackTrace();

                                    }
                                });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        databaseError.toException().printStackTrace();

                    }
                });


    }

    private void showNumParticipantes(Fragment fragmentContext, Tribu tribu) {
        long numParticipantes = tribu.getProfile().getNumFollowers();

        String message = null;

        if (numParticipantes > 1 ) {
            message = "A mTribu " + "'" + tribu.getProfile().getNameTribu() + "'" + " tem " +
                    String.valueOf(numParticipantes) + " participantes.";
        }
        else if(numParticipantes <= 1){
            message = "A mTribu " + "'" + tribu.getProfile().getNameTribu() + "'" + " tem " + String.valueOf(numParticipantes) + " participante.";
        }

        Toast.makeText(fragmentContext.getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void showNumTopics(Fragment fragmentContext, long numTopic, Tribu tribu) {
        String message = null;

        if (numTopic > 1 ) {
            message = "'" + tribu.getProfile().getNameTribu() + "'" + " tem " + String.valueOf(numTopic) + " tópicos de conversa.";
        }
        else if(numTopic < 1){
            message = "'" + tribu.getProfile().getNameTribu() + "'" + " ainda não tem tópicos de conversa.";
        }
        else if(numTopic == 1){
            message = "'" + tribu.getProfile().getNameTribu() + "'" + " tem " + String.valueOf(numTopic) + " tópico de conversa.";
        }

        Toast.makeText(fragmentContext.getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void showNumSurveys(Fragment fragmentContext, long numSurvey, Tribu tribu) {
        String message = null;

        if (numSurvey > 1 ) {
            //message = "A mTribu " + "'" + mTribu.getProfile().getNameTribu() + "'" + " tem " + String.valueOf(numSurvey) + " enquetes.";
            message = "'" + tribu.getProfile().getNameTribu() + "'" + " tem " + String.valueOf(numSurvey) + " enquetes.";
        }
        else if(numSurvey < 1){
            message = "'" + tribu.getProfile().getNameTribu() + "'" + " ainda não tem enquetes.";
        }
        else if(numSurvey == 1){
            message = "'" + tribu.getProfile().getNameTribu() + "'" + " tem " + String.valueOf(numSurvey) + " enquete.";
        }

        Toast.makeText(fragmentContext.getContext(), message, Toast.LENGTH_SHORT).show();
    }


    private void setNumTopics(long numTopic) {
        mTvTopicNumber.setText(String.valueOf(numTopic));
    }

    private void setNumSurvey(long numTopic) {
        mTvSurveyNumber.setText(String.valueOf(numTopic));
    }

    private void showLocked(Fragment fragmentContext, Tribu tribu) {
        String message = null;

        if (tribu.getProfile().isPublic() && tribu.getAdmin().getUidAdmin().equals(mAuth.getCurrentUser().getUid())) {
            message = "Você definiu " + "'" + tribu.getProfile().getNameTribu() + "'" + " como uma mTribu PÚBLICA.";
        }
        else if(!tribu.getProfile().isPublic() && tribu.getAdmin().getUidAdmin().equals(mAuth.getCurrentUser().getUid())) {
            message = "Você definiu " + "'" + tribu.getProfile().getNameTribu() + "'" + " como uma mTribu RESTRITA.";
        }
        else if(!tribu.getProfile().isPublic() && !tribu.getAdmin().getUidAdmin().equals(mAuth.getCurrentUser().getUid())) {
            message = "'" + tribu.getProfile().getNameTribu() + "'" + " é uma mTribu RESTRITA, ou seja, você precisa da aprovação do Admin para participar.";
        }
        else if(tribu.getProfile().isPublic() && !tribu.getAdmin().getUidAdmin().equals(mAuth.getCurrentUser().getUid())) {
            message = "'" + tribu.getProfile().getNameTribu() + "'" + " é uma mTribu PÚBLICA.";
        }

            Toast.makeText(fragmentContext.getContext(), message, Toast.LENGTH_SHORT).show();
    }


    private static void openImageAdmin(User admin, Tribu tribu, Fragment fragmentContext) {

        //CREATE DIALOG TO SHOW NEW IMAGE
        //CONFIGURATION OF DIALOG
        //if(admin.getId().equals(mTribu.getAdmin().getUidAdmin())) {
        AlertDialog.Builder builder = new AlertDialog.Builder(fragmentContext.getContext(), R.style.MyDialogTheme);
        LayoutInflater inflater = fragmentContext.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_profile_admin, null);
        SimpleDraweeView mSdAdminPhoto = dialogView.findViewById(R.id.sd_large_image_admin);
        TextView mTvNameOfAdmin = dialogView.findViewById(R.id.tv_name_of_admin);
        TextView mTvUsernameOfAdmin = dialogView.findViewById(R.id.tv_username_admin);
        TextView mTvAdminSince = dialogView.findViewById(R.id.tv_admin_since);
        dialogView.setBackgroundColor(fragmentContext.getActivity().getResources().getColor(R.color.transparent));


        mTvNameOfAdmin.setText(admin.getName());
        String appendAdminUsername = "(" + admin.getUsername() + ")";
        mTvUsernameOfAdmin.setText(appendAdminUsername);
        builder.setView(dialogView);

        /*if (tribu.getAdmin().getTimestampCreated() != null) {
            SimpleDateFormat sfd = new SimpleDateFormat("dd/MM");
            String time = sfd.format(new Date(tribu.getAdmin().getTimestampCreatedLong()));

            String appendTime = "Admin desta mTribu desde " + time;
            mTvAdminSince.setText(appendTime);

        } else {*/
            String time = GetTimeAgo.getTimeAgo(tribu.getAdmin().getDate(), fragmentContext.getContext());
            String append = "Admin destra tribu ";
            String appendDate = append + time;
            mTvAdminSince.setText(appendDate);
        //}

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
                .setUri(Uri.parse(admin.getImageUrl()))
                .setControllerListener(listener)
                .setOldController(mSdAdminPhoto.getController())
                .build();
        mSdAdminPhoto.setController(dc);

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
    }


    private void openShareFragmentToCard(Tribu tribu) {

        String textInfo = tribu.getProfile().getNameTribu().toUpperCase();

        SpannableString styledString = new SpannableString(textInfo);
        styledString.setSpan(
                new ForegroundColorSpan(mContext.getResources().getColor(R.color.colorAccent)),
                0,
                textInfo.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        try {
            String packageName = mContext.getPackageName();
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_SUBJECT, "Tribus");
            intent.setType("text/plain");

            String strShareMessage = "\nEi, instala esse app e participa da mTribu " + styledString
                    + ". Eu já estou participando! Você vai gostar!\n\n";

            strShareMessage = strShareMessage + "https://play.google.com/store/apps/details?id=" + packageName;

            intent.putExtra(Intent.EXTRA_TEXT, strShareMessage);

            //Uri screenshotUri = Uri.parse("android.resource://apptribus.com.tribus/mimmap/ic_launcher_borda_maior");
            //intent.setType("image/png");
            //intent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
            //intent.setPackage("com.whatsapp");
            //intent.putExtra(Intent.EXTRA_TEXT, "Ei, instala o Tribus! Você vai gostar!");
            //context.startActivity(Intent.createChooser(intent, "Compartilhar..."));
            mContext.startActivity(intent);

        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(mContext, "Não foi encontrada uma atividade para compartilhamento.", Toast.LENGTH_LONG)
                    .show();
        }
    }



    private void verifyIfFollowerWaitingPermissions(String user, Tribu tribu) {

        //SHOW "WAITING ADMIN'S PERMISSION" IN BUTTON
        mRefAdminPermissions
                .child(tribu.getProfile().getUniqueName())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(user)) {

                                mBtnParticipar.setText("Aguardando Aprovação");
                                mBtnParticipar.setTextColor(mContext
                                        .getResources().getColor(R.color.red));
                                mBtnParticipar.setEnabled(false);


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        databaseError.toException().printStackTrace();

                    }
                });

    }


    private void verifyIfFollower(String user, Tribu tribu) {

        mTribusFollowers
                .child(tribu.getProfile().getUniqueName())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(user)) {
                            Follower mFollower = dataSnapshot
                                    .child(mAuth.getCurrentUser().getUid()).getValue(Follower.class);

                            if (mFollower.isAdmin()) {
                                    mBtnParticipar.setVisibility(INVISIBLE);
                                    mIvLocked.setVisibility(VISIBLE);

                            }
                            else {
                                    mBtnParticipar.setVisibility(VISIBLE);
                                    mBtnParticipar
                                            .setTextColor(mContext
                                                    .getResources().getColor(R.color.colorIcons));
                                    mBtnParticipar.setText("Participando!");
                                    mBtnParticipar.setEnabled(false);

                                    mIvLocked.setVisibility(GONE);
                            }
                        }
                        else {
                                mBtnParticipar.setVisibility(VISIBLE);
                                mBtnParticipar
                                        .setTextColor(mContext
                                                .getResources().getColor(R.color.accent));
                                mBtnParticipar.setText("Participar");
                                mBtnParticipar.setEnabled(true);
                                mIvLocked.setVisibility(VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        databaseError.toException().printStackTrace();

                    }

                });


        //}

    }


    //SHOW DIALOG
    private void showDialogToCreatTribu() {


        //CONFIGURATION OF DIALOG
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Para participar de outras tribus, primeiro, crie uma mTribu sobre algo que você tenha afinidade. É só tocar no botão azul na tela inicial. Vamos manter a interação sempre viva por aqui...");

        String positiveText = "OK, ENTENDI!";
        builder.setPositiveButton(positiveText, (dialog, which) -> {
            dialog.dismiss();

        });


        AlertDialog dialog = builder.create();
        dialog.show();
    }


    //SHOW DIALOG
    private void showDialog(Tribu tribu) {

        //THIS user is CURRENT user, NOT the current mTribu's admin
        //INSTANCE OF PROGRESS DIALOG
        progress = new ProgressDialog(mContext);
        progress.setCancelable(false);

        //CONFIGURATION OF DIALOG
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Participar da mTribu " + '"' + tribu.getProfile().getNameTribu() + '"' + "?");

        String positiveText = "SIM";
        builder.setPositiveButton(positiveText, (dialog, which) -> {
            dialog.dismiss();
            showProgressDialog(true);
            createFollowers(tribu);

        });

        String negativeText = "NÃO";
        builder.setNegativeButton(negativeText, (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    //SHOW PROGRESS
    private void showProgressDialog(boolean load) {
        if (load) {
            progress.show();
        } else {
            progress.dismiss();
        }
    }

    //CREATE FOLLOWER
    private void createFollowers(Tribu tribu) {


        //THIS user is CURRENT user, NOT the current mTribu's admin
        Follower newFollower = new Follower(mAuth.getCurrentUser().getUid(), false);
        //newFollower.setUidFollower(mAuth.getCurrentUser().getUid());
        //newFollower.setAdmin(false);
        Date date = new Date(System.currentTimeMillis());
        newFollower.setDate(date);


        //CREATE REQUEST TRIBU'S OBJECT IF TRIBU IS RESTRICT
        RequestTribu requestTribu = new RequestTribu(tribu.getProfile().getUniqueName());
        Date date1 = new Date(System.currentTimeMillis());
        requestTribu.setDate(date1);


        //CREATE TRIBU TO STORE INSIDE UNIQUE FOLLOWER'S OBJECT THE FOLLO, TRIBU'S UNIQUE NAME AND DATE
        Tribu tribuToFollowers = new Tribu(mAuth.getCurrentUser().getUid(), tribu.getProfile().getUniqueName());
        Date date2 = new Date(System.currentTimeMillis());
        tribuToFollowers.setDate(date2);

        if (tribu.getProfile().isPublic()) {

            //CREATE FOLLOWER STORING TRIBUS OBJECT WHICH HAS ADMIN'S ID, TRIBU'S UNIQUE NAME AND DATE
            mReferenceFollowers
                    .child(mAuth.getCurrentUser().getUid())
                    .child(tribu.getProfile().getUniqueName())
                    .setValue(tribuToFollowers)
                    .addOnSuccessListener(task -> {

                        //CREATE TRIBUS FOLLOWER STORING CURRENT FOLLOWER OBJECT WHICH HAS FOLLOWERS'S ID(CURRENT USER),
                        // FALSE TO ADMIN AND DATE
                        mTribusFollowers
                                .child(tribu.getProfile().getUniqueName())
                                .child(mAuth.getCurrentUser().getUid())
                                .setValue(newFollower)
                                .addOnSuccessListener(aVoid -> {

                                    setFollowersInsideTribu(tribu);
                                    showProgressDialog(false);
                                    Toast.makeText(mContext, "Tribu adicionada!", Toast.LENGTH_SHORT).show();
                                    mBtnParticipar.setText("Participando!");
                                    mBtnParticipar.setTextColor(mContext.getResources().getColor(R.color.colorIcons));
                                    mBtnParticipar.setEnabled(false);
                                    mIvLocked.setVisibility(GONE);
                                })
                                .addOnFailureListener(Throwable::getLocalizedMessage);
                    })
                    .addOnFailureListener(Throwable::getLocalizedMessage);

        } else {

            mRefAdminPermissions
                    .child(tribu.getProfile().getUniqueName())
                    .child(mAuth.getCurrentUser().getUid())
                    .setValue(newFollower)
                    .addOnSuccessListener(aVoid -> {

                        //SET TRIBU REQUEST INSIDE USER NODE
                        mRefAdminInvitations
                                .child(mAuth.getCurrentUser().getUid())
                                .child(tribu.getProfile().getUniqueName())
                                .setValue(requestTribu)
                                .addOnSuccessListener(aVoid1 -> {

                                    showProgressDialog(false);
                                    Toast.makeText(mContext, "Aguardando aprovação pelo Admin.", Toast.LENGTH_LONG).show();
                                    mBtnParticipar.setText("Aguardando Aprovação");
                                    mBtnParticipar.setTextColor(mContext.getResources().getColor(R.color.red));
                                    mBtnParticipar.setEnabled(false);
                                    mIvLocked.setVisibility(VISIBLE);
                                })
                                .addOnFailureListener(Throwable::getLocalizedMessage);


                    })
                    .addOnFailureListener(Throwable::getLocalizedMessage);

        }
    }


    private void setFollowersInsideTribu(Tribu tribu) {
        mReferenceTribus
                .child(tribu.getProfile().getUniqueName())
                .child("profile")
                .child("numFollowers")
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        long num = (long) mutableData.getValue();
                        num++;
                        mutableData.setValue(num);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                        if(databaseError != null) {
                            databaseError.toException().printStackTrace();
                        }
                    }
                });
    }


    private void openProfileTribuUser(Tribu tribu) {
        Intent intent = new Intent(mContext, ProfileTribuUserActivity.class);
        intent.putExtra(TRIBU_UNIQUE_NAME, tribu.getProfile().getUniqueName());
        intent.putExtra(TRIBU_KEY, tribu.getKey());
        mContext.startActivity(intent);
    }

    private void openFeatureChoiceActivity(Tribu tribu) {
        Intent intent = new Intent(mContext, FeatureChoiceTribusActivity.class);
        intent.putExtra(TRIBU_UNIQUE_NAME, tribu.getProfile().getUniqueName());
        intent.putExtra(TRIBU_KEY, tribu.getKey());
        mContext.startActivity(intent);
    }

    private void openProfileTribuAdmin(Tribu tribu) {
        Intent intent = new Intent(mContext, ProfileTribuAdminActivity.class);
        intent.putExtra(TRIBU_UNIQUE_NAME, tribu.getProfile().getUniqueName());
        intent.putExtra(TRIBU_KEY, tribu.getKey());
        mContext.startActivity(intent);
    }

}
