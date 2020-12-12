package apptribus.com.tribus.activities.main_activity.fragment_tribus.repository;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import apptribus.com.tribus.activities.main_activity.fragment_tribus.adapter.TribusFragmentAdapter;
import apptribus.com.tribus.activities.main_activity.fragment_tribus.mvp.TribusFragmentView;
import apptribus.com.tribus.pojo.Tribu;
import rx.Observable;

import static android.view.View.GONE;
import static apptribus.com.tribus.util.Constantes.ALL;
import static apptribus.com.tribus.util.Constantes.DATE;
import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.PARTICIPATING;
import static apptribus.com.tribus.util.Constantes.REMOVED_TRIBUS;
import static apptribus.com.tribus.util.Constantes.THEMATIC;
import static apptribus.com.tribus.util.Constantes.TRIBUS_THEMATICS;

public class TribusFragmentAPI {

    //INSTANCES - FIREBASE
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();


    //FIRESTORE INSTANCE
    private static FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    //FIRESTORE REFERENCES
    private static CollectionReference mTribusCollection = mFirestore.collection(GENERAL_TRIBUS);
    private static CollectionReference mUserCollection = mFirestore.collection(GENERAL_USERS);
    private static CollectionReference mThematicsCollection = mFirestore.collection(TRIBUS_THEMATICS);

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


