package apptribus.com.tribus.activities.main_activity.fragment_timeline.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.NonNull;
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
import android.view.ViewGroup;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.List;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.main_activity.fragment_timeline.mvp.TimeLinePresenter;
import apptribus.com.tribus.activities.main_activity.fragment_timeline.mvp.TimeLineView;
import apptribus.com.tribus.pojo.Follower;
import apptribus.com.tribus.pojo.RequestTribu;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.GetTimeAgo;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static apptribus.com.tribus.util.Constantes.ADMIN_PERMISSION;
import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.PARTICIPATING;
import static apptribus.com.tribus.util.Constantes.SURVEYS;
import static apptribus.com.tribus.util.Constantes.TOPICS;
import static apptribus.com.tribus.util.Constantes.TRIBUS_INVITATIONS;
import static apptribus.com.tribus.util.Constantes.TRIBUS_PARTICIPANTS;

/**
 * Created by User on 5/26/2017.
 */

public class TribusLineAdapter extends RecyclerView.Adapter<TribusLineAdapter.TribusLineViewHolder>{

    private List<Tribu> mTribusList;
    private Fragment mContext;
    private TimeLineView mView;
    private TribusLineAdapterListener mListener;
    private ProgressDialog progress;

    //FIRESTORE INSTANCE
    private FirebaseFirestore mFirestore;

    //FIRESTORE COLLECTIONS REFERENCES
    private CollectionReference mUsersCollection;
    private CollectionReference mTribusCollection;

    //FIREBASE INSTANCES
    private FirebaseAuth mAuth;

    public TribusLineAdapter(Fragment context, List<Tribu> tribus, TimeLineView view, TimeLinePresenter presenter) {

        this.mContext = context;
        this.mTribusList = tribus;
        this.mView = view;
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mUsersCollection = mFirestore.collection(GENERAL_USERS);
        mTribusCollection = mFirestore.collection(GENERAL_TRIBUS);

        if (presenter != null){
            mListener = presenter;
        }

    }

