package apptribus.com.tribus.activities.main_activity.fragment_tribus.mvp;

import android.support.v4.app.Fragment;
import android.widget.ProgressBar;

import java.util.List;

import apptribus.com.tribus.activities.main_activity.fragment_tribus.adapter.TribusFragmentAdapter;
import apptribus.com.tribus.activities.main_activity.fragment_tribus.repository.TribusFragmentAPI;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.util.firestore.FirestoreService;
import rx.Observable;

/**
 * Created by User on 6/7/2017.
 */

public class TribusFragmentModel {

    public final Fragment fragment;

    private FirestoreService mFirestoreService;

    public TribusFragmentModel(Fragment fragment) {
        this.fragment = fragment;
        mFirestoreService = new FirestoreService(fragment.getActivity());
    }

    //POPULATE RV THEMATICS
    //followed tribus
    public Observable<List<String>> getFollowedTribusThematics(List<String> tribuList){
        return TribusFragmentAPI.getFollowedThematicsToPopulateRv(tribuList);
    }
    //removed tribus
    public Observable<List<String>> getRemovedTribusThematics(List<String> tribuList){
        return TribusFragmentAPI.getRemovedThematicsToPopulateRv(tribuList);
    }


    public Observable<List<Tribu>> getAllTribus(List<Tribu> tribus){
        return TribusFragmentAPI.getAllTribus(tribus);
    }

    public void loadMoreTribus(List<Tribu> tribus, TribusFragmentAdapter mTribusFragmentAdapter, ProgressBar mProgressBarBottom){
        TribusFragmentAPI.loadMoreTribus(tribus, mTribusFragmentAdapter, mProgressBarBottom);
    }

    public Observable<List<Tribu>> getTribusByThematics(List<Tribu> tribus, String thematic){
        return TribusFragmentAPI.getTribusByThematics(tribus, thematic);
    }

    public void loadMoreTribusByThematics(List<Tribu> tribus, String thematic, TribusFragmentAdapter mTribusFragmentAdapter,
                                          TribusFragmentView view){
        TribusFragmentAPI.loadMoreTribusByThematics(tribus, thematic, mTribusFragmentAdapter, view);
    }
















    //OLD CODE
    /*public final Fragment fragment;

    private FirestoreService mFirestoreService;

    public TribusFragmentModel(Fragment fragment) {
        this.fragment = fragment;
        mFirestoreService = new FirestoreService(fragment.getActivity());
    }

    //GET ADMIN USER
    public Observable<User> getUser(){
        return TribusFragmentAPI.getUser();
    }

    //GET CURRENT USER
    public Observable<User> getCurrentUser(){
        return TribusFragmentAPI.getUser();
    }

    //POPULATE RV THEMATICS
    //followed tribus
    public Observable<List<String>> getFollowedTribusThematics(List<String> tribuList){
        return TribusFragmentAPI.getFollowedTribusThematics(tribuList);
    }


    //invited tribus
    public Observable<List<String>> getInvitedTribusThematics(List<String> tribuList){
        return TribusFragmentAPI.getInvitedTribusThematics(tribuList);
    }

    //removed tribus
    public Observable<List<String>> getRemovedTribusThematics(List<String> tribuList){
        return TribusFragmentAPI.getRemovedTribusThematics(tribuList);
    }

    //ended tribus
    public Observable<List<String>> getEndedTribusThematics(List<String> tribuList){
        return TribusFragmentAPI.getEndedTribusThematics(tribuList);
    }


    //SET ADAPTER TO SHOW LIST OF TRIBUS
    public FirebaseRecyclerAdapter<Tribu, TribusFragmentViewHolder> setAdapter(User user){
        return TribusFragmentAPI.getTribus(fragment, user, mFirestoreService);
    }

    public Observable<Boolean> hasChildren(){
        return TribusFragmentAPI.hasChildren();
    }

    //GET ADMIN
    public Observable<Tribu> getTribuAdmin(User user){
        return TribusFragmentAPI.getTribuAdmin(user);
    }


    public void getMessagesTribuAdmin(Tribu tribuAdmin, TribusFragmentView view){
        TribusFragmentAPI.getMessagesTribuAdmin(tribuAdmin, view, fragment);
    }

    public void getTopicMessages(Tribu tribuAdmin, TribusFragmentView view){
        TribusFragmentAPI.getTopicsMessage(tribuAdmin, view, fragment);
    }


    //OPEN CHAT TRIBU - ADMIN
    public void openConversationTopicActivity(Tribu tribu){
        Intent intent = new Intent(fragment.getActivity(), ConversationTopicsActivity.class);
        intent.putExtra(TRIBU_UNIQUE_NAME, tribu.getProfile().getUniqueName());
        fragment.getActivity().startActivity(intent);

    }

    public void removeListeners(){
        if (TribusFragmentAPI.mValueListenerRefUser != null){
            TribusFragmentAPI.mReferenceUsers.removeEventListener(TribusFragmentAPI.mValueListenerRefUser);
        }
        if (TribusFragmentAPI.mValueListenerRefCurrentUser != null){
            TribusFragmentAPI.mReferenceUsers.removeEventListener(TribusFragmentAPI.mValueListenerRefCurrentUser);
        }
        if (TribusFragmentAPI.mValueListenerRefFollowers != null){
            TribusFragmentAPI.mReferenceFollowers.removeEventListener(TribusFragmentAPI.mValueListenerRefFollowers);
        }
        if (TribusFragmentAPI.mValueListenerRefTribus != null){
            TribusFragmentAPI.mReferenceTribus.removeEventListener(TribusFragmentAPI.mValueListenerRefTribus);
        }
        if (TribusFragmentAPI.mValueListenerRefTopics != null){
            TribusFragmentAPI.mReferenceTopics.removeEventListener(TribusFragmentAPI.mValueListenerRefTopics);
        }
        if (TribusFragmentAPI.mValueListenerRefMessagesTribus != null){
            TribusFragmentAPI.mReferenceMessagesTribus.removeEventListener(TribusFragmentAPI.mValueListenerRefMessagesTribus);
        }
        if (TribusFragmentAPI.mValueListenerRefTopics2 != null){
            TribusFragmentAPI.mReferenceTopics.removeEventListener(TribusFragmentAPI.mValueListenerRefTopics2);
        }
        if (TribusFragmentAPI.mValueListenerRefMessagesTribus2 != null){
            TribusFragmentAPI.mReferenceMessagesTribus.removeEventListener(TribusFragmentAPI.mValueListenerRefMessagesTribus2);
        }
        if (TribusFragmentAPI.mValueListenerRefAdmin != null){
            TribusFragmentAPI.mReferenceAdmin.removeEventListener(TribusFragmentAPI.mValueListenerRefAdmin);
        }
        if (TribusFragmentAPI.mValueListenerRefTribus2 != null){
            TribusFragmentAPI.mReferenceTribus.removeEventListener(TribusFragmentAPI.mValueListenerRefTribus2);
        }
    }*/
}
