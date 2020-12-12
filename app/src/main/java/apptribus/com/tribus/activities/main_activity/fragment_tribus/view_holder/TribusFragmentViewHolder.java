package apptribus.com.tribus.activities.main_activity.fragment_tribus.view_holder;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

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
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.feature_choice_tribus.FeatureChoiceTribusActivity;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.GetTimeAgo;
import apptribus.com.tribus.util.firestore.FirestoreService;
import butterknife.BindView;
import butterknife.ButterKnife;

import static apptribus.com.tribus.util.Constantes.ADMINS;
import static apptribus.com.tribus.util.Constantes.FOLLOWERS;
import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.TRIBUS_MESSAGES;
import static apptribus.com.tribus.util.Constantes.TRIBUS_SURVEY;
import static apptribus.com.tribus.util.Constantes.TRIBUS_TOPICS;
import static apptribus.com.tribus.util.Constantes.TRIBU_KEY;
import static apptribus.com.tribus.util.Constantes.TRIBU_UNIQUE_NAME;

/**
 * Created by User on 6/7/2017.
 */

public class TribusFragmentViewHolder extends RecyclerView.ViewHolder{

    private Context mContext;

    @BindView(R.id.row_tribus)
    CardView mCardView;

    @BindView(R.id.constraint_layout)
    ConstraintLayout mConstraintLayout;

    @BindView(R.id.circle_image_of_tribu)
    SimpleDraweeView mCircleImageTribu;

    @BindView(R.id.tv_name_of_tribu)
    TextView mTvNameTribu;

    @BindView(R.id.tv_unique_name)
    TextView mTvUniqueName;

    @BindView(R.id.tv_description)
    TextView mTvDescription;


    //FIREBASE INSTANCES
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceUsers;
    private DatabaseReference mReferenceFollowers;
    private DatabaseReference mReferenceAdmin;
    private DatabaseReference mReferenceTribus;
    private DatabaseReference mReferenceMessagesTribus;
    private DatabaseReference mReferenceTopics;
    private DatabaseReference mReferenceSurvey;

    //LISTENERS
    private ValueEventListener mValueListenerRefUser;
    private ValueEventListener mValueListenerRefAdmin;
    private ValueEventListener mValueListenerRefCurrentUser;
    private ValueEventListener mValueListenerRefFollowers;
    private ValueEventListener mValueListenerRefTribus;
    private ValueEventListener mValueListenerRefTribus2;
    private ValueEventListener mValueListenerRefTopics;
    private ValueEventListener mValueListenerRefTopics2;
    private ValueEventListener mValueListenerRefMessagesTribus;
    private ValueEventListener mValueListenerRefMessagesTribus2;


    public TribusFragmentViewHolder(View itemView) {
        super(itemView);
        mContext = itemView.getContext();
        ButterKnife.bind(this, itemView);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceUsers = mDatabase.getReference().child(GENERAL_USERS);
        mReferenceFollowers = mDatabase.getReference().child(FOLLOWERS);
        mReferenceAdmin = mDatabase.getReference().child(ADMINS);
        mReferenceTribus = mDatabase.getReference().child(GENERAL_TRIBUS);
        mReferenceMessagesTribus = mDatabase.getReference().child(TRIBUS_MESSAGES);
        mReferenceTopics = mDatabase.getReference().child(TRIBUS_TOPICS);
        mReferenceSurvey = mDatabase.getReference().child(TRIBUS_SURVEY);
    }


    //SET IMAGE OF TRIBU
    private void setImageOfTribu(String url){

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
                //Log.d("Valor: ", "onFinalImageSet - id: " + id + "imageInfo: " + imageInfo + "animatable: " + animatable);
            }

            @Override
            public void onIntermediateImageSet(String id, Object imageInfo) {
                super.onIntermediateImageSet(id, imageInfo);
                //Log.d("Valor: ", "onIntermediateImageSet - id: " + id + "imageInfo: " + imageInfo);
            }

            @Override
            public void onRelease(String id) {
                super.onRelease(id);
                //Log.d("Valor: ", "onRelease - id: " + id);
            }

            @Override
            public void onSubmit(String id, Object callerContext) {
                super.onSubmit(id, callerContext);
                //Log.d("Valor: ", "onSubmit - id: " + id + "callerContext: " + callerContext);
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
                //.setControllerListener(listener)
                .setOldController(mCircleImageTribu.getController())
                .build();
        mCircleImageTribu.setController(dc);

    }


