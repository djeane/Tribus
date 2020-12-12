package apptribus.com.tribus.activities.user_profile.repository;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

import apptribus.com.tribus.activities.user_profile.adapter.UserProfileUpdatesAdapter;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.pojo.UserUpdate;
import id.zelory.compressor.Compressor;
import rx.Observable;

import static android.view.View.GONE;
import static apptribus.com.tribus.util.Constantes.DATE;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.IMAGES_USERS;
import static apptribus.com.tribus.util.Constantes.INDIVIDUAL_USERS;
import static apptribus.com.tribus.util.Constantes.USERS_UPDATES;

public class UserProfileAPI {

    //INSTANCES - FIREBASE
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static FirebaseStorage mStorage = FirebaseStorage.getInstance();

    //FIRESTORE INSTANCE
    private static FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    //FIRESTORE COLLECTIONS REFERENCES
    private static CollectionReference mUsersCollection = mFirestore.collection(GENERAL_USERS);
    private static CollectionReference mUsersnameCollection = mFirestore.collection(INDIVIDUAL_USERS);
    private static StorageReference mRefStorageUser = mStorage.getReference().child(IMAGES_USERS);

    //OBJECTS
    private static DocumentSnapshot mLastUpdateVisible = null;

    private static List<DocumentSnapshot> mListDocSnapshotAllUpdates;
    private static List<DocumentSnapshot> mListDocSnapshotAllUpdatesClear;


    private static ProgressDialog progress;

