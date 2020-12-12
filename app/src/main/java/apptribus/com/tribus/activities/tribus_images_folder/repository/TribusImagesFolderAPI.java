package apptribus.com.tribus.activities.tribus_images_folder.repository;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.chat_tribu.adapter.ChatTribuAdapter;
import apptribus.com.tribus.activities.tribus_images_folder.adapter.TribusImagesFolderAdapter;
import apptribus.com.tribus.activities.tribus_images_folder.adapter.TribusVideosFolderAdapter;
import apptribus.com.tribus.activities.tribus_images_folder.view_holder.TribusImagesFolderViewHolder;
import apptribus.com.tribus.pojo.ConversationTopic;
import apptribus.com.tribus.pojo.MessageUser;
import apptribus.com.tribus.pojo.Talk;
import apptribus.com.tribus.pojo.Tribu;
import rx.Observable;

import static android.view.View.*;
import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.TRIBUS_MESSAGES;
import static apptribus.com.tribus.util.Constantes.TRIBUS_TOPICS;

/**
 * Created by User on 9/23/2017.
 */

public class TribusImagesFolderAPI {

    //INSTANCES - FIREBASE
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private static FirebaseStorage mStorage = FirebaseStorage.getInstance();


    //REFERENCES - FIREBASE
    private static DatabaseReference mReferenceTribu = mDatabase.getReference().child(GENERAL_TRIBUS);
    public static DatabaseReference mRefTribusMessage = mDatabase.getReference().child(TRIBUS_MESSAGES);
    private static DatabaseReference mRefTribusTopics = mDatabase.getReference().child(TRIBUS_TOPICS);


    public static ChildEventListener mChildListenerImages;
    public static ChildEventListener mChildListenerVideos;

    //VARIABLES
    public static Tribu mTribu;
    private static FirebaseRecyclerAdapter<MessageUser, TribusImagesFolderViewHolder> mAdapter;



