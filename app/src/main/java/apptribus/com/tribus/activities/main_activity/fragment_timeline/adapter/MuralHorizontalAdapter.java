package apptribus.com.tribus.activities.main_activity.fragment_timeline.adapter;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import java.util.List;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.main_activity.fragment_timeline.mvp.TimeLinePresenter;
import apptribus.com.tribus.activities.main_activity.fragment_timeline.mvp.TimeLineView;
import apptribus.com.tribus.pojo.CollectionTag;
import apptribus.com.tribus.pojo.MessageTag;
import apptribus.com.tribus.pojo.MessageUser;
import apptribus.com.tribus.pojo.Tribu;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.VISIBLE;
import static apptribus.com.tribus.util.Constantes.ADMIN_PERMISSION;
import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.MESSAGE_TAGS;
import static apptribus.com.tribus.util.Constantes.TAG_COLLECTION;
import static apptribus.com.tribus.util.Constantes.TEXT;
import static apptribus.com.tribus.util.Constantes.TOPICS;
import static apptribus.com.tribus.util.Constantes.TOPIC_MESSAGES;
import static apptribus.com.tribus.util.Constantes.TRIBUS_PARTICIPANTS;
import static apptribus.com.tribus.util.Constantes.TRIBUS_TOPICS;

public class MuralHorizontalAdapter extends RecyclerView.Adapter<MuralHorizontalAdapter.MuralViewHolder>{

    private Context mContext;
    private List<CollectionTag> mTagList;

    //FIRESTORE INSTANCE
    private FirebaseFirestore mFirestore;

    //FIRESTORE COLLECTIONS REFERENCES
    private CollectionReference mUsersCollection;
    private CollectionReference mTribusCollection;
    private CollectionReference mTagCollection;

    //FIREBASE INSTANCES
    private FirebaseAuth mAuth;

    public MuralHorizontalAdapter(){

    }

    public void notifyMuralHorizontalAdapter(List<CollectionTag> tagList){
        if (tagList != null){
            mTagList = tagList;
            notifyDataSetChanged();
        }
    }

    public void setMuralHorizontalAdapter(Context context, List<CollectionTag> tagList, TimeLinePresenter presenter){
        this.mContext = context;
        this.mTagList = tagList;

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mUsersCollection = mFirestore.collection(GENERAL_USERS);
        mTribusCollection = mFirestore.collection(GENERAL_TRIBUS);
        mTagCollection = mFirestore.collection(TAG_COLLECTION);

    }


