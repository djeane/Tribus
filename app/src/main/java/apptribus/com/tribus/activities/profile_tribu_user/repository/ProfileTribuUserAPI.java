package apptribus.com.tribus.activities.profile_tribu_user.repository;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import apptribus.com.tribus.activities.profile_tribu_user.adapter.ProfileTribuUserAdapter;
import apptribus.com.tribus.pojo.Follower;
import apptribus.com.tribus.pojo.RequestTribu;
import apptribus.com.tribus.pojo.Tribu;
import rx.Observable;

import static android.view.View.GONE;
import static apptribus.com.tribus.util.Constantes.ADMIN_PERMISSION;
import static apptribus.com.tribus.util.Constantes.DATE;
import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.PARTICIPATING;
import static apptribus.com.tribus.util.Constantes.TRIBUS_INVITATIONS;
import static apptribus.com.tribus.util.Constantes.TRIBUS_PARTICIPANTS;

public class ProfileTribuUserAPI {

    //INSTANCES - FIREBASE
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    //FIRESTORE INSTANCE
    private static FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    //FIRESTORE COLLECTIONS REFERENCES
    private static CollectionReference mTribusCollection = mFirestore.collection(GENERAL_TRIBUS);
    private static CollectionReference mUsersCollection = mFirestore.collection(GENERAL_USERS);

    //OBJECTS
    private static DocumentSnapshot mLastFollowerVisible = null;
    private static List<DocumentSnapshot> mListDocSnapshotAllFollowers;
    private static List<DocumentSnapshot> mListDocSnapshotAllFollowersClear;

    private static ProgressDialog progress;

