package apptribus.com.tribus.activities.sharing.fragments.sharing_talker.repository;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.sharing.fragments.sharing_talker.mvp.SharingTalkerFragmentView;
import apptribus.com.tribus.activities.sharing.mvp.SharingView;
import apptribus.com.tribus.activities.sharing.view_holder.SharingTalkersViewHolder;
import apptribus.com.tribus.pojo.Talk;
import apptribus.com.tribus.pojo.User;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.USERS_TALKS;

/**
 * Created by User on 1/15/2018.
 */

public class SharingTalkerFragmentAPI {

    //INSTANCES - FIREBASE
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private static FirebaseStorage mStorage = FirebaseStorage.getInstance();

    //REFERENCES - FIREBASE
    private static DatabaseReference mReferenceUser = mDatabase.getReference().child(GENERAL_USERS);
    private static DatabaseReference mRefTalkers = mDatabase.getReference().child(USERS_TALKS);

    private static FirebaseRecyclerAdapter<Talk, SharingTalkersViewHolder> mAdapterTalkers;

    //VARIABLES
    private static User mUser;

    //GET USER(CURRENT TALKER)
    public static Observable<User> getUser() {
        String uid = mAuth.getCurrentUser().getUid();

        return Observable.create(subscriber ->
                mReferenceUser
                        .child(uid)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
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

    public static FirebaseRecyclerAdapter<Talk, SharingTalkersViewHolder> getTalkers(Fragment fragment, String mLink){

        return mAdapterTalkers = new FirebaseRecyclerAdapter<Talk, SharingTalkersViewHolder>(
                Talk.class,
                R.layout.row_sharing_talks,
                SharingTalkersViewHolder.class,
                mRefTalkers.child(mAuth.getCurrentUser().getUid())
        ) {
            @Override
            protected void populateViewHolder(SharingTalkersViewHolder viewHolder, Talk talkerParam, int position) {

                viewHolder.initSharingTalkersVH(talkerParam, fragment, mLink);
                /*mReferenceUser
                        .child(talkerParam.getTalkerId())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                User talkerUser = dataSnapshot.getValue(User.class);

                                if(talkerUser != null) {
                                    viewHolder.setImage(talkerUser.getImageUrl());
                                    viewHolder.setTvName(talkerUser.getName());
                                    viewHolder.setTvUsername(talkerUser.getUsername());

                                    viewHolder.mCardView.setOnClickListener(v -> {
                                        openChatTalkerActivity(talkerUser, view.mLink, activity);
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                databaseError.toException().printStackTrace();
                            }
                        });*/

            }
        };
    }


    //HAS CHILDREN
    public static Observable<Boolean> hasChildrenTalkers() {
        return Observable.create(subscriber -> {
            mRefTalkers.child(mAuth.getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()) {
                                subscriber.onNext(true);
                            } else {
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

}
