package apptribus.com.tribus.activities.profile_tribu_user.adapter;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.profile_tribu_user.mvp.ProfileTribuUserPresenter;
import apptribus.com.tribus.pojo.Follower;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.GetTimeAgo;
import butterknife.BindView;
import butterknife.ButterKnife;

import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;

public class ProfileTribuUserAdapter extends RecyclerView.Adapter<ProfileTribuUserAdapter.ProfileTribuAdminViewHolder> {

    private Context mContext;
    private List<Follower> mListFollowers;
    private String mTribusKey;
    private ProfileTribuUserAdapterListener mListener;

    public ProfileTribuUserAdapter(Context context, List<Follower> listFollowers, String tribusKey,
                                   ProfileTribuUserPresenter presenter) {
        this.mContext = context;
        this.mListFollowers = listFollowers;
        this.mTribusKey = tribusKey;

        if (presenter != null) {
            mListener = presenter;
        }
    }

    @NonNull
    @Override
    public ProfileTribuAdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_new_followers, parent, false);

        return new ProfileTribuAdminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileTribuAdminViewHolder holder, int position) {

        Follower follower = mListFollowers.get(position);

        holder.initProfileTribuFollowerVH(follower);

    }

    @Override
    public int getItemCount() {
        return mListFollowers.size();
    }


    public class ProfileTribuAdminViewHolder extends RecyclerView.ViewHolder {

        private Context mContext;

        @BindView(R.id.circle_image_of_follower)
        SimpleDraweeView mImageFollower;

        @BindView(R.id.tv_name_of_follower)
        TextView mTvFollowersName;

        @BindView(R.id.tv_username_follower)
        TextView mTvUsernameFollower;

        @BindView(R.id.tv_following_since)
        TextView mTvFollowingSince;

        //FIREBASE INSTANCES
        private FirebaseAuth mAuth;

        //FIRESTORE INSTANCE
        private FirebaseFirestore mFirestore;

        //FIRESTORE COLLECTIONS REFERENCES
        private CollectionReference mUsersCollection;


        public ProfileTribuAdminViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            ButterKnife.bind(this, itemView);

            mAuth = FirebaseAuth.getInstance();
            mFirestore = FirebaseFirestore.getInstance();
            mUsersCollection = mFirestore.collection(GENERAL_USERS);

        }


        //set image
        private void setImageFollower(String url) {
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
                    .setOldController(mImageFollower.getController())
                    .build();
            mImageFollower.setController(dc);

        }

        //set username
        private void setTvUsernameFollower(String usernameFollower) {
            mTvUsernameFollower.setText(usernameFollower);
        }

        //set follower's name
        private void setTvFollowersName(String followersName) {
            mTvFollowersName.setText(followersName);
        }

        //set following since
        private void setTvFollowingSince(Date followingSince) {

            String date = GetTimeAgo.getTimeAgo(followingSince, mContext);
            mTvFollowingSince.setText(date);
        }


        private void initProfileTribuFollowerVH(Follower follower) {

            mUsersCollection
                    .document(follower.getUidFollower())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {

                        User user = documentSnapshot.toObject(User.class);

                        if (!user.getId().equals(mAuth.getCurrentUser().getUid())) {
                            setTvUsernameFollower(user.getUsername());
                            setTvFollowersName(user.getName());

                            if (user.getThumb() != null) {
                                setImageFollower(user.getThumb());
                            } else {
                                setImageFollower(user.getImageUrl());
                            }

                            mImageFollower.setOnClickListener(v -> {
                                mListener.openFollowerProfile(user.getId(), mTribusKey);
                            });
                        } else {
                            setTvUsernameFollower(user.getUsername());
                            setTvFollowersName("Você");
                            setImageFollower(user.getImageUrl());
                            setTvFollowingSince(follower.getDate());

                            mImageFollower.setOnClickListener(v -> {
                                mListener.openCurrentUserProfile();
                            });

                        }

                    })
                    .addOnFailureListener(Throwable::printStackTrace);


        }

    }

    public interface ProfileTribuUserAdapterListener {
        void openFollowerProfile(String followerId, String tribuKey);

        void openCurrentUserProfile();
    }
}
