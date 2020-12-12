package apptribus.com.tribus.activities.show_video.repository;

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

import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.TRIBUS_MESSAGES;

/**
 * Created by User on 7/20/2017.
 */

public class ShowVideoAPI {

    //INSTANCES - FIREBASE
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

    //REFERENCES - FIREBASE
    private static DatabaseReference mReferenceTribu = mDatabase.getReference().child(GENERAL_TRIBUS);
    private static DatabaseReference mRefTribusMessage = mDatabase.getReference().child(TRIBUS_MESSAGES);


    //VARIABLES
    private static FirebaseRecyclerAdapter<MessageUser, UserMessageViewHolder> messagesChat;
    public static Tribu tribu;
    private static User mUser;
    private static long mTribusKey;
    private static ProgressDialog progress;
    private static MessageUser mMessageUser;


    //GET TRIBU
    public static Observable<Tribu> getTribu(String uniqueName) {
        return Observable.create(subscriber -> mReferenceTribu
                .child(uniqueName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        tribu = dataSnapshot.getValue(Tribu.class);
                        subscriber.onNext(tribu);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        databaseError.toException().printStackTrace();
                    }
                }));
    }


    //GET MESSAGE
    public static Observable<MessageUser> getMessage(String uniqueName, String messageReference){
        return Observable.create(subscriber -> {
            mRefTribusMessage
                    .child(uniqueName)
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