    public static Observable<List<Follower>> getAllFollowers(List<Follower> followers, String tribuKey) {

        Query firstQuery = mTribusCollection
                .document(tribuKey)
                .collection(TRIBUS_PARTICIPANTS)
                .orderBy(DATE, Query.Direction.DESCENDING)
                .limit(4);

        if (mListDocSnapshotAllFollowers == null) {
            mListDocSnapshotAllFollowers = new ArrayList<>();
            mListDocSnapshotAllFollowersClear = new ArrayList<>();
        } else {
            mListDocSnapshotAllFollowers.clear();
            mListDocSnapshotAllFollowersClear.clear();
        }


        return Observable.create(subscriber ->

                firstQuery
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {

                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                    followers.add(documentSnapshot.toObject(Follower.class));
                                    mListDocSnapshotAllFollowers.add(documentSnapshot);


                                }

                            if (queryDocumentSnapshots.size() == 1) {
                                mLastFollowerVisible = mListDocSnapshotAllFollowers.get(mListDocSnapshotAllFollowers.size());
                            }
                            else if (queryDocumentSnapshots.size() > 1){

                                mLastFollowerVisible = mListDocSnapshotAllFollowers.get(mListDocSnapshotAllFollowers.size() - 1);
                            }
                                subscriber.onNext(followers);

                        })
                        .addOnFailureListener(e -> {

                            subscriber.onNext(null);

                        }));

    }

    public static void loadMoreFollowers(List<Follower> followers, ProfileTribuUserAdapter profileTribuUserAdapter,
                                         ProgressBar mProgressBarBottom, String tribuKey) {

        if (mLastFollowerVisible != null) {
            Query nextQuery = mTribusCollection
                    .document(tribuKey)
                    .collection(TRIBUS_PARTICIPANTS)
                    .orderBy(DATE, Query.Direction.DESCENDING)
                    .startAfter(mLastFollowerVisible)
                    .limit(4);

            nextQuery
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            mListDocSnapshotAllFollowersClear.clear();

                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                if (!mListDocSnapshotAllFollowers.contains(documentSnapshot)) {


                                    mListDocSnapshotAllFollowers.add(documentSnapshot);
                                    mListDocSnapshotAllFollowersClear.add(documentSnapshot);


                                }
                            }

                            mLastFollowerVisible = mListDocSnapshotAllFollowers
                                    .get(mListDocSnapshotAllFollowers.size() - 1);


                            for (DocumentSnapshot documentSnapshot : mListDocSnapshotAllFollowersClear) {

                                followers.add(documentSnapshot.toObject(Follower.class));


                            }

                            profileTribuUserAdapter.notifyDataSetChanged();
                            mProgressBarBottom.setVisibility(GONE);

                        } else {
                            mProgressBarBottom.setVisibility(GONE);
                            mLastFollowerVisible = null;

                        }

                    })
                    .addOnFailureListener(e -> {
                        mProgressBarBottom.setVisibility(GONE);
                    });

        } else {
            mProgressBarBottom.setVisibility(GONE);
        }
    }


    //SET FOLLOWER
    //SHOW DIALOG
    public static void showDialog(Context context, Tribu tribu, AppCompatButton mBtnFollow) {


        //THIS user is CURRENT user, NOT the current mTribu's admin
        //INSTANCE OF PROGRESS DIALOG
        progress = new ProgressDialog(context);
        progress.setCancelable(false);

        //CONFIGURATION OF DIALOG
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Participar da mTribu " + '"' + tribu.getProfile().getNameTribu() + '"' + "?");

        String positiveText = "SIM";
        builder.setPositiveButton(positiveText, (dialog, which) -> {
            mBtnFollow.setEnabled(true);
            dialog.dismiss();
            showProgressDialog(true);
            createFollowers(context, tribu);

        });

        String negativeText = "NÃO";
        builder.setNegativeButton(negativeText, (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //SHOW PROGRESS
    private static void showProgressDialog(boolean load) {
        if (load) {
            progress.show();
        } else {
            progress.dismiss();
        }
    }


    //CREATE FOLLOWER
    private static void createFollowers(Context context, Tribu tribu) {

        Date date = new Date(System.currentTimeMillis());

        Follower newFollowerTribu = new Follower(mAuth.getCurrentUser().getUid(), false);
        newFollowerTribu.setDate(date);


        //CREATE REQUEST TRIBU'S OBJECT IF TRIBU IS RESTRICT
        RequestTribu requestTribu = new RequestTribu(tribu.getProfile().getUniqueName());
        Date date1 = new Date(System.currentTimeMillis());
        requestTribu.setDate(date1);


        Tribu newFollower = new Tribu();
        newFollower.setDate(date);
        newFollower.setKey(tribu.getKey());

        if (tribu.getProfile().isPublic()) {


            mTribusCollection
                    .document(tribu.getKey())
                    .collection(TRIBUS_PARTICIPANTS)
                    .document(mAuth.getCurrentUser().getUid())
                    .set(newFollowerTribu)
                    .addOnSuccessListener(aVoid -> {

                        mUsersCollection
                                .document(mAuth.getCurrentUser().getUid())
                                .collection(PARTICIPATING)
                                .document(tribu.getKey())
                                .set(newFollower)
                                .addOnSuccessListener(aVoid12 -> {
                                    showProgressDialog(false);
                                    Toast.makeText(context, "Tribu adicionada!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    e.printStackTrace();
                                    showProgressDialog(false);
                                    Toast.makeText(context, "Houve um erro na sua solicitação.",
                                            Toast.LENGTH_SHORT).show();

                                });
                        ;

                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        showProgressDialog(false);
                        Toast.makeText(context, "Houve um erro na sua solicitação.",
                                Toast.LENGTH_SHORT).show();

                    });


        } else {

            mTribusCollection
                    .document(tribu.getKey())
                    .collection(ADMIN_PERMISSION)
                    .document(mAuth.getCurrentUser().getUid())
                    .set(newFollower)
                    .addOnSuccessListener(aVoid -> {

                        mUsersCollection
                                .document(mAuth.getCurrentUser().getUid())
                                .collection(TRIBUS_INVITATIONS)
                                .document(tribu.getKey())
                                .set(requestTribu)
                                .addOnSuccessListener(aVoid13 -> {
                                    showProgressDialog(false);
                                    Toast.makeText(context, "Aguardando aprovação pelo Admin.", Toast.LENGTH_LONG).show();
                                })
                                .addOnFailureListener(e -> {
                                    e.printStackTrace();
                                    showProgressDialog(false);
                                    Toast.makeText(context, "Houve um erro na sua solicitação.",
                                            Toast.LENGTH_SHORT).show();

                                });


                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        showProgressDialog(false);
                        Toast.makeText(context, "Houve um erro na sua solicitação.",
                                Toast.LENGTH_SHORT).show();

                    });

        }
    }


    public static Observable<Boolean> getFollowerToSetButton(String tribusKey) {
        return Observable.create(subscriber -> {

            mTribusCollection
                    .document(tribusKey)
                    .collection(TRIBUS_PARTICIPANTS)
                    .document(mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {

                        if (documentSnapshot.exists()) {
                            subscriber.onNext(true);
                        } else {
                            subscriber.onNext(false);
                        }

                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        subscriber.onNext(false);
                    });
        });
    }


    public static Observable<Boolean> setButtonIfWaitingPermission(String mTribusKey) {
        return Observable.create(subscriber -> {

            mTribusCollection
                    .document(mTribusKey)
                    .collection(ADMIN_PERMISSION)
                    .document(mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {

                        if (documentSnapshot.exists()) {
                            subscriber.onNext(true);
                        } else {
                            subscriber.onNext(false);
                        }

                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        subscriber.onNext(false);

                    });
        });
    }
}
