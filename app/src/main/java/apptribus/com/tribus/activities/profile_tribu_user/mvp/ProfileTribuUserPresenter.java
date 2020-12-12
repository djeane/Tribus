package apptribus.com.tribus.activities.profile_tribu_user.mvp;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.comments.CommentsActivity;
import apptribus.com.tribus.activities.main_activity.MainActivity;
import apptribus.com.tribus.activities.profile_tribu_user.adapter.ProfileTribuUserAdapter;
import apptribus.com.tribus.pojo.Follower;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.GetTimeAgo;
import apptribus.com.tribus.util.PresenceSystemAndLastSeen;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static apptribus.com.tribus.util.Constantes.TRIBU_UNIQUE_NAME;

public class ProfileTribuUserPresenter implements ProfileTribuUserAdapter.ProfileTribuUserAdapterListener {

    private final ProfileTribuUserView view;
    private final ProfileTribuUserModel model;
    private CompositeSubscription subscription = new CompositeSubscription();
    private Tribu mTribu;
    private User mUserAdmin;
    public static boolean isOpen;

    private List<Follower> mListFollowers;
    private ProfileTribuUserAdapter mProfileTribuUserAdapter;


    public ProfileTribuUserPresenter(ProfileTribuUserView view, ProfileTribuUserModel model) {
        this.view = view;
        this.model = model;
    }