    //SET DESCRIPTION
    private void setUniqueName(String uniqueName){
        mTvUniqueName.setText(uniqueName);
    }

    private void setTribusDescription(String description){
        mTvDescription.setText(description);
    }

    //SET THEMATIC
    private void setNameTribu(String nameTribu){
        mTvNameTribu.setText(nameTribu);
    }


    public void initTribusFragmentViewHolder(Tribu tribuParam, Fragment context, User mainUser,
                                             FirestoreService mFirestoreService){


        mReferenceTribus
                .child(tribuParam.getUniqueNameTribu())
                .addValueEventListener(mValueListenerRefTribus = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshotTribu) {

                        Tribu tribu = dataSnapshotTribu.getValue(Tribu.class);


                        mFirestoreService.addParticipatingIntoFirestore(mainUser.getId(), tribu.getKey(), tribuParam);

                                                        //VIEWS VISIBILITY
                                                        mCardView.setVisibility(View.VISIBLE);
                                                        mConstraintLayout.setVisibility(View.VISIBLE);
                                                        mCircleImageTribu.setVisibility(View.VISIBLE);
                                                        mCircleImageTribu.bringToFront();
                                                        mConstraintLayout.invalidate();
                                                        mTvNameTribu.setVisibility(View.VISIBLE);
                                                        mTvUniqueName.setVisibility(View.VISIBLE);

                                                        setNameTribu(tribu.getProfile().getNameTribu());
                                                        setUniqueName(tribu.getProfile().getUniqueName());
                                                        setImageOfTribu(tribu.getProfile().getImageUrl());
                                                        setTribusDescription(tribu.getProfile().getDescription());


                                                        itemView.setOnClickListener(v -> {
                                                            openFeatureChoiceActivity(tribu.getKey(), tribu.getProfile().getUniqueName());
                                                        });

                                                        mCircleImageTribu.setOnClickListener(v -> {
                                                            openImageTribu(tribu, context);
                                                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        databaseError.toException().printStackTrace();
                    }
                });

    }


    private void openImageTribu(Tribu tribu, Fragment fragmentContext) {

        //CREATE DIALOG TO SHOW NEW IMAGE
        //CONFIGURATION OF DIALOG
        AlertDialog.Builder builder = new AlertDialog.Builder(fragmentContext.getActivity(), R.style.MyDialogTheme);
        LayoutInflater inflater = fragmentContext.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_image_tribu, null);
        SimpleDraweeView mSdImageTribu = dialogView.findViewById(R.id.sd_image_tribu);
        TextView mTvNameOfTribu = dialogView.findViewById(R.id.tv_name_of_tribu);
        TextView mTvUniqueName = dialogView.findViewById(R.id.tv_unique_name);
        TextView mTvAdminSince = dialogView.findViewById(R.id.tv_created_date);


        mTvNameOfTribu.setText(tribu.getProfile().getNameTribu());
        mTvUniqueName.setText(tribu.getProfile().getUniqueName());
        builder.setView(dialogView);

        /*if (tribu.getTimestampCreated() != null) {
            SimpleDateFormat sfd = new SimpleDateFormat("dd/MM", Locale.getDefault());
            String time = sfd.format(new Date(tribu.getTimestampCreatedLong()));

            String appendTime = "Criada em " + time;
            mTvAdminSince.setText(appendTime);

        } else {*/
            String time = GetTimeAgo.getTimeAgo(tribu.getProfile().getCreationDate(), fragmentContext.getActivity());
            String append = "Criada ";
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
                .setUri(Uri.parse(tribu.getProfile().getImageUrl()))
                .setControllerListener(listener)
                .setOldController(mSdImageTribu.getController())
                .build();
        mSdImageTribu.setController(dc);

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


    private void openFeatureChoiceActivity(String tribuKey, String tribuUniqueName) {
        Intent intent = new Intent(mContext, FeatureChoiceTribusActivity.class);
        intent.putExtra(TRIBU_KEY, tribuKey);
        intent.putExtra(TRIBU_UNIQUE_NAME, tribuUniqueName);
        mContext.startActivity(intent);
    }

}
