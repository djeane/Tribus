package apptribus.com.tribus.activities.register_user.repository;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import apptribus.com.tribus.pojo.User;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.IMAGES_USERS;
import static apptribus.com.tribus.util.Constantes.INDIVIDUAL_USERS;

/**
 * Created by User on 5/21/2017.
 */

public class UserAPI {

    //INSTANCES - FIREBASE
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private static FirebaseStorage mStorage = FirebaseStorage.getInstance();

    //REFERENCES - FIREBASE
    private static DatabaseReference mReferenceUser = mDatabase.getReference().child(GENERAL_USERS);
    private static DatabaseReference mReferenceIndvidualUser = mDatabase.getReference().child(INDIVIDUAL_USERS);
    private static StorageReference mRefStorageUser = mStorage.getReference().child(IMAGES_USERS);


    //OTHERS VARIABLES
    private static String uid;


    //CREATE A NEW USER WITH AN OBSERVABLE
    public static boolean createUser(User userParam){
        Log.d("Valor: ", "entrou createUser -API");
        final boolean[] sucesso = {false};
        mAuth.createUserWithEmailAndPassword(userParam.getEmail(), userParam.getPassword())
                .addOnCompleteListener(task -> {sucesso[0] = true;
                    Log.d("Valor", "sucesso[0] - task - createUserWithEmailAndPassword: " + sucesso[0]);

                    uid = mAuth.getCurrentUser().getUid();
                    userParam.setId(uid);
                    String token = FirebaseInstanceId.getInstance().getToken();
                    userParam.setToken(token);
                    mReferenceUser.child(uid).setValue(userParam).addOnCompleteListener(taskDatabase -> {
                        sucesso[0] = true;
                        Log.d("Valor", "sucesso[0] - task - setValue: " + sucesso[0]);
                        saveUsername(userParam.getName(), userParam.getUsername(), userParam.getImageUrl());

                    });

                });
        Log.d("Valor: ", "sucesso[0] - API: " + sucesso[0]);
        Log.d("Valor: ", "saiu createUser -API");
        return true;
    }


    //SAVE NAME AND USERNAME
    public static boolean saveUsername(String name, String username, String url){
        boolean[] sucess = {false};
        Log.d("Valor: ", "saveUsername -API");
        mReferenceIndvidualUser.child(username).setValue(name).addOnCompleteListener(task -> {sucess[0] = true;

            storeImage(username, Uri.parse(url));

        });
        Log.d("Valor: ", "sucess[0] - saveUsername -API: " + sucess[0] );
        Log.d("Valor: ", "saiu saveUsername -API");
        return true;
    }


