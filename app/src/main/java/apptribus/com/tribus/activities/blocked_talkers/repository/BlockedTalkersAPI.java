package apptribus.com.tribus.activities.blocked_talkers.repository;

import android.content.Context;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.blocked_talkers.view_holder.BlockedTalkersViewHolder;
import apptribus.com.tribus.pojo.Talk;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.BLOCKED_TALKERS;
import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;

public class BlockedTalkersAPI {

    //INSTANCES - FIREBASE
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private static FirebaseStorage mStorage = FirebaseStorage.getInstance();

    //REFERENCES - FIREBASE
    private static DatabaseReference mReferenceUser = mDatabase.getReference().child(GENERAL_USERS);
    private static DatabaseReference mRefBlockedTalkers = mDatabase.getReference().child(BLOCKED_TALKERS);
    private static DatabaseReference mReferenceTribus = mDatabase.getReference().child(GENERAL_TRIBUS);


    //VARIABLES
    private static User mUser;

    //GET USER(CURRENT TALKER)
    public static Observable<User> getUser() {
        String uid = mAuth.getCurrentUser().getUid();

        return Observable.create(subscriber ->
                mReferenceUser.child(uid)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                mUser = dataSnapshot.getValue(User.class);
                                subscriber.onNext(mUser);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        }));
    }


    public static Observable<Boolean> hasChildren() {
        return Observable.create(subscriber -> {
            mRefBlockedTalkers.child(mAuth.getCurrentUser().getUid())
                    .addValueEventListener(new ValueEventListener() {
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

                        }
                    });
        });

    }


    public static FirebaseRecyclerAdapter<Talk, BlockedTalkersViewHolder> getTalkers(Context activity) {

        return new FirebaseRecyclerAdapter<Talk, BlockedTalkersViewHolder>(
                Talk.class,
                R.layout.row_blocked_talkers,
                BlockedTalkersViewHolder.class,
                mRefBlockedTalkers.child(mUser.getId())
        ) {
            @Override
            protected void populateViewHolder(BlockedTalkersViewHolder viewHolder, Talk talker, int position) {
                long talkerKey = getItemId(position);

                mReferenceUser
                        .child(talker.getTalkerId())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                User userTalker = dataSnapshot.getValue(User.class);

                                mReferenceTribus
                                        //.child(talker.getUniqueNameTribu())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                Tribu tribu = dataSnapshot.getValue(Tribu.class);

                                                viewHolder.setTvTalkersName(userTalker.getName());
                                                viewHolder.setTvUsernameTalkers(userTalker.getUsername());
                                                viewHolder.setImageTalker(userTalker.getImageUrl());
                                                viewHolder.setImageTribu(tribu.getProfile().getImageUrl());
                                                viewHolder.setTvNameOfTribu(tribu.getProfile().getNameTribu());
                                                viewHolder.setTvUniqueOfTribu(tribu.getProfile().getUniqueName());
                                                //viewHolder.setTvBlockedTalkerSince(talker.getTimestampCreatedLong());


                                                viewHolder.mBtnUnblock.setOnClickListener(v -> {

                                                    unBlockTalker(mAuth.getCurrentUser().getUid(), talker, activity, talkerKey);

                                                });

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


            }
        };
    }

    private static void unBlockTalker(String uid, Talk talker, Context activity, long talkerKey) {
        if (talkerKey != 0) {
            mRefBlockedTalkers
                    .child(uid)
                    .child(talker.getTalkerId())
                    .removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(activity.getApplicationContext(), "Contato desbloqueado!", Toast.LENGTH_LONG)
                                .show();
                    });
        }
    }

}
