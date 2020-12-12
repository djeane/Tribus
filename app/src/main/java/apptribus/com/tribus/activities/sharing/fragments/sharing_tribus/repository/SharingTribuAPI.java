package apptribus.com.tribus.activities.sharing.fragments.sharing_tribus.repository;

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
import apptribus.com.tribus.activities.sharing.fragments.sharing_tribus.mvp.SharingTribusFragmentView;
import apptribus.com.tribus.activities.sharing.mvp.SharingView;
import apptribus.com.tribus.activities.sharing.view_holder.SharingTribusViewHolder;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.FOLLOWERS;
import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;

/**
 * Created by User on 1/15/2018.
 */

public class SharingTribuAPI {

    //INSTANCES - FIREBASE
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private static FirebaseStorage mStorage = FirebaseStorage.getInstance();

    //REFERENCES - FIREBASE
    private static DatabaseReference mReferenceTribus = mDatabase.getReference().child(GENERAL_TRIBUS);
    private static DatabaseReference mReferenceFollowers = mDatabase.getReference().child(FOLLOWERS);
    private static DatabaseReference mReferenceUser = mDatabase.getReference().child(GENERAL_USERS);
    private static FirebaseRecyclerAdapter<Tribu, SharingTribusViewHolder> mAdapterTribus;

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

    //SET RECYCLER VIEW
    public static FirebaseRecyclerAdapter<Tribu, SharingTribusViewHolder> getTribus(Fragment fragment, String mLink){
        return mAdapterTribus = new FirebaseRecyclerAdapter<Tribu, SharingTribusViewHolder>(
                Tribu.class,
                R.layout.row_sharing_tribus,
                SharingTribusViewHolder.class,
                mReferenceFollowers.child(mAuth.getCurrentUser().getUid())
        ) {
            @Override
            protected void populateViewHolder(SharingTribusViewHolder viewHolder, Tribu tribuParam, int position) {

                viewHolder.initSharingTribusVH(tribuParam, mLink, fragment);

                /*mReferenceTribus
                        .child(tribuParam.getUniqueNameTribu())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                Tribu mTribu = dataSnapshot.getValue(Tribu.class);

                                if(mTribu != null){
                                    viewHolder.setImage(mTribu.getProfile().getImageUrl());
                                    viewHolder.setTvNameTribu(mTribu.getProfile().getNameTribu());
                                    viewHolder.setTvUniqueName(mTribu.getProfile().getUniqueName());


                                    viewHolder.mCardView.setOnClickListener(v -> {
                                        //openConversationTopicActivity(mTribu, view.mLink, activity);
                                        openConversationTopicActivity(mTribu, view.mLink, activity);
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

    public static Observable<Boolean> hasChildrenTribus() {
        return Observable.create(subscriber -> {
            mReferenceFollowers
                    .child(mAuth.getCurrentUser().getUid())
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