    //STORE USER IMAGE
    public static boolean storeImage(String username, Uri image){
        final boolean[] sucesso = {false};

        Log.d("Valor: ", "entrou em storeImage - API");
        mRefStorageUser.child(uid).child(username).child(image.getLastPathSegment());
        UploadTask task = mRefStorageUser.child(uid).putFile(image);

        Task<Uri> urlTask = task.continueWithTask(task12 -> {
            if (!task12.isSuccessful()) {
                throw task12.getException();
            }

            // Continue with the task to get the download URL
            return mRefStorageUser.getDownloadUrl();
        });


        urlTask.addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                String downloadUri = task1.getResult().toString();

                mReferenceUser.child(uid).child("imageUrl").setValue(downloadUri);

                StorageReference folderUser = mRefStorageUser.child(uid).child(username).child("__w-200-400-600-800-1000__/").child(image.getLastPathSegment());
                UploadTask uploadTaskFolder = folderUser.putFile(image);
                returnTrue(uploadTaskFolder);

                //FOLDER w200
                StorageReference folder200User = mRefStorageUser.child(uid).child(username).child("w200").child(image.getLastPathSegment());
                UploadTask uploadTaskFolder200 = folder200User.putFile(image);
                returnTrue(uploadTaskFolder200);

                //FOLDER w400
                StorageReference folder400User = mRefStorageUser.child(uid).child(username).child("w400").child(image.getLastPathSegment());
                UploadTask uploadTaskFolder400 = folder400User.putFile(image);
                returnTrue(uploadTaskFolder400);

                //FirebaseStorageOLDER w600
                StorageReference folder600User = mRefStorageUser.child(uid).child(username).child("w600").child(image.getLastPathSegment());
                UploadTask uploadTaskFolder600 = folder600User.putFile(image);
                returnTrue(uploadTaskFolder600);

                //FOLDER w800
                StorageReference folder800User = mRefStorageUser.child(uid).child(username).child("w800").child(image.getLastPathSegment());
                UploadTask uploadTaskFolder800 = folder800User.putFile(image);
                returnTrue(uploadTaskFolder800);

                //FOLDER w1000
                StorageReference folder1000User = mRefStorageUser.child(uid).child(username).child("w1000").child(image.getLastPathSegment());
                UploadTask uploadTaskFolder1000 = folder1000User.putFile(image);
                returnTrue(uploadTaskFolder1000);


                sucesso[0] = true;

            } else {
                // Handle failures
                task1.getException().getMessage();
            }
        });


        /*task.addOnSuccessListener(taskSnapshot -> {

            @SuppressWarnings("VisibleForTests")
            String downloadUri = mRefStorageUser.getDownloadUrl().toString();

            mReferenceUser.child(uid).child("imageUrl").setValue(downloadUri);

            StorageReference folderUser = mRefStorageUser.child(uid).child(username).child("__w-200-400-600-800-1000__/").child(image.getLastPathSegment());
            UploadTask uploadTaskFolder = folderUser.putFile(image);
            returnTrue(uploadTaskFolder);

            //FOLDER w200
            StorageReference folder200User = mRefStorageUser.child(uid).child(username).child("w200").child(image.getLastPathSegment());
            UploadTask uploadTaskFolder200 = folder200User.putFile(image);
            returnTrue(uploadTaskFolder200);

            //FOLDER w400
            StorageReference folder400User = mRefStorageUser.child(uid).child(username).child("w400").child(image.getLastPathSegment());
            UploadTask uploadTaskFolder400 = folder400User.putFile(image);
            returnTrue(uploadTaskFolder400);

            //FirebaseStorageOLDER w600
            StorageReference folder600User = mRefStorageUser.child(uid).child(username).child("w600").child(image.getLastPathSegment());
            UploadTask uploadTaskFolder600 = folder600User.putFile(image);
            returnTrue(uploadTaskFolder600);

            //FOLDER w800
            StorageReference folder800User = mRefStorageUser.child(uid).child(username).child("w800").child(image.getLastPathSegment());
            UploadTask uploadTaskFolder800 = folder800User.putFile(image);
            returnTrue(uploadTaskFolder800);

            //FOLDER w1000
            StorageReference folder1000User = mRefStorageUser.child(uid).child(username).child("w1000").child(image.getLastPathSegment());
            UploadTask uploadTaskFolder1000 = folder1000User.putFile(image);
            returnTrue(uploadTaskFolder1000);

            sucesso[0] = true;
        });*/

        Log.d("Valor: ", "saiu storeImage -API");
        return sucesso[0];
    }

    private static boolean returnTrue(UploadTask task){
        boolean[] sucesso = {false};
        task.addOnCompleteListener(taskSnapshot -> {
            sucesso[0] = true;
            Log.d("Valor: ", "returnTrue: " + task + " - sucesso[0]: " + sucesso[0]);
        });

        return true;
    }


    //OBSERVABLE GET USER
    @NonNull
    public static Observable<User> getUser(){
        User user = new User();
        Log.d("Valor: ", "entrou getAdmin -API");
        return rx.Observable.create(subscriber -> mReferenceUser.child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("Valor: ", "dataSnapshot: " + dataSnapshot);
                        User useSnapshot = dataSnapshot.getValue(User.class);
                        user.setName(useSnapshot.getName());
                        user.setUsername(useSnapshot.getUsername());
                        useSnapshot.setEmail(useSnapshot.getEmail());
                        user.setPhoneNumber(useSnapshot.getPhoneNumber());
                        user.setImageUrl(useSnapshot.getImageUrl());
                        Log.d("Valor: ", "User - name: " + user.getName());
                        Log.d("Valor: ", "User - username: " + user.getUsername());
                        Log.d("Valor: ", "User - email: " + user.getEmail());
                        Log.d("Valor: ", "User - phone number: " + user.getPhoneNumber());
                        Log.d("Valor: ", "User - imageUrl: " + user.getImageUrl());
                        Log.d("Valor: ", "User - user objetc: " + user);
                        Log.d("Valor: ", "saiu getAdmin -API");
                        subscriber.onNext(user);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }));
    }


}