    //get all updates
    public static Observable<List<UserUpdate>> getAllUpdates(List<UserUpdate> updates) {

        Query firstQuery = mUsersCollection
                .document(mAuth.getCurrentUser().getUid())
                .collection(USERS_UPDATES)
                .orderBy(DATE, Query.Direction.DESCENDING)
                .limit(3);

        if (mListDocSnapshotAllUpdates == null) {
            mListDocSnapshotAllUpdates = new ArrayList<>();
            mListDocSnapshotAllUpdatesClear = new ArrayList<>();
        }
        else {
            mListDocSnapshotAllUpdates.clear();
            mListDocSnapshotAllUpdatesClear.clear();
        }


        return Observable.create(subscriber ->

                firstQuery
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {

                            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()){
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                    updates.add(documentSnapshot.toObject(UserUpdate.class));
                                    mListDocSnapshotAllUpdates.add(documentSnapshot);

                                }

                                mLastUpdateVisible = mListDocSnapshotAllUpdates.get(mListDocSnapshotAllUpdates.size() - 1);
                                subscriber.onNext(updates);

                            }
                            else {
                                subscriber.onNext(null);
                            }


                        })
                        .addOnFailureListener(e -> {

                            subscriber.onNext(null);

                        }));

    }

    //load more updates
    public static void loadMoreUpdates(List<UserUpdate> updates, UserProfileUpdatesAdapter mUserProfileUpdatesAdapter,
                                       ProgressBar mProgressBarBottom) {

        if (mLastUpdateVisible != null) {
            Query nextQuery = mUsersCollection
                    .document(mAuth.getCurrentUser().getUid())
                    .collection(USERS_UPDATES)
                    .orderBy(DATE, Query.Direction.DESCENDING)
                    .startAfter(mLastUpdateVisible)
                    .limit(3);

            nextQuery
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            mListDocSnapshotAllUpdatesClear.clear();

                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                if (!mListDocSnapshotAllUpdates.contains(documentSnapshot)) {

                                    mListDocSnapshotAllUpdates.add(documentSnapshot);
                                    mListDocSnapshotAllUpdatesClear.add(documentSnapshot);

                                }
                            }

                            mLastUpdateVisible = mListDocSnapshotAllUpdates
                                    .get(mListDocSnapshotAllUpdates.size() - 1);


                            for (DocumentSnapshot documentSnapshot : mListDocSnapshotAllUpdatesClear) {

                                updates.add(documentSnapshot.toObject(UserUpdate.class));
                            }

                            mUserProfileUpdatesAdapter.notifyDataSetChanged();
                            mProgressBarBottom.setVisibility(GONE);

                        } else {
                            mProgressBarBottom.setVisibility(GONE);
                            mLastUpdateVisible = null;

                        }

                    })
                    .addOnFailureListener(e -> {
                        mProgressBarBottom.setVisibility(GONE);
                    });

        } else {
            mProgressBarBottom.setVisibility(GONE);
        }
    }



    public static Observable<User> getUser(AppCompatActivity activity) {

        return Observable.create(subscriber -> {

            mUsersCollection
                    .document(mAuth.getCurrentUser().getUid())
                    .addSnapshotListener(activity, (documentSnapshot, e) -> {

                        if (e != null){
                            return;
                        }

                        if (documentSnapshot != null && documentSnapshot.exists()){

                            User user = documentSnapshot.toObject(User.class);
                            subscriber.onNext(user);
                        }
                        else {
                            subscriber.onNext(null);
                        }
                    });

        });


    }


    public static void updateName(String name, AlertDialog dialog, AppCompatActivity activity) {

        Map<String, Object> updateName = new HashMap<>();
        updateName.put("name", name);

        mUsersCollection
                .document(mAuth.getCurrentUser().getUid())
                .update(updateName)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(activity, "Seu nome foi atualizado!",
                            Toast.LENGTH_LONG).show();

                    dialog.dismiss();
                })
                .addOnFailureListener(Throwable::printStackTrace);

    }

    //UPDATE ABOUT ME
    public static void updateAboutMe(String aboutMe) {
        Map<String, Object> updateAboutMe = new HashMap<>();
        updateAboutMe.put("aboutMe", aboutMe);

        mUsersCollection
                .document(mAuth.getCurrentUser().getUid())
                .update(updateAboutMe)
                .addOnFailureListener(Throwable::getLocalizedMessage);

        /*mReferenceUsers.child(mUser.getId())
                .updateChildren(updateAboutMe)
                .addOnFailureListener(Throwable::getLocalizedMessage);*/

    }

    //UPDATE AGE
    public static void updateAge(int year, int month, int day) {
        Map<String, Object> updateAge= new HashMap<>();
        updateAge.put("year", year);
        updateAge.put("month", month);
        updateAge.put("day", day);

        mUsersCollection
                .document(mAuth.getCurrentUser().getUid())
                .update(updateAge)
                .addOnFailureListener(Throwable::getLocalizedMessage);

        /*mReferenceUsers
                .child(mUser.getId())
                .updateChildren(updateAge)
                .addOnFailureListener(Throwable::getLocalizedMessage);*/

    }

    //UPDATE ABOUT ME
    public static void updateGender(String gender) {
        Map<String, Object> updateGender = new HashMap<>();
        updateGender.put("gender", gender);

        mUsersCollection
                .document(mAuth.getCurrentUser().getUid())
                .update(updateGender)
                .addOnFailureListener(Throwable::getLocalizedMessage);

        /*mReferenceUsers.child(mUser.getId())
                .updateChildren(updateGender)
                .addOnFailureListener(Throwable::getLocalizedMessage);*/

    }


    //DIALOG FOR PERMISSIONS UPDATE
    public static void showDialogForIsAccepted(AppCompatActivity activity, boolean isAccepted, CompoundButton toggleButton) {
        //INSTANCE OF PROGRESS DIALOG
        progress = new ProgressDialog(activity);
        progress.setMessage("Atualizando permissões...");
        progress.setCancelable(false);

        mUsersCollection
                .document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    User user = documentSnapshot.toObject(User.class);

                    //CONFIGURATION OF DIALOG
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle("Editar Permissão");
                    builder.setCancelable(false);
                    if (isAccepted) {
                        builder.setMessage("Atualizar permissão para SIM?");
                    } else {
                        builder.setMessage("Atualizar permissão para NÃO?");
                    }

                    String positiveText = "ATUALIZAR";
                    builder.setPositiveButton(positiveText, (dialog, which) -> {
                        dialog.dismiss();
                        showProgressDialog(true);
                        upDatePermission(isAccepted, activity);


                    });

                    String negativeText = "CANCELAR";
                    builder.setNegativeButton(negativeText, (dialog, which) -> {
                        dialog.dismiss();

                        if (String.valueOf(user.isAccepted()).equals("true")) {
                            toggleButton.setText("SIM");
                        } else {
                            toggleButton.setText("NÃO");

                        }

                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();

                })
                .addOnFailureListener(Throwable::printStackTrace);


    }

    private static void upDatePermission(boolean isAccepted, AppCompatActivity activity) {
        Map<String, Object> updateIsAccepted = new HashMap<>();
        updateIsAccepted.put("accepted", isAccepted);

        mUsersCollection
                .document(mAuth.getCurrentUser().getUid())
                .update(updateIsAccepted)
                .addOnSuccessListener(aVoid -> {
                    showProgressDialog(false);
                    showToast(activity, "Permissão atualizada com sucesso!");

                })
                .addOnFailureListener(e -> {
                    showProgressDialog(false);
                    showToast(activity, "Ocorreu um erro ao atualizar a permissão!");

                });
        /*mReferenceUsers
                .child(mUser.getId())
                .updateChildren(updateIsAccepted)
                .addOnSuccessListener(aVoid -> {

                })
                .addOnFailureListener(Throwable::getLocalizedMessage);*/

    }

    private static void showToast(AppCompatActivity activity, String message){
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

    //SHOW PROGRESS
    private static void showProgressDialog(boolean load) {
        if (load) {
            progress.show();
        } else {
            progress.dismiss();
        }
    }


    public static void updateUsername(AppCompatActivity activity, String username, AlertDialog dialog) {
        Map<String, Object> updateUsernameEmIndividualUsers = new HashMap<>();
        updateUsernameEmIndividualUsers.put("username", username);

        mUsersnameCollection
                .document(username)
                .delete()
                .addOnSuccessListener(aVoid -> {

                    mUsersCollection
                            .document(mAuth.getCurrentUser().getUid())
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {

                                User user = documentSnapshot.toObject(User.class);

                                Map<String, Object> updateUsername = new HashMap<>();
                                updateUsername.put(username, user.getUsername());

                                mUsersnameCollection
                                        .document(username)
                                        .set(updateUsername)
                                        .addOnSuccessListener(aVoid12 -> {

                                            mUsersCollection
                                                    .document(mAuth.getCurrentUser().getUid())
                                                    .update(updateUsernameEmIndividualUsers)
                                                    .addOnSuccessListener(aVoid13 -> {
                                                        Toast.makeText(activity, "Seu nome de usuário foi atualizado!",
                                                                Toast.LENGTH_LONG).show();
                                                        dialog.dismiss();
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        e.printStackTrace();
                                                        Toast.makeText(activity, "Houve um erro ao atualizar seu nome de usuário.",
                                                                Toast.LENGTH_LONG).show();
                                                        dialog.dismiss();
                                                    });


                                        })
                                        .addOnFailureListener(e -> {
                                            e.printStackTrace();
                                            Toast.makeText(activity, "Houve um erro ao atualizar seu nome de usuário.",
                                                    Toast.LENGTH_LONG).show();
                                            dialog.dismiss();
                                        });

                            })
                            .addOnFailureListener(e -> {
                                e.printStackTrace();
                                Toast.makeText(activity, "Houve um erro ao atualizar seu nome de usuário.",
                                        Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            });

                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(activity, "Houve um erro ao atualizar seu nome de usuário.",
                            Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                });


        //REMOVE OLD USERNAME FROM USERS_NAME
        /*mReferenceUserName
                .child(mUser.getUsername())
                .removeValue()
                .addOnSuccessListener(aVoid1 -> {

                    //ADD NEW USERNAME INSIDE USERS-NAME
                    mReferenceUserName
                            .child(username)
                            .setValue(username)
                            .addOnSuccessListener(aVoid -> {

                                //UPDATE USERNAME INSIDE INDIVIDUAL USERS
                                mReferenceUsers
                                        .child(mUser.getId())
                                        .updateChildren(updateUsernameEmIndividualUsers)
                                        .addOnSuccessListener(aVoid2 -> {
                                                    Toast.makeText(activity, "Seu nome de usuário foi atualizado!",
                                                            Toast.LENGTH_LONG).show();
                                                    dialog.dismiss();
                                                }
                                        )
                                        .addOnFailureListener(Throwable::getLocalizedMessage);

                            })
                            .addOnFailureListener(Throwable::getLocalizedMessage);

                })
                .addOnFailureListener(Throwable::getLocalizedMessage);*/


    }


    public static void uploadImageToFirebase(AppCompatActivity activity, Uri image) {

        //SET PROGRESS
        progress = new ProgressDialog(activity);
        progress.setCanceledOnTouchOutside(false);
        progress.setMessage("Atualizando foto, por favor, aguarde...");
        showProgressDialog(true);


        StorageReference mReferenceImageUser = mRefStorageUser
                .child(mAuth.getCurrentUser().getUid())
                .child("profile_image")
                .child(image.getLastPathSegment());

        UploadTask task = mReferenceImageUser.putFile(image);

        Task<Uri> urlTask = task.continueWithTask(task13 -> {
            if (!task13.isSuccessful()) {
                throw task13.getException();
            }

            // Continue with the task to get the download URL
            return mReferenceImageUser.getDownloadUrl();
        });

        urlTask.addOnCompleteListener(task14 -> {
            if (task14.isSuccessful()) {
                String downloadUri = task14.getResult().toString();

                Map<String, Object> updateUser = new HashMap<>();
                updateUser.put("imageUrl", downloadUri);

                mUsersCollection
                        .document(mAuth.getCurrentUser().getUid())
                        .update(updateUser)
                        .addOnSuccessListener(aVoid -> {

                            File thumbPath = new File(image.getPath());

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


                                StorageReference mReferenceImageUserThumb = mRefStorageUser
                                        .child(mAuth.getCurrentUser().getUid())
                                        .child("thumb")
                                        .child(image.getLastPathSegment());

                                UploadTask task2 = mReferenceImageUserThumb.putBytes(thumbData);

                                Task<Uri> urlTask2 = task2.continueWithTask(task1 -> {
                                    if (!task1.isSuccessful()) {
                                        throw task1.getException();

                                    }

                                    // Continue with the task to get the download URL
                                    return mReferenceImageUserThumb.getDownloadUrl();
                                });

                                urlTask2.addOnCompleteListener(task12 -> {
                                    if (task12.isSuccessful()) {
                                        String uriThumb = task12.getResult().toString();

                                        Map<String, Object> updateUserThumb = new HashMap<>();
                                        updateUserThumb.put("thumb", uriThumb);

                                        mUsersCollection
                                                .document(mAuth.getCurrentUser().getUid())
                                                .update(updateUserThumb)
                                                .addOnFailureListener(e -> {
                                                    e.printStackTrace();
                                                    showProgressDialog(false);
                                                    Toast.makeText(activity.getApplicationContext(), "Ocorreu um erro no seu cadastro.",
                                                            Toast.LENGTH_SHORT).show();
                                                    return;

                                                });

                                        /*mReferenceUser
                                                .child(mAuth.getCurrentUser().getUid())
                                                .updateChildren(updateUserThumb)
                                                .addOnFailureListener(Throwable::getLocalizedMessage);*/


                                    } else {
                                        // Handle failures
                                        task12.getException().getMessage();
                                        showProgressDialog(false);
                                        Toast.makeText(activity.getApplicationContext(), "Ocorreu um erro no seu cadastro.", Toast.LENGTH_SHORT).show();
                                        return;

                                    }
                                });


                            } catch (IOException e) {
                                e.printStackTrace();
                                showProgressDialog(false);
                                Toast.makeText(activity.getApplicationContext(), "Ocorreu um erro no seu cadastro.",
                                        Toast.LENGTH_SHORT).show();
                                return;

                            }
                        })
                        .addOnFailureListener(e -> {
                            e.printStackTrace();
                            showProgressDialog(false);
                            Toast.makeText(activity.getApplicationContext(), "Ocorreu um erro no seu cadastro.",
                                    Toast.LENGTH_SHORT).show();
                            return;

                        });


            } else {
                // Handle failures
                task14.getException().getMessage();
                showProgressDialog(false);
                Toast.makeText(activity.getApplicationContext(), "Ocorreu um erro no seu cadastro.",
                        Toast.LENGTH_SHORT).show();

            }
        });

    }

    /*public static void uploadImageToFirebase(AppCompatActivity activity, Uri image) {


        //SET PROGRESS
        progress = new ProgressDialog(activity);
        progress.setCanceledOnTouchOutside(false);
        progress.setMessage("Atualizando foto, por favor, aguarde...");
        showProgressDialog(true);

        StorageReference mReferenceImageUser = mRefStorageUser
                .child(mAuth.getCurrentUser().getUid())
                .child("profile_image")
                .child(image.getLastPathSegment());

        UploadTask task = mReferenceImageUser.putFile(image);

        Task<Uri> urlTask = task.continueWithTask(task13 -> {
            if (!task13.isSuccessful()) {
                throw task13.getException();
            }

            // Continue with the task to get the download URL
            return mReferenceImageUser.getDownloadUrl();
        });

        urlTask.addOnCompleteListener(task14 -> {

            if (task14.isSuccessful()) {

                String downloadUrl = task14.getResult().toString();

                Bitmap bitmap = null;
                try {
                    RotateBitmap rotateBitmap = new RotateBitmap();
                    bitmap = rotateBitmap.HandleSamplingAndRotationBitmap(activity, image);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                if (bitmap != null){
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                    byte[] thumbData = byteArrayOutputStream.toByteArray();

                    StorageReference mReferenceImageUserThumb = mRefStorageUser
                            .child(mAuth.getCurrentUser().getUid())
                            .child(mUser.getUsername())
                            .child("thumb")
                            .child(image.getLastPathSegment());

                    UploadTask task2 = mReferenceImageUserThumb.putBytes(thumbData);

                    Task<Uri> urlTask2 = task2.continueWithTask(task1 -> {
                        if (!task1.isSuccessful()) {
                            throw task1.getException();
                        }

                        // Continue with the task to get the download URL
                        return mReferenceImageUserThumb.getDownloadUrl();
                    });

                    urlTask2.addOnCompleteListener(task12 -> {
                        if (task12.isSuccessful()) {

                            Map<String, Object> updateUser = new HashMap<>();
                            updateUser.put("imageUrl", downloadUrl);

                            mReferenceUsers
                                    .child(mAuth.getCurrentUser().getUid())
                                    .updateChildren(updateUser)
                                    .addOnSuccessListener(aVoid -> {

                                        String uriThumb = task12.getResult().toString();

                                        Map<String, Object> updateUserThumb = new HashMap<>();
                                        updateUserThumb.put("thumb", uriThumb);

                                        mReferenceUsers
                                                .child(mAuth.getCurrentUser().getUid())
                                                .updateChildren(updateUserThumb)
                                                .addOnSuccessListener(aVoid1 -> {
                                                    showProgressDialog(false);
                                                    Toast.makeText(activity, "Foto atualizada!", Toast.LENGTH_SHORT).show();

                                                })
                                                .addOnFailureListener(e -> {
                                                    Objects.requireNonNull(task12.getException()).getMessage();
                                                    showProgressDialog(false);
                                                });

                                    })
                                    .addOnFailureListener(e -> {
                                        e.getMessage();
                                        showProgressDialog(false);
                                        Toast.makeText(activity, "Não foi possível concluir a atualização!", Toast.LENGTH_SHORT).show();

                                    });


                        } else {
                            // Handle failures
                            Objects.requireNonNull(task12.getException()).getMessage();
                            showProgressDialog(false);
                            Toast.makeText(activity, "Houve um erro na atualização.", Toast.LENGTH_SHORT).show();
                        }


                    });
                } else {
                    Objects.requireNonNull(task14.getException()).getMessage();
                    showProgressDialog(false);
                    Toast.makeText(activity, "Não foi possível atualizar sua foto!", Toast.LENGTH_SHORT).show();
                }

            } else {
                // Handle failures
                //Objects.requireNonNull(task14.getException()).getMessage();
                showProgressDialog(false);
                Toast.makeText(activity, "Não foi possível atualizar sua foto.", Toast.LENGTH_SHORT).show();
            }


    }*/




    /*@SuppressWarnings("VisibleForTests")
    public static void uploadImageToFirebase(AppCompatActivity activity, Uri image) {


        //SET PROGRESS
        progress = new ProgressDialog(activity);
        progress.setCanceledOnTouchOutside(false);
        progress.setMessage("Atualizando foto, por favor, aguarde...");
        showProgressDialog(true);

        StorageReference mReferenceImageUser = mRefStorageUser
                .child(mAuth.getCurrentUser().getUid())
                .child(mUser.getUsername())
                .child(image.getLastPathSegment());

        UploadTask task = mReferenceImageUser.putFile(image);

        Task<Uri> urlTask = task.continueWithTask(task13 -> {
            if (!task13.isSuccessful()) {
                throw task13.getException();
            }

            // Continue with the task to get the download URL
            return mReferenceImageUser.getDownloadUrl();
        });

        urlTask.addOnCompleteListener(task14 -> {

            if (task14.isSuccessful()) {

                String downloadUrl = task14.getResult().toString();

                Bitmap bitmap = null;
                try {
                    RotateBitmap rotateBitmap = new RotateBitmap();
                    bitmap = rotateBitmap.HandleSamplingAndRotationBitmap(activity, image);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                    if (bitmap != null){
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                        byte[] thumbData = byteArrayOutputStream.toByteArray();

                        StorageReference mReferenceImageUserThumb = mRefStorageUser
                            .child(mAuth.getCurrentUser().getUid())
                            .child(mUser.getUsername())
                            .child("thumb")
                            .child(image.getLastPathSegment());

                    UploadTask task2 = mReferenceImageUserThumb.putBytes(thumbData);

                    Task<Uri> urlTask2 = task2.continueWithTask(task1 -> {
                        if (!task1.isSuccessful()) {
                            throw task1.getException();
                        }

                        // Continue with the task to get the download URL
                        return mReferenceImageUserThumb.getDownloadUrl();
                    });

                    urlTask2.addOnCompleteListener(task12 -> {
                        if (task12.isSuccessful()) {

                            Map<String, Object> updateUser = new HashMap<>();
                            updateUser.put("imageUrl", downloadUrl);

                            mReferenceUsers
                                    .child(mAuth.getCurrentUser().getUid())
                                    .updateChildren(updateUser)
                                    .addOnSuccessListener(aVoid -> {

                                        String uriThumb = task12.getResult().toString();

                                        Map<String, Object> updateUserThumb = new HashMap<>();
                                        updateUserThumb.put("thumb", uriThumb);

                                        mReferenceUsers
                                                .child(mAuth.getCurrentUser().getUid())
                                                .updateChildren(updateUserThumb)
                                                .addOnSuccessListener(aVoid1 -> {
                                                    showProgressDialog(false);
                                                    Toast.makeText(activity, "Foto atualizada!", Toast.LENGTH_SHORT).show();

                                                })
                                                .addOnFailureListener(e -> {
                                                    Objects.requireNonNull(task12.getException()).getMessage();
                                                    showProgressDialog(false);
                                                });

                                    })
                                    .addOnFailureListener(e -> {
                                        e.getMessage();
                                        showProgressDialog(false);
                                        Toast.makeText(activity, "Não foi possível concluir a atualização!", Toast.LENGTH_SHORT).show();

                                    });


                        } else {
                            // Handle failures
                            Objects.requireNonNull(task12.getException()).getMessage();
                            showProgressDialog(false);
                            Toast.makeText(activity, "Houve um erro na atualização.", Toast.LENGTH_SHORT).show();
                        }


                    });
                } else {
                    Objects.requireNonNull(task14.getException()).getMessage();
                    showProgressDialog(false);
                    Toast.makeText(activity, "Não foi possível atualizar sua foto!", Toast.LENGTH_SHORT).show();
                }

            } else {
                // Handle failures
                //Objects.requireNonNull(task14.getException()).getMessage();
                showProgressDialog(false);
                Toast.makeText(activity, "Não foi possível atualizar sua foto.", Toast.LENGTH_SHORT).show();
            }

        });
    }*/
}
