package apptribus.com.tribus.activities.phone_number_authentication.repository;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.firestore.FirestoreService;
import id.zelory.compressor.Compressor;

import static android.view.View.*;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.IMAGES_USERS;
import static apptribus.com.tribus.util.Constantes.INDIVIDUAL_USERS;

/**
 * Created by User on 11/20/2017.
 */

public class PhoneNumberAuthenticationAPI {

    //INSTANCES - FIREBASE
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private static FirebaseStorage mStorage = FirebaseStorage.getInstance();

    //REFERENCES - FIREBASE
    private static DatabaseReference mReferenceUser = mDatabase.getReference().child(GENERAL_USERS);
    private static DatabaseReference mReferenceIndvidualUser = mDatabase.getReference().child(INDIVIDUAL_USERS);
    private static StorageReference mRefStorageUser = mStorage.getReference().child(IMAGES_USERS);


    //FIRESTORE INSTANCE
    private static FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    //FIRESTORE COLLECTIONS REFERENCES
    private static CollectionReference mUsersCollections = mFirestore.collection(GENERAL_USERS);
    private static CollectionReference mUsersnameCollections = mFirestore.collection(INDIVIDUAL_USERS);


    //OTHERS VARIABLES
    private static String uid;
    //private static PhoneAuthProvider.OnVerificationStateChangedCallbacks mVerificationCallbacks;
    private static String mPhoneVerificationId;
    private static PhoneAuthProvider.ForceResendingToken mResendToken;


    public static void setUpVerificationCallbacksSendCode(Button mBtnResend, Button mBtnSend, Button mBtnSendVerificationCode,
                                                          TextView mTvInfoVerificationCode, EditText mEtVerificationCode,
                                                          String mPhoneNumber, AppCompatActivity activity,
                                                          PhoneAuthProvider.OnVerificationStateChangedCallbacks
                                                                  mVerificationCallbacks, ProgressDialog progress){

        mVerificationCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                if (e instanceof FirebaseAuthInvalidCredentialsException){
                    //Log.d("Valor: ", "Credencial inválida.");
                    Toast.makeText(activity, "Número de telefone inválido.", Toast.LENGTH_SHORT).show();
                }
                else if (e instanceof FirebaseTooManyRequestsException){
                    //Log.d("Valor: ", "Cota de SMS excedida.");
                    Toast.makeText(activity, "Número de tentativas excedido.", Toast.LENGTH_SHORT).show();
                }
                mBtnResend.setVisibility(VISIBLE);
                mBtnResend.setEnabled(true);
                mBtnSend.setEnabled(false);
                progress.dismiss();

            }

