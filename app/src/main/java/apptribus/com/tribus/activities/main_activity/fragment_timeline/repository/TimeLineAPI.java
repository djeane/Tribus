package apptribus.com.tribus.activities.main_activity.fragment_timeline.repository;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.main_activity.fragment_timeline.adapter.TribusLineAdapter;
import apptribus.com.tribus.activities.main_activity.fragment_timeline.mvp.TimeLineView;
import apptribus.com.tribus.pojo.ConversationTopic;
import apptribus.com.tribus.pojo.Follower;
import apptribus.com.tribus.pojo.MessageUser;
import apptribus.com.tribus.pojo.RequestTribu;
import apptribus.com.tribus.pojo.Tribu;
import rx.Observable;

import static android.view.View.*;
import static apptribus.com.tribus.util.Constantes.ALL;
import static apptribus.com.tribus.util.Constantes.GENERAL_TAGS;
import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.MESSAGE_TAGS;
import static apptribus.com.tribus.util.Constantes.PROFILE_CREATION_DATE;
import static apptribus.com.tribus.util.Constantes.PROFILE_THEMATIC;
import static apptribus.com.tribus.util.Constantes.TOPICS;
import static apptribus.com.tribus.util.Constantes.TOPIC_MESSAGES;
import static apptribus.com.tribus.util.Constantes.TRIBUS_THEMATICS;

/**
 * Created by User on 5/27/2017.
 */

public class TimeLineAPI {

    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    //FIRESTORE INSTANCE
    private static FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    //FIRESTORE REFERENCES
    private static CollectionReference mTribusCollection = mFirestore.collection(GENERAL_TRIBUS);
    private static CollectionReference mThematicsCollection = mFirestore.collection(TRIBUS_THEMATICS);
    private static CollectionReference mTagsCollection = mFirestore.collection(GENERAL_TAGS);

    //OBJECTS
    private static DocumentSnapshot mLastTribuVisible = null;
    private static DocumentSnapshot mLastTribuVisibleByThematics = null;
    private static DocumentSnapshot mLastAllTribuVisibleByThematics = null;

    private static List<DocumentSnapshot> mListDocSnapshotAllTribus;
    private static List<DocumentSnapshot> mListDocSnapshotAllTribusClear;

    private static List<DocumentSnapshot> mListDocSnapshotAllTribusByThematics = new ArrayList<>();
    private static List<DocumentSnapshot> mListDocSnapshotAllTribusByThematicsClear = new ArrayList<>();

    private static List<DocumentSnapshot> mListDocSnapshotTribusByThematics = new ArrayList<>();
    private static List<DocumentSnapshot> mListDocSnapshotTribusByThematicsClear = new ArrayList<>();

    private static ProgressDialog progress;


    public static Observable<List<String>> getThematicsToPopulateRv(List<String> thematics, TimeLineView view) {

        return Observable.create(subscriber ->
                mThematicsCollection
                        .addSnapshotListener(view.mContext.getActivity(), (queryDocumentSnapshots, e) -> {

                            if (e != null) {
                                return;
                            }

                            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {

                                thematics.add(0, ALL);

                                for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {

                                    if (documentChange.getType() == DocumentChange.Type.ADDED) {

                                        documentChange.getDocument().getData().size();
                                        thematics.add(documentChange.getDocument().getId());
                                    }
                                }
                                subscriber.onNext(thematics);

                            } else {
                                subscriber.onNext(null);
                            }
                        }));
    }

