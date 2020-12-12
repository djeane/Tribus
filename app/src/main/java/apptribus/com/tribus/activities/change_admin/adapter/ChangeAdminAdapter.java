package apptribus.com.tribus.activities.change_admin.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
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
import apptribus.com.tribus.activities.change_admin.mvp.ChangeAdminPresenter;
import apptribus.com.tribus.activities.change_admin.mvp.ChangeAdminView;
import apptribus.com.tribus.pojo.Follower;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import butterknife.BindView;
import butterknife.ButterKnife;

import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;

public class ChangeAdminAdapter extends RecyclerView.Adapter<ChangeAdminAdapter.ChangeAdminViewHolder> {

    private Context mContext;
    private List<Follower> mFollowersList;
    private ChangeAdminView mView;
    private ChangeAdminOnClickListener mChangeAdminOnClickListener;


    public ChangeAdminAdapter(Context context, List<Follower> followersList, ChangeAdminView view, ChangeAdminPresenter presenter) {
        this.mContext = context;
        this.mFollowersList = followersList;
        this.mView = view;
        if (presenter != null) {
            mChangeAdminOnClickListener = presenter;
        }
    }

    @NonNull
    @Override
    public ChangeAdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_potencial_admin, parent, false);
        return new ChangeAdminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChangeAdminViewHolder holder, int position) {

        Follower follower = mFollowersList.get(position);

        if (!follower.isAdmin()){
            holder.initChangeAdminViewHolder(follower, mView);
        }
        else {
            mFollowersList.remove(follower);
            //holder.mRowChangeAdmin.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mFollowersList.size();
    }


    public class ChangeAdminViewHolder extends RecyclerView.ViewHolder {

        private Context mContext;

        @BindView(R.id.row_change_admin)
        RelativeLayout mRowChangeAdmin;

        @BindView(R.id.constraint_layout_potential_admin)
        ConstraintLayout mConstraintLayoutChangeAdmin;

        @BindView(R.id.constraint_buttons)
        ConstraintLayout mConstraintButtons;

        @BindView(R.id.card_change_admin)
        CardView mCardChangeAdmin;

        @BindView(R.id.circle_image_of_follower)
        SimpleDraweeView mImageFollower;

        @BindView(R.id.tv_name_of_follower)
        TextView mTvFollowersName;

        @BindView(R.id.tv_username_follower)
        TextView mTvUsernameFollower;

        @BindView(R.id.tv_follower_since)
        TextView mTvFollowerSince;

        @BindView(R.id.btn_change_admin)
        Button mBtnChangeAdmin;

        private ProgressDialog progress;

        //FIRESTORE INSTANCE
        private FirebaseFirestore mFirestore;

        //FIRESTORE COLLECTIONS REFERENCES
        private CollectionReference mUsersCollections;


        public ChangeAdminViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            ButterKnife.bind(this, itemView);
            mFirestore = FirebaseFirestore.getInstance();
            mUsersCollections = mFirestore.collection(GENERAL_USERS);


        }

        private void setImageUser(String url) {
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
                    .setControllerListener(listener)
                    .setOldController(mImageFollower.getController())
                    .build();
            mImageFollower.setController(dc);

        }


        //set Username
        private void setTvUsernameFollower(String usernameFollower) {
            mTvUsernameFollower.setText(usernameFollower);
        }

        //set name
        private void setTvFollowersName(String followersName) {
            mTvFollowersName.setText(followersName);
        }


        private void initChangeAdminViewHolder(Follower follower, ChangeAdminView view) {

            mUsersCollections
                    .document(follower.getUidFollower())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {

                        User userFollower = documentSnapshot.toObject(User.class);

                        //setup views
                        if (userFollower.getThumb() != null) {
                            setImageUser(userFollower.getThumb());
                        } else {
                            setImageUser(userFollower.getImageUrl());
                        }

                        setTvFollowersName(userFollower.getName());
                        setTvUsernameFollower(userFollower.getUsername());

                        mBtnChangeAdmin.setOnClickListener(v -> {
                            //CHECK INTERNET CONNECTION
                            if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                                ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
                            } else {
                                ShowSnackBarInfoInternet.showSnack(true, view);
                                mChangeAdminOnClickListener.bntChangeAdminOnClick(userFollower);
                            }

                        });

                        //LISTENER TO OPEN USER PROFILE ACTIVITY
                        mImageFollower.setOnClickListener(v -> {
                            mChangeAdminOnClickListener.openProfileActivityOnClick(userFollower.getId());
                        });

                        mTvUsernameFollower.setOnClickListener(v -> {
                            mChangeAdminOnClickListener.openProfileActivityOnClick(userFollower.getId());
                        });

                        mTvFollowersName.setOnClickListener(v -> {
                            mChangeAdminOnClickListener.openProfileActivityOnClick(userFollower.getId());
                        });

                    })
                    .addOnFailureListener(Throwable::printStackTrace);


        }

    }


    public interface ChangeAdminOnClickListener {

        void bntChangeAdminOnClick(User follower);

        void openProfileActivityOnClick(String userFollowerId);

    }
}
