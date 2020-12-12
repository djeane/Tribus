package apptribus.com.tribus.activities.comments.repository;

import android.app.ProgressDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Map;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.comments.view_pager.CommentsViewHolder;
import apptribus.com.tribus.pojo.Comment;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import rx.Observable;

import static android.view.View.*;
import static apptribus.com.tribus.util.Constantes.DISLIKES_TRIBUS_COMMENTS;
import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.LIKES_TRIBUS_COMMENTS;
import static apptribus.com.tribus.util.Constantes.TRIBUS_COMMENTS;

public class CommentsAPI {

    //FIREBASE INSTANCES
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private static FirebaseStorage mStorage = FirebaseStorage.getInstance();

    //REFERENCES - FIREBASE
    private static DatabaseReference mReferenceTribu = mDatabase.getReference().child(GENERAL_TRIBUS);
    private static DatabaseReference mReferenceUser = mDatabase.getReference().child(GENERAL_USERS);
    private static DatabaseReference mReferenceTribus = mDatabase.getReference().child(GENERAL_TRIBUS);
    private static DatabaseReference mTribusComments = mDatabase.getReference().child(TRIBUS_COMMENTS);
    private static DatabaseReference mLikesTribusComments = mDatabase.getReference().child(LIKES_TRIBUS_COMMENTS);
    private static DatabaseReference mDislikesTribusComments = mDatabase.getReference().child(DISLIKES_TRIBUS_COMMENTS);
    private static FirebaseRecyclerAdapter<Comment, CommentsViewHolder> commentsFirebaseRecyclerAdapter;

    //VARIABLES
    private static User mUser;
    private static Tribu mTribu;
    private static ProgressDialog progress;
    private static boolean mProcessLike = false;
    private static boolean mProcessDislike = false;
    private static User mUserAdmin;


