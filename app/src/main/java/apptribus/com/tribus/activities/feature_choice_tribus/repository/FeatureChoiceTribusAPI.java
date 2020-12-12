package apptribus.com.tribus.activities.feature_choice_tribus.repository;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.chat_tribu.ChatTribuActivity;
import apptribus.com.tribus.pojo.ConversationTopic;
import apptribus.com.tribus.pojo.RemovedTribu;
import apptribus.com.tribus.pojo.Tribu;

import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.PARTICIPATING;
import static apptribus.com.tribus.util.Constantes.REMOVED_TRIBUS;
import static apptribus.com.tribus.util.Constantes.TOPICS;
import static apptribus.com.tribus.util.Constantes.TOPIC_KEY;
import static apptribus.com.tribus.util.Constantes.TOPIC_NAME;
import static apptribus.com.tribus.util.Constantes.TRIBUS_PARTICIPANTS;
import static apptribus.com.tribus.util.Constantes.TRIBUS_TOPICS;
import static apptribus.com.tribus.util.Constantes.TRIBU_KEY;
import static apptribus.com.tribus.util.Constantes.TRIBU_NAME;
import static apptribus.com.tribus.util.Constantes.TRIBU_UNIQUE_NAME;


public class FeatureChoiceTribusAPI {

    //FIRESTORE INSTANCE
    private static FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    //FIRESTORE COLLECTIONS REFERENCES
    private static CollectionReference mUsersCollection = mFirestore.collection(GENERAL_USERS);
    private static CollectionReference mTribusCollection = mFirestore.collection(GENERAL_TRIBUS);


    //INSTANCES - FIREBASE
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

    public static DatabaseReference mRefTribusTopics = mDatabase.getReference().child(TRIBUS_TOPICS);

    private static ProgressDialog progress;


    //OPEN DIALOG ASKING ABOUT LEAVE TRIBU
    public static void leaveTribu(AppCompatActivity activity, Tribu tribu) {

        //INSTANCE OF PROGRESS DIALOG
        progress = new ProgressDialog(activity);
        progress.setCancelable(false);
        progress.setMessage(activity.getResources().getString(R.string.progress_message_leave_tribu)
                + " " + tribu.getProfile().getNameTribu() + "...");

        //CONFIGURATION OF DIALOG
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getResources().getString(R.string.leave_tribu));
        builder.setMessage(activity.getResources().getString(R.string.leave_tribu_message)
                + '"' + tribu.getProfile().getNameTribu() + '"' + "?");

        String positiveText = activity.getResources().getString(R.string.yes);
        builder.setPositiveButton(positiveText, (dialog, which) -> {
            dialog.dismiss();
            showProgressDialog(true);
            leaveActualTribu(tribu, activity);

        });
        String negativeText = activity.getResources().getString(R.string.no);
        builder.setNegativeButton(negativeText, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    //LEAVE ACTUAL TRIBU
    private static void leaveActualTribu(Tribu tribu, AppCompatActivity activity) {

        mTribusCollection
                .document(tribu.getKey())
                .collection(TRIBUS_PARTICIPANTS)
                .document(mAuth.getCurrentUser().getUid())
                .delete()
                .addOnSuccessListener(aVoid -> {

                    mUsersCollection
                            .document(mAuth.getCurrentUser().getUid())
                            .collection(PARTICIPATING)
                            .document(tribu.getKey())
                            .delete()
                            .addOnSuccessListener(aVoid12 -> {

                                RemovedTribu removedTribu = new RemovedTribu();
                                Date date = new Date(System.currentTimeMillis());
                                removedTribu.setKey(tribu.getKey());
                                removedTribu.setThematic(tribu.getProfile().getThematic());
                                removedTribu.setDate(date);

                                mUsersCollection
                                        .document(mAuth.getCurrentUser().getUid())
                                        .collection(REMOVED_TRIBUS)
                                        .document(tribu.getKey())
                                        .set(removedTribu)
                                        .addOnSuccessListener(aVoid1 -> {

                                            showProgressDialog(false);
                                            Toast.makeText(activity,
                                                    activity.getResources().getString(R.string.toast_leave_tribu_successfully)
                                                            + " " + tribu.getProfile().getNameTribu(),
                                                    Toast.LENGTH_LONG)
                                                    .show();
                                            openMainActivity(activity);
                                        })
                                        .addOnFailureListener(e -> {
                                            e.printStackTrace();
                                            showProgressDialog(false);
                                            Toast.makeText(activity,
                                                    activity.getResources().getString(R.string.toast_error_leave_tribu),
                                                    Toast.LENGTH_SHORT).show();
                                        });

                            })
                            .addOnFailureListener(e -> {
                                e.printStackTrace();
                                showProgressDialog(false);
                                Toast.makeText(activity,
                                        activity.getResources().getString(R.string.toast_error_leave_tribu),
                                        Toast.LENGTH_SHORT).show();
                            });

                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    showProgressDialog(false);
                    Toast.makeText(activity,
                            activity.getResources().getString(R.string.toast_error_leave_tribu),
                            Toast.LENGTH_SHORT).show();
                });

    }

    //SHOW PROGRESS
    private static void showProgressDialog(boolean load) {

        if (load) {
            progress.show();
        } else {
            progress.dismiss();
        }
    }

    //SEND MESSAGE TO FIREBASE
    public static void sendTopicToFirebase(Tribu tribu, ConversationTopic conversationTopic, AppCompatActivity activity) {

        if (conversationTopic != null) {

            progress = new ProgressDialog(activity);
            progress.setCancelable(false);
            progress.setMessage(activity.getResources().getString(R.string.creating_topic));

            showProgressDialog(true);

            Date date = new Date(System.currentTimeMillis());
            conversationTopic.setDate(date);
            String topicKey = mRefTribusTopics.push().getKey();
            conversationTopic.setKey(topicKey);
            conversationTopic.setIdParticipant(mAuth.getCurrentUser().getUid());

            //FirebaseMessaging.getInstance().subscribeToTopic(topicKey);

            mTribusCollection
                    .document(tribu.getKey())
                    .collection(TOPICS)
                    .document(topicKey)
                    .set(conversationTopic)
                    .addOnSuccessListener(aVoid -> {
                        showProgressDialog(false);
                        Toast.makeText(activity,
                                activity.getResources().getString(R.string.topic_created),
                                Toast.LENGTH_SHORT).show();
                        openChatTribuActivity(tribu, activity, conversationTopic);
                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        showProgressDialog(false);
                        Toast.makeText(activity,
                                activity.getResources().getString(R.string.toast_error_creating_topic),
                                Toast.LENGTH_SHORT).show();
                    });


        }
    }

    private static void openChatTribuActivity(Tribu tribu, AppCompatActivity activity, ConversationTopic topic) {
        Intent intent = new Intent(activity, ChatTribuActivity.class);
        intent.putExtra(TRIBU_UNIQUE_NAME, tribu.getProfile().getUniqueName());
        intent.putExtra(TRIBU_KEY, tribu.getKey());
        intent.putExtra(TRIBU_NAME, tribu.getProfile().getNameTribu());
        intent.putExtra(TRIBU_KEY, tribu.getKey());
        intent.putExtra(TOPIC_KEY, topic.getKey());
        intent.putExtra(TOPIC_NAME, topic.getTopic());
        activity.startActivity(intent);
    }

    //OPEN MAIN ACTIVITY AFTER LEAVE TRIBU
    private static void openMainActivity(AppCompatActivity activity) {
        activity.finish();
    }

}
