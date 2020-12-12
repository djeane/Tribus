package apptribus.com.tribus.activities.show_survey.repository;

import android.support.v7.app.AppCompatActivity;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.show_survey.view_holder.ShowSurveyViewHolder;
import apptribus.com.tribus.pojo.Survey;
import apptribus.com.tribus.pojo.Tribu;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.TRIBUS_SURVEY;

/**
 * Created by User on 1/22/2018.
 */

public class ShowSurveyAPI {

    //INSTANCES - FIREBASE
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

    //REFERENCES - FIREBASE
    public static DatabaseReference mReferenceUser = mDatabase.getReference().child(GENERAL_USERS);
    private static DatabaseReference mReferenceSurvey = mDatabase.getReference().child(TRIBUS_SURVEY);
    private static DatabaseReference mReferenceTribu = mDatabase.getReference().child(GENERAL_TRIBUS);
    private static FirebaseRecyclerAdapter<Survey, ShowSurveyViewHolder> mAdapter;

    private static Tribu mTribu;

    //GET TRIBU
    public static Observable<Tribu> getTribu(String uniqueName) {

        return Observable.create(subscriber -> mReferenceTribu
                .child(uniqueName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mTribu = dataSnapshot.getValue(Tribu.class);
                        subscriber.onNext(mTribu);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        databaseError.toException().printStackTrace();
                    }
                }));
    }


    public static FirebaseRecyclerAdapter<Survey, ShowSurveyViewHolder> getSurvey(Tribu tribu, AppCompatActivity activity) {

        return mAdapter = new FirebaseRecyclerAdapter<Survey, ShowSurveyViewHolder>(
                Survey.class,
                R.layout.row_show_survey,
                ShowSurveyViewHolder.class,
                mReferenceSurvey.child(tribu.getProfile().getUniqueName())
        ) {
            @Override
            protected void populateViewHolder(ShowSurveyViewHolder viewHolder, Survey survey, int position) {

                viewHolder.initShowSurveyViewHolder(survey, tribu, activity);

            }
        };

    }
}