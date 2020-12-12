package apptribus.com.tribus.activities.profile_tribu_admin.repository;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import apptribus.com.tribus.activities.profile_tribu_admin.adapter.ProfileTribuAdminAdapter;
import apptribus.com.tribus.pojo.Follower;
import apptribus.com.tribus.pojo.Tribu;
import id.zelory.compressor.Compressor;
import rx.Observable;

import static android.view.View.GONE;
import static apptribus.com.tribus.util.Constantes.DATE;
import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.IMAGES_TRIBUS;
import static apptribus.com.tribus.util.Constantes.IMAGE_URL;
import static apptribus.com.tribus.util.Constantes.PROFILE;
import static apptribus.com.tribus.util.Constantes.PROFILE_IMAGE;
import static apptribus.com.tribus.util.Constantes.THUMB;
import static apptribus.com.tribus.util.Constantes.TRIBUS_PARTICIPANTS;

public class ProfileTribuAdminAPI {

    //FIRESTORE INSTANCE
    private static FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    //FIRESTORE COLLECTIONS REFERENCES
    private static CollectionReference mTribusCollection = mFirestore.collection(GENERAL_TRIBUS);
    private static CollectionReference mUsersCollection = mFirestore.collection(GENERAL_USERS);

    private static FirebaseStorage mStorage = FirebaseStorage.getInstance();

    private static StorageReference mRefStorageTribus = mStorage.getReference().child(IMAGES_TRIBUS);


    private static ProgressDialog progress;

    //OBJECTS
    private static DocumentSnapshot mLastFollowerVisible = null;
    private static List<DocumentSnapshot> mListDocSnapshotAllFollowers;
    private static List<DocumentSnapshot> mListDocSnapshotAllFollowersClear;