    //GET THEMATICS'S LIST FROM FOLLOWED TRIBUS
    public static Observable<List<String>> getFollowedThematicsToPopulateRv(List<String> thematics) {

        return Observable.create(subscriber ->

                mUserCollection
                        .document(mAuth.getCurrentUser().getUid())
                        .collection(PARTICIPATING)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {

                            if (!queryDocumentSnapshots.isEmpty()) {

                                thematics.add(0, ALL);

                                for (DocumentSnapshot key : queryDocumentSnapshots.getDocuments()) {

                                    //String tribuKey = key.getId();

                                    Tribu tribu = key.toObject(Tribu.class);

                                    if (tribu != null) {

                                        if (!thematics.contains(tribu.getThematic())) {
                                            thematics.add(tribu.getThematic());
                                        }

                                    }

                                /*mTribusCollection
                                        .document(tribuKey)
                                        .get()
                                        .addOnSuccessListener(documentSnapshot -> {

                                            Tribu tribu = documentSnapshot.toObject(Tribu.class);

                                            if (tribu != null) {
                                                if (!thematics.contains(tribu.getProfile().getThematic())) {
                                                    thematics.add(tribu.getProfile().getThematic());
                                                }
                                            }

                                        })
                                        .addOnFailureListener(Throwable::printStackTrace);*/

                                }

                                subscriber.onNext(thematics);
                            }
                            else {
                                subscriber.onNext(null);
                            }
                        })
                        .addOnFailureListener(e -> {
                            e.printStackTrace();
                            subscriber.onNext(null);
                        }));


    }

    //GET THEMATICS'S LIST FROM REMOVED TRIBUS
    public static Observable<List<String>> getRemovedThematicsToPopulateRv(List<String> thematics) {

        return Observable.create(subscriber ->

                mUserCollection
                        .document(mAuth.getCurrentUser().getUid())
                        .collection(REMOVED_TRIBUS)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {

                            if (!queryDocumentSnapshots.isEmpty()) {

                                thematics.add(0, ALL);

                                for (DocumentSnapshot key : queryDocumentSnapshots.getDocuments()) {

                                    Tribu tribu = key.toObject(Tribu.class);

                                    if (tribu != null) {

                                        if (!thematics.contains(tribu.getThematic())) {
                                            thematics.add(tribu.getThematic());
                                        }

                                    }

                                /*String tribuKey = key.getId();

                                mTribusCollection
                                        .document(tribuKey)
                                        .get()
                                        .addOnSuccessListener(documentSnapshot -> {

                                            Tribu tribu = documentSnapshot.toObject(Tribu.class);

                                            if (tribu != null) {
                                                if (!thematics.contains(tribu.getProfile().getThematic())) {
                                                    thematics.add(tribu.getProfile().getThematic());
                                                }
                                            }

                                        })
                                        .addOnFailureListener(Throwable::printStackTrace);*/

                                }

                                subscriber.onNext(thematics);
                            }
                            else {
                                subscriber.onNext(null);
                            }
                        })
                        .addOnFailureListener(e -> {
                            e.printStackTrace();
                            subscriber.onNext(null);
                        }));


    }


    //POPULARING RECYCLERVIEW FOR THE FIRST TIME
    //get all tribus
    public static Observable<List<Tribu>> getAllTribus(List<Tribu> tribus) {

        Query firstQuery = mUserCollection
                .document(mAuth.getCurrentUser().getUid())
                .collection(PARTICIPATING)
                .orderBy(DATE, Query.Direction.DESCENDING)
                .limit(4);

        if (mListDocSnapshotAllTribus == null) {
            mListDocSnapshotAllTribus = new ArrayList<>();
            mListDocSnapshotAllTribusClear = new ArrayList<>();
        } else {
            mListDocSnapshotAllTribus.clear();
            mListDocSnapshotAllTribusClear.clear();
        }

        if (mListDocSnapshotTribusByThematics != null) {
            mListDocSnapshotTribusByThematics.clear();
        }

        if (mListDocSnapshotTribusByThematicsClear != null) {
            mListDocSnapshotTribusByThematicsClear.clear();
        }

        if (mListDocSnapshotAllTribusByThematics != null) {
            mListDocSnapshotAllTribusByThematics.clear();
        }

        if (mListDocSnapshotAllTribusByThematicsClear != null) {
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

                            mLastTribuVisible = mListDocSnapshotAllTribus.get(mListDocSnapshotAllTribus.size() - 1);

                            subscriber.onNext(tribus);
                        })
                        .addOnFailureListener(e -> {

                            subscriber.onNext(null);

                        }));

    }

    //load more all tribus
    public static void loadMoreTribus(List<Tribu> tribus, TribusFragmentAdapter mTribusFragmentAdapter,
                                      ProgressBar mProgressBarBottom) {

        if (mLastTribuVisible != null) {
            Query nextQuery = mUserCollection
                    .document(mAuth.getCurrentUser().getUid())
                    .collection(PARTICIPATING)
                    .orderBy(DATE, Query.Direction.DESCENDING)
                    .startAfter(mLastTribuVisible)
                    .limit(4);


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

                            mTribusFragmentAdapter.notifyDataSetChanged();
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
            throws IndexOutOfBoundsException {

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

        Query firstQuery = mUserCollection
                .document(mAuth.getCurrentUser().getUid())
                .collection(PARTICIPATING)
                .whereEqualTo(THEMATIC, thematic)
                .orderBy(DATE, Query.Direction.DESCENDING)
                .limit(3);

        Log.e("tribus: ", "query: firstQuery by thematics " + firstQuery);

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
                                                 TribusFragmentAdapter mTribusFragmentAdapterByThematics,
                                                 TribusFragmentView view) {

        if (thematic.equals(ALL)) {
            mLastTribuVisible = null;
            mLastTribuVisibleByThematics = null;
            loadMoreAllTribusByThematics(tribus, mTribusFragmentAdapterByThematics, view.mProgressBarBottom);
        } else {

            mLastTribuVisible = null;
            mLastAllTribuVisibleByThematics = null;

            if (mLastTribuVisibleByThematics != null) {
                Query nextQuery = mUserCollection
                        .document(mAuth.getCurrentUser().getUid())
                        .collection(PARTICIPATING)
                        .whereEqualTo(THEMATIC, thematic)
                        .orderBy(DATE, Query.Direction.DESCENDING)
                        .startAfter(mLastTribuVisibleByThematics)
                        .limit(3);

                Log.e("tribus: ", "query: nextQuery by thematics " + nextQuery);

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

                                mTribusFragmentAdapterByThematics.notifyDataSetChanged();
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
    private static Observable<List<Tribu>> getAllTribusByThematics(List<Tribu> tribus) {

        Query firstQuery = mUserCollection
                .document(mAuth.getCurrentUser().getUid())
                .collection(PARTICIPATING)
                .orderBy(DATE, Query.Direction.DESCENDING)
                .limit(3);

        if (mListDocSnapshotAllTribusByThematics == null) {
            mListDocSnapshotAllTribusByThematics = new ArrayList<>();
            mListDocSnapshotAllTribusByThematicsClear = new ArrayList<>();
        } else {
            mListDocSnapshotAllTribusByThematics.clear();
            mListDocSnapshotAllTribusByThematicsClear.clear();
        }

        if (mListDocSnapshotTribusByThematics != null) {
            mListDocSnapshotTribusByThematics.clear();
        }

        if (mListDocSnapshotTribusByThematicsClear != null) {
            mListDocSnapshotTribusByThematicsClear.clear();
        }

        if (mListDocSnapshotAllTribus != null) {
            mListDocSnapshotAllTribus.clear();
        }

        if (mListDocSnapshotAllTribusClear != null) {
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
    private static void loadMoreAllTribusByThematics(List<Tribu> tribus, TribusFragmentAdapter mTribusFragmentAdapter,
                                                     ProgressBar mProgressBarBottom) {

        if (mLastAllTribuVisibleByThematics != null) {
            Query nextQuery = mUserCollection
                    .document(mAuth.getCurrentUser().getUid())
                    .collection(PARTICIPATING)
                    .orderBy(DATE, Query.Direction.DESCENDING)
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

                            mTribusFragmentAdapter.notifyDataSetChanged();
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


}










    //GET ALL FOLLOWED TRIBUS THEMATIC
    /*public static Observable<List<String>> getFollowedTribusThematics(List<String> tribuList) {



        mUid = mAuth.getCurrentUser().getUid();

        return Observable.create(subscriber -> {

            mReferenceFollowers
                    .child(mUid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot != null) {
                                for (DataSnapshot uniqueTribu : dataSnapshot.getChildren()) {

                                    Tribu tribu = uniqueTribu.getValue(Tribu.class);

                                    mReferenceTribus
                                            .child(tribu.getUniqueNameTribu())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshotThematics) {

                                                    if(dataSnapshotThematics != null){
                                                        Tribu tribuItem = dataSnapshotThematics.getValue(Tribu.class);

                                                        if (!tribuList.contains(tribuItem.getProfile().getThematic())){

                                                            tribuList.add(tribuItem.getProfile().getThematic());

                                                        }
                                                        subscriber.onNext(tribuList);
                                                    }
                                                    else {
                                                        subscriber.onNext(null);
                                                    }
                                                    //for (DataSnapshot mTribu : dataSnapshot.getChildren()) {


                                                    //}

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    databaseError.toException().printStackTrace();
                                                }
                                            });

                                }
                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            databaseError.toException().printStackTrace();
                        }
                    });


        });


    }



    //GET ALL INVITED TRIBUS THEMATIC
    public static Observable<List<String>> getInvitedTribusThematics(List<String> tribuList){
        return Observable.create(subscriber -> {
            mReferenceAdminInvitations
                    .child(mAuth.getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot != null) {

                                for (DataSnapshot uniqueTribu : dataSnapshot.getChildren()) {

                                    Tribu tribu = uniqueTribu.getValue(Tribu.class);

                                    mReferenceTribus
                                            .child(tribu.getUniqueNameTribu())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                    //for (DataSnapshot mTribu : dataSnapshot.getChildren()) {

                                                    Tribu tribuItem = dataSnapshot.getValue(Tribu.class);

                                                    if (!tribuList.contains(tribuItem.getProfile().getThematic())){
                                                        tribuList.add(tribuItem.getProfile().getThematic());
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    databaseError.toException().printStackTrace();
                                                }
                                            });

                                }
                            }

                            subscriber.onNext(tribuList);
                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            databaseError.getMessage();
                        }
                    });

        });

    }


    //GET ALL REMOVED TRIBUS THEMATIC
    public static Observable<List<String>> getRemovedTribusThematics(List<String> tribuList){
        return Observable.create(subscriber -> {
            mReferenceUsersRemovedTribus
                    .child(mAuth.getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot != null) {

                                for (DataSnapshot uniqueTribu : dataSnapshot.getChildren()) {

                                    Tribu tribu = uniqueTribu.getValue(Tribu.class);

                                    mReferenceTribus
                                            .child(tribu.getUniqueNameTribu())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                    //for (DataSnapshot mTribu : dataSnapshot.getChildren()) {

                                                    Tribu tribuItem = dataSnapshot.getValue(Tribu.class);

                                                    if (!tribuList.contains(tribuItem.getProfile().getThematic())){
                                                        tribuList.add(tribuItem.getProfile().getThematic());
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    databaseError.toException().printStackTrace();
                                                }
                                            });

                                }
                            }

                            subscriber.onNext(tribuList);
                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            databaseError.getMessage();
                        }
                    });

        });

    }

    //GET ALL ENDED TRIBUS THEMATIC
    public static Observable<List<String>> getEndedTribusThematics(List<String> tribuList){
        return Observable.create(subscriber -> {
            mReferenceUsersEndedTribus
                    .child(mAuth.getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot != null) {

                                for (DataSnapshot uniqueTribu : dataSnapshot.getChildren()) {

                                    Tribu tribu = uniqueTribu.getValue(Tribu.class);

                                    mReferenceTribus
                                            .child(tribu.getUniqueNameTribu())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                    //for (DataSnapshot mTribu : dataSnapshot.getChildren()) {

                                                    Tribu tribuItem = dataSnapshot.getValue(Tribu.class);

                                                    if (!tribuList.contains(tribuItem.getProfile().getThematic())){
                                                        tribuList.add(tribuItem.getProfile().getThematic());
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    databaseError.toException().printStackTrace();
                                                }
                                            });

                                }
                            }

                            subscriber.onNext(tribuList);
                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            databaseError.getMessage();
                        }
                    });

        });

    }*/


























































    //OLD CODE
    /*//FIREBASE INSTANCES
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    public static DatabaseReference mReferenceUsers = mDatabase.getReference().child(GENERAL_USERS);
    public static DatabaseReference mReferenceFollowers = mDatabase.getReference().child(FOLLOWERS);
    public static DatabaseReference mReferenceAdmin = mDatabase.getReference().child(ADMINS);
    public static DatabaseReference mReferenceTribus = mDatabase.getReference().child(GENERAL_TRIBUS);
    public static DatabaseReference mReferenceMessagesTribus = mDatabase.getReference().child(TRIBUS_MESSAGES);
    public static DatabaseReference mReferenceTopics = mDatabase.getReference().child(TRIBUS_TOPICS);
    public static DatabaseReference mReferenceAdminInvitations = mDatabase.getReference().child(ADMINS_INVITATIONS);
    public static DatabaseReference mReferenceUsersRemovedTribus = mDatabase.getReference().child(USERS_REMOVED_TRIBUS);
    public static DatabaseReference mReferenceUsersEndedTribus = mDatabase.getReference().child(USERS_ENDED_TRIBUS);

    //VARIABLES
    private static User mUser;
    private static String mUid;
    private static long mTribusKey;
    private static Admin mAdmin;

    //LISTENERS
    public static ValueEventListener mValueListenerRefUser;
    public static ValueEventListener mValueListenerRefAdmin;
    public static ValueEventListener mValueListenerRefCurrentUser;
    public static ValueEventListener mValueListenerRefFollowers;
    public static ValueEventListener mValueListenerRefTribus;
    public static ValueEventListener mValueListenerRefTribus2;
    public static ValueEventListener mValueListenerRefTopics;
    public static ValueEventListener mValueListenerRefTopics2;
    public static ValueEventListener mValueListenerRefMessagesTribus;
    public static ValueEventListener mValueListenerRefMessagesTribus2;



    //GET ALL FOLLOWED TRIBUS THEMATIC
    public static Observable<List<String>> getFollowedTribusThematics(List<String> tribuList) {

        mUid = mAuth.getCurrentUser().getUid();

        return Observable.create(subscriber -> {

            mReferenceFollowers
                    .child(mUid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot != null) {
                                for (DataSnapshot uniqueTribu : dataSnapshot.getChildren()) {

                                    Tribu tribu = uniqueTribu.getValue(Tribu.class);

                                    mReferenceTribus
                                            .child(tribu.getUniqueNameTribu())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshotThematics) {

                                                    if(dataSnapshotThematics != null){
                                                        Tribu tribuItem = dataSnapshotThematics.getValue(Tribu.class);

                                                        if (!tribuList.contains(tribuItem.getProfile().getThematic())){

                                                            tribuList.add(tribuItem.getProfile().getThematic());

                                                        }
                                                        subscriber.onNext(tribuList);
                                                    }
                                                    else {
                                                        subscriber.onNext(null);
                                                    }
                                                    //for (DataSnapshot mTribu : dataSnapshot.getChildren()) {


                                                    //}

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    databaseError.toException().printStackTrace();
                                                }
                                            });

                                }
                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            databaseError.toException().printStackTrace();
                        }
                    });


        });


    }



    //GET ALL INVITED TRIBUS THEMATIC
    public static Observable<List<String>> getInvitedTribusThematics(List<String> tribuList){
        return Observable.create(subscriber -> {
            mReferenceAdminInvitations
                .child(mAuth.getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot != null) {

                                for (DataSnapshot uniqueTribu : dataSnapshot.getChildren()) {

                                    Tribu tribu = uniqueTribu.getValue(Tribu.class);

                                    mReferenceTribus
                                            .child(tribu.getUniqueNameTribu())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                    //for (DataSnapshot mTribu : dataSnapshot.getChildren()) {

                                                    Tribu tribuItem = dataSnapshot.getValue(Tribu.class);

                                                    if (!tribuList.contains(tribuItem.getProfile().getThematic())){
                                                        tribuList.add(tribuItem.getProfile().getThematic());
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    databaseError.toException().printStackTrace();
                                                }
                                            });

                                }
                            }

                            subscriber.onNext(tribuList);
                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            databaseError.getMessage();
                        }
                    });

        });

    }


    //GET ALL REMOVED TRIBUS THEMATIC
    public static Observable<List<String>> getRemovedTribusThematics(List<String> tribuList){
        return Observable.create(subscriber -> {
            mReferenceUsersRemovedTribus
                    .child(mAuth.getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot != null) {

                                for (DataSnapshot uniqueTribu : dataSnapshot.getChildren()) {

                                    Tribu tribu = uniqueTribu.getValue(Tribu.class);

                                    mReferenceTribus
                                            .child(tribu.getUniqueNameTribu())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                    //for (DataSnapshot mTribu : dataSnapshot.getChildren()) {

                                                    Tribu tribuItem = dataSnapshot.getValue(Tribu.class);

                                                    if (!tribuList.contains(tribuItem.getProfile().getThematic())){
                                                        tribuList.add(tribuItem.getProfile().getThematic());
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    databaseError.toException().printStackTrace();
                                                }
                                            });

                                }
                            }

                            subscriber.onNext(tribuList);
                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            databaseError.getMessage();
                        }
                    });

        });

    }

    //GET ALL ENDED TRIBUS THEMATIC
    public static Observable<List<String>> getEndedTribusThematics(List<String> tribuList){
        return Observable.create(subscriber -> {
            mReferenceUsersEndedTribus
                    .child(mAuth.getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot != null) {

                                for (DataSnapshot uniqueTribu : dataSnapshot.getChildren()) {

                                    Tribu tribu = uniqueTribu.getValue(Tribu.class);

                                    mReferenceTribus
                                            .child(tribu.getUniqueNameTribu())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                    //for (DataSnapshot mTribu : dataSnapshot.getChildren()) {

                                                    Tribu tribuItem = dataSnapshot.getValue(Tribu.class);

                                                    if (!tribuList.contains(tribuItem.getProfile().getThematic())){
                                                        tribuList.add(tribuItem.getProfile().getThematic());
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    databaseError.toException().printStackTrace();
                                                }
                                            });

                                }
                            }

                            subscriber.onNext(tribuList);
                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            databaseError.getMessage();
                        }
                    });

        });

    }

    public static Observable<Boolean> hasChildren(){
        return Observable.create(subscriber -> { mReferenceFollowers
                .child(mAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(mValueListenerRefFollowers = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChildren()){

                            subscriber.onNext(true);
                        }
                        else {
                            subscriber.onNext(false);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        databaseError.toException().printStackTrace();
                    }
                });
        });

    }


    public static FirebaseRecyclerAdapter<Tribu, TribusFragmentViewHolder> getTribus(Fragment context,
                                                                                     User mainUser,
                                                                                     FirestoreService mFirestoreService) {

        return new FirebaseRecyclerAdapter<Tribu, TribusFragmentViewHolder>(
                Tribu.class,
                R.layout.row_tribus,
                TribusFragmentViewHolder.class,
                mReferenceFollowers.child(mAuth.getCurrentUser().getUid()).orderByKey()
        ) {

            @Override
            protected void populateViewHolder(TribusFragmentViewHolder viewHolder, Tribu tribuParam, int position) {



                viewHolder.initTribusFragmentViewHolder(getItem(viewHolder.getAdapterPosition()), context, mainUser, mFirestoreService);

            }
        };
    }

    //GET USER(TRIBU'S ADMIN)
    public static Observable<User> getUser() {
        mUid = mAuth.getCurrentUser().getUid();

        return Observable.create(subscriber ->
                mReferenceUsers
                        .child(mUid)
                        .addValueEventListener(mValueListenerRefUser = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                mUser = dataSnapshot.getValue(User.class);
                                subscriber.onNext(mUser);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                databaseError.toException().printStackTrace();
                            }
                        }));
    }

    //GET CURRENT USER
    public static Observable<User> getCurrentUser() {
        mUid = mAuth.getCurrentUser().getUid();

        return Observable.create(subscriber -> mReferenceUsers
                .child(mUid)
                .addValueEventListener(mValueListenerRefCurrentUser = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mUser = dataSnapshot.getValue(User.class);
                        subscriber.onNext(mUser);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        databaseError.toException().printStackTrace();
                    }
                }));
    }

*/























/*
    private static void setMessage(Tribu tribu, TribusFragmentViewHolder viewHolder){

        mReferenceMessagesTribus
                .child(tribu.getProfile().getUniqueName())
                .orderByChild("timestamp")
                .limitToLast(1)
                .addValueEventListener(mValueListenerRefMessagesTribus = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChildren()) {

                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                                MessageUser message = postSnapshot.getValue(MessageUser.class);

                                mReferenceUsers
                                        .child(message.getUidUser())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshotUser) {

                                                User user = dataSnapshotUser.getValue(User.class);
                                                /*viewHolder.setLastMessageDate(message.getDate());
                                                viewHolder.mTvData.setVisibility(View.VISIBLE);

                                                if(user.getId().equals(mAuth.getCurrentUser().getUid())){

                                                    viewHolder.setTvMessageChildAdded(viewHolder, message, "Você");
                                                }
                                                else {
                                                    viewHolder.setTvMessageChildAdded(viewHolder, message, user.getName());
                                                }*/

                                            /*}

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                databaseError.toException().printStackTrace();
                                            }
                                        });


                            }
                        }else {
                            /*viewHolder.setTvMessageChildAdded(viewHolder, null, null);
                            viewHolder.mTvData.setVisibility(GONE);*/


                    /*}

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        databaseError.toException().printStackTrace();
                    }
                });

    }

    private static void openConversationTopicActivity(Fragment context, String tribu) {
        Intent intent = new Intent(context.getActivity(), ConversationTopicsActivity.class);
        intent.putExtra(TRIBU_UNIQUE_NAME, tribu);
        context.startActivity(intent);

    }

    public static void getTopicsMessage(Tribu tribuAdmin, TribusFragmentView view, Fragment fragment){

        mReferenceTopics
                .child(tribuAdmin.getProfile().getUniqueName())
                .addValueEventListener(mValueListenerRefTopics2 = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChildren()) {

                            long numTopic = dataSnapshot.getChildrenCount();

                            setNumTopics(numTopic, view, fragment);

                        }
                        else {
                            long numTopic = 0;

                            setNumTopics(numTopic, view, fragment);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private static void setNumTopics(long numTopic, TribusFragmentView view, Fragment fragment) {
        /*view.mTvNumTopics.setVisibility(View.VISIBLE);
        if (numTopic > 1){
            String appendNumTopic = String.valueOf(numTopic) + " tópicos de conversa";
            view.mTvNumTopics.setText(appendNumTopic);


        }
        else if(numTopic == 1){
            String appendNumTopic = String.valueOf(numTopic) + " tópico de conversa";
            view.mTvNumTopics.setText(appendNumTopic);
        }
        if (numTopic < 1){
            String appendNumTopic = "Ainda não há tópicos de conversa nesta mTribu";
            view.mTvNumTopics.setText(appendNumTopic);
        }*/

    /*}

    public static void getMessagesTribuAdmin(Tribu tribuAdmin, TribusFragmentView view, Fragment fragment){
            mReferenceMessagesTribus
                    .child(tribuAdmin.getProfile().getUniqueName())
                    .orderByChild("timestamp")
                    .limitToLast(1)
                    .addValueEventListener(mValueListenerRefMessagesTribus2 = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.hasChildren()) {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    MessageUser messageUser = postSnapshot.getValue(MessageUser.class);

                                    mReferenceUsers
                                            .child(messageUser.getUidUser())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshotUser) {

                                                    User user = dataSnapshotUser.getValue(User.class);

                                                    /*if(view.mTvDate != null) {
                                                        setLastMessageDate(view, messageUser, fragment);
                                                        view.mTvDate.setVisibility(View.VISIBLE);
                                                    }
                                                    if(user.getId().equals(mAuth.getCurrentUser().getUid())) {
                                                        if (view.mTvDate != null) {
                                                            setLastMessage(messageUser, view, "Você");
                                                        }
                                                    }
                                                    else {
                                                        if(view.mTvDate != null) {
                                                            setLastMessage(messageUser, view, user.getName());
                                                        }
                                                    }*/

                                                /*}

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    databaseError.toException().printStackTrace();
                                                }
                                            });



                                }
                            }else {
                                /*if(view.mTvDate != null) {
                                    setLastMessage(null, view, null);
                                }*/
                            /*}
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            databaseError.toException().printStackTrace();
                        }
                    });


    }

    /*private static void setLastMessageDate(TribusFragmentView view, MessageUser messageUser, Fragment fragment) {
        /*if (fragment != null && view.mTvDate != null) {
            String time = GetTimeAgo.getTimeAgo(messageUser.getDate(), fragment.getContext());
            view.mTvDate.setText(time);
        }*/
    /*}


    //LastMessage
    private static void setLastMessage(MessageUser messageUser, TribusFragmentView view, String name){

        /*if (messageUser != null && messageUser.getContentType() != null && view.mTvMessage != null) {
            switch (messageUser.getContentType()) {
                case "TEXT":
                case "LINK_INTO_MESSAGE":
                    String[] firstName = name.split(" ");
                    String appendNameAndMessage = firstName[0] + ": " + messageUser.getMessage();
                    view.mTvMessage.setText(appendNameAndMessage);
                    break;

                case "VOICE":
                    String[] firstNameAudio = name.split(" ");
                    String appendNameAndMessageAudio = firstNameAudio[0] + ": " + "Mensagem de Áudio";
                    view.mTvMessage.setText(appendNameAndMessageAudio);
                    break;

                case "IMAGE":
                    String[] firstNameImage = name.split(" ");
                    String appendNameAndMessageImage = firstNameImage[0] + ": " + "Mensagem com Imagem";
                    view.mTvMessage.setText(appendNameAndMessageImage);
                    break;

                case "VIDEO":
                    String[] firstNameVideo = name.split(" ");
                    String appendNameAndMessageVideo = firstNameVideo[0] + ": " + "Mensagem de Vídeo";
                    view.mTvMessage.setText(appendNameAndMessageVideo);
                    break;

                default: {
                    String emptyMessage = "Não há mensagem no momento.";
                    view.mTvMessage.setText(emptyMessage);
                    break;
                }
            }

        }
        else if((messageUser == null || messageUser.getContentType() == null) && view.mTvMessage != null) {
            String emptyMessage = "Não há mensagem no momento.";
            view.mTvMessage.setText(emptyMessage);
        }*/

    /*}


    public static Observable<Tribu> getTribuAdmin(User user) {
        return Observable.create(subscriber -> {
            mReferenceAdmin
                    .addValueEventListener(mValueListenerRefAdmin = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(user.getId())) {

                                mAdmin = dataSnapshot.child(user.getId()).getValue(Admin.class);

                                mReferenceTribus
                                        .child(mAdmin.getTribuUniqueName())
                                        .addValueEventListener(mValueListenerRefTribus2 = new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Tribu tribu = dataSnapshot.getValue(Tribu.class);
                                                subscriber.onNext(tribu);
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                databaseError.toException().printStackTrace();
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            databaseError.toException().printStackTrace();
                        }
                    });


        });
    }

    //OPEN PROFILE ADMIN - FOTO / NAME / USERNAME / ADMIN SINCE
    //SET NEW IMAGE
    private static void openImageTribu(Tribu tribu, Fragment fragmentContext) {

        //CREATE DIALOG TO SHOW NEW IMAGE
        //CONFIGURATION OF DIALOG
        AlertDialog.Builder builder = new AlertDialog.Builder(fragmentContext.getActivity(), R.style.MyDialogTheme);
        LayoutInflater inflater = fragmentContext.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_image_tribu, null);
        SimpleDraweeView mSdImageTribu = dialogView.findViewById(R.id.sd_image_tribu);
        TextView mTvNameOfTribu = dialogView.findViewById(R.id.tv_name_of_tribu);
        TextView mTvUniqueName = dialogView.findViewById(R.id.tv_unique_name);
        TextView mTvAdminSince = dialogView.findViewById(R.id.tv_created_date);


        mTvNameOfTribu.setText(tribu.getProfile().getNameTribu());
        mTvUniqueName.setText(tribu.getProfile().getUniqueName());
        builder.setView(dialogView);

        /*if (tribu.getTimestampCreated() != null) {
            SimpleDateFormat sfd = new SimpleDateFormat("dd/MM", Locale.getDefault());
            String time = sfd.format(new Date(tribu.getTimestampCreatedLong()));

            String appendTime = "Criada em " + time;
            mTvAdminSince.setText(appendTime);

        } else {*/
            /*String time = GetTimeAgo.getTimeAgo(tribu.getProfile().getCreationDate(), fragmentContext.getActivity());
            String append = "Criada ";
            String appendDate = append + time;
            mTvAdminSince.setText(appendDate);
        //}

        ControllerListener listener = new BaseControllerListener() {
            @Override
            public void onFailure(String id, Throwable throwable) {
                super.onFailure(id, throwable);
                //Log.d("Valor: ", "onFailure - View: " + throwable.toString());

            }

            @Override
            public void onIntermediateImageFailed(String id, Throwable throwable) {
                super.onIntermediateImageFailed(id, throwable);
            }

            @Override
            public void onFinalImageSet(String id, Object imageInfo, Animatable animatable) {
                super.onFinalImageSet(id, imageInfo, animatable);
            }

            @Override
            public void onIntermediateImageSet(String id, Object imageInfo) {
                super.onIntermediateImageSet(id, imageInfo);
            }

            @Override
            public void onRelease(String id) {
                super.onRelease(id);
            }

            @Override
            public void onSubmit(String id, Object callerContext) {
                super.onSubmit(id, callerContext);
                //Log.d("Valor: ", "onSubmit");

            }
        };
        DraweeController dc = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse(tribu.getProfile().getImageUrl()))
                .setControllerListener(listener)
                .setOldController(mSdImageTribu.getController())
                .build();
        mSdImageTribu.setController(dc);

        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams wmlp = dialog.getWindow()
                .getAttributes();
        wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        wmlp.gravity = WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;
        dialog.getWindow().setGravity(wmlp.gravity);


        dialog.show();

    }

*/