    @NonNull
    @Override
    public MuralViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_mural_text, parent, false);

        return new MuralViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MuralViewHolder holder, int position) {

        CollectionTag tag = mTagList.get(position);

        holder.initMuralVH(tag);

    }

    @Override
    public int getItemCount() {
        if (mTagList != null) {
            return mTagList.size();
        }
        else {
            return -1;
        }
    }



    public class MuralViewHolder extends RecyclerView.ViewHolder{

        private Context mContext;

        @BindView(R.id.relative_row_mural_text)
        RelativeLayout mRelativeRowMuralText;

        @BindView(R.id.constraint_layout)
        ConstraintLayout mConstraintLayout;

        @BindView(R.id.circle_image_of_tribu)
        SimpleDraweeView mCircleImageOfTribu;

        @BindView(R.id.tv_tribus_name)
        TextView mTvTribusName;

        @BindView(R.id.tv_tribus_uniquename)
        TextView mTvTribusUniqueName;

        @BindView(R.id.btn_follow)
        Button mBtnFollow;

        @BindView(R.id.tv_message)
        TextView mTvMessage;

        @BindView(R.id.iv_inspiration)
        ImageView mIvInspiration;

        @BindView(R.id.tv_num_inspiration)
        TextView mTvNumInspiration;

        @BindView(R.id.iv_love)
        ImageView mIvLove;

        @BindView(R.id.tv_num_love)
        TextView mTvNumLove;

        @BindView(R.id.iv_genius)
        ImageView mIvGenius;

        @BindView(R.id.tv_num_genius)
        TextView mTvNumGenius;

        @BindView(R.id.tv_tags)
        TextView mTvTags;


        public MuralViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            ButterKnife.bind(this, itemView);

            mCircleImageOfTribu.bringToFront();
            mConstraintLayout.invalidate();

        }

        private void initMuralVH(CollectionTag tag){

            setTvNumInspiration(0);
            setTvNumLove(0);
            setTvNumGenius(0);


            //get Tribu's data
            mTribusCollection
                    .document(tag.getTribuKeyTag())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {

                        Tribu tribu = documentSnapshot.toObject(Tribu.class);

                        if (tribu.getProfile().getThumb() != null){
                            setTribusImage(tribu.getProfile().getThumb());
                        }
                        else {
                            setTribusImage(tribu.getProfile().getImageUrl());
                        }

                        setTvTribusName(tribu.getProfile().getNameTribu());
                        setTvTribusUniqueName(tribu.getProfile().getUniqueName());

                    })
                    .addOnFailureListener(Throwable::printStackTrace);


            //get messaage's data
            mTribusCollection
                    .document(tag.getTribuKeyTag())
                    .collection(TOPICS)
                    .document(tag.getTopicKeyTag())
                    .collection(TOPIC_MESSAGES)
                    .document(tag.getMessageKeyTag())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {

                        MessageUser message = documentSnapshot.toObject(MessageUser.class);

                        if (message.getContentType().equals(TEXT)){
                            setTvMessage(message.getMessage());
                        }

                    })
                    .addOnFailureListener(Throwable::printStackTrace);



            //get number of tags
            mTribusCollection
                    .document(tag.getTribuKeyTag())
                    .collection(TOPICS)
                    .document(tag.getTopicKeyTag())
                    .collection(TOPIC_MESSAGES)
                    .document(tag.getMessageKeyTag())
                    .collection(MESSAGE_TAGS)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()){
                            setTvTag(queryDocumentSnapshots.size());
                        }

                    })
                    .addOnFailureListener(Throwable::printStackTrace);


            //get follower
            mTribusCollection
                    .document(tag.getTribuKeyTag())
                    .collection(TRIBUS_PARTICIPANTS)
                    .document(mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshotFollower -> {

                        mTribusCollection
                                .document(tag.getTribuKeyTag())
                                .collection(ADMIN_PERMISSION)
                                .document(mAuth.getCurrentUser().getUid())
                                .get()
                                .addOnSuccessListener(documentSnapshotWaitingPermission -> {

                                    if (documentSnapshotFollower != null && documentSnapshotFollower.exists()){

                                        mBtnFollow.setBackground(mContext.getResources().getDrawable(R.drawable.button_thematics_pressed));
                                        mBtnFollow.setTextColor(mContext.getResources().getColor(R.color.colorIcons));
                                        mBtnFollow.setText("Participando");

                                    }
                                    else if (documentSnapshotWaitingPermission != null && documentSnapshotWaitingPermission.exists()){

                                        mBtnFollow.setBackground(mContext.getResources().getDrawable(R.drawable.button_leave_tribu));
                                        mBtnFollow.setTextColor(mContext.getResources().getColor(R.color.red));
                                        mBtnFollow.setText("Solicitado");

                                    }
                                    else {

                                        mBtnFollow.setBackground(mContext.getResources().getDrawable(R.drawable.button_accepted_and_added));
                                        mBtnFollow.setTextColor(mContext.getResources().getColor(R.color.accent));
                                        mBtnFollow.setText("Participar");

                                    }

                                    mBtnFollow.setVisibility(VISIBLE);

                                })
                                .addOnFailureListener(Throwable::printStackTrace);


                    })
                    .addOnFailureListener(Throwable::printStackTrace);

        }

        private void setTvTribusName(String tribuName){
            mTvTribusName.setText(tribuName);
        }

        private void setTvTribusUniqueName(String tribuUniqueName){
            mTvTribusUniqueName.setText(tribuUniqueName);
        }

        private void setTvNumInspiration(int numInspiration){
            mTvNumInspiration.setText(String.valueOf(numInspiration));
        }

        private void setTvNumLove(int numLove){
            mTvNumLove.setText(String.valueOf(numLove));
        }

        private void setTvNumGenius(int numGenius){
            mTvNumGenius.setText(String.valueOf(numGenius));
        }

        private void setTvTag(int numTags){
            String appenNumTags;

            if (numTags <= 1){
                appenNumTags = String.valueOf(numTags) + "tag";
            }
            else {
                appenNumTags = String.valueOf(numTags) + "tags";

            }
            mTvTags.setText(String.valueOf(appenNumTags));
        }

        private void setTvMessage(String message){
            mTvMessage.setText(message);
        }

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
                    .setOldController(mCircleImageOfTribu.getController())
                    .build();
            mCircleImageOfTribu.setController(dc);

        }

    }
}
