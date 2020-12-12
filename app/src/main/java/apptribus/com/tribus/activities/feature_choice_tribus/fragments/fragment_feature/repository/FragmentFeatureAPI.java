package apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_feature.repository;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import rx.Observable;

import static apptribus.com.tribus.util.Constantes.ADMIN_PERMISSION;
import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;

public class FragmentFeatureAPI {


    private static FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private static CollectionReference mTribusCollection = mFirestore.collection(GENERAL_TRIBUS);


    public static Observable<Integer> getFollowersInvitationNumber(String tribuKey) {

        return Observable.create(subscriber -> {

            mTribusCollection
                    .document(tribuKey)
                    .collection(ADMIN_PERMISSION)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        if (queryDocumentSnapshots == null || queryDocumentSnapshots.isEmpty()) {
                            subscriber.onNext(0);
                        } else {
                            subscriber.onNext(queryDocumentSnapshots.getDocuments().size());
                        }

                    })
                    .addOnFailureListener(Throwable::printStackTrace);

        });

    }


}