            @Override
            public void onCodeSent(String phoneVerificationId, PhoneAuthProvider.ForceResendingToken resendingToken) {

                mPhoneVerificationId = phoneVerificationId;
                mResendToken = resendingToken;

                progress.dismiss();
                mBtnSend.setEnabled(false);
                mBtnResend.setEnabled(false);
                mBtnSendVerificationCode.setVisibility(VISIBLE);
                mBtnSendVerificationCode.setEnabled(true);
                mEtVerificationCode.setVisibility(VISIBLE);
                mEtVerificationCode.setEnabled(true);
                mTvInfoVerificationCode.setVisibility(VISIBLE);

            }
        };

        sendCode(mPhoneNumber, activity, mVerificationCallbacks);
    }

    private static void sendCode(String mPhoneNumber, AppCompatActivity activity,
                                 PhoneAuthProvider.OnVerificationStateChangedCallbacks mVerificationCallbacks){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mPhoneNumber,
                60,
                TimeUnit.SECONDS,
                activity,
                mVerificationCallbacks

        );
    }

    public static void setUpVerificationCallbacksResendCode(Button mBtnResend, Button mBtnSend, Button mBtnSendVerificationCode,
                                                            TextView mTvInfoVerificationCode, EditText mEtVerificationCode,
                                                            String mPhoneNumber, AppCompatActivity activity,
                                                            PhoneAuthProvider.OnVerificationStateChangedCallbacks mVerificationCallbacks,
                                                            ProgressDialog progress){

        mVerificationCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException){
                    //Log.d("Valor: ", "Credencial inválida.");
                    Toast.makeText(activity, "Número de telefone inválido.", Toast.LENGTH_SHORT).show();
                }
                else if (e instanceof FirebaseTooManyRequestsException){
                    //Log.d("Valor: ", "Cota de SMS excedida.");
                    Toast.makeText(activity, "Número de tentativas excedido.", Toast.LENGTH_SHORT).show();
                }
                mBtnResend.setVisibility(VISIBLE);
                mBtnResend.setEnabled(true);
                mBtnSend.setEnabled(false);
                progress.dismiss();

            }

            @Override
            public void onCodeSent(String phoneVerificationId, PhoneAuthProvider.ForceResendingToken resendingToken) {

                mPhoneVerificationId = phoneVerificationId;
                mResendToken = resendingToken;

                progress.dismiss();
                mBtnResend.setVisibility(VISIBLE);
                mBtnResend.setEnabled(false);
                mBtnSend.setEnabled(false);
                mBtnSendVerificationCode.setVisibility(VISIBLE);
                mBtnSendVerificationCode.setEnabled(true);
                mEtVerificationCode.setVisibility(VISIBLE);
                mEtVerificationCode.setEnabled(true);
                mTvInfoVerificationCode.setVisibility(VISIBLE);

            }
        };

        resendCode(mPhoneNumber, activity, mVerificationCallbacks);
    }

    private static void resendCode(String mPhoneNumber, AppCompatActivity activity, PhoneAuthProvider.OnVerificationStateChangedCallbacks mVerificationCallbacks){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mPhoneNumber,
                60,
                TimeUnit.SECONDS,
                activity,
                mVerificationCallbacks

        );
    }





    //CREATE A NEW USER WITH AN OBSERVABLE
    public static boolean createUser(User userParam, String code, AppCompatActivity activity, ProgressDialog progress,
                                     Button mBtnSendVerificationCode){

        //FirestoreService mFirestoreService = new FirestoreService(activity);

        final boolean[] sucesso = {false};

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mPhoneVerificationId, code);

        //CREATE USER WITH PHONE NUMBER
        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(task -> {
                                sucesso[0] = true;
                                if (mAuth.getCurrentUser() == null){
                                    Toast.makeText(activity, "Código de autenticação incorreto.", Toast.LENGTH_SHORT).show();
                                    progress.dismiss();
                                    mBtnSendVerificationCode.setEnabled(true);

                                }
                                else {
                                    uid = mAuth.getCurrentUser().getUid();
                                    userParam.setId(uid);
                                    String token = FirebaseInstanceId.getInstance().getToken();
                                    userParam.setToken(token);

                                    mUsersCollections
                                            .document(userParam.getId())
                                            .set(userParam)
                                            .addOnSuccessListener(aVoid -> {
                                                saveUsername(userParam.getName(), userParam.getUsername(),
                                                        userParam.getImageUrl(), activity, progress);

                                            })
                                            .addOnFailureListener(e -> {
                                                e.printStackTrace();
                                                progress.dismiss();
                                                Toast.makeText(activity.getApplicationContext(), "Ocorreu um erro no seu cadastro.",
                                                        Toast.LENGTH_SHORT).show();
                                            });

                                    /*mFirestoreService
                                            .addUserIntoFirestore(userParam);*/



                                    /*mReferenceUser.child(uid).setValue(userParam)
                                            .addOnSuccessListener(taskDatabase -> {
                                                sucesso[0] = true;
                                                saveUsername(userParam.getName(), userParam.getUsername(),
                                                        userParam.getImageUrl(), activity);

                                            })
                                            .addOnFailureListener(Throwable::getLocalizedMessage);*/
                                }
                })
                .addOnFailureListener(Throwable::getLocalizedMessage);;


        return true;
    }

    //SAVE NAME AND USERNAME
    private static boolean saveUsername(String name, String username, String url, AppCompatActivity activity,
                                        ProgressDialog progress){
        boolean[] sucess = {false};

        Map<String, Object> updateUsername = new HashMap<>();
        updateUsername.put(username, name);

        mUsersnameCollections
                .document(username)
                .update(updateUsername)
                .addOnSuccessListener(aVoid -> {
                    storeImage(username, Uri.parse(url), activity, progress);
                    sucess[0] = true;
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    progress.dismiss();
                    Toast.makeText(activity.getApplicationContext(), "Ocorreu um erro no seu cadastro.",
                            Toast.LENGTH_SHORT).show();
                    sucess[0] = false;
                });

        /*mReferenceIndvidualUser.child(username).setValue(name)
                .addOnSuccessListener(task -> {sucess[0] = true;
                    storeImage(username, Uri.parse(url), activity);
                    sucess[0] = true;
                })
                .addOnFailureListener(e -> {
                    e.getLocalizedMessage();
                    sucess[0] = false;
                });*/
        return sucess[0];
    }

    //STORE USER IMAGE
    private static boolean storeImage(String username, Uri image, AppCompatActivity activity, ProgressDialog progress) {

        final boolean[] sucess = {false};

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

                mUsersCollections
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

                                        mUsersCollections
                                                .document(mAuth.getCurrentUser().getUid())
                                                .update(updateUserThumb)
                                                .addOnFailureListener(e -> {
                                                    e.printStackTrace();
                                                    progress.dismiss();
                                                    Toast.makeText(activity.getApplicationContext(), "Ocorreu um erro no seu cadastro.",
                                                            Toast.LENGTH_SHORT).show();
                                                    sucess[0] = false;
                                                    return;

                                                });

                                        /*mReferenceUser
                                                .child(mAuth.getCurrentUser().getUid())
                                                .updateChildren(updateUserThumb)
                                                .addOnFailureListener(Throwable::getLocalizedMessage);*/


                                    } else {
                                        // Handle failures
                                        task12.getException().getMessage();
                                        progress.dismiss();
                                        Toast.makeText(activity.getApplicationContext(), "Ocorreu um erro no seu cadastro.", Toast.LENGTH_SHORT).show();
                                        sucess[0] = false;
                                        return;

                                    }
                                });


                            } catch (IOException e) {
                                e.printStackTrace();
                                progress.dismiss();
                                Toast.makeText(activity.getApplicationContext(), "Ocorreu um erro no seu cadastro.",
                                        Toast.LENGTH_SHORT).show();
                                sucess[0] = false;
                                return;

                            }
                        })
                        .addOnFailureListener(e -> {
                            e.printStackTrace();
                            progress.dismiss();
                            Toast.makeText(activity.getApplicationContext(), "Ocorreu um erro no seu cadastro.",
                                    Toast.LENGTH_SHORT).show();
                            sucess[0] = false;
                            return;

                        });


            } else {
                // Handle failures
                task14.getException().getMessage();
                progress.dismiss();
                Toast.makeText(activity.getApplicationContext(), "Ocorreu um erro no seu cadastro.",
                        Toast.LENGTH_SHORT).show();
                sucess[0] = false;

            }
        });

        /*urlTask.addOnCompleteListener(task14 -> {
            if (task14.isSuccessful()) {
                String downloadUri = task14.getResult().toString();

                Map<String, Object> updateUser = new HashMap<>();
                updateUser.put("imageUrl", downloadUri);

                mReferenceUser
                        .child(mAuth.getCurrentUser().getUid())
                        .updateChildren(updateUser)
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
                                        .child(username)
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

                                        mReferenceUser
                                                .child(mAuth.getCurrentUser().getUid())
                                                .updateChildren(updateUserThumb)
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
        });*/

        return sucess[0];
    }

}
