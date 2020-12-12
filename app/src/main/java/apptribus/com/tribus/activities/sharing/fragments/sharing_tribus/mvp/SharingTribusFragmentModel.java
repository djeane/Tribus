package apptribus.com.tribus.activities.sharing.fragments.sharing_tribus.mvp;


import android.support.v4.app.Fragment;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import apptribus.com.tribus.activities.sharing.fragments.sharing_tribus.repository.SharingTribuAPI;
import apptribus.com.tribus.activities.sharing.view_holder.SharingTribusViewHolder;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import rx.Observable;

/**
 * Created by User on 1/15/2018.
 */

public class SharingTribusFragmentModel {

    private final Fragment fragment;

    public SharingTribusFragmentModel(Fragment fragment) {
        this.fragment = fragment;
    }

    //Get the current talker
    public Observable<User> getUser() {
        return SharingTribuAPI.getUser();
    }

    //SET ADAPTER TO SHOW LIST OF TALKERS
    public FirebaseRecyclerAdapter<Tribu, SharingTribusViewHolder> setRecyclerViewTribus(String mLink){
        return SharingTribuAPI.getTribus(fragment, mLink);
    }

    public Observable<Boolean> hasChildrenTribus(){
        return SharingTribuAPI.hasChildrenTribus();
    }
}