    @NonNull
    @Override
    public TribusLineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tribus_item, parent, false);

        return new TribusLineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TribusLineViewHolder holder, int position) {

        if (mTribusList != null) {
            Tribu tribu = mTribusList.get(position);

            holder.initTimeLineVH(tribu);
        }
    }

    @Override
    public int getItemCount() {
        if (mTribusList != null) {
            return mTribusList.size();
        }
        else {
            return 0;
        }
    }




    public class TribusLineViewHolder extends RecyclerView.ViewHolder{

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

        private Boolean mIsFollower;
        private Boolean mIsAdmin;
        private Boolean mIsUser;



        public TribusLineViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            ButterKnife.bind(this, itemView);
            mIsFollower = false;
            mIsAdmin = false;
            mIsUser = false;

        }

        private void initTimeLineVH(Tribu tribu) {

            //get Admin
            mUsersCollection
                    .document(tribu.getAdmin().getUidAdmin())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {

                        User admin = documentSnapshot.toObject(User.class);

                        if (admin.getThumb() != null) {
                            setImageAdmin(admin.getThumb());
                        } else {
                            setImageAdmin(admin.getImageUrl());
                        }

                        setAdminsName(admin.getName());
                        setAdminsUsername(admin.getUsername());

                        mCircleImageAdmin.setOnClickListener(v -> {
                            openImageAdmin(admin, tribu);
                        });

                        mTvName.setOnClickListener(v -> {
                            openImageAdmin(admin, tribu);
                        });

                    })
                    .addOnFailureListener(Throwable::printStackTrace);


            //get Topics
            mTribusCollection
                    .document(tribu.getKey())
                    .collection(TOPICS)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshotsTopics -> {

                        int numTopic;
                        //set topic number
                        if (queryDocumentSnapshotsTopics != null
                                && !queryDocumentSnapshotsTopics.isEmpty()) {
                            numTopic = queryDocumentSnapshotsTopics.size();
                            setNumTopics(numTopic);
                        }
                        else {
                            numTopic = 0;
                            setNumTopics(numTopic);
                        }

                        mIvTopic.setOnClickListener(v -> {
                            showNumTopics(numTopic, tribu);
                        });

                    })
                    .addOnFailureListener(Throwable::printStackTrace);


            //get Surveys
            mTribusCollection
                    .document(tribu.getKey())
                    .collection(SURVEYS)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshotsSurveys -> {

                        int numSurvey;

                        if (queryDocumentSnapshotsSurveys != null
                                && !queryDocumentSnapshotsSurveys.isEmpty()) {
                            numSurvey = queryDocumentSnapshotsSurveys.size();
                            setNumSurvey(numSurvey);
                        }
                        else {
                            numSurvey = 0;
                            setNumSurvey(numSurvey);
                        }

                        mIvSurvey.setOnClickListener(v -> {
                            showNumSurveys(numSurvey, tribu);
                        });

                    })
                    .addOnFailureListener(Throwable::printStackTrace);


            //get Participants
            mTribusCollection
                    .document(tribu.getKey())
                    .collection(TRIBUS_PARTICIPANTS)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshotsParticipants -> {
                        setNumFollowers(queryDocumentSnapshotsParticipants.size());

                    })
                    .addOnFailureListener(Throwable::printStackTrace);


            //get Participating
            mTribusCollection
                    .document(tribu.getKey())
                    .collection(TRIBUS_PARTICIPANTS)
                    .document(mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshotIsFollower -> {

                        //get Admin Permission
                        mTribusCollection
                                .document(tribu.getKey())
                                .collection(ADMIN_PERMISSION)
                                .document(mAuth.getCurrentUser().getUid())
                                .get()
                                .addOnSuccessListener(documentSnapshotIsWaitingPermission -> {

                                    if (tribu.getAdmin().getUidAdmin().equals(mAuth.getCurrentUser().getUid())) {
                                        mBtnParticipar.setVisibility(INVISIBLE);
                                        mIvLocked.setVisibility(VISIBLE);
                                        mIsFollower = true;
                                    }
                                    else if (documentSnapshotIsFollower != null
                                            && documentSnapshotIsFollower.exists()) {
                                        //set button participando
                                        mIsFollower = true;
                                        mBtnParticipar
                                                .setTextColor(mContext
                                                        .getResources().getColor(R.color.colorIcons));
                                        mBtnParticipar.setText("Participando!");
                                        mBtnParticipar.setEnabled(false);
                                        mBtnParticipar.setVisibility(VISIBLE);
                                        mIvLocked.setVisibility(GONE);

                                    }
                                    else if (documentSnapshotIsWaitingPermission != null
                                            && documentSnapshotIsWaitingPermission.exists()) {
                                        //set button aguardando permissao
                                        mIsFollower = false;
                                        mBtnParticipar.setText("Aguardando Aprovação");
                                        mBtnParticipar.setTextColor(mContext
                                                .getResources().getColor(R.color.red));
                                        mBtnParticipar.setEnabled(false);
                                        mBtnParticipar.setVisibility(VISIBLE);
                                        mIvLocked.setVisibility(VISIBLE);
                                    }
                                    else {
                                        //set button participar
                                        mIsFollower = false;
                                        mBtnParticipar
                                                .setTextColor(mContext
                                                        .getResources().getColor(R.color.accent));
                                        mBtnParticipar.setText("Participar");
                                        mBtnParticipar.setEnabled(true);
                                        mIvLocked.setVisibility(VISIBLE);
                                        mBtnParticipar.setVisibility(VISIBLE);
                                    }

                                    //LISTENER FOR CARDVIEW
                                    mTvTribusName.setOnClickListener(v -> {

                                        if (!mIsFollower){
                                            mListener.openProfileTribuUserActivity(tribu);
                                        }
                                        else {
                                            mListener.openFeatureChoiceActivity(tribu);
                                        }

                                    });

                                    mImageTribu.setOnClickListener(v -> {
                                        if (!mIsFollower){
                                            mListener.openProfileTribuUserActivity(tribu);
                                        }
                                        else {
                                            mListener.openFeatureChoiceActivity(tribu);
                                        }

                                    });

                                })
                                .addOnFailureListener(Throwable::printStackTrace);
                    })
                    .addOnFailureListener(Throwable::printStackTrace);


            if (tribu.getProfile().getThumb() == null){
                setTribusImage(tribu.getProfile().getImageUrl());
            }
            else {
                setTribusImage(tribu.getProfile().getThumb());
            }

            setTribusName(tribu.getProfile().getNameTribu());
            setTribusDescription(tribu.getProfile().getDescription());
            setTribusThematic(tribu.getProfile().getThematic());

            //SET isPUBLIC
            if (tribu.getProfile().isPublic()) {
                mIvLocked.setImageResource(R.drawable.ic_public_tribu);
            } else {
                mIvLocked.setImageResource(R.drawable.ic_restricted_tribu);
            }

            mIvLocked.setOnClickListener(v -> {
                showLocked(tribu);
            });


            mCardView.setVisibility(VISIBLE);

            mBtnParticipar.setOnClickListener(v -> {
                //CHECK INTERNET CONNECTION
                if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                    ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
                } else {
                    ShowSnackBarInfoInternet.showSnack(true, mView);
                    //mListener.createFollower(tribu);
                    showDialogToCreateFollower(tribu);
                }
            });


            //LISTENER FOR ICON SHARE
            mIvShare.setOnClickListener(v -> {
                //CHECK INTERNET CONNECTION
                if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                    ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
                } else {
                    ShowSnackBarInfoInternet.showSnack(true, mView);
                    //openShareFragmentToCard(tribu);
                    mListener.openShareFragment(tribu);
                }
            });


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

        private void showNumTopics(long numTopic, Tribu tribu) {
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

            Toast.makeText(mView.mContext.getActivity(), message, Toast.LENGTH_SHORT).show();
        }

        private void showNumSurveys(long numSurvey, Tribu tribu) {
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

            Toast.makeText(mView.mContext.getActivity(), message, Toast.LENGTH_SHORT).show();
        }

        private void setNumTopics(long numTopic) {
            mTvTopicNumber.setText(String.valueOf(numTopic));
        }

        private void setNumSurvey(long numTopic) {
            mTvSurveyNumber.setText(String.valueOf(numTopic));
        }

        private void showLocked(Tribu tribu) {
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

            Toast.makeText(mView.mContext.getActivity(), message, Toast.LENGTH_SHORT).show();
        }

        private void openImageAdmin(User admin, Tribu tribu) {

            //CREATE DIALOG TO SHOW NEW IMAGE
            //CONFIGURATION OF DIALOG
            //if(admin.getId().equals(mTribu.getAdmin().getUidAdmin())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mView.mContext.getActivity(), R.style.MyDialogTheme);
            LayoutInflater inflater = mView.mContext.getActivity().getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_profile_admin, null);
            SimpleDraweeView mSdAdminPhoto = dialogView.findViewById(R.id.sd_large_image_admin);
            TextView mTvNameOfAdmin = dialogView.findViewById(R.id.tv_name_of_admin);
            TextView mTvUsernameOfAdmin = dialogView.findViewById(R.id.tv_username_admin);
            TextView mTvAdminSince = dialogView.findViewById(R.id.tv_admin_since);
            dialogView.setBackgroundColor(mView.mContext.getActivity().getResources().getColor(R.color.transparent));


            mTvNameOfAdmin.setText(admin.getName());
            String appendAdminUsername = "(" + admin.getUsername() + ")";
            mTvUsernameOfAdmin.setText(appendAdminUsername);
            builder.setView(dialogView);

            String time = GetTimeAgo.getTimeAgo(tribu.getAdmin().getDate(), mView.mContext.getActivity());
            String append = "Admin destra tribu ";
            String appendDate = append + time;
            mTvAdminSince.setText(appendDate);

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


        //SHOW DIALOG
        private void showDialogToCreateFollower(Tribu tribu) {

            //THIS user is CURRENT user, NOT the current mTribu's admin
            //INSTANCE OF PROGRESS DIALOG
            progress = new ProgressDialog(mView.mContext.getActivity());
            progress.setCancelable(false);

            //CONFIGURATION OF DIALOG
            AlertDialog.Builder builder = new AlertDialog.Builder(mView.mContext.getActivity());
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

            Date date = new Date(System.currentTimeMillis());

            Follower newFollowerTribu = new Follower(); //vai na tribu se for publica ou privada
            newFollowerTribu.setAdmin(false);
            newFollowerTribu.setUidFollower(mAuth.getCurrentUser().getUid());
            newFollowerTribu.setDate(date);


            Tribu newFollower = new Tribu(); //vai em user
            newFollower.setDate(date);
            newFollower.setKey(tribu.getKey());
            newFollower.setThematic(tribu.getProfile().getThematic());

            //CREATE REQUEST TRIBU'S OBJECT IF TRIBU IS RESTRICT
            RequestTribu requestTribu = new RequestTribu(); // vai no user, se a tribu for privada
            requestTribu.setTribuKey(tribu.getKey());
            requestTribu.setThematic(tribu.getProfile().getThematic());
            requestTribu.setDate(date);


            if (tribu.getProfile().isPublic()) {

                mTribusCollection
                        .document(tribu.getKey())
                        .collection(TRIBUS_PARTICIPANTS)
                        .document(mAuth.getCurrentUser().getUid())
                        .set(newFollowerTribu)
                        .addOnSuccessListener(aVoid -> {

                            mUsersCollection
                                    .document(mAuth.getCurrentUser().getUid())
                                    .collection(PARTICIPATING)
                                    .document(tribu.getKey())
                                    .set(newFollower)
                                    .addOnSuccessListener(aVoid12 -> {

                                        showProgressDialog(false);
                                        Toast.makeText(mContext, "Tribu adicionada!", Toast.LENGTH_SHORT).show();
                                        mBtnParticipar.setText("Participando!");
                                        mBtnParticipar.setTextColor(mContext.getResources().getColor(R.color.colorIcons));
                                        mBtnParticipar.setEnabled(false);
                                        mIvLocked.setVisibility(GONE);

                                    })
                                    .addOnFailureListener(e -> {
                                        e.printStackTrace();
                                        showProgressDialog(false);
                                        Toast.makeText(mView.mContext.getActivity(),
                                                "Houve um erro ao adicionar esta tribu.",
                                                Toast.LENGTH_SHORT).show();

                                    });
                            ;

                        })
                        .addOnFailureListener(e -> {
                            e.printStackTrace();
                            showProgressDialog(false);
                            Toast.makeText(mView.mContext.getActivity(),
                                    "Houve um erro ao adicionar esta tribu.",
                                    Toast.LENGTH_SHORT).show();

                        });

            }
            else {

                mTribusCollection
                        .document(tribu.getKey())
                        .collection(ADMIN_PERMISSION)
                        .document(mAuth.getCurrentUser().getUid())
                        .set(newFollowerTribu)
                        .addOnSuccessListener(aVoid -> {

                            mUsersCollection
                                    .document(mAuth.getCurrentUser().getUid())
                                    .collection(TRIBUS_INVITATIONS)
                                    .document(tribu.getKey())
                                    .set(requestTribu)
                                    .addOnSuccessListener(aVoid13 -> {

                                        showProgressDialog(false);
                                        Toast.makeText(mContext, "Aguardando aprovação pelo Admin.", Toast.LENGTH_LONG).show();
                                        mBtnParticipar.setText("Aguardando Aprovação");
                                        mBtnParticipar.setTextColor(mContext.getResources().getColor(R.color.red));
                                        mBtnParticipar.setEnabled(false);
                                        mIvLocked.setVisibility(VISIBLE);

                                    })
                                    .addOnFailureListener(e -> {
                                        e.printStackTrace();
                                        showProgressDialog(false);
                                        Toast.makeText(mView.mContext.getActivity(),
                                                "Houve um erro ao adicionar esta tribu.",
                                                Toast.LENGTH_SHORT).show();

                                    });

                        })
                        .addOnFailureListener(e -> {
                            e.printStackTrace();
                            showProgressDialog(false);
                            Toast.makeText(mView.mContext.getActivity(),
                                    "Houve um erro ao adicionar esta tribu.",
                                    Toast.LENGTH_SHORT).show();

                        });

            }


            //CREATE TRIBU TO STORE INSIDE UNIQUE FOLLOWER'S OBJECT THE FOLLO, TRIBU'S UNIQUE NAME AND DATE
            /*Tribu tribuToFollowers = new Tribu(mAuth.getCurrentUser().getUid(), tribu.getProfile().getUniqueName());
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

            }*/
        }

    }

    public interface TribusLineAdapterListener{
        void openFeatureChoiceActivity(Tribu tribu);
        void openProfileTribuUserActivity(Tribu tribu);
        //void createFollower(Tribu tribu);
        void openShareFragment(Tribu tribu);
    }

}
