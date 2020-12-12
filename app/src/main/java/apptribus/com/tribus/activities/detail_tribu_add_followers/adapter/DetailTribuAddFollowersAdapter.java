package apptribus.com.tribus.activities.detail_tribu_add_followers.adapter;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.detail_tribu_add_followers.mvp.DetailTribuAddFollowersPresenter;
import apptribus.com.tribus.activities.detail_tribu_add_followers.mvp.DetailTribuAddFollowersView;
import apptribus.com.tribus.pojo.Follower;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import butterknife.BindView;
import butterknife.ButterKnife;

import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;

public class DetailTribuAddFollowersAdapter extends RecyclerView.Adapter<DetailTribuAddFollowersAdapter.DetailTribuAddFollowersViewHolder>{

    private Context mContext;
    private List<Follower> mFollowersList;
    private DetailTribuAddFollowersView mView;
    private DetailTribuAddFollowersAdapterListener mDetailTribuAddFollowersAdapterListener;
    private String mTribuKey;

    public DetailTribuAddFollowersAdapter(Context context, List<Follower> followersList, DetailTribuAddFollowersView view,
                                          DetailTribuAddFollowersPresenter presenter, String tribuKey){
        this.mContext = context;
        this.mFollowersList = followersList;
        this.mView = view;
        this.mTribuKey = tribuKey;

        if (presenter != null){
            mDetailTribuAddFollowersAdapterListener = presenter;
        }
    }


    @NonNull
    @Override
    public DetailTribuAddFollowersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user_waiting_permission, parent,false);

        return new DetailTribuAddFollowersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailTribuAddFollowersViewHolder holder, int position) {

        Follower follower = mFollowersList.get(position);

        holder.initDetailTribuAddFollowers(follower, mView);

    }

    @Override
    public int getItemCount() {
        return mFollowersList.size();
    }

    public class DetailTribuAddFollowersViewHolder extends RecyclerView.ViewHolder{

        private Context mContext;

        @BindView(R.id.card_user_waiting_permission)
        CardView mCardUserWaitingPermission;

        @BindView(R.id.circle_image_of_user)
        SimpleDraweeView mImageUser;

        @BindView(R.id.tv_name_of_user)
        TextView mTvUsersName;

        @BindView(R.id.tv_username)
        TextView mTvUsernameUserPermission;

        @BindView(R.id.tv_request_since)
        TextView mTvRequestSince;

        @BindView(R.id.btn_accept_follower)
        Button mBtnAcceptFollower;

        @BindView(R.id.btn_deny)
        Button mBtnDenyInvitation;

        //FIRESTORE INSTANCE
        private FirebaseFirestore mFirestore;

        //FIRESTORE COLLECTIONS REFERENCES
        private CollectionReference mUsersCollection;


        public DetailTribuAddFollowersViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            ButterKnife.bind(this, itemView);

            mFirestore = FirebaseFirestore.getInstance();
            mUsersCollection = mFirestore.collection(GENERAL_USERS);

        }


        private void setImageUser(String url){

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
                    .setOldController(mImageUser.getController())
                    .build();
            mImageUser.setController(dc);

        }

        //set username
        private void setTvUsernameUserPermission(String usernameUserPermission){
            mTvUsernameUserPermission.setText(usernameUserPermission);
        }

        //set User's name
        private void setTvUsersName(String usersName){
            mTvUsersName.setText(usersName);
        }

        public void initDetailTribuAddFollowers(Follower follower, DetailTribuAddFollowersView view){

            mUsersCollection
                    .document(follower.getUidFollower())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {

                        User userFollower = documentSnapshot.toObject(User.class);

                        if (userFollower.getThumb() != null) {
                            setImageUser(userFollower.getThumb());
                        }
                        else {
                            setImageUser(userFollower.getImageUrl());
                        }

                        setTvUsernameUserPermission(userFollower.getUsername());
                        setTvUsersName(userFollower.getName());

                        //LISTENER FOR BUTTON ACCEPT FOLLOWER
                        mBtnAcceptFollower.setOnClickListener(v -> {
                            //CHECK INTERNET CONNECTION
                            if(!ShowSnackBarInfoInternet.checkConnectionAnother()){
                                ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
                            }
                            else {
                                ShowSnackBarInfoInternet.showSnack(true, view);
                                mDetailTribuAddFollowersAdapterListener.btnAcceptFollowerOnClickListener(follower, userFollower);
                            }
                        });


                        //LISTENER FOR BUTTON DON'T ACCEPT FOLLOWER
                        mBtnDenyInvitation.setOnClickListener(v -> {
                            //CHECK INTERNET CONNECTION
                            if(!ShowSnackBarInfoInternet.checkConnectionAnother()){
                                ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
                            }
                            else {
                                ShowSnackBarInfoInternet.showSnack(true, view);
                                mDetailTribuAddFollowersAdapterListener.btnDenyInvitationOnClickListener(follower, userFollower);
                            }
                        });

                        //LISTENER TO SHOW PONTENCIAL'S FOLLOWERS PROFILES
                        mImageUser.setOnClickListener(v -> {
                            mDetailTribuAddFollowersAdapterListener.openUserProfileOnClickListener(follower, mTribuKey);
                        });

                        mTvUsernameUserPermission.setOnClickListener(v -> {
                            mDetailTribuAddFollowersAdapterListener.openUserProfileOnClickListener(follower, mTribuKey);
                        });

                        mTvUsersName.setOnClickListener(v -> {
                            mDetailTribuAddFollowersAdapterListener.openUserProfileOnClickListener(follower, mTribuKey);
                        });
                    })
                    .addOnFailureListener(Throwable::printStackTrace);


        }

    }

    public interface DetailTribuAddFollowersAdapterListener{
        void btnAcceptFollowerOnClickListener(Follower follower, User userFollower);
        void btnDenyInvitationOnClickListener(Follower follower, User userFollower);
        void openUserProfileOnClickListener(Follower follower, String tribuKey);

    }
}
