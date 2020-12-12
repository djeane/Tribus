package apptribus.com.tribus.activities.create_tribu.repository;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import apptribus.com.tribus.R;
import apptribus.com.tribus.pojo.Admin;
import apptribus.com.tribus.pojo.Follower;
import apptribus.com.tribus.pojo.Tribu;
import id.zelory.compressor.Compressor;

import static apptribus.com.tribus.util.Constantes.ADMIN;
import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.IMAGES_TRIBUS;
import static apptribus.com.tribus.util.Constantes.IMAGE_URL;
import static apptribus.com.tribus.util.Constantes.KEY;
import static apptribus.com.tribus.util.Constantes.KEY_TRIBU;
import static apptribus.com.tribus.util.Constantes.PARTICIPATING;
import static apptribus.com.tribus.util.Constantes.PROFILE;
import static apptribus.com.tribus.util.Constantes.PROFILE_IMAGE;
import static apptribus.com.tribus.util.Constantes.PROFILE_IMAGE_URL;
import static apptribus.com.tribus.util.Constantes.PROFILE_THUMB;
import static apptribus.com.tribus.util.Constantes.THUMB;
import static apptribus.com.tribus.util.Constantes.TRIBU;
import static apptribus.com.tribus.util.Constantes.TRIBUS;
import static apptribus.com.tribus.util.Constantes.TRIBUS_PARTICIPANTS;
import static apptribus.com.tribus.util.Constantes.TRIBUS_THEMATICS;
import static apptribus.com.tribus.util.Constantes.TRIBUS_UNIQUE;


public class CreateTribuAPI {

    //FIRESTORE INSTANCE
    private static FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    //FIRESTORE REFERENCE
    private static CollectionReference mTribusCollections = mFirestore.collection(GENERAL_TRIBUS);
    private static CollectionReference mTribusThematicsCollections = mFirestore.collection(TRIBUS_THEMATICS);
    private static CollectionReference mTribusUniqueNameCollections = mFirestore.collection(TRIBUS_UNIQUE);
    private static CollectionReference mUsersCollections = mFirestore.collection(GENERAL_USERS);


    //INSTANCES - FIREBASE
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private static FirebaseStorage mStorage = FirebaseStorage.getInstance();


    //REFERENCES - FIREBASE
    private static DatabaseReference mRefTribus = mDatabase.getReference().child(GENERAL_TRIBUS);
    private static StorageReference mRefStorageTribus = mStorage.getReference().child(IMAGES_TRIBUS);




    //CREATING TRIBU
    public static void createTribu(Tribu tribu, AppCompatActivity activity){

        String key = mRefTribus.push().getKey();
        tribu.setKey(key);

        mTribusCollections
                .document(tribu.getKey())
                .set(tribu)
                /*.addOnSuccessListener(aVoid -> {
                    saveUniqueName(tribu, activity);
                })*/
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(activity,
                            activity.getResources().getString(R.string.toast_error_create_tribu), Toast.LENGTH_SHORT).show();
                    return;
                });

    }

    //SAVE TRIBU UNIQUENAME
    public static void saveUniqueName(Tribu tribu, AppCompatActivity activity){

        Map<String, Object> setUniqueName = new HashMap<>();
        setUniqueName.put(tribu.getProfile().getUniqueName(), tribu.getProfile().getNameTribu());

        mTribusUniqueNameCollections
                .document(tribu.getProfile().getUniqueName())
                .set(setUniqueName)
                /*.addOnSuccessListener(aVoid -> {
                    saveAdmin(tribu, activity);
                })*/
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(activity,
                            activity.getResources().getString(R.string.toast_error_create_tribu), Toast.LENGTH_SHORT).show();
                    return;
                });


    }

    //SAVE ADMIN
    public static void saveAdmin(Tribu tribu, AppCompatActivity activity){

        Admin admin = new Admin();
        admin.setUidAdmin(tribu.getAdmin().getUidAdmin());
        admin.setDate(tribu.getProfile().getCreationDate());
        admin.setTribuKey(tribu.getKey());

        mTribusCollections
                .document(tribu.getKey())
                .collection(ADMIN)
                .document(tribu.getAdmin().getUidAdmin())
                .set(admin)
                /*.addOnSuccessListener(aVoid -> {
                    saveParticipantAdmin(tribu, activity);
                })*/
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(activity,
                            activity.getResources().getString(R.string.toast_error_create_tribu), Toast.LENGTH_SHORT).show();
                    return;
                });
    }

    //SAVE PARTICIPANT ADMIN
    public static void saveParticipantAdmin(Tribu tribu, AppCompatActivity activity) {

        Date date = new Date(System.currentTimeMillis());
        String userId = mAuth.getCurrentUser().getUid();

        Follower follower = new Follower();
        follower.setUidFollower(userId);
        follower.setAdmin(true);
        follower.setDate(date);

        Tribu participating = new Tribu();
        participating.setKey(tribu.getKey());
        participating.setDate(date);
        participating.setAdmin(tribu.getAdmin());
        participating.setThematic(tribu.getProfile().getThematic());

        DocumentReference usersCollectionsRef = mUsersCollections
                .document(userId)
                .collection(PARTICIPATING)
                .document(tribu.getKey());

        DocumentReference tribusCollectionRef = mTribusCollections
                .document(tribu.getKey())
                .collection(TRIBUS_PARTICIPANTS)
                .document(follower.getUidFollower());

        usersCollectionsRef
                .set(participating)
                .addOnSuccessListener(aVoid -> {
                    tribusCollectionRef
                            .set(follower)
                            /*.addOnSuccessListener(aVoid1 -> {
                                updateThematics(tribu, activity);
                            })*/
                            .addOnFailureListener(e -> {
                                e.printStackTrace();
                                Toast.makeText(activity,
                                        activity.getResources().getString(R.string.toast_error_create_tribu), Toast.LENGTH_SHORT).show();
                                return;

                            });

                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(activity,
                            activity.getResources().getString(R.string.toast_error_create_tribu), Toast.LENGTH_SHORT).show();
                    return;
                });


    }

    public static void updateThematics(Tribu tribu, AppCompatActivity activity){
        Map<String, Object> updateThematics = new HashMap<>();
        updateThematics.put(KEY, tribu.getKey());

        mTribusThematicsCollections
                .document(tribu.getProfile().getThematic())
                //.collection(TRIBU)
                //.document(tribu.getKey())
                .set(updateThematics)
                /*.addOnSuccessListener(aVoid -> {
                    storageImageTribu(tribu, activity);
                })*/
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(activity,
                            activity.getResources().getString(R.string.toast_error_create_tribu), Toast.LENGTH_SHORT).show();
                    return;
                });

    }


    public static void storageImageTribu(Tribu tribu, AppCompatActivity activity) {

        if (tribu.getProfile().getImageUrl() != null) {

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
                    updateTribuImage.put(PROFILE_IMAGE_URL, downloadUri);

                    mTribusCollections
                            .document(tribu.getKey())
                            .update(updateTribuImage)
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
                                            updateTribuThumb.put(PROFILE_THUMB, uriThumb);

                                            mTribusCollections
                                                    .document(tribu.getKey())
                                                    .update(updateTribuThumb)
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

        }
    }
}
