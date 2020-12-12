package apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_container.repository;

import android.widget.ProgressBar;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_container.adapter.FragmentContainerSurveysAdapter;
import apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_container.adapter.FragmentContainerTopicsAdapter;
import apptribus.com.tribus.pojo.ConversationTopic;
import apptribus.com.tribus.pojo.Survey;
import rx.Observable;

import static android.view.View.GONE;
import static apptribus.com.tribus.util.Constantes.DATE;
import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.SURVEYS;
import static apptribus.com.tribus.util.Constantes.TOPICS;

public class FragmentContainerAPI {

    //FIRESTORE INSTANCE
    private static FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    //FIRESTORE COLLECTIONS REFERENCES
    private static CollectionReference mTribusCollection = mFirestore.collection(GENERAL_TRIBUS);

    //OBJECTS TOPICS
    private static DocumentSnapshot mLastTopicVisible = null;
    private static List<DocumentSnapshot> mListDocSnapshotAllTopics;
    private static List<DocumentSnapshot> mListDocSnapshotAllTopicsClear;

    //OBJECTS SURVEY
    private static DocumentSnapshot mLastSurveyVisible = null;
    private static List<DocumentSnapshot> mListDocSnapshotAllSurveys;
    private static List<DocumentSnapshot> mListDocSnapshotAllSurveysClear;


    //GET TOPICS
    public static Observable<List<ConversationTopic>> getAllTopics(List<ConversationTopic> topics, String tribuKey) {

        Query firstQuery = mTribusCollection
                .document(tribuKey)
                .collection(TOPICS)
                .orderBy(DATE, Query.Direction.DESCENDING)
                .limit(5);

        if (mListDocSnapshotAllTopics == null) {
            mListDocSnapshotAllTopics = new ArrayList<>();
            mListDocSnapshotAllTopicsClear = new ArrayList<>();
        } else {
            mListDocSnapshotAllTopics.clear();
            mListDocSnapshotAllTopicsClear.clear();
        }


        return Observable.create(subscriber ->

                firstQuery
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {

                            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {

                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                    topics.add(documentSnapshot.toObject(ConversationTopic.class));
                                    mListDocSnapshotAllTopics.add(documentSnapshot);


                                }

                                mLastTopicVisible = mListDocSnapshotAllTopics.get(mListDocSnapshotAllTopics.size() - 1);

                                subscriber.onNext(topics);
                            }
                            else {
                                subscriber.onNext(null);
                            }
                        })
                        .addOnFailureListener(e -> {

                            subscriber.onNext(null);

                        }));

    }

    public static void loadMoreTopics(List<ConversationTopic> contacts, FragmentContainerTopicsAdapter fragmentContainerTopicsAdapter,
                                      ProgressBar mProgressBarBottom, String tribuKey) {

        if (mLastTopicVisible != null) {
            Query nextQuery = mTribusCollection
                    .document(tribuKey)
                    .collection(TOPICS)
                    .orderBy(DATE, Query.Direction.DESCENDING)
                    .startAfter(mLastTopicVisible)
                    .limit(4);

            nextQuery
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            mListDocSnapshotAllTopicsClear.clear();

                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                if (!mListDocSnapshotAllTopics.contains(documentSnapshot)) {


                                    mListDocSnapshotAllTopics.add(documentSnapshot);
                                    mListDocSnapshotAllTopicsClear.add(documentSnapshot);


                                }
                            }

                            mLastTopicVisible = mListDocSnapshotAllTopics
                                    .get(mListDocSnapshotAllTopics.size() - 1);


                            for (DocumentSnapshot documentSnapshot : mListDocSnapshotAllTopicsClear) {

                                contacts.add(documentSnapshot.toObject(ConversationTopic.class));


                            }

                            fragmentContainerTopicsAdapter.notifyDataSetChanged();
                            mProgressBarBottom.setVisibility(GONE);

                        } else {
                            mProgressBarBottom.setVisibility(GONE);
                            mLastTopicVisible = null;

                        }

                    })
                    .addOnFailureListener(e -> {
                        mProgressBarBottom.setVisibility(GONE);
                    });

        } else {
            mProgressBarBottom.setVisibility(GONE);
        }
    }


    //GET SURVEYS
    public static Observable<List<Survey>> getAllSurveys(List<Survey> surveys, String tribuKey) {

        Query firstQuery = mTribusCollection
                .document(tribuKey)
                .collection(SURVEYS)
                .orderBy(DATE, Query.Direction.DESCENDING)
                .limit(5);

        if (mListDocSnapshotAllSurveys == null) {
            mListDocSnapshotAllSurveys = new ArrayList<>();
            mListDocSnapshotAllSurveysClear = new ArrayList<>();
        } else {
            mListDocSnapshotAllSurveys.clear();
            mListDocSnapshotAllSurveysClear.clear();
        }


        return Observable.create(subscriber ->

                firstQuery
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {

                            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {

                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                    surveys.add(documentSnapshot.toObject(Survey.class));
                                    mListDocSnapshotAllSurveys.add(documentSnapshot);


                                }

                                mLastSurveyVisible = mListDocSnapshotAllSurveys.get(mListDocSnapshotAllSurveys.size() - 1);

                                subscriber.onNext(surveys);
                            }
                            else {
                                subscriber.onNext(null);
                            }
                        })
                        .addOnFailureListener(e -> {

                            subscriber.onNext(null);

                        }));

    }

    public static void loadMoreSurveys(List<Survey> surveys, FragmentContainerSurveysAdapter fragmentContainerSurveysAdapter,
                                       ProgressBar mProgressBarBottom, String tribuKey) {

        if (mLastSurveyVisible != null) {
            Query nextQuery = mTribusCollection
                    .document(tribuKey)
                    .collection(SURVEYS)
                    .orderBy(DATE, Query.Direction.DESCENDING)
                    .startAfter(mLastSurveyVisible)
                    .limit(4);

            nextQuery
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            mListDocSnapshotAllSurveysClear.clear();

                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                if (!mListDocSnapshotAllSurveys.contains(documentSnapshot)) {


                                    mListDocSnapshotAllSurveys.add(documentSnapshot);
                                    mListDocSnapshotAllSurveysClear.add(documentSnapshot);


                                }
                            }

                            mLastSurveyVisible = mListDocSnapshotAllSurveys
                                    .get(mListDocSnapshotAllSurveys.size() - 1);


                            for (DocumentSnapshot documentSnapshot : mListDocSnapshotAllSurveysClear) {

                                surveys.add(documentSnapshot.toObject(Survey.class));


                            }

                            fragmentContainerSurveysAdapter.notifyDataSetChanged();
                            mProgressBarBottom.setVisibility(GONE);

                        } else {
                            mProgressBarBottom.setVisibility(GONE);
                            mLastTopicVisible = null;

                        }

                    })
                    .addOnFailureListener(e -> {
                        mProgressBarBottom.setVisibility(GONE);
                    });

        } else {
            mProgressBarBottom.setVisibility(GONE);
        }
    }


}