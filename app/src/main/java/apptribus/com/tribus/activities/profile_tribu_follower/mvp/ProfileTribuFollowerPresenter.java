package apptribus.com.tribus.activities.profile_tribu_follower.mvp;

import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
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

import java.util.ArrayList;
import java.util.List;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.profile_tribu_follower.adapter.ProfileTribuFollowerAdapter;
import apptribus.com.tribus.pojo.Follower;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.GetTimeAgo;
import apptribus.com.tribus.util.PresenceSystemAndLastSeen;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static android.view.View.*;

public class ProfileTribuFollowerPresenter implements ProfileTribuFollowerAdapter.ProfileTribuFollowerAdapterListener {

    private final ProfileTribuFollowerView view;
    private final ProfileTribuFollowerModel model;
    private CompositeSubscription subscription = new CompositeSubscription();
    private Tribu mTribu;
    private User mUserAdmin;
    public static boolean isOpen;
    private Tribu mTribuFollower;

    private List<Follower> mListFollowers;
    private ProfileTribuFollowerAdapter mProfileTribuFollowerAdapter;


    public ProfileTribuFollowerPresenter(ProfileTribuFollowerView view, ProfileTribuFollowerModel model) {
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

        subscription.add(observeBtnArrowBack());
        subscription.add(observeImageAdmin());
        //subscription.add(observableIconFolders());
        //subscription.add(observeTvFolderPublic());


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

        isOpen = true;
    }

    private Subscription getAllFollowers() {
        mListFollowers.clear();
        return model.getAllFollowers(mListFollowers, view.mTribuKey)
                .subscribe(contacts -> {
                            mListFollowers = contacts;

                            mProfileTribuFollowerAdapter = new ProfileTribuFollowerAdapter(view.mContext,
                                    mListFollowers, view.mTribuKey, this);

                            view.mRvFollowers.setAdapter(mProfileTribuFollowerAdapter);
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

                    model.loadMoreFollowers(mListFollowers, mProfileTribuFollowerAdapter, view.mProgressBarBottom, view.mTribuKey);
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
                .switchMap(user -> {
                    mUserAdmin = user;
                    return model.getTribuFollower(mTribu);
                })
                .subscribe(tribu -> {
                            if (mUserAdmin.getThumb() != null) {
                                setImage(mTribu, mUserAdmin.getThumb());
                            } else {
                                setImage(mTribu, mUserAdmin.getImageUrl());
                            }

                            setProfileTribu(mTribu, mUserAdmin);
                            setToolbarTitle(mTribu);
                        },
                        Throwable::printStackTrace
                );
    }


    private void setToolbarTitle(Tribu tribu) {
        view.mToolbarDetailTribu.setTitle(tribu.getProfile().getNameTribu());
    }


    private Subscription observeTvFolderPublic() {
        return view.observableTvFolderPublic()
                .filter(__ -> {
                    if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                        ShowSnackBarInfoInternet.showToastInfoInternet(view.mContext);
                        return false;
                    } else {
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
                    if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                        ShowSnackBarInfoInternet.showToastInfoInternet(view.mContext);
                        return false;
                    } else {
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

        showDialogFolder();

        /*String message = "A mTribu " + mTribu.getProfile().getNameTribu() + " é restrita. " +
                "Você precisa segui-la para acessar este conteúdo.";

        if (mTribu.getAdmin().getUidAdmin().equals(mAuth.getCurrentUser().getUid())){
            showDialogFolder();
        }
        else {
            if(mTribu.getProfile().isPublic()){
                showDialogFolder();
            }
            else {
                Toast.makeText(view.mContext, message , Toast.LENGTH_LONG).show();
            }
        }*/
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
            //SimpleDateFormat sfd = new SimpleDateFormat("dd/MM HH:mm");
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
                .subscribe(
                        __ -> model.backToMainActivity(),
                        Throwable::printStackTrace
                );
    }

    private void setProfileTribu(Tribu tribu, User admin) {

        view.mTvUsername.setText(admin.getUsername());
        String appendName = "admin: " + admin.getName();
        view.mTvNameAdmin.setText(appendName);
        //view.mTvUsernameAdmin.setText(admin.getUsername());
        //view.mTvTribusName.setText(mTribu.getProfile().getNameTribu());
        view.mTvUniqueName.setText(tribu.getProfile().getUniqueName());
        view.mTvDescription.setText(tribu.getProfile().getDescription());
        view.mTvThematic.setText(tribu.getProfile().getThematic());

        if (tribu.getProfile().getNumFollowers() <= 1) {
            String appendFollowers = "Participante (" + String.valueOf(tribu.getProfile().getNumFollowers()) + ")";
            view.mTvParticipant.setText(appendFollowers);
        } else {
            String appendFollowers = "Participantes (" + String.valueOf(tribu.getProfile().getNumFollowers()) + ")";
            view.mTvParticipant.setText(appendFollowers);
        }

        if (tribu.getProfile().isPublic()) {
            view.mIvIsPublic.setVisibility(VISIBLE);
            view.mIvIsRestrict.setVisibility(GONE);
        } else {
            view.mIvIsPublic.setVisibility(GONE);
            view.mIvIsRestrict.setVisibility(VISIBLE);
        }

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