    public static Observable<List<Follower>> getAllFollowers(List<Follower> followers, String tribuKey) {

        Query firstQuery = mTribusCollection
                .document(tribuKey)
                .collection(TRIBUS_PARTICIPANTS)
                .orderBy(DATE, Query.Direction.DESCENDING)
                .limit(4);

        if (mListDocSnapshotAllFollowers == null) {
            mListDocSnapshotAllFollowers = new ArrayList<>();
            mListDocSnapshotAllFollowersClear = new ArrayList<>();
        } else {
            mListDocSnapshotAllFollowers.clear();
            mListDocSnapshotAllFollowersClear.clear();
        }


        return Observable.create(subscriber ->

                firstQuery
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {

                            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                    followers.add(documentSnapshot.toObject(Follower.class));
                                    mListDocSnapshotAllFollowers.add(documentSnapshot);


                                }
                                //if (queryDocumentSnapshots.size() == 1) {
                                    //mLastFollowerVisible = mListDocSnapshotAllFollowers.get(mListDocSnapshotAllFollowers.size());
                                //} else if (queryDocumentSnapshots.size() > 1) {

                                    mLastFollowerVisible = mListDocSnapshotAllFollowers.get(mListDocSnapshotAllFollowers.size() - 1);
                                //}

                                subscriber.onNext(followers);
                            }
                            else {
                                subscriber.onNext(null);
                            }
                        })
                        .addOnFailureListener(e -> {

                            subscriber.onNext(null);

                        }));

    }

    public static void loadMoreFollowers(List<Follower> followers, ProfileTribuAdminAdapter profileTribuAdminAdapter,
                                         ProgressBar mProgressBarBottom, String tribuKey) {

        if (mLastFollowerVisible != null) {
            Query nextQuery = mTribusCollection
                    .document(tribuKey)
                    .collection(TRIBUS_PARTICIPANTS)
                    .orderBy(DATE, Query.Direction.DESCENDING)
                    .startAfter(mLastFollowerVisible)
                    .limit(4);

            nextQuery
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            mListDocSnapshotAllFollowersClear.clear();

                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                if (!mListDocSnapshotAllFollowers.contains(documentSnapshot)) {


                                    mListDocSnapshotAllFollowers.add(documentSnapshot);
                                    mListDocSnapshotAllFollowersClear.add(documentSnapshot);


                                }
                            }

                            mLastFollowerVisible = mListDocSnapshotAllFollowers
                                    .get(mListDocSnapshotAllFollowers.size() - 1);


                            for (DocumentSnapshot documentSnapshot : mListDocSnapshotAllFollowersClear) {

                                followers.add(documentSnapshot.toObject(Follower.class));


                            }

                            profileTribuAdminAdapter.notifyDataSetChanged();
                            mProgressBarBottom.setVisibility(GONE);

                        } else {
                            mProgressBarBottom.setVisibility(GONE);
                            mLastFollowerVisible = null;

                        }

                    })
                    .addOnFailureListener(e -> {
                        mProgressBarBottom.setVisibility(GONE);
                    });

        } else {
            mProgressBarBottom.setVisibility(GONE);
        }
    }


    //UPDATE ABOUT ME
    public static void updateTribusDescription(AppCompatActivity activity, String tribuKey, String description) {

        Map<String, Object> updateDescription = new HashMap<>();
        updateDescription.put("description", description);

        mTribusCollection
                .document(tribuKey)
                .update(PROFILE, updateDescription)
                .addOnFailureListener(Throwable::getLocalizedMessage);


    }

    @SuppressWarnings("VisibleForTests")
    public static void uploadImageToFirebase(AppCompatActivity activity, Uri image, String tribuKey) {


        mTribusCollection
                .document(tribuKey)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    Tribu tribu = documentSnapshot.toObject(Tribu.class);

                    //SET PROGRESS
                    progress = new ProgressDialog(activity);
                    progress.setCanceledOnTouchOutside(false);
                    progress.setMessage("Atualizando foto... Por favor, aguarde...");

                    showProgressDialog(true);

                    Uri tribuImage = Uri.parse(tribu.getProfile().getImageUrl());

                    StorageReference folderTribu = mRefStorageTribus
                            .child(tribu.getKey())
                            .child(PROFILE_IMAGE)
                            .child(tribuImage.getLastPathSegment());

                    UploadTask task = folderTribu.putFile(tribuImage);


                    Task<Uri> urlTask = task.continueWithTask(task1 -> {
                        if (!task1.isSuccessful()) {
                            if (task1.getException() != null)
                                throw task1.getException();
                        }
                        return folderTribu.getDownloadUrl();
                    });

                    urlTask.addOnCompleteListener(task14 -> {
                        if (task14.isSuccessful()) {
                            String downloadUri = task14.getResult().toString();

                            Map<String, Object> updateTribuImage = new HashMap<>();
                            updateTribuImage.put(IMAGE_URL, downloadUri);

                            mTribusCollection
                                    .document(tribu.getKey())
                                    .update(PROFILE, updateTribuImage)
                                    .addOnSuccessListener(aVoid -> {

                                        File thumbPath = new File(tribuImage.getPath());

                                        Bitmap thumbBitmap = null;

                                        try {
                                            thumbBitmap = new Compressor(activity)
                                                    .setMaxWidth(200)
                                                    .setMaxHeight(200)
                                                    .setQuality(75)
                                                    .compressToBitmap(thumbPath);

                                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                            thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                                            byte[] thumbData = byteArrayOutputStream.toByteArray();


                                            StorageReference mReferenceImageTribuThumb = mRefStorageTribus
                                                    .child(tribu.getKey())
                                                    .child(THUMB)
                                                    .child(tribuImage.getLastPathSegment());


                                            UploadTask task2 = mReferenceImageTribuThumb.putBytes(thumbData);

                                            Task<Uri> urlTask2 = task2.continueWithTask(task1 -> {
                                                if (!task1.isSuccessful()) {
                                                    throw task1.getException();
                                                }

                                                // Continue with the task to get the download URL
                                                return mReferenceImageTribuThumb.getDownloadUrl();
                                            });

                                            urlTask2.addOnCompleteListener(task12 -> {
                                                if (task12.isSuccessful()) {
                                                    String uriThumb = task12.getResult().toString();

                                                    Map<String, Object> updateTribuThumb = new HashMap<>();
                                                    updateTribuThumb.put(THUMB, uriThumb);

                                                    mTribusCollection
                                                            .document(tribu.getKey())
                                                            .update(PROFILE, updateTribuThumb)
                                                            .addOnFailureListener(Throwable::getLocalizedMessage);


                                                } else {
                                                    // Handle failures
                                                    task12.getException().getMessage();
                                                }
                                            });


                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    })
                                    .addOnFailureListener(Throwable::getLocalizedMessage);

                        } else {
                            // Handle failures
                            task14.getException().getMessage();
                        }
                    });


                })
                .addOnFailureListener(Throwable::printStackTrace);

    }

    //SHOW PROGRESS
    private static void showProgressDialog(boolean load) {
        if (load) {
            progress.show();
        } else {
            progress.dismiss();
        }
    }


    //DIALOG FOR PERMISSIONS UPDATE
    public static void showDialogToChangeIsPublic(AppCompatActivity activity, String tribuKey, String isPublic, boolean option) {
        //INSTANCE OF PROGRESS DIALOG
        progress = new ProgressDialog(activity);
        progress.setMessage("Atualizando para " + isPublic + "...");
        progress.setCancelable(false);

        //CONFIGURATION OF DIALOG
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (isPublic.equals("PÚBLICA")) {
            builder.setTitle("Atualizar para PÚBLICA?");
        } else {
            builder.setTitle("Atualizar para RESTRITA?");
        }

        //String title = "Atualizar para " + isPublic + "?";
        //builder.setTitle(title);
        //builder.setCancelable(false);
        if (isPublic.equals("PÚBLICA")) {
            builder.setMessage("Ao atualizar a mTribu para PÚBLICA, qualquer usuário poderá participar desta Tribu sem que você precise aceitá-lo.");
        } else {
            builder.setMessage("Ao atualizar a mTribu para RESTRITA, você, como admin, terá que aceitar os usuários antes deles participarem desta mTribu.");
        }

        String positiveText = "ATUALIZAR";
        builder.setPositiveButton(positiveText, (dialog, which) -> {
            dialog.dismiss();
            showProgressDialog(true);
            updatePermission(activity, option, tribuKey);


        });

        String negativeText = "CANCELAR";
        builder.setNegativeButton(negativeText, (dialog, which) -> {
            dialog.dismiss();

        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private static void updatePermission(AppCompatActivity activity, boolean option, String tribuKey) {

        Map<String, Object> updateIsAccepted = new HashMap<>();
        updateIsAccepted.put("public", option);

        mTribusCollection
                .document(tribuKey)
                .update(PROFILE, updateIsAccepted)
                .addOnSuccessListener(aVoid -> {
                    showProgressDialog(false);
                    Toast.makeText(activity, "Perfil da tribu atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    showProgressDialog(false);
                    Toast.makeText(activity, "Houve um erro ao atualizar a tribu.", Toast.LENGTH_SHORT).show();
                });

    }
}
