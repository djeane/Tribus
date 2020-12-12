package apptribus.com.tribus.activities.survey.repository;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

import apptribus.com.tribus.activities.show_survey.ShowSurveyActivity;
import apptribus.com.tribus.pojo.Survey;
import apptribus.com.tribus.pojo.Tribu;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.SURVEYS;
import static apptribus.com.tribus.util.Constantes.TRIBUS_SURVEY;
import static apptribus.com.tribus.util.Constantes.TRIBU_KEY;
import static apptribus.com.tribus.util.Constantes.TRIBU_UNIQUE_NAME;

/**
 * Created by User on 1/19/2018.
 */

public class SurveyAPI {

    //FIRESTORE INSTANCE
    private static FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    private static CollectionReference mTribusCollection = mFirestore.collection(GENERAL_TRIBUS);;


    //INSTANCES - FIREBASE
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

    //REFERENCES - FIREBASE
    private static DatabaseReference mReferenceTribu = mDatabase.getReference().child(GENERAL_TRIBUS);
    private static DatabaseReference mReferenceTribuSurvey = mDatabase.getReference().child(TRIBUS_SURVEY);

    public static Tribu mTribu;

    public static ProgressDialog progress;



    public static void sendSurveyToFirebase(Tribu tribu, Survey survey, AppCompatActivity activity){

        if (survey != null) {

            progress = new ProgressDialog(activity);
            progress.setCancelable(false);
            progress.setMessage("Criando enquete...");

            showProgressDialog(true);

            Date date = new Date(System.currentTimeMillis());
            survey.setDate(date);
            String surveyKey = mReferenceTribuSurvey.push().getKey();
            survey.setKey(surveyKey);
            survey.setIdUser(mAuth.getCurrentUser().getUid());

            //FirebaseMessaging.getInstance().subscribeToTopic(topicKey);

            //STORE MESSAGE INSIDE TRIBU'S DATABASE
            mTribusCollection
                    .document(tribu.getKey())
                    .collection(SURVEYS)
                    .document(surveyKey)
                    .set(survey)
                    .addOnSuccessListener(aVoid -> {
                        showProgressDialog(false);
                        Toast.makeText(activity, "Enquete criada!", Toast.LENGTH_SHORT).show();

                        //OPEN SURVEY VOTE ACTIVITY
                        openShowSurveyActivity(tribu, activity);
                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        showProgressDialog(false);
                    });

        }
    }

    //OPEN SURVEY VOTE ACTIVITY
    private static void openShowSurveyActivity(Tribu tribu, AppCompatActivity activity) {

        Intent intent = new Intent(activity, ShowSurveyActivity.class);
        intent.putExtra(TRIBU_KEY, tribu.getKey());
        activity.startActivity(intent);
        activity.finish();
    }

    //SHOW PROGRESS
    private static void showProgressDialog(boolean load) {

        if (load) {
            progress.show();
        } else {
            progress.dismiss();
        }
    }

}
