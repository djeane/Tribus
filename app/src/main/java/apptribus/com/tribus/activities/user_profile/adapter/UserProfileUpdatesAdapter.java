package apptribus.com.tribus.activities.user_profile.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.user_profile.mvp.UserProfilePresenter;
import apptribus.com.tribus.activities.user_profile.mvp.UserProfileView;
import apptribus.com.tribus.pojo.MessageUser;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.UserUpdate;
import butterknife.BindView;
import butterknife.ButterKnife;

import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.MESSAGE_TAGS;
import static apptribus.com.tribus.util.Constantes.TAG_COLLECTION;
import static apptribus.com.tribus.util.Constantes.TEXT;
import static apptribus.com.tribus.util.Constantes.TOPICS;
import static apptribus.com.tribus.util.Constantes.TOPIC_MESSAGES;

public class UserProfileUpdatesAdapter extends RecyclerView.Adapter<UserProfileUpdatesAdapter.UserProfileUpdatesTextViewHolder>{

    private Context mContext;
    private List<UserUpdate> mUpdateList;
    private UserProfileView mView;

    //FIRESTORE INSTANCE
    private FirebaseFirestore mFirestore;

    //FIRESTORE COLLECTIONS REFERENCES
    private CollectionReference mUsersCollection;
    private CollectionReference mTribusCollection;
    private CollectionReference mTagCollection;

    //FIREBASE INSTANCES
    private FirebaseAuth mAuth;


    public UserProfileUpdatesAdapter(Context context, List<UserUpdate> updateList, UserProfileView view,
                                     UserProfilePresenter presenter){

        this.mContext = context;
        this.mUpdateList = updateList;
        this.mView = view;

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mUsersCollection = mFirestore.collection(GENERAL_USERS);
        mTribusCollection = mFirestore.collection(GENERAL_TRIBUS);
        mTagCollection = mFirestore.collection(TAG_COLLECTION);

    }


    @NonNull
    @Override
    public UserProfileUpdatesTextViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_update_tag_text, parent, false);

        return new UserProfileUpdatesTextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserProfileUpdatesTextViewHolder holder, int position) {

        UserUpdate update = mUpdateList.get(position);

        holder.initUserProfileUpdatesTextVH(update);
    }

    @Override
    public int getItemCount() {
        if (mUpdateList != null){
            return mUpdateList.size();
        }
        else {
            return -1;
        }

    }

    public class UserProfileUpdatesTextViewHolder extends RecyclerView.ViewHolder{

        private Context mContext;

        @BindView(R.id.tv_title)
        TextView mTvTitle;

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


        public UserProfileUpdatesTextViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            ButterKnife.bind(this, itemView);

        }

        private void initUserProfileUpdatesTextVH(UserUpdate update){

            setTvNumInspiration(0);
            setTvNumLove(0);
            setTvNumGenius(0);

            //get Tribu's data
            mTribusCollection
                    .document(update.getTag().getTribuKeyTag())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {

                        Tribu tribu = documentSnapshot.toObject(Tribu.class);

                        setTvTile(tribu.getProfile().getNameTribu());

                    })
                    .addOnFailureListener(Throwable::printStackTrace);


            //get number of tags
            mTribusCollection
                    .document(update.getTag().getTribuKeyTag())
                    .collection(TOPICS)
                    .document(update.getTag().getTopicKeyTag())
                    .collection(TOPIC_MESSAGES)
                    .document(update.getTag().getMessageKeyTag())
                    .collection(MESSAGE_TAGS)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()){
                            setTvTag(queryDocumentSnapshots.size());
                        }

                    })
                    .addOnFailureListener(Throwable::printStackTrace);

            //get messaage's data
            mTribusCollection
                    .document(update.getTag().getTribuKeyTag())
                    .collection(TOPICS)
                    .document(update.getTag().getTopicKeyTag())
                    .collection(TOPIC_MESSAGES)
                    .document(update.getTag().getMessageKeyTag())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {

                        MessageUser message = documentSnapshot.toObject(MessageUser.class);

                        if (message.getContentType().equals(TEXT)){
                            setTvMessage(message.getMessage());
                        }

                    })
                    .addOnFailureListener(Throwable::printStackTrace);


        }

        private void setTvTile(String tribusName){

            String appendTitle = "Você adicionou uma tag à mensagem publicada na tribu '" + tribusName
                    + "'.";

            mTvTitle.setText(appendTitle);
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

    }
}