    //GET THE CURRENT TRIBU
    //GET TRIBU
    public static Observable<Tribu> getTribu(String uniqueName) {

        return Observable.create(subscriber -> mReferenceTribu.child(uniqueName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mTribu = dataSnapshot.getValue(Tribu.class);
                subscriber.onNext(mTribu);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }));
    }


    //GET USER
    public static Observable<User> getUser() {

        String uid = mAuth.getCurrentUser().getUid();

        return Observable.create(subscriber -> mReferenceUser.child(uid).addValueEventListener(new ValueEventListener() {
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


    public static FirebaseRecyclerAdapter<Comment, CommentsViewHolder> getComments(Tribu tribu, AppCompatActivity activity) {

        return commentsFirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Comment, CommentsViewHolder>(
                Comment.class,
                R.layout.row_comments,
                CommentsViewHolder.class,
                mTribusComments.child(tribu.getProfile().getUniqueName()).orderByKey()
        ) {
            @Override
            protected void populateViewHolder(CommentsViewHolder viewHolder, Comment comment, int position) {

                mReferenceUser
                        .child(comment.getUidUser())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                User userComment = dataSnapshot.getValue(User.class);

                                if(comment.getUidUser().equals(mAuth.getCurrentUser().getUid())){

                                    //SET CURRENT USER'S DATA
                                    viewHolder.setTvUsernameComment(mUser.getUsername());
                                    viewHolder.setUsersImage(mUser.getImageUrl());

                                }
                                else {
                                    //SET OTHER USER'S DATA
                                    viewHolder.setTvUsernameComment(userComment.getUsername());
                                    viewHolder.setUsersImage(userComment.getImageUrl());

                                }

                                //SET COMMMENT'S DATA
                                viewHolder.setTvComment(comment.getComment());
                                viewHolder.setTvNumLikes(comment.getNumLikes());
                                viewHolder.setmTvNumDislikes(comment.getNumDislikes());

                                //SETUP TIME
                                viewHolder.setTvCommentDate(comment.getData());

                                //SET USER'S DATA
                                viewHolder.setTvUsernameComment(userComment.getUsername());
                                viewHolder.setUsersImage(userComment.getImageUrl());

                                //SET COMMMENT'S DATA
                                viewHolder.setTvComment(comment.getComment());
                                viewHolder.setTvNumLikes(comment.getNumLikes());
                                viewHolder.setmTvNumDislikes(comment.getNumDislikes());

                                //SETUP TIME
                                viewHolder.setTvCommentDate(comment.getData());


                                //SETUP ICONS
                                setLikeButton(mUser, getRef(position), viewHolder);
                                setDislikeButton(mUser, getRef(position), viewHolder);

                                if (mAuth.getCurrentUser().getUid().equals(userComment.getId())) {
                                    viewHolder.mBtnClear.setVisibility(VISIBLE);
                                    viewHolder.mBtnEdit.setVisibility(VISIBLE);

                                } else if (tribu.getAdmin().getUidAdmin().equals(mAuth.getCurrentUser().getUid())) {
                                    viewHolder.mBtnEdit.setVisibility(GONE);
                                    viewHolder.mBtnClear.setVisibility(VISIBLE);

                                } else {
                                    viewHolder.mBtnClear.setVisibility(GONE);
                                    viewHolder.mBtnEdit.setVisibility(GONE);
                                }

                                viewHolder.mBtnClear.setOnClickListener(v -> {
                                    getRef(position).removeValue();
                                    mReferenceTribus
                                            .child(tribu.getProfile().getUniqueName())
                                            .child("profile").child("numComments")
                                            .runTransaction(new Transaction.Handler() {
                                                @Override
                                                public Transaction.Result doTransaction(MutableData mutableData) {
                                                    long num = (long) mutableData.getValue();
                                                    num--;
                                                    mutableData.setValue(num);
                                                    return Transaction.success(mutableData);
                                                }

                                                @Override
                                                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                                                }
                                            });
                                });

                                //CLICK FOR BTN EDIT - TO EDIT COMMENT
                                viewHolder.mBtnEdit.setOnClickListener(v -> {
                                    showDialog(activity, getRef(position), comment);
                                });

                                //CLICK FOR BTN LIKE - TO LIKE COMMENT
                                viewHolder.mIconLike.setOnClickListener(v -> {
                                    setLike(getRef(position), mUser);
                                });

                                //CLICK FOR BTN DISLIKE - TO DISLIKE COMMENT
                                viewHolder.mIconDislike.setOnClickListener(v -> {
                                    setDislike(getRef(position), mUser);
                                });


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


            }
        };
    }

    //Method to verify the last position in RecyclerView and scroll until there
    public static void verifyLastPosition(RecyclerView recyclerView, LinearLayoutManager
            layoutManager) {
        commentsFirebaseRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = commentsFirebaseRecyclerAdapter.getItemCount();
                int lastVisiblePosition =
                        layoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    recyclerView.scrollToPosition(positionStart);
                }
            }
        });

    }

    //FOR COMMENTS
    //Validate fields
    public static void sendCommentToFirebase(Comment comment) {

        mTribusComments
                .child(mTribu.getProfile().getUniqueName())
                .push()
                .setValue(comment)
                .addOnSuccessListener(aVoid -> {
                    setCommentsInsideTribu(mTribu);
                });
    }

    private static void setCommentsInsideTribu(Tribu tribu) {
        mReferenceTribus
                .child(tribu.getProfile().getUniqueName())
                .child("profile").child("numComments")
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        long num = (long) mutableData.getValue();
                        num++;
                        mutableData.setValue(num);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                    }
                });
    }

    //DIALOG TO UPDATE COMMENTS
    public static void showDialog(AppCompatActivity activity, DatabaseReference ref, Comment comment) {
        //INSTANCE OF PROGRESS DIALOG
        progress = new ProgressDialog(activity);
        progress.setMessage("Atualizando comentário...");

        //CONFIGURATION OF DIALOG
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.edit_user_name, null);
        builder.setView(dialogView);
        EditText editName = (EditText) dialogView.findViewById(R.id.et_edit_name);
        editName.setText(comment.getComment());
        builder.setTitle("Edite seu comentário...");

        String positiveText = "ATUALIZAR";
        builder.setPositiveButton(positiveText, (dialog, which) -> {
            String newComment = editName.getText().toString().trim();
            dialog.dismiss();
            showProgressDialog(true);
            if (validateField(activity, newComment)) {
                updateComment(newComment, ref);
            }

        });

        String negativeText = "CANCELAR";
        builder.setNegativeButton(negativeText, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private static boolean validateField(AppCompatActivity activity, String newName) {
        boolean isEmpty = true;
        if (newName.equals("")) {
            progress.dismiss();
            isEmpty = false;
        }
        return isEmpty;

    }

    private static void updateComment(String comment, DatabaseReference refComment) {
        Map<String, Object> updateComment = new HashMap<>();
        updateComment.put("comment", comment);
        refComment
                .updateChildren(updateComment)
                .addOnSuccessListener(aVoid -> showProgressDialog(false));
    }

    //SHOW PROGRESS
    private static void showProgressDialog(boolean load) {
        if (load) {
            progress.show();
        } else {
            progress.dismiss();
        }
    }


    //SET LIKES
    private static void setLike(DatabaseReference refComment, User user) {

        mProcessLike = true;
        mLikesTribusComments
                .child(refComment.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (mProcessLike) {
                            if (dataSnapshot.hasChild(user.getId())) {

                                mLikesTribusComments
                                        .child(refComment.getKey())
                                        .child(user.getId())
                                        .removeValue()
                                        .addOnSuccessListener(aVoid -> {
                                            setRemoveLikeTransaction(refComment);
                                        });

                            } else {
                                mLikesTribusComments
                                        .child(refComment.getKey())
                                        .child(user.getId())
                                        .setValue(user.getId())
                                        .addOnSuccessListener(aVoid -> {
                                            setAddLikeTransaction(user, refComment);
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    //SET REMOVE LIKE TRANSACTION
    private static void setRemoveLikeTransaction(DatabaseReference refComment) {
        refComment
                .child("numLikes")
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        long num = (long) mutableData.getValue();
                        num--;
                        mutableData.setValue(num);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean success, DataSnapshot dataSnapshot) {
                        if (success) {
                            mProcessLike = false;
                        }

                    }
                });

    }

    //SET ADD LIKE TRANSACTION
    private static void setAddLikeTransaction(User user, DatabaseReference refComment) {

        refComment
                .child("numLikes")
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        long num = (long) mutableData.getValue();
                        num++;
                        mutableData.setValue(num);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean success, DataSnapshot dataSnapshot1) {
                        if (success) {

                            mDislikesTribusComments
                                    .child(refComment.getKey())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChild(user.getId())) {
                                                setDislike(refComment, user);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });


                            mProcessLike = false;
                        }
                    }
                });

    }


    //SET LIKE BUTTON
    private static void setLikeButton(User user, DatabaseReference refComment, CommentsViewHolder viewHolder) {

        mLikesTribusComments
                .child(refComment.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(user.getId())) {
                            viewHolder.mIconLike.setImageResource(R.drawable.ic_add_like);
                        } else {
                            viewHolder.mIconLike.setImageResource(R.drawable.ic_recommendation_new);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    //SET DISLIKE BUTTON
    private static void setDislikeButton(User user, DatabaseReference refComment, CommentsViewHolder viewHolder) {

        mDislikesTribusComments
                .child(refComment.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(user.getId())) {
                            viewHolder.mIconDislike.setImageResource(R.drawable.ic_dislike_clicked);
                        } else {
                            viewHolder.mIconDislike.setImageResource(R.drawable.ic_dislike_comments);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    //SET DISLIKES
    private static void setDislike(DatabaseReference refComment, User user) {

        mProcessDislike = true;
        mDislikesTribusComments
                .child(refComment.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (mProcessDislike) {
                            if (dataSnapshot.hasChild(user.getId())) {

                                mDislikesTribusComments
                                        .child(refComment.getKey())
                                        .child(user.getId())
                                        .removeValue()
                                        .addOnSuccessListener(aVoid -> {
                                            setRemoveDislikeTransaction(refComment);
                                        });

                            } else {
                                mDislikesTribusComments
                                        .child(refComment.getKey())
                                        .child(user.getId())
                                        .setValue(user.getId())
                                        .addOnSuccessListener(aVoid -> {

                                            setAddDislikeTransaction(user, refComment);
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }


    //SET REMOVE DISLIKE TRANSACTION
    private static void setRemoveDislikeTransaction(DatabaseReference refComment) {
        refComment
                .child("numDislikes")
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        long num = (long) mutableData.getValue();
                        num--;
                        mutableData.setValue(num);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean success, DataSnapshot dataSnapshot) {
                        if (success) {
                            mProcessDislike = false;
                        }

                    }
                });

    }

    //SET ADD DISLIKE TRANSACTION
    private static void setAddDislikeTransaction(User user, DatabaseReference refComment) {
        refComment
                .child("numDislikes")
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        long num = (long) mutableData.getValue();
                        num++;
                        mutableData.setValue(num);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean success, DataSnapshot dataSnapshot) {
                        if (success) {

                            mLikesTribusComments
                                    .child(refComment.getKey())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChild(user.getId())) {
                                                setLike(refComment, user);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });


                            mProcessDislike = false;
                        }
                    }
                });

    }

}