    //GET TRIBU
    public static Observable<Tribu> getTribu(String uniqueName) {

        return Observable.create(subscriber -> mReferenceTribu
                .child(uniqueName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mTribu = dataSnapshot.getValue(Tribu.class);
                        subscriber.onNext(mTribu);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        databaseError.toException().printStackTrace();
                    }
                }));
    }


    //LOAD IMAGES
    public static void loadImages(String mTribusKey, TribusImagesFolderAdapter mTribusImagesFolderAdapter, List<MessageUser> mMessagesList,
                                  RecyclerView mRvTribusImagesFolder, int totalItemsToLoad, int mCurrentPage){

        mMessagesList.clear();

        DatabaseReference messageRef = mRefTribusMessage.child(mTribusKey);
        DatabaseReference messageRefTopics = mRefTribusTopics.child(mTribusKey);

        //Query messageQuery = messageRef.limitToLast(mCurrentPage * totalItemsToLoad);

        messageRefTopics
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChildren()){

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                ConversationTopic conversationTopic = snapshot.getValue(ConversationTopic.class);

                                messageRef
                                        .child(conversationTopic.getKey())
                                        .addChildEventListener(mChildListenerImages = new ChildEventListener() {
                                            @Override
                                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                                MessageUser message = dataSnapshot.getValue(MessageUser.class);

                                                if (message.getContentType().equals("IMAGE")) {

                                                    mMessagesList.add(message);
                                                    mTribusImagesFolderAdapter.notifyDataSetChanged();
                                                }
                                                //mRvTribusImagesFolder.scrollToPosition(mMessagesList.size() - 1);
                                            }

                                            @Override
                                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                                MessageUser message = dataSnapshot.getValue(MessageUser.class);

                                                if (message.getContentType().equals("IMAGE")) {
                                                    int index = getItemIndex(mMessagesList, message);
                                                    mMessagesList.set(index, message);
                                                    mTribusImagesFolderAdapter.notifyItemChanged(index);
                                                    //mRvTribusImagesFolder.scrollToPosition(mMessagesList.size() - 1);
                                                }
                                            }

                                            @Override
                                            public void onChildRemoved(DataSnapshot dataSnapshot) {

                                            }

                                            @Override
                                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                databaseError.toException().printStackTrace();
                                            }

                                });
                            }
                        }




                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        databaseError.toException().printStackTrace();
                    }
                });

    }


    public static void loadMessagesSwipe(String mTribusKey, ChatTribuAdapter mChatTribuAdapter, List<MessageUser> mMessagesList,
                                         SwipeRefreshLayout mSwipeLayout, int totalItemsToLoad, int mCurrentPage){

        mMessagesList.clear();

        DatabaseReference messageRef = mRefTribusMessage.child(mTribusKey);

        Query messageQuery = messageRef.limitToLast(mCurrentPage * totalItemsToLoad);

        messageQuery
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        MessageUser message = dataSnapshot.getValue(MessageUser.class);

                        mMessagesList.add(message);
                        mChatTribuAdapter.notifyDataSetChanged();
                        mSwipeLayout.setRefreshing(false);
                        //mRvChat.scrollToPosition(mMessagesList.size() - 1);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        MessageUser message = dataSnapshot.getValue(MessageUser.class);

                        int index = getItemIndex(mMessagesList, message);
                        mMessagesList.set(index, message);
                        mChatTribuAdapter.notifyItemChanged(index);
                        //mRvChat.scrollToPosition(mMessagesList.size() - 1);
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private static int getItemIndex(List<MessageUser> messageUserList, MessageUser message){
        int index = -1;

        for(int i = 0; i < messageUserList.size(); i++){
            if(messageUserList.get(i).getKey().equals(message.getKey())){
                index = i;
                break;
            }
        }
        return index;
    }


    //LOAD VIDEOS
    public static void loadVideos(String mTribusKey, TribusVideosFolderAdapter mTribusVideosFolderAdapter, List<MessageUser> mMessagesList,
                                  RecyclerView mRvTribusImagesFolder, int totalItemsToLoad, int mCurrentPage){

        mMessagesList.clear();

        DatabaseReference messageRef = mRefTribusMessage.child(mTribusKey);
        DatabaseReference messageRefTopics = mRefTribusTopics.child(mTribusKey);

        //Query messageQuery = messageRef.limitToLast(mCurrentPage * totalItemsToLoad);

        messageRefTopics
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChildren()){

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                ConversationTopic conversationTopic = snapshot.getValue(ConversationTopic.class);

                                messageRef
                                        .child(conversationTopic.getKey())
                                        .addChildEventListener(mChildListenerVideos = new ChildEventListener() {
                                            @Override
                                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                                MessageUser message = dataSnapshot.getValue(MessageUser.class);

                                                if (message.getContentType().equals("VIDEO")) {

                                                    mMessagesList.add(message);
                                                    mTribusVideosFolderAdapter.notifyDataSetChanged();
                                                }
                                                //mRvTribusImagesFolder.scrollToPosition(mMessagesList.size() - 1);
                                            }

                                            @Override
                                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                                MessageUser message = dataSnapshot.getValue(MessageUser.class);

                                                if (message.getContentType().equals("VIDEO")) {
                                                    int index = getItemIndex(mMessagesList, message);
                                                    mMessagesList.set(index, message);
                                                    mTribusVideosFolderAdapter.notifyItemChanged(index);
                                                    //mRvTribusImagesFolder.scrollToPosition(mMessagesList.size() - 1);
                                                }
                                            }

                                            @Override
                                            public void onChildRemoved(DataSnapshot dataSnapshot) {

                                            }

                                            @Override
                                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                databaseError.toException().printStackTrace();
                                            }

                                        });
                            }
                        }




                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        databaseError.toException().printStackTrace();
                    }
                });
    }



    public static FirebaseRecyclerAdapter<MessageUser, TribusImagesFolderViewHolder> getImages(String mTribusKey){
        return mAdapter = new FirebaseRecyclerAdapter<MessageUser, TribusImagesFolderViewHolder>(
                MessageUser.class,
                R.layout.row_tribus_images_folder,
                TribusImagesFolderViewHolder.class,
                mRefTribusMessage.child(mTribusKey).orderByKey()
        ) {
            @Override
            protected void populateViewHolder(TribusImagesFolderViewHolder viewHolder, MessageUser messageUser, int position) {

                viewHolder.initTribusImageFolderVH(messageUser, mTribusKey);
                /*viewHolder.mFrameFolder.setVisibility(GONE);

                if(messageUser.getContentType().equals("IMAGE")){
                    viewHolder.mFrameFolder.setVisibility(VISIBLE);

                    viewHolder.setImage(messageUser.getImage().getDownloadUri());
                    viewHolder.setTvTime(messageUser.getTimestampCreatedLong());
                }*/

            }
        };
    }

}
