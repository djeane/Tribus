package apptribus.com.tribus.activities.profile_tribu_follower.repository;

import android.widget.ProgressBar;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import apptribus.com.tribus.activities.profile_tribu_follower.adapter.ProfileTribuFollowerAdapter;
import apptribus.com.tribus.pojo.Follower;
import rx.Observable;

import static android.view.View.GONE;
import static apptribus.com.tribus.util.Constantes.DATE;
import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.TRIBUS_PARTICIPANTS;

public class ProfileTribuFollowerAPI {

    //FIRESTORE INSTANCE
    private static FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    //FIRESTORE COLLECTIONS REFERENCES
    private static CollectionReference mTribusCollection = mFirestore.collection(GENERAL_TRIBUS);
    private static CollectionReference mUsersCollection = mFirestore.collection(GENERAL_USERS);

    //OBJECTS
    private static DocumentSnapshot mLastFollowerVisible = null;
    private static List<DocumentSnapshot> mListDocSnapshotAllFollowers;
    private static List<DocumentSnapshot> mListDocSnapshotAllFollowersClear;


    public static Observable<List<Follower>> getAllFollowers(List<Follower> followers, String tribuKey) {

        Query firstQuery = mTribusCollection
                .document(tribuKey)
                .collection(TRIBUS_PARTICIPANTS)
                .orderBy(DATE, Query.Direction.DESCENDING)
                .limit(4);

        if (mListDocSnapshotAllFollowers == null) {
            mListDocSnapshotAllFollowers = new ArrayList<>();
            mListDocSnapshotAllFollowersClear = new ArrayList<>();
        } else {
            mListDocSnapshotAllFollowers.clear();
            mListDocSnapshotAllFollowersClear.clear();
        }


        return Observable.create(subscriber ->

                firstQuery
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {

                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                followers.add(documentSnapshot.toObject(Follower.class));
                                mListDocSnapshotAllFollowers.add(documentSnapshot);


                            }

                            if (queryDocumentSnapshots.size() == 1) {
                                mLastFollowerVisible = mListDocSnapshotAllFollowers.get(mListDocSnapshotAllFollowers.size());
                            }
                            else if (queryDocumentSnapshots.size() > 1){

                                mLastFollowerVisible = mListDocSnapshotAllFollowers.get(mListDocSnapshotAllFollowers.size() - 1);
                            }

                            subscriber.onNext(followers);
                        })
                        .addOnFailureListener(e -> {

                            subscriber.onNext(null);

                        }));

    }

    public static void loadMoreFollowers(List<Follower> followers, ProfileTribuFollowerAdapter profileTribuFollowerAdapter,
                                         ProgressBar mProgressBarBottom, String tribuKey) {

        if (mLastFollowerVisible != null) {
            Query nextQuery = mTribusCollection
                    .document(tribuKey)
                    .collection(TRIBUS_PARTICIPANTS)
                    .orderBy(DATE, Query.Direction.DESCENDING)
                    .startAfter(mLastFollowerVisible)
                    .limit(4);

            nextQuery
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            mListDocSnapshotAllFollowersClear.clear();

                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                if (!mListDocSnapshotAllFollowers.contains(documentSnapshot)) {


                                    mListDocSnapshotAllFollowers.add(documentSnapshot);
                                    mListDocSnapshotAllFollowersClear.add(documentSnapshot);


                                }
                            }

                            mLastFollowerVisible = mListDocSnapshotAllFollowers
                                    .get(mListDocSnapshotAllFollowers.size() - 1);


                            for (DocumentSnapshot documentSnapshot : mListDocSnapshotAllFollowersClear) {

                                followers.add(documentSnapshot.toObject(Follower.class));


                            }

                            profileTribuFollowerAdapter.notifyDataSetChanged();
                            mProgressBarBottom.setVisibility(GONE);

                        } else {
                            mProgressBarBottom.setVisibility(GONE);
                            mLastFollowerVisible = null;

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
