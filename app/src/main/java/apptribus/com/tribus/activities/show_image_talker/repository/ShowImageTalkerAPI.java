package apptribus.com.tribus.activities.show_image_talker.repository;

import android.app.ProgressDialog;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import apptribus.com.tribus.activities.chat_tribu.view_holder.UserMessageViewHolder;
import apptribus.com.tribus.pojo.MessageUser;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.TALKERS_MESSAGES;

/**
 * Created by User on 8/16/2017.
 */

public class ShowImageTalkerAPI {

    //INSTANCES - FIREBASE
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

    //REFERENCES - FIREBASE
    private static DatabaseReference mReferenceUser = mDatabase.getReference().child(GENERAL_USERS);
    private static DatabaseReference mRefUsersTalkMessage = mDatabase.getReference().child(TALKERS_MESSAGES);


    //VARIABLES
    private static FirebaseRecyclerAdapter<MessageUser, UserMessageViewHolder> messagesChat;
    public static Tribu tribu;
    private static User mUser;
    private static long mTribusKey;
    private static ProgressDialog progress;
    private static User mTalkerUser;
    private static MessageUser mMessageUser;



    //GET TALKER
    public static Observable<User> getTalkerUser(String talkerId) {
        return Observable.create(subscriber ->
                mReferenceUser
                        .child(talkerId)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                mTalkerUser = dataSnapshot.getValue(User.class);
                                subscriber.onNext(mTalkerUser);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                databaseError.toException().printStackTrace();
                            }
                        }));
    }


    //GET MESSAGE
    public static Observable<MessageUser> getMessage(String mTalkerKey, String messageReference){
        return Observable.create(subscriber -> {
            mRefUsersTalkMessage
                    .child(mAuth.getCurrentUser().getUid())
                    .child(mTalkerKey)
                    .child(messageReference)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mMessageUser = dataSnapshot.getValue(MessageUser.class);
                            subscriber.onNext(mMessageUser);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            databaseError.toException().printStackTrace();
                        }
                    });

        });
    }
}