    //get all tribus
    public static Observable<List<Tribu>> getAllTribus(List<Tribu> tribus) {

        Query firstQuery = mTribusCollection
                .orderBy(PROFILE_CREATION_DATE, Query.Direction.DESCENDING)
                .limit(3);

        if (mListDocSnapshotAllTribus == null) {
            mListDocSnapshotAllTribus = new ArrayList<>();
            mListDocSnapshotAllTribusClear = new ArrayList<>();
        }
        else {
            mListDocSnapshotAllTribus.clear();
            mListDocSnapshotAllTribusClear.clear();
        }

        if (mListDocSnapshotTribusByThematics != null){
            mListDocSnapshotTribusByThematics.clear();
        }

        if (mListDocSnapshotTribusByThematicsClear != null){
            mListDocSnapshotTribusByThematicsClear.clear();
        }

        if (mListDocSnapshotAllTribusByThematics != null){
            mListDocSnapshotAllTribusByThematics.clear();
        }

        if (mListDocSnapshotAllTribusByThematicsClear != null){
            mListDocSnapshotAllTribusByThematicsClear.clear();
        }


        return Observable.create(subscriber ->

                firstQuery
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {

                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                tribus.add(documentSnapshot.toObject(Tribu.class));
                                mListDocSnapshotAllTribus.add(documentSnapshot);

                            }


                            //mLastTribuVisible = mListDocSnapshotAllTribus.get(mListDocSnapshotAllTribus.size() - 1);
                            mLastTribuVisible = mListDocSnapshotAllTribus.get(mListDocSnapshotAllTribus.size() - 1);

                            subscriber.onNext(tribus);
                        })
                        .addOnFailureListener(e -> {

                            subscriber.onNext(null);

                        }));

    }

    //load more all tribus
    public static void loadMoreTribus(List<Tribu> tribus, TribusLineAdapter mTimeLineAdapter, ProgressBar mProgressBarBottom) {

        if (mLastTribuVisible != null) {
            Query nextQuery = mTribusCollection
                    .orderBy(PROFILE_CREATION_DATE, Query.Direction.DESCENDING)
                    .startAfter(mLastTribuVisible)
                    .limit(3);

            nextQuery
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            mListDocSnapshotAllTribusClear.clear();

                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                if (!mListDocSnapshotAllTribus.contains(documentSnapshot)) {

                                    mListDocSnapshotAllTribus.add(documentSnapshot);
                                    mListDocSnapshotAllTribusClear.add(documentSnapshot);

                                }
                            }

                            mLastTribuVisible = mListDocSnapshotAllTribus
                                    .get(mListDocSnapshotAllTribus.size() - 1);


                            for (DocumentSnapshot documentSnapshot : mListDocSnapshotAllTribusClear) {

                                tribus.add(documentSnapshot.toObject(Tribu.class));
                            }

                            mTimeLineAdapter.notifyDataSetChanged();
                            mProgressBarBottom.setVisibility(GONE);

                        } else {
                            mProgressBarBottom.setVisibility(GONE);
                            mLastTribuVisible = null;

                        }

                    })
                    .addOnFailureListener(e -> {
                        mProgressBarBottom.setVisibility(GONE);
                    });

        } else {
            mProgressBarBottom.setVisibility(GONE);
        }
    }

    //get tribus by thematics
    public static Observable<List<Tribu>> getTribusByThematics(List<Tribu> tribus, String thematic)
            throws IndexOutOfBoundsException{

        if (thematic.equals(ALL)) {
            mLastTribuVisible = null;
            mLastAllTribuVisibleByThematics = null;
            mLastTribuVisibleByThematics = null;
            return getAllTribusByThematics(tribus);
        }

        if (mListDocSnapshotTribusByThematics == null) {
            mListDocSnapshotTribusByThematics = new ArrayList<>();
            mListDocSnapshotTribusByThematicsClear = new ArrayList<>();
        } else {
            mListDocSnapshotTribusByThematics.clear();
            mListDocSnapshotTribusByThematicsClear.clear();
        }

        if (mListDocSnapshotAllTribus != null) {
            mListDocSnapshotAllTribus.clear();
        }

        if (mListDocSnapshotAllTribusClear != null) {
            mListDocSnapshotAllTribusClear.clear();
        }

        if (mListDocSnapshotAllTribusByThematics != null) {
            mListDocSnapshotAllTribusByThematics.clear();
        }

        if (mListDocSnapshotAllTribusByThematicsClear != null) {
            mListDocSnapshotAllTribusByThematicsClear.clear();
        }

        Query firstQuery = mTribusCollection
                .whereEqualTo(PROFILE_THEMATIC, thematic)
                .orderBy(PROFILE_CREATION_DATE, Query.Direction.DESCENDING)
                .limit(3);


        return Observable.create(subscriber ->

                firstQuery
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {

                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                tribus.add(documentSnapshot.toObject(Tribu.class));
                                mListDocSnapshotTribusByThematics.add(documentSnapshot);

                            }

                            mLastTribuVisibleByThematics = mListDocSnapshotTribusByThematics
                                    .get(mListDocSnapshotTribusByThematics.size() - 1);

                            subscriber.onNext(tribus);

                        })
                        .addOnFailureListener(e -> {

                            subscriber.onNext(null);

                        }));

    }

    //load more tribus by thematics
    public static void loadMoreTribusByThematics(List<Tribu> tribus, String thematic,
                                                 TribusLineAdapter mTimeLineAdapterByThematics, TimeLineView view) {

        if (thematic.equals(ALL)) {
            mLastTribuVisible = null;
            mLastTribuVisibleByThematics = null;
            loadMoreAllTribusByThematics(tribus, mTimeLineAdapterByThematics, view.mProgressBarBottom);
        }
        else {

            mLastTribuVisible = null;
            mLastAllTribuVisibleByThematics = null;

            if (mLastTribuVisibleByThematics != null) {
                Query nextQuery = mTribusCollection
                        //.orderBy("profile.thematic")
                        .whereEqualTo(PROFILE_THEMATIC, thematic)
                        .orderBy(PROFILE_CREATION_DATE, Query.Direction.DESCENDING)
                        .startAfter(mLastTribuVisibleByThematics)
                        .limit(3);

                nextQuery
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {

                            if (!queryDocumentSnapshots.isEmpty()) {

                                mListDocSnapshotTribusByThematicsClear.clear();

                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                    if (!mListDocSnapshotTribusByThematics.contains(documentSnapshot)) {

                                        mListDocSnapshotTribusByThematics.add(documentSnapshot);
                                        mListDocSnapshotTribusByThematicsClear.add(documentSnapshot);

                                    }
                                }

                                mLastTribuVisibleByThematics = mListDocSnapshotTribusByThematics
                                        .get(mListDocSnapshotTribusByThematics.size() - 1);

                                for (DocumentSnapshot documentSnapshot : mListDocSnapshotTribusByThematicsClear) {

                                    tribus.add(documentSnapshot.toObject(Tribu.class));
                                }

                                mTimeLineAdapterByThematics.notifyDataSetChanged();
                                view.mProgressBarBottom.setVisibility(GONE);

                            } else {
                                view.mProgressBarBottom.setVisibility(GONE);
                                mLastTribuVisibleByThematics = null;

                            }

                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(view.mContext.getActivity(), "Houve um erro ao processar sua solicitação. Por favor, tente novamente.",
                                    Toast.LENGTH_SHORT).show();
                            view.mProgressBarBottom.setVisibility(View.GONE);
                        });
            } else {
                view.mProgressBarBottom.setVisibility(GONE);
            }
        }

    }

    //get all tribus by themtatics
    private static Observable<List<Tribu>> getAllTribusByThematics(List<Tribu> tribus){

        Query firstQuery = mTribusCollection
                .orderBy(PROFILE_CREATION_DATE, Query.Direction.DESCENDING)
                .limit(3);

        if (mListDocSnapshotAllTribusByThematics == null) {
            mListDocSnapshotAllTribusByThematics = new ArrayList<>();
            mListDocSnapshotAllTribusByThematicsClear = new ArrayList<>();
        }
        else {
            mListDocSnapshotAllTribusByThematics.clear();
            mListDocSnapshotAllTribusByThematicsClear.clear();
        }

        if (mListDocSnapshotTribusByThematics != null){
            mListDocSnapshotTribusByThematics.clear();
        }

        if (mListDocSnapshotTribusByThematicsClear != null){
            mListDocSnapshotTribusByThematicsClear.clear();
        }

        if (mListDocSnapshotAllTribus != null){
            mListDocSnapshotAllTribus.clear();
        }

        if (mListDocSnapshotAllTribusClear != null){
            mListDocSnapshotAllTribusClear.clear();
        }


        return Observable.create(subscriber ->

                firstQuery
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {

                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {


                                tribus.add(documentSnapshot.toObject(Tribu.class));
                                mListDocSnapshotAllTribusByThematics.add(documentSnapshot);
                            }

                            mLastAllTribuVisibleByThematics = mListDocSnapshotAllTribusByThematics.get(mListDocSnapshotAllTribusByThematics.size() - 1);

                            subscriber.onNext(tribus);
                                                    })
                        .addOnFailureListener(e -> {

                            subscriber.onNext(null);

                        }));

    }

    //load more all tribus by thematics
    private static void loadMoreAllTribusByThematics(List<Tribu> tribus, TribusLineAdapter mTimeLineAdapter,
                                                     ProgressBar mProgressBarBottom) {

        if (mLastAllTribuVisibleByThematics != null) {
            Query nextQuery = mTribusCollection
                    .orderBy(PROFILE_CREATION_DATE, Query.Direction.DESCENDING)
                    .startAfter(mLastAllTribuVisibleByThematics)
                    .limit(3);

            nextQuery
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            mListDocSnapshotAllTribusByThematicsClear.clear();

                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                if (!mListDocSnapshotAllTribusByThematics.contains(documentSnapshot)) {

                                    mListDocSnapshotAllTribusByThematics.add(documentSnapshot);
                                    mListDocSnapshotAllTribusByThematicsClear.add(documentSnapshot);

                                }
                            }

                            mLastAllTribuVisibleByThematics = mListDocSnapshotAllTribusByThematics
                                    .get(mListDocSnapshotAllTribusByThematics.size() - 1);


                            for (DocumentSnapshot documentSnapshot : mListDocSnapshotAllTribusByThematicsClear) {

                                tribus.add(documentSnapshot.toObject(Tribu.class));
                            }

                            mTimeLineAdapter.notifyDataSetChanged();
                            mProgressBarBottom.setVisibility(GONE);

                        } else {
                            mProgressBarBottom.setVisibility(GONE);
                            mLastAllTribuVisibleByThematics = null;
                        }

                    })
                    .addOnFailureListener(e -> {
                        mProgressBarBottom.setVisibility(GONE);
                    });

        } else {
            mProgressBarBottom.setVisibility(GONE);

        }
    }


    public static Observable<List<String>> getThematicsTag(List<String> tagThematics){

        return Observable.create(subscriber -> {

            mTagsCollection
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()){

                            Log.e("tribus: ", "queryDocumentSnapshots NÃO É EMPTY");

                            for (DocumentSnapshot thematic : queryDocumentSnapshots){
                                tagThematics.add(thematic.getId());
                            }

                            subscriber.onNext(tagThematics);
                        }
                        else {
                            Log.e("tribus: ", "queryDocumentSnapshots É EMPTY");
                            subscriber.onNext(null);
                        }

                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        subscriber.onNext(null);
                    });

        });
    }



    //OLD RIGHT CODE
    //get thematics
    /*public static Observable<List<String>> getThematicsToPopulateRv(List<String> thematics, TimeLineView view) {

        return Observable.create(subscriber ->
                mThematicsCollection
                        .addSnapshotListener(view.mContext.getActivity(), (queryDocumentSnapshots, e) -> {

                            if (e != null) {
                                return;
                            }

                            if (queryDocumentSnapshots != null) {

                                for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {

                                    if (documentChange.getType() == DocumentChange.Type.ADDED) {

                                        documentChange.getDocument().getData().size();
                                        thematics.add(documentChange.getDocument().getId());
                                    }
                                }
                                subscriber.onNext(thematics);
                            } else {
                                subscriber.onNext(null);
                            }
                        }));
    }

    //get all tribus
    public static Observable<List<Tribu>> getAllTribus(List<Tribu> tribus) {

        Query firstQuery = mTribusCollection
                .orderBy("timestampCreated.timestamp", Query.Direction.DESCENDING)
                .limit(3);

        if (mListDocSnapshotAllTribus == null) {
            mListDocSnapshotAllTribus = new ArrayList<>();
            mListDocSnapshotAllTribusClear = new ArrayList<>();
        }
        else {
            mListDocSnapshotAllTribus.clear();
            mListDocSnapshotAllTribusClear.clear();
        }

        return Observable.create(subscriber ->

                firstQuery
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {

                            //if (mFirstPageFirstLoad) {
                            //mLastTribuVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);


                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                tribus.add(documentSnapshot.toObject(Tribu.class));
                                mListDocSnapshotAllTribus.add(documentSnapshot);

                            }

                            mLastTribuVisible = mListDocSnapshotAllTribus.get(mListDocSnapshotAllTribus.size() - 1);
                            Log.e("mLastTribuVisible F: ", mLastTribuVisible.toString());

                            subscriber.onNext(tribus);
                            //}

                            //mFirstPageFirstLoad = false;
                        })
                        .addOnFailureListener(e -> {

                            subscriber.onNext(null);

                        }));

    }

    //load more all tribus
    public static void loadMoreTribus(List<Tribu> tribus, TimeLineAdapter mTimeLineAdapter, ProgressBar mProgressBarBottom) {

        if (mLastTribuVisible != null) {
            Query nextQuery = mTribusCollection
                    .orderBy("timestampCreated.timestamp", Query.Direction.DESCENDING)
                    .startAfter(mLastTribuVisible)
                    .limit(3);

            nextQuery
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            mListDocSnapshotAllTribusClear.clear();

                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                if (!mListDocSnapshotAllTribus.contains(documentSnapshot)) {


                                    mListDocSnapshotAllTribus.add(documentSnapshot);
                                    mListDocSnapshotAllTribusClear.add(documentSnapshot);

                                }
                            }

                            //mLastTribuVisible = mListDocSnapshotAllTribus
                              //      .get(queryDocumentSnapshots.size() - 1);


                            mLastTribuVisible = mListDocSnapshotAllTribus
                                    .get(mListDocSnapshotAllTribus.size() - 1);

                            Log.e("mLastTribuVisible N: ", mLastTribuVisible.toString());


                            for (DocumentSnapshot documentSnapshot : mListDocSnapshotAllTribusClear) {

                                tribus.add(documentSnapshot.toObject(Tribu.class));
                            }

                            mTimeLineAdapter.notifyDataSetChanged();
                            mProgressBarBottom.setVisibility(GONE);

                        } else {
                            mProgressBarBottom.setVisibility(GONE);
                            mLastTribuVisible = null;

                            //mFirstPageFirstLoad = true;
                        }

                    })
                    .addOnFailureListener(e -> {
                        mProgressBarBottom.setVisibility(GONE);
                        Log.e("tribus loadMore: ", e.getMessage());
                    });
            //mFirstPageFirstLoad = true;

        } else {
            mProgressBarBottom.setVisibility(GONE);
            //mFirstPageFirstLoad = true;
        }
    }

    //get tribus by thematics
    public static Observable<List<Tribu>> getTribusByThematics(List<Tribu> tribus, String thematic) {

        if (thematic.equals(ALL)) {
            mLastTribuVisible = null;
            mLastAllTribuVisibleByThematics = null;
            return getAllTribusByThematics(tribus);
        } else {

            if (mListDocSnapshotTribusByThematics == null) {
                mListDocSnapshotTribusByThematics = new ArrayList<>();
                mListDocSnapshotTribusByThematicsClear = new ArrayList<>();
            }
            else {
                mListDocSnapshotTribusByThematics.clear();
                mListDocSnapshotTribusByThematicsClear.clear();
            }


            Query firstQuery = mTribusCollection
                    //.orderBy("profile.thematic")
                    .whereEqualTo("profile.thematic", thematic)
                    .orderBy("timestampCreated.timestamp", Query.Direction.DESCENDING)
                    .limit(3);


            return Observable.create(subscriber ->

                    firstQuery
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {

                                //mLastTribuVisibleByThematics = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);

                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                    Log.e("documentSnapshot: ", documentSnapshot.toString());

                                    tribus.add(documentSnapshot.toObject(Tribu.class));
                                    mListDocSnapshotTribusByThematics.add(documentSnapshot);

                                }

                                mLastTribuVisibleByThematics = mListDocSnapshotTribusByThematics.get(mListDocSnapshotTribusByThematics.size() - 1);

                                subscriber.onNext(tribus);

                            })
                            .addOnFailureListener(e -> {

                                subscriber.onNext(null);

                            }));

        }
    }

    //load more tribus by thematics
    public static void loadMoreTribusByThematics(List<Tribu> tribus, String thematic,
                                                 TimeLineAdapter mTimeLineAdapterByThematics, TimeLineView view) {

        if (thematic.equals(ALL)) {
            loadMoreAllTribusByThematics(tribus, mTimeLineAdapterByThematics, view.mProgressBarBottom);
        } else {

            if (mLastTribuVisibleByThematics != null) {
                Query nextQuery = mTribusCollection
                        //.orderBy("profile.thematic")
                        .whereEqualTo("profile.thematic", thematic)
                        .orderBy("timestampCreated.timestamp", Query.Direction.DESCENDING)
                        .startAfter(mLastTribuVisibleByThematics)
                        .limit(3);

                nextQuery
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {

                            if (!queryDocumentSnapshots.isEmpty()) {

                                mListDocSnapshotTribusByThematicsClear.clear();

                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                    if (!mListDocSnapshotTribusByThematics.contains(documentSnapshot)) {

                                        mListDocSnapshotTribusByThematics.add(documentSnapshot);
                                        mListDocSnapshotTribusByThematicsClear.add(documentSnapshot);

                                    }
                                }

                                //mLastTribuVisible = mListDocSnapshotTribusByThematics
                                  //      .get(queryDocumentSnapshots.size() - 1);

                                mLastTribuVisibleByThematics = mListDocSnapshotTribusByThematics
                                        .get(mListDocSnapshotTribusByThematics.size() - 1);

                                Log.e("mLastTribuVisible N: ", mLastTribuVisible.toString());


                                for (DocumentSnapshot documentSnapshot : mListDocSnapshotTribusByThematicsClear) {

                                    tribus.add(documentSnapshot.toObject(Tribu.class));
                                }

                                mTimeLineAdapterByThematics.notifyDataSetChanged();
                                view.mProgressBarBottom.setVisibility(GONE);

                            } else {
                                view.mProgressBarBottom.setVisibility(GONE);
                                mLastTribuVisibleByThematics = null;

                            }

                        })
                        .addOnFailureListener(e -> {
                            Log.e("loadMoretribus ByThem: ", e.getMessage());
                            Toast.makeText(view.mContext.getActivity(), "Houve um erro ao processar sua solicitação. Por favor, tente novamente.",
                                    Toast.LENGTH_SHORT).show();
                            view.mProgressBarBottom.setVisibility(View.GONE);
                        });
            } else {
                view.mProgressBarBottom.setVisibility(GONE);
            }
        }

    }

    //get all tribus by themtatics
    public static Observable<List<Tribu>> getAllTribusByThematics(List<Tribu> tribus) {

        Query firstQuery = mTribusCollection
                .orderBy("timestampCreated.timestamp", Query.Direction.DESCENDING)
                .limit(3);

        if (mListDocSnapshotAllTribusByThematics == null) {
            mListDocSnapshotAllTribusByThematics = new ArrayList<>();
            mListDocSnapshotAllTribusByThematicsClear = new ArrayList<>();
        }
        else {
            mListDocSnapshotAllTribusByThematics.clear();
            mListDocSnapshotAllTribusByThematicsClear.clear();
        }

        return Observable.create(subscriber ->

                firstQuery
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {

                            //if (mFirstPageFirstLoadByThematics) {

                                //mLastAllTribuVisibleByThematics = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);


                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {


                                    tribus.add(documentSnapshot.toObject(Tribu.class));
                                    mListDocSnapshotAllTribusByThematics.add(documentSnapshot);
                                }

                            mLastAllTribuVisibleByThematics = mListDocSnapshotAllTribusByThematics.get(mListDocSnapshotAllTribusByThematics.size() - 1);

                            Log.e("mLastTribuVisible F: ", mLastAllTribuVisibleByThematics.toString());

                            subscriber.onNext(tribus);
                            //}

                            //mFirstPageFirstLoadByThematics = false;
                        })
                        .addOnFailureListener(e -> {

                            subscriber.onNext(null);

                        }));

    }

    //load more all tribus by thematics
    private static void loadMoreAllTribusByThematics(List<Tribu> tribus, TimeLineAdapter mTimeLineAdapter,
                                                     ProgressBar mProgressBarBottom) {

        if (mLastAllTribuVisibleByThematics != null) {
            Query nextQuery = mTribusCollection
                    .orderBy("timestampCreated.timestamp", Query.Direction.DESCENDING)
                    .startAfter(mLastAllTribuVisibleByThematics)
                    .limit(3);

            nextQuery
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            mListDocSnapshotAllTribusByThematicsClear.clear();

                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                if (!mListDocSnapshotAllTribusByThematics.contains(documentSnapshot)) {

                                    mListDocSnapshotAllTribusByThematics.add(documentSnapshot);
                                    mListDocSnapshotAllTribusByThematicsClear.add(documentSnapshot);

                                }
                            }

                            //mLastTribuVisible = mListDocSnapshotAllTribusByThematics
                              //      .get(queryDocumentSnapshots.size() - 1);

                            mLastAllTribuVisibleByThematics = mListDocSnapshotAllTribusByThematics
                                    .get(mListDocSnapshotAllTribusByThematics.size() - 1);

                            Log.e("LasAllTribVisByThem N: ", mLastAllTribuVisibleByThematics.toString());


                            for (DocumentSnapshot documentSnapshot : mListDocSnapshotAllTribusByThematicsClear) {

                                tribus.add(documentSnapshot.toObject(Tribu.class));
                            }

                            mTimeLineAdapter.notifyDataSetChanged();
                            mProgressBarBottom.setVisibility(GONE);

                        } else {
                            mProgressBarBottom.setVisibility(GONE);
                            mLastAllTribuVisibleByThematics = null;
                        }

                    })
                    .addOnFailureListener(e -> {
                        mProgressBarBottom.setVisibility(GONE);
                        Log.e("tribus loadMore: ", e.getMessage());
                    });

        } else {
            mProgressBarBottom.setVisibility(GONE);

        }
    }*/


















    //OLD CODE
    //get all tribus
    /*public static Observable<List<Tribu>> getAllTribus(List<Tribu> tribus) {

        Query firstQuery = mTribusCollection
                .orderBy("timestampCreated.timestamp", Query.Direction.DESCENDING)
                .limit(3);

        return Observable.create(subscriber ->

                firstQuery
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {

                            //if (mFirstPageFirstLoad) {
                                mLastTribuVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);

                                Log.e("mLastTribuVisible F: ", mLastTribuVisible.toString());

                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {


                                    tribus.add(documentSnapshot.toObject(Tribu.class));

                                }

                                subscriber.onNext(tribus);
                            //}

                            //mFirstPageFirstLoad = false;
                        })
                        .addOnFailureListener(e -> {

                            subscriber.onNext(null);

                        }));

    }

    //load more all tribus
    public static void loadMoreTribus(List<Tribu> tribus, TimeLineAdapter mTimeLineAdapter, ProgressBar mProgressBarBottom) {

        if (mLastTribuVisible != null) {
            Query nextQuery = mTribusCollection
                    .orderBy("timestampCreated.timestamp", Query.Direction.DESCENDING)
                    .startAfter(mLastTribuVisible)
                    .limit(3);

            nextQuery
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            mLastTribuVisible = queryDocumentSnapshots
                                    .getDocuments()
                                    .get(queryDocumentSnapshots.size() - 1);

                            Log.e("mLastTribuVisible N: ", mLastTribuVisible.toString());


                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                tribus.add(documentSnapshot.toObject(Tribu.class));
                            }

                            mTimeLineAdapter.notifyDataSetChanged();
                            mProgressBarBottom.setVisibility(GONE);

                        } else {
                            mProgressBarBottom.setVisibility(GONE);
                            mLastTribuVisible = null;
                            //mFirstPageFirstLoad = true;
                        }

                    })
                    .addOnFailureListener(e -> {
                        mProgressBarBottom.setVisibility(GONE);
                        Log.e("tribus loadMore: ", e.getMessage());
                    });
            //mFirstPageFirstLoad = true;

        } else {
            mProgressBarBottom.setVisibility(GONE);
            //mFirstPageFirstLoad = true;
        }
    }


    //get tribus by thematics
    public static Observable<List<Tribu>> getTribusByThematics(List<Tribu> tribus, String thematic) {

        if (thematic.equals(ALL)) {
            mLastTribuVisible = null;
            mLastAllTribuVisibleByThematics = null;
            return getAllTribusByThematics(tribus);
        } else {
            Query firstQuery = mTribusCollection
                    //.orderBy("profile.thematic")
                    .whereEqualTo("profile.thematic", thematic)
                    .orderBy("timestampCreated.timestamp", Query.Direction.DESCENDING)
                    .limit(3);


            return Observable.create(subscriber ->

                    firstQuery
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {

                                mLastTribuVisibleByThematics = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);

                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                    Log.e("documentSnapshot: ", documentSnapshot.toString());

                                    tribus.add(documentSnapshot.toObject(Tribu.class));

                                }

                                subscriber.onNext(tribus);

                            })
                            .addOnFailureListener(e -> {

                                subscriber.onNext(null);

                            }));

        }
    }

    //load more tribus by thematics
    public static void loadMoreTribusByThematics(List<Tribu> tribus, String thematic,
                                                 TimeLineAdapter mTimeLineAdapterByThematics, TimeLineView view) {

        if (thematic.equals(ALL)) {
            loadMoreAllTribusByThematics(tribus, mTimeLineAdapterByThematics, view.mProgressBarBottom);
        } else {

            if (mLastTribuVisibleByThematics != null) {
                Query nextQuery = mTribusCollection
                        //.orderBy("profile.thematic")
                        .whereEqualTo("profile.thematic", thematic)
                        .orderBy("timestampCreated.timestamp", Query.Direction.DESCENDING)
                        .startAfter(mLastTribuVisibleByThematics)
                        .limit(3);

                nextQuery
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {

                            if (!queryDocumentSnapshots.isEmpty()) {

                                mLastTribuVisibleByThematics = queryDocumentSnapshots
                                        .getDocuments()
                                        .get(queryDocumentSnapshots.size() - 1);

                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                    tribus.add(documentSnapshot.toObject(Tribu.class));
                                }

                                mTimeLineAdapterByThematics.notifyDataSetChanged();
                                view.mProgressBarBottom.setVisibility(GONE);

                            } else {
                                view.mProgressBarBottom.setVisibility(GONE);
                                mLastTribuVisibleByThematics = null;
                            }

                        })
                        .addOnFailureListener(e -> {
                            Log.e("loadMoretribus ByThem: ", e.getMessage());
                            Toast.makeText(view.mContext.getActivity(), "Houve um erro ao processar sua solicitação. Por favor, tente novamente.",
                                    Toast.LENGTH_SHORT).show();
                            view.mProgressBarBottom.setVisibility(View.GONE);
                        });
            } else {
                view.mProgressBarBottom.setVisibility(GONE);
            }
        }

    }

    //get all tribus by themtatics
    public static Observable<List<Tribu>> getAllTribusByThematics(List<Tribu> tribus) {

        Query firstQuery = mTribusCollection
                .orderBy("timestampCreated.timestamp", Query.Direction.DESCENDING)
                .limit(3);

        return Observable.create(subscriber ->

                firstQuery
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {

                            //if (mFirstPageFirstLoadByThematics) {

                                mLastAllTribuVisibleByThematics = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);

                                Log.e("mLastTribuVisible F: ", mLastAllTribuVisibleByThematics.toString());

                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {


                                    tribus.add(documentSnapshot.toObject(Tribu.class));

                                //}

                                subscriber.onNext(tribus);
                            }

                            //mFirstPageFirstLoadByThematics = false;
                        })
                        .addOnFailureListener(e -> {

                            subscriber.onNext(null);

                        }));

    }

    //load more all tribus by thematics
    private static void loadMoreAllTribusByThematics(List<Tribu> tribus, TimeLineAdapter mTimeLineAdapter,
                                                     ProgressBar mProgressBarBottom) {

        if (mLastAllTribuVisibleByThematics != null) {
            Query nextQuery = mTribusCollection
                    .orderBy("timestampCreated.timestamp", Query.Direction.DESCENDING)
                    .startAfter(mLastAllTribuVisibleByThematics)
                    .limit(3);

            nextQuery
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            mLastAllTribuVisibleByThematics = queryDocumentSnapshots
                                    .getDocuments()
                                    .get(queryDocumentSnapshots.size() - 1);

                            Log.e("mLastTribuVisible N: ", mLastAllTribuVisibleByThematics.toString());


                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                tribus.add(documentSnapshot.toObject(Tribu.class));
                            }

                            mTimeLineAdapter.notifyDataSetChanged();
                            mProgressBarBottom.setVisibility(GONE);

                        } else {
                            mProgressBarBottom.setVisibility(GONE);
                            mLastAllTribuVisibleByThematics = null;
                            //mFirstPageFirstLoad = true;
                        }

                    })
                    .addOnFailureListener(e -> {
                        mProgressBarBottom.setVisibility(GONE);
                        Log.e("tribus loadMore: ", e.getMessage());
                    });
            //mFirstPageFirstLoadByThematics = true;
        } else {
            mProgressBarBottom.setVisibility(GONE);
            //mFirstPageFirstLoadByThematics = true;
        }
    }*/

}