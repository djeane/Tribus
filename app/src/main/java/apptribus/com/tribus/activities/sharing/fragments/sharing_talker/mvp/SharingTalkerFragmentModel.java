package apptribus.com.tribus.activities.sharing.fragments.sharing_talker.mvp;

import android.support.v4.app.Fragment;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import apptribus.com.tribus.activities.sharing.fragments.sharing_talker.repository.SharingTalkerFragmentAPI;
import apptribus.com.tribus.activities.sharing.view_holder.SharingTalkersViewHolder;
import apptribus.com.tribus.pojo.Talk;
import apptribus.com.tribus.pojo.User;
import rx.Observable;

/**
 * Created by User on 1/15/2018.
 */

public class SharingTalkerFragmentModel {

    private final Fragment fragment;

    public SharingTalkerFragmentModel(Fragment fragment) {
        this.fragment = fragment;
    }

    //OBSERVABLES
    //Get the current talker
    public Observable<User> getUser() {
        return SharingTalkerFragmentAPI.getUser();
    }

    public Observable<Boolean> hasChildrenTalkes(){
        return SharingTalkerFragmentAPI.hasChildrenTalkers();
    }

    //SET ADAPTER TO SHOW LIST OF TALKERS
    public FirebaseRecyclerAdapter<Talk, SharingTalkersViewHolder> setRecyclerViewTalkers(String mLink){
        return SharingTalkerFragmentAPI.getTalkers(fragment, mLink);
    }


}
