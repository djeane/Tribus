package apptribus.com.tribus.activities.main_activity.repository;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.main_activity.fragment_timeline.view_holder.ThematicsViewHolder;
import apptribus.com.tribus.pojo.Admin;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.firestore.FirestoreService;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import id.zelory.compressor.Compressor;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.ADMINS;
import static apptribus.com.tribus.util.Constantes.CHAT_TALKER;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.IMAGES_USERS;


/**
 * Created by User on 6/4/2017.
 */

public class MainAPI {

    //INSTANCES - FIREBASE
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private static FirebaseStorage mStorage = FirebaseStorage.getInstance();

    //INSTACES - FIRESTORE
    private static FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();


    //REFERENCES - FIREBASE
    public static DatabaseReference mReferenceUser = mDatabase.getReference().child(GENERAL_USERS);
    private static DatabaseReference mRefAdmin = mDatabase.getReference().child(ADMINS);
    private static DatabaseReference mRefChatTalker = mDatabase.getReference().child(CHAT_TALKER);
    private static StorageReference mRefStorageUser = mStorage.getReference().child(IMAGES_USERS);

    private static FirebaseRecyclerAdapter<User, ThematicsViewHolder> mAdapterUsers;
    //REFERENCES - FIRESTORE
    private static CollectionReference mUserCollection = mFirestore.collection(GENERAL_USERS);


    //VARIABLES
    public static User mUser;
    private static Admin mAdmin = null;
    private static String uid;
    private static File mImageFolder;




    public static void openShareFragmentToCard(Tribu tribu, AppCompatActivity activity) {

        String textInfo = tribu.getProfile().getNameTribu().toUpperCase();

        SpannableString styledString = new SpannableString(textInfo);
        styledString.setSpan(
                new ForegroundColorSpan(activity.getResources().getColor(R.color.colorAccent)),
                0,
                textInfo.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        try {
            String packageName = activity.getPackageName();
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_SUBJECT, "Tribus");
            intent.setType("text/plain");

            String strShareMessage = "\nEi, instala esse app e participa da mTribu " + styledString
                    + ". Eu já estou participando! Você vai gostar!\n\n";

            strShareMessage = strShareMessage + "https://play.google.com/store/apps/details?id=" + packageName;

            intent.putExtra(Intent.EXTRA_TEXT, strShareMessage);

            //Uri screenshotUri = Uri.parse("android.resource://apptribus.com.tribus/mimmap/ic_launcher_borda_maior");
            //intent.setType("image/png");
            //intent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
            //intent.setPackage("com.whatsapp");
            //intent.putExtra(Intent.EXTRA_TEXT, "Ei, instala o Tribus! Você vai gostar!");
            //context.startActivity(Intent.createChooser(intent, "Compartilhar..."));
            activity.startActivity(intent);

        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(activity, "Não foi encontrada uma atividade para compartilhamento.", Toast.LENGTH_LONG)
                    .show();
        }
    }








    //GET USER(FUTURE ADMIN OF TRIBU)
    /*public static Observable<User> getCurrentUser() {

        if (mAuth.getCurrentUser() != null) {
            uid = mAuth.getCurrentUser().getUid();
        }

        return Observable.create(subscriber -> mReferenceUser
                .child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mUser = dataSnapshot.getValue(User.class);
                        subscriber.onNext(mUser);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        if (databaseError != null) {
                            databaseError.toException().printStackTrace();
                        }
                    }
                }));

    }*/