    public void onStart() {

        PresenceSystemAndLastSeen.presenceSystem();

        if (mListFollowers == null) {
            mListFollowers = new ArrayList<>();
        }

        subscription.add(observeTribu());

        subscription.add(getAllFollowers());

        loadMore();

        subscription.add(observeProfileImage());
        subscription.add(observeBtnArrowBack());
        subscription.add(observeImageAdmin());
        subscription.add(observableIconIsPublic());
        subscription.add(observableIconIsRestrict());
        subscription.add(observableTvFolder());
        subscription.add(observableIconFolders());
        subscription.add(observeBtnFollow());
        subscription.add(observeBtnIfWaitingAdminsPermission());


        NotificationManager notificationManager = (NotificationManager) view.mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.cancel(7);

        isOpen = true;
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

    public void onStop() {

        isOpen = false;
    }

    private Subscription getAllFollowers() {
        mListFollowers.clear();
        return model.getAllFollowers(mListFollowers, view.mTribuKey)
                .subscribe(contacts -> {
                            mListFollowers = contacts;

                            mProfileTribuUserAdapter = new ProfileTribuUserAdapter(view.mContext,
                                    mListFollowers, view.mTribuKey, this);

                            view.mRvFollowers.setAdapter(mProfileTribuUserAdapter);
                            view.mProgressBar.setVisibility(View.GONE);
                            view.mProgressBarBottom.setVisibility(View.GONE);

                        },
                        Throwable::printStackTrace);

    }

    private void loadMore() {
        view.mRvFollowers.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                if (reachedBottom) {
                    view.mProgressBarBottom.bringToFront();
                    view.mProgressBarBottom.setVisibility(VISIBLE);

                    model.loadMoreFollowers(mListFollowers, mProfileTribuUserAdapter, view.mProgressBarBottom, view.mTribuKey);
                }
            }
        });

    }


    private Subscription observeTribu() {
        return model.getTribu(view.mTribuKey)
                .switchMap(tribu -> {
                    mTribu = tribu;
                    return model.getAdmin(tribu);
                })
                .subscribe(user -> {
                            mUserAdmin = user;
                        if (mUserAdmin.getThumb() != null){
                            setImage(mTribu, mUserAdmin.getThumb());

                        }
                        else {
                            setImage(mTribu, mUserAdmin.getImageUrl());
                        }
                            setToolbarTitle(mTribu);
                            setProfileTribu(mTribu, mUserAdmin);
                            setFoldersAndIcons(mTribu);
                        },
                        Throwable::printStackTrace
                );
    }

    private Subscription observeProfileImage(){
        return view.observeProfileImage()
                .subscribe(__ -> {
                            if (mTribu != null){
                                model.openShowProfileActivity(mTribu.getProfile().getImageUrl());
                            }
                        },
                        Throwable::printStackTrace
                );
    }


    private void setToolbarTitle(Tribu tribu) {
        view.mToolbarDetailTribu.setTitle(tribu.getProfile().getNameTribu());
    }


    private void setFoldersAndIcons(Tribu tribu){
        String messageFoldersRestrict = "A mTribu " + mTribu.getProfile().getNameTribu() + " é restrita. " +
                "Você precisa participar dela para acessar este conteúdo.";

        String messageFoldersPublic = "Toque para visualizar fotos e vídeos compartilhados nesta mTribu";

        if(tribu.getProfile().isPublic()){
            view.mIconIsPublic.setVisibility(VISIBLE);
            view.mIconIsRestrict.setVisibility(GONE);
            view.mIconFolders.setImageResource(R.drawable.ic_action_folder_public);
            view.mTvFolderPublic.setTextColor(view.mContext.getResources().getColor(R.color.colorAccent));
            view.mTvFolderPublic.setText(messageFoldersPublic);
        }
        else { view.mIconIsRestrict.setVisibility(VISIBLE);
            view.mIconIsPublic.setVisibility(GONE);
            view.mIconFolders.setImageResource(R.drawable.ic_action_folder_restrict);
            view.mTvFolderPublic.setTextColor(view.mContext.getResources().getColor(R.color.red));
            view.mTvFolderPublic.setText(messageFoldersRestrict);
        }
    }

    private Subscription observableIconIsPublic(){

        String message = "Esta mTribu é pública.";

        return view.observeIconIsPublic()
                .subscribe(__ -> {
                    Toast.makeText(view.mContext, message, Toast.LENGTH_SHORT).show();
                });
    }

    private Subscription observableIconIsRestrict(){

        String message = "Esta mTribu é restrita aos participantes.";

        return view.observeIconIsRestrict()
                .subscribe(__ -> {
                    Toast.makeText(view.mContext, message, Toast.LENGTH_SHORT).show();
                });
    }

    private Subscription observableTvFolder() {
        return view.observableTvFolder()
                .filter(__ -> {
                    if(!ShowSnackBarInfoInternet.checkConnectionAnother()){
                        ShowSnackBarInfoInternet.showToastInfoInternet(view.mContext);
                        return false;
                    }
                    else {
                        ShowSnackBarInfoInternet.showSnack(true, view);
                        return true;
                    }
                })

                .subscribe(__ -> {
                            openFolders();
                        },
                        Throwable::printStackTrace
                );
    }



    private Subscription observableIconFolders() {
        return view.observableIconFolders()
                .filter(__ -> {
                    if(!ShowSnackBarInfoInternet.checkConnectionAnother()){
                        ShowSnackBarInfoInternet.showToastInfoInternet(view.mContext);
                        return false;
                    }
                    else {
                        ShowSnackBarInfoInternet.showSnack(true, view);
                        return true;
                    }
                })
                .subscribe(__ -> {
                            openFolders();
                        },
                        Throwable::printStackTrace
                );
    }

    private void openFolders() {
        String message = "A mTribu " + mTribu.getProfile().getNameTribu() + " é restrita. " +
                "Você precisa participar dela para acessar este conteúdo.";

        if (mTribu.getProfile().isPublic()) {
            showDialogFolder();
        }
        else {
            Toast.makeText(view.mContext, message, Toast.LENGTH_LONG).show();
        }
    }

    private void showDialogFolder() {
        CharSequence options[] = new CharSequence[]{"Imagens", "Vídeos"};

        String appendTitle = "Ver pastas de " + '"' + mTribu.getProfile().getNameTribu() + '"' + ":";

        AlertDialog.Builder builder = new AlertDialog.Builder(view.mContext);
        builder.setTitle(appendTitle);

        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    model.openTribusImagesOrVideosFolderActivity(view.mTribuKey, "image");
                    break;
                case 1:
                    model.openTribusImagesOrVideosFolderActivity(view.mTribuKey, "video");
                    break;
            }
        });
        builder.show();
    }


    private Subscription observeBtnFollow() {
        return view.observableBtnFollow()
                .filter(__ -> {
                    if(!ShowSnackBarInfoInternet.checkConnectionAnother()){
                        ShowSnackBarInfoInternet.showToastInfoInternet(view.mContext);
                        return false;
                    }
                    else {
                        ShowSnackBarInfoInternet.showSnack(true, view);
                        return true;
                    }
                })
                .doOnNext(__ -> model.creatFollower(mTribu, view.mBtnFollow))
                .switchMap(__ -> model.getFollowerToSetButton(view.mTribuKey))
                .subscribe(isFollower -> {
                            if (isFollower) {
                                setBtnFollower(true);
                                setViewIfParticipant(mTribu);
                            } else {
                                setBtnFollower(false);
                            }

                        },
                        Throwable::printStackTrace
                );
    }

    private void setViewIfParticipant(Tribu tribu){
        view.mRelativeBottom.setVisibility(GONE);
        String appendParticipants = "Participantes (" + tribu.getProfile().getNumFollowers() + ")";
        view.mTvParticipants.setText(appendParticipants);
        view.mRelativeShowFollowers.setVisibility(VISIBLE);
        view.mCoordinatorRecycler.setVisibility(VISIBLE);
    }




    private Subscription observeBtnIfWaitingAdminsPermission() {
        return model.setButtonIfWaitingPermission(view.mTribuKey)
                .subscribe(isWaiting -> {
                    if (isWaiting) {
                        view.mBtnFollow.setText("Aguardando aprovação");
                        view.mBtnFollow.setTextColor(view.mContext.getResources().getColor(R.color.red));
                        view.mBtnFollow.setEnabled(false);
                    }
                });
    }


    private Subscription observeImageAdmin() {
        return view.observableImageAdmin()
                .subscribe(__ -> {
                            if (mTribu != null && mUserAdmin != null) {
                                openImageAdmin(mTribu, mUserAdmin);
                            }
                        },
                        Throwable::printStackTrace
                );
    }

    private void openImageAdmin(Tribu tribu, User admin) {

        //CREATE DIALOG TO SHOW NEW IMAGE
        //CONFIGURATION OF DIALOG
        //if(admin.getId().equals(mTribu.getAdmin().getUidAdmin())) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.mContext, R.style.MyDialogTheme);
        LayoutInflater inflater = view.mContext.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_profile_admin, null);
        SimpleDraweeView mSdImageAdmin = (SimpleDraweeView) dialogView.findViewById(R.id.sd_large_image_admin);
        TextView mTvNameOfTribu = (TextView) dialogView.findViewById(R.id.tv_name_of_admin);
        TextView mTvUniqueName = (TextView) dialogView.findViewById(R.id.tv_username_admin);
        TextView mTvAdminSince = (TextView) dialogView.findViewById(R.id.tv_admin_since);
        //dialogView.setBackgroundColor(fragmentContext.getResources().getColor(R.color.transparent));


        mTvNameOfTribu.setText(admin.getName());
        mTvUniqueName.setText(admin.getUsername());
        builder.setView(dialogView);

        /*if (tribu.getAdmin().getTimestampCreated() != null) {
            SimpleDateFormat sfd = new SimpleDateFormat("dd/MM");
            String time = sfd.format(new Date(tribu.getAdmin().getTimestampCreatedLong()));

            String appendTime = "Admin desta mTribu desde: " + time;
            mTvAdminSince.setText(appendTime);

        } else {*/
            String time = GetTimeAgo.getTimeAgo(tribu.getAdmin().getDate(), view.mContext);
            String append = "Admin destra tribu ";
            String appendDate = append + time;
            mTvAdminSince.setText(appendDate);
        //}

        //Log.d("Valor: ", "image - View: " + admin.getImageUrl());
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
                .setOldController(mSdImageAdmin.getController())
                .build();
        mSdImageAdmin.setController(dc);

        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams wmlp = dialog.getWindow()
                .getAttributes();
        wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        //wmlp.gravity = WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;
        //dialog.getWindow().setGravity(wmlp.gravity);


        dialog.show();

        /*dialog.setOnShowListener(d -> {
            //ImageView image = (ImageView) dialog.findViewById(R.id.goProDialogImage);
            Bitmap icon = BitmapFactory.decodeFile(admin.getImageUrl());
            float imageWidthInPX = (float) mSdChangePhoto.getWidth();

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(Math.round(imageWidthInPX),
                    Math.round(imageWidthInPX * (float) icon.getHeight() / (float) icon.getWidth()));
            mSdChangePhoto.setLayoutParams(layoutParams);
            //}
            //CLICK LISTENER TO BUTTON


        });*/

    }


    private void openCommentsActivity() {

        Intent intent = new Intent(view.mContext, CommentsActivity.class);
        intent.putExtra(TRIBU_UNIQUE_NAME, view.mTribuKey);
        view.mContext.startActivity(intent);
    }


    /*private void setBtnFollow(Tribu tribu, User mUserAdmin) {
        if (mAuth.getCurrentUser().getUid().equals(mTribu.getAdmin().getUidAdmin())) {
            view.mBtnFollow.setText("SOU ADMIN");
        } else if (tribu != null && !mAuth.getCurrentUser().getUid().equals(mTribu.getAdmin().getUidAdmin())) {
            view.mBtnFollow.setText("SEGUINDO");
        } else {
            view.mBtnFollow.setText("SEGUIR");
        }

    }*/


    private void setBtnFollower(boolean isFollower) {
        if (isFollower) {
            //view.mBtnFollow.setText("SEGUINDO");
            view.mBtnFollow.setVisibility(GONE);
            view.mBtnFollow.setEnabled(false);
        } else {
            view.mBtnFollow.setText("Aguardando Aprovação");
            view.mBtnFollow.setTextColor(view.mContext.getResources().getColor(R.color.red));
            view.mBtnFollow.setEnabled(false);
        }

    }


    //SETTING VIEWS
    //mLargeUserImage
    private void setImage(Tribu tribu, String adminImageUrl) {
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
                .setOldController(view.mSdLargeImageTribu.getController())
                .build();
        view.mSdLargeImageTribu.setController(dc);

        DraweeController dcAdmin = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse(adminImageUrl))
                //.setControllerListener(listener)
                .setOldController(view.mSdRoundImageAdmin.getController())
                .build();
        view.mSdRoundImageAdmin.setController(dcAdmin);
    }

    private Subscription observeBtnArrowBack() {
        return view.observableBtnArrowBack()
                .subscribe(__ -> {
                            if (view.fromNotification != null) {
                                Intent intent = new Intent(view.mContext, MainActivity.class);
                                view.mContext.startActivity(intent);
                                view.mContext.finish();

                            } else {
                                model.backToMainActivity();
                            }

                        },
                        Throwable::printStackTrace
                );
    }

    private void setProfileTribu(Tribu tribu, User admin) {

        view.mTvUsername.setText(admin.getUsername());
        String appendName = "admin: " + admin.getName();
        view.mTvNameAdmin.setText(appendName);
        view.mTvTribusName.setText(tribu.getProfile().getNameTribu());
        view.mTvUniqueName.setText(tribu.getProfile().getUniqueName());
        view.mTvDescription.setText(tribu.getProfile().getDescription());
        view.mTvThematic.setText(tribu.getProfile().getThematic());
        view.mTvNumParticipants.setText(String.valueOf(tribu.getProfile().getNumFollowers()));

    }


    public void onDestroy() {
            subscription.clear();

    }

    @Override
    public void openFollowerProfile(String followerId, String tribuKey) {
        model.openFollowerProfile(followerId, tribuKey);
    }

    @Override
    public void openCurrentUserProfile() {
        model.openCurrentUserProfile();
    }
}
