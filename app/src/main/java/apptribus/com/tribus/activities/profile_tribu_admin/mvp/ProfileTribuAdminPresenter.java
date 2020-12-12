package apptribus.com.tribus.activities.profile_tribu_admin.mvp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.enums.EPickType;

import java.util.ArrayList;
import java.util.List;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.profile_tribu_admin.adapter.ProfileTribuAdminAdapter;
import apptribus.com.tribus.activities.tribus_images_folder.TribusImagesFolderActivity;
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

public class ProfileTribuAdminPresenter implements ProfileTribuAdminAdapter.ProfileTribuAdminAdapterListener {

    private final ProfileTribuAdminView view;
    private final ProfileTribuAdminModel model;
    private CompositeSubscription subscription = new CompositeSubscription();
    private Tribu mTribu;
    private User mUserAdmin;
    private Tribu mTribuFollower;
    private boolean response = true;
    private ProgressDialog progress;
    private String mDescription;
    private boolean mOptionIsPublic;
    public static boolean isOpen;
    private FirebaseAuth mAuth;


    private List<Follower> mListFollowers;
    private ProfileTribuAdminAdapter mProfileTribuAdminAdapter;


    public ProfileTribuAdminPresenter(ProfileTribuAdminView view, ProfileTribuAdminModel model) {
        this.view = view;
        this.model = model;
        mAuth = FirebaseAuth.getInstance();

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
        //subscription.add(observableIconFolders());
        //subscription.add(observeTvFolderPublic());
        subscription.add(observeBtnChangeDescription());
        subscription.add(observeBtnSaveChangedDescription());
        subscription.add(observeImageAdmin());
        subscription.add(observeFab());
        subscription.add(observeTvChangeIsPublic());
        subscription.add(observeRelativeEditTribu());


        //SET PROGRESS BAR
        progress = new ProgressDialog(view.mContext);
        progress.setMessage(view.getContext().getString(R.string.checking_username));
        progress.setCancelable(false);

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

                            mProfileTribuAdminAdapter = new ProfileTribuAdminAdapter(view.mContext,
                                    mListFollowers, view.mTribuKey, this);

                            view.mRvFollowers.setAdapter(mProfileTribuAdminAdapter);
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

                    model.loadMoreFollowers(mListFollowers, mProfileTribuAdminAdapter, view.mProgressBarBottom, view.mTribuKey);
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
                            if (tribu != null) {
                                mTribuFollower = tribu;
                            }
                            if (mUserAdmin.getThumb() != null) {
                                setImage(mTribu, mUserAdmin.getThumb());
                            } else {
                                setImage(mTribu, mUserAdmin.getImageUrl());
                            }

                            setBtnFollow(mTribuFollower, mUserAdmin);
                            setToolbarTitle(mTribu);
                            setProfileTribu(mTribu, mUserAdmin);

                        },
                        Throwable::printStackTrace
                );
    }

    private Subscription observeProfileImage() {
        return view.observeProfileImage()
                .subscribe(__ -> {
                            if (mTribu != null) {
                                model.openShowProfileImageActivity(mTribu.getProfile().getImageUrl());
                            }
                        },
                        Throwable::printStackTrace
                );
    }


    private void setToolbarTitle(Tribu tribu) {
        view.mToolbarDetailTribu.setTitle(tribu.getProfile().getNameTribu());
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
                            view.mEtProfileTribuEditDescription.setVisibility(GONE);
                            view.mTvDescription.setVisibility(VISIBLE);
                            view.mBtnChangeDescription.setVisibility(VISIBLE);
                            view.mBtnSaveChangedDescription.setVisibility(GONE);
                            openFolders();
                        },
                        Throwable::printStackTrace
                );
    }

    private void openFolders() {
        String message = "A mTribu " + mTribu.getProfile().getNameTribu() + " é restrita. " +
                "Você precisa participar dela para acessar este conteúdo.";

        if (mTribu.getAdmin().getUidAdmin().equals(mAuth.getCurrentUser().getUid())) {
            showDialogFolder();
        } else {
            if (mTribu.getProfile().isPublic()) {
                showDialogFolder();
            } else {
                Toast.makeText(view.mContext, message, Toast.LENGTH_LONG).show();
            }
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
                    openTribusImagesOrVideosFolderActivity(mTribu, "image");
                    break;
                case 1:
                    openTribusImagesOrVideosFolderActivity(mTribu, "video");
                    break;
            }
        });
        builder.show();
    }

    private void openTribusImagesOrVideosFolderActivity(Tribu tribu, String intentExtra) {
        if (intentExtra.equals("image")) {
            Intent intent = new Intent(view.mContext, TribusImagesFolderActivity.class);
            intent.putExtra("intentExtra", intentExtra);
            intent.putExtra("cameFrom", "fromProfileTribuAdmin");
            intent.putExtra(TRIBU_UNIQUE_NAME, tribu.getProfile().getUniqueName());
            view.mContext.startActivity(intent);
        } else {
            Intent intent = new Intent(view.mContext, TribusImagesFolderActivity.class);
            intent.putExtra("intentExtra", intentExtra);
            intent.putExtra("cameFrom", "fromProfileTribuAdmin");
            intent.putExtra(TRIBU_UNIQUE_NAME, tribu.getProfile().getUniqueName());
            view.mContext.startActivity(intent);
        }


    }


    private void setBtnFollow(Tribu tribu, User mUserAdmin) {

        if (tribu != null && mAuth.getCurrentUser().getUid().equals(mTribu.getAdmin().getUidAdmin())) {
            view.mBtnFollow.setText("SOU ADMIN");
        } else if (tribu != null && !mAuth.getCurrentUser().getUid().equals(mTribu.getAdmin().getUidAdmin())) {
            view.mBtnFollow.setText("SEGUINDO");
        } else {
            view.mBtnFollow.setText("SEGUIR");
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
                view.mNestedScrollView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onRelease(String id) {
                super.onRelease(id);
            }

            @Override
            public void onSubmit(String id, Object callerContext) {
                super.onSubmit(id, callerContext);
                //Log.d("Valor: ", "onSubmit");
                view.mNestedScrollView.setVisibility(VISIBLE);


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
                //.setControllerListener(listenerImageAdmin)
                .setOldController(view.mSdRoundImageAdmin.getController())
                .build();
        view.mSdRoundImageAdmin.setController(dcAdmin);
    }

    private Subscription observeBtnArrowBack() {
        return view.observableBtnArrowBack()
                .subscribe(__ -> {
                            model.backToMainActivity();
                        },
                        Throwable::printStackTrace
                );
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

    private Subscription observeRelativeEditTribu() {
        return view.observableRelativeEditTribu()
                .subscribe(__ -> {
                            view.mTvDescription.setVisibility(VISIBLE);
                            view.mEtProfileTribuEditDescription.setVisibility(GONE);
                            view.mBtnChangeDescription.setVisibility(VISIBLE);
                            view.mBtnSaveChangedDescription.setVisibility(GONE);

                        },
                        Throwable::printStackTrace
                );
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
                            view.mEtProfileTribuEditDescription.setVisibility(GONE);
                            view.mTvDescription.setVisibility(VISIBLE);
                            view.mBtnChangeDescription.setVisibility(VISIBLE);
                            view.mBtnSaveChangedDescription.setVisibility(GONE);
                            openFolders();
                        },
                        Throwable::printStackTrace
                );
    }


    private void setProfileTribu(Tribu tribu, User admin) {

        String appendName = "Você é o Admin";
        view.mTvNameAdmin.setText(appendName);
        view.mTvTribusName.setText(tribu.getProfile().getNameTribu());
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
            view.mTvChangeIsPublic.setText("Toque para mudar para RESTRITA");
            view.mTvChangeIsPublic.setTextColor(view.getResources().getColor(R.color.red));
        } else {
            view.mIvIsPublic.setVisibility(GONE);
            view.mIvIsRestrict.setVisibility(VISIBLE);
            view.mTvChangeIsPublic.setText("Toque para mudar para PÚBLICA");
            view.mTvChangeIsPublic.setTextColor(view.getResources().getColor(R.color.colorAccent));

        }
    }

    private void openImageAdmin(Tribu tribu, User admin) {

        //CREATE DIALOG TO SHOW NEW IMAGE
        //CONFIGURATION OF DIALOG
        //if(admin.getId().equals(mTribu.getAdmin().getUidAdmin())) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.mContext, R.style.MyDialogTheme);
        LayoutInflater inflater = view.mContext.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_profile_admin, null);
        SimpleDraweeView mSdImageAdmin = dialogView.findViewById(R.id.sd_large_image_admin);
        TextView mTvNameOfTribu = dialogView.findViewById(R.id.tv_name_of_admin);
        TextView mTvUniqueName = dialogView.findViewById(R.id.tv_username_admin);
        TextView mTvAdminSince = dialogView.findViewById(R.id.tv_admin_since);
        //dialogView.setBackgroundColor(fragmentContext.getResources().getColor(R.color.transparent));


        mTvNameOfTribu.setText(admin.getName());
        mTvUniqueName.setText(admin.getUsername());
        builder.setView(dialogView);

        /*if (tribu.getAdmin().getTimestampCreated() != null) {
            SimpleDateFormat sfd = new SimpleDateFormat("dd/MM HH:mm");
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
        Log.d("Valor: ", "DraweeController - View: " + dc);
        mSdImageAdmin.setController(dc);

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

    private Subscription observeBtnChangeDescription() {
        return view.observableBtnChangeDescription()
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
                            view.mTvDescription.setVisibility(GONE);
                            view.mEtProfileTribuEditDescription.setVisibility(VISIBLE);
                            view.mBtnChangeDescription.setVisibility(GONE);
                            view.mBtnSaveChangedDescription.setVisibility(VISIBLE);

                            if (mTribu != null) {
                                view.mEtProfileTribuEditDescription.setText(mTribu.getProfile().getDescription());
                            }

                            View viewFocus = view.mContext.getCurrentFocus();
                            if (viewFocus != null) {
                                InputMethodManager imm = (InputMethodManager) view.mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(viewFocus.getWindowToken(), 0);
                            }
                        },
                        Throwable::printStackTrace
                );
    }


    private Subscription observeBtnSaveChangedDescription() {
        return view.observableBtnSaveChangedDescription()
                .filter(__ -> {
                    if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                        ShowSnackBarInfoInternet.showToastInfoInternet(view.mContext);
                        return false;
                    } else {
                        ShowSnackBarInfoInternet.showSnack(true, view);
                        return true;
                    }
                })
                .map(aboutMe -> getDescription())
                .filter(description -> {
                    if (TextUtils.isEmpty(description)) {
                        //setToast("Por favor, informe um nome de usuário");
                        response = false;
                        view.mEtProfileTribuEditDescription.setVisibility(GONE);
                        view.mTvDescription.setVisibility(VISIBLE);
                        view.mBtnSaveChangedDescription.setVisibility(GONE);
                        view.mBtnChangeDescription.setVisibility(VISIBLE);
                        return false;
                    }

                    mDescription = description;
                    return true;
                })
                .doOnNext(__ -> showLoading(true))
                .doOnNext(description -> model.updateTribusDescription(view.mTribuKey, description))
                .subscribe(description -> {
                            showLoading(false);
                            setToast("Descrição da tribu atualizada com sucesso!");
                            view.mEtProfileTribuEditDescription.setVisibility(GONE);
                            view.mTvDescription.setVisibility(VISIBLE);
                            view.mBtnSaveChangedDescription.setVisibility(GONE);
                            view.mBtnChangeDescription.setVisibility(VISIBLE);
                        },
                        Throwable::printStackTrace
                );

    }

    private Subscription observeFab() {
        return view.observableFab()
                .filter(__ -> {
                    if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                        ShowSnackBarInfoInternet.showToastInfoInternet(view.mContext);
                        return false;
                    } else {
                        ShowSnackBarInfoInternet.showSnack(true, view);
                        return true;
                    }
                })
                .doOnNext(__ -> model.requirePermission())
                .filter(__ -> true)
                //.doOnNext(__ -> model.getImage()) //GET IMAGE STRAIGHT IF THERE IS PERMISSION ALREADY
                .doOnNext(__ -> showDialogToUpdateImage())
                .subscribe();
    }


    private Subscription observeTvChangeIsPublic() {
        return view.observableTvChangeIsPublic()
                .filter(__ -> {
                    if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                        ShowSnackBarInfoInternet.showToastInfoInternet(view.mContext);
                        return false;
                    } else {
                        ShowSnackBarInfoInternet.showSnack(true, view);
                        return true;
                    }
                })
                .map(__ -> getOptionIsPublic())
                .doOnNext(isPublic -> model.showDialogToChangeIsPublic(view.mTribuKey, isPublic, mOptionIsPublic))
                .subscribe(__ -> {
                            view.mEtProfileTribuEditDescription.setVisibility(GONE);
                            view.mTvDescription.setVisibility(VISIBLE);
                            view.mBtnSaveChangedDescription.setVisibility(GONE);
                            view.mBtnChangeDescription.setVisibility(VISIBLE);

                        },

                        Throwable::printStackTrace
                );

    }


    private String getOptionIsPublic() {
        String optionText = view.mTvChangeIsPublic.getText().toString().trim();
        if (optionText.equals("Toque para mudar para RESTRITA")) {
            mOptionIsPublic = false;
            return "RESTRITA";
        } else {
            mOptionIsPublic = true;
            return "PÚBLICA";
        }
    }

    //GET NAME
    private String getDescription() {
        String aboutTribu = view.mEtProfileTribuEditDescription.getText().toString().trim();
        if (!aboutTribu.equals("")) {
            return aboutTribu;
        }
        return "";

    }

    //SHOW TOAST
    private void setToast(String message) {
        Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();
    }


    //SHOW LOADING
    private void showLoading(boolean loading) {

        if (loading) {
            progress.show();
        } else {
            progress.dismiss();
        }
    }

    private void showDialogToUpdateImage() {

        PickSetup setup = new PickSetup()
                .setTitle("Escolha")
                .setTitleColor(view.getResources().getColor(R.color.primary_text))
                .setBackgroundColor(Color.WHITE)
                .setCancelText("Cancelar")
                .setFlip(true)
                .setMaxSize(500)
                .setPickTypes(EPickType.GALLERY, EPickType.CAMERA)
                .setCameraButtonText("Câmera")
                .setGalleryButtonText("Galeria de Imagens")
                .setIconGravity(Gravity.LEFT)
                .setButtonOrientation(LinearLayoutCompat.VERTICAL)
                .setSystemDialog(false)
                .setCameraIcon(R.mipmap.camera_colored)
                .setGalleryIcon(R.mipmap.gallery_colored);

        PickImageDialog.build(setup).show(view.mContext);
    }


    //SET NEW IMAGE
    public void setImageTribu(Uri image) {

        //CREATE DIALOG TO SHOW NEW IMAGE
        //CONFIGURATION OF DIALOG
        AlertDialog.Builder builder = new AlertDialog.Builder(view.mContext);
        LayoutInflater inflater = view.mContext.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_change_profile_image, null);
        builder.setView(dialogView);
        SimpleDraweeView mSdChangePhoto = (SimpleDraweeView) dialogView.findViewById(R.id.sd_large_image_user);
        AppCompatButton mBtnChangePhoto = (AppCompatButton) dialogView.findViewById(R.id.btn_change_photo);
        AppCompatButton mBtnCancel = (AppCompatButton) dialogView.findViewById(R.id.btn_cancel);

        builder.setMessage("Mudar foto da Tribu?");

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
                .setUri(image)
                .setControllerListener(listener)
                .setOldController(mSdChangePhoto.getController())
                .build();
        mSdChangePhoto.setController(dc);

        AlertDialog dialog = builder.create();
        dialog.show();

        //CLICK LISTENER TO BUTTONS
        mBtnChangePhoto.setOnClickListener(v -> {
            //CHECK INTERNET CONNECTION
            if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                ShowSnackBarInfoInternet.showToastInfoInternet(view.mContext);
            } else {
                ShowSnackBarInfoInternet.showSnack(true, view);
                model.uploadImageToFirebase(image, view.mTribuKey);
                dialog.dismiss();
            }

        });

        mBtnCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }


    //GET IMAGE AFTER GRANTED PERMISSION
    public void getImage() {
        model.getImage();
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