    public static void updateLastMessage(String mTalkerId) {

        if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
            return;
        } else {
            mRefChatTalker
                    .child(mAuth.getCurrentUser().getUid())
                    .child(mTalkerId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.hasChildren()) {
                                //ChatTalker chatTalker = dataSnapshot.getValue(ChatTalker.class);
                                //UPDATE ONLINE
                                Map<String, Object> updateChatTalker = new HashMap<>();
                                updateChatTalker.put("unreadMessages", 0);
                                //updateChatTalker.put("talkerIsOnline", mCurrentUser.isOnlineInChat());

                                mRefChatTalker
                                        .child(mAuth.getCurrentUser().getUid())
                                        .child(mTalkerId)
                                        .updateChildren(updateChatTalker);

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            if (databaseError != null) {
                                databaseError.getMessage();
                            }
                        }
                    });

        }

    }


    public static void openShareFragmentToApp(Context context) {
        try {
            String packageName = context.getPackageName();
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_SUBJECT, "Tribus");
            intent.setType("text/plain");

            String strShareMessage = "\nEi, eu tô participando do TRIBUS! Instala ele aí! Você vai gostar!\n\n";

            strShareMessage = strShareMessage + "https://play.google.com/store/apps/details?id=" + packageName;

            intent.putExtra(Intent.EXTRA_TEXT, strShareMessage);
            context.startActivity(intent);

        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(context, "Não foi encontrada uma atividade para compartilhamento.", Toast.LENGTH_LONG)
                    .show();
        }
    }


    public static void createThumbImage(AppCompatActivity activity, String imageUrl, User currentUser) {

        Uri image = Uri.parse(imageUrl);

        StorageReference imageRef = mStorage.getReferenceFromUrl(imageUrl);

        File jpegFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        mImageFolder = new File(jpegFile, "Tribus Imagens");
        if (!mImageFolder.exists()) {
            mImageFolder.mkdirs();
        }

        String currentTimestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String prepend = "JPEG_" + currentTimestamp + "_";
        File imageFile = null;

        try {
            imageFile = File.createTempFile(prepend, ".jpg", mImageFolder);
            File finalImageFile = imageFile;

            imageRef
                    .getFile(imageFile)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Local temp file has been created

                        File tumbPath = new File(finalImageFile.getPath());

                        Bitmap thumbBitmap = null;

                        try {
                            thumbBitmap = new Compressor(activity)
                                    .setMaxWidth(200)
                                    .setMaxHeight(200)
                                    .setQuality(75)
                                    .compressToBitmap(tumbPath);

                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                            byte[] thumbData = byteArrayOutputStream.toByteArray();


                            StorageReference mReferenceImageUser = mRefStorageUser
                                    .child(mAuth.getCurrentUser().getUid())
                                    .child(currentUser.getUsername())
                                    .child("thumb");
                            UploadTask task = mReferenceImageUser.putBytes(thumbData);

                            Task<Uri> urlTask = task.continueWithTask(task1 -> {
                                if (!task1.isSuccessful()) {
                                    throw task1.getException();
                                }

                                // Continue with the task to get the download URL
                                return mReferenceImageUser.getDownloadUrl();
                            });


                            urlTask.addOnCompleteListener(task12 -> {
                                if (task12.isSuccessful()) {
                                    String downloadUri = task12.getResult().toString();

                                    Map<String, Object> updateUser = new HashMap<>();
                                    updateUser.put("thumb", downloadUri);

                                    mReferenceUser
                                            .child(mAuth.getCurrentUser().getUid())
                                            .updateChildren(updateUser)
                                            .addOnFailureListener(Throwable::getLocalizedMessage);


                                } else {
                                    // Handle failures
                                    task12.getException().getMessage();
                                }
                            });


                            //OLD CODE
                            /*if (task != null) {
                                task.addOnSuccessListener(taskSnapshot2 -> {
                                    String uri = mReferenceImageUser.getDownloadUrl().toString();
                                    Map<String, Object> updateUser = new HashMap<>();
                                    updateUser.put("thumb", uri);

                                    mReferenceUser
                                            .child(mAuth.getCurrentUser().getUid())
                                            .updateChildren(updateUser)
                                            .addOnFailureListener(Throwable::getLocalizedMessage);

                                }).addOnFailureListener(Throwable::getLocalizedMessage);
                            }*/

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }).addOnFailureListener(Throwable::getLocalizedMessage);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}