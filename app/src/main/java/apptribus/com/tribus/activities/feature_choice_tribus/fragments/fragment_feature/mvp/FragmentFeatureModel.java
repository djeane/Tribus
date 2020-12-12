package apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_feature.mvp;

import android.support.v4.app.Fragment;

import apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_feature.repository.FragmentFeatureAPI;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.util.firestore.FirestoreService;
import rx.Observable;

public class FragmentFeatureModel {

    private final Fragment fragment;
    private FirestoreService mFirestoreService;

    public FragmentFeatureModel(Fragment fragment) {
        this.fragment = fragment;
        mFirestoreService = new FirestoreService(fragment.getActivity());
    }

    public Observable<Tribu> getTribu(String tribuKey) {
        return mFirestoreService.getTribu(tribuKey);
    }

    public Observable<Integer> getFollowersInvitationNum(String tribuKey) {
        return FragmentFeatureAPI.getFollowersInvitationNumber(tribuKey);
    }
}
