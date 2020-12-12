package apptribus.com.tribus.activities.detail_tribu_add_followers.repository;

import android.app.ProgressDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.detail_tribu_add_followers.adapter.DetailTribuAddFollowersAdapter;
import apptribus.com.tribus.pojo.Follower;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import rx.Observable;

import static android.view.View.GONE;
import static apptribus.com.tribus.util.Constantes.ADMIN_PERMISSION;
import static apptribus.com.tribus.util.Constantes.DATE;
import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.PARTICIPATING;
import static apptribus.com.tribus.util.Constantes.TRIBUS_INVITATIONS;
import static apptribus.com.tribus.util.Constantes.TRIBUS_PARTICIPANTS;

public class DetailTribuAddFollowersAPI {

    private static ProgressDialog progress;

    //FIRESTORE INSTANCE
    private static FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    //FIRESTORE COLLECTIONS REFERENCES
    private static CollectionReference mTribusCollection = mFirestore.collection(GENERAL_TRIBUS);
    private static CollectionReference mUsersCollection = mFirestore.collection(GENERAL_USERS);


    //OBJECTS
    private static DocumentSnapshot mLastFollowerVisible = null;
    private static List<DocumentSnapshot> mListDocSnapshotAllFollowers;
    private static List<DocumentSnapshot> mListDocSnapshotAllFollowersClear;


    public static Observable<List<Follower>> getAllFollowers(List<Follower> followers, String tribuKey) {

        Query firstQuery = mTribusCollection
                .document(tribuKey)
                .collection(ADMIN_PERMISSION)
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

                            mLastFollowerVisible = mListDocSnapshotAllFollowers.get(mListDocSnapshotAllFollowers.size() - 1);

                            subscriber.onNext(followers);
                        })
                        .addOnFailureListener(e -> {

                            subscriber.onNext(null);

                        }));

    }

    public static void loadMoreFollowers(List<Follower> followers, DetailTribuAddFollowersAdapter detailTribuAddFollowersAdapter,
                                         ProgressBar mProgressBarBottom, String tribuKey) {

        if (mLastFollowerVisible != null) {
            Query nextQuery = mTribusCollection
                    .document(tribuKey)
                    .collection(ADMIN_PERMISSION)
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

                            detailTribuAddFollowersAdapter.notifyDataSetChanged();
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


    public static void showDialogToDeny(Follower follower, User userFollower, String tribuKey, AppCompatActivity activity) {
        //INSTANCE OF PROGRESS DIALOG
        progress = new ProgressDialog(activity);
        progress.setCancelable(false);

        //CONFIGURATION OF DIALOG
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);


        builder.setTitle(activity.getResources().getString(R.string.dont_add_participants));

        builder.setMessage(activity.getResources().getString(R.string.removing_request)
                + userFollower.getName() + '"' + "?");

        String positiveText = activity.getResources().getString(R.string.yes);
        builder.setPositiveButton(positiveText, (dialog, which) -> {
            dialog.dismiss();
            showProgressDialog(true);
            removeFollowerOfAdminPermissionAfterDenied(follower, tribuKey, activity);

        });
        String negativeText = activity.getResources().getString(R.string.no);
        builder.setNegativeButton(negativeText, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private static void removeFollowerOfAdminPermissionAfterDenied(Follower follower, String tribuKey, AppCompatActivity activity) {

        mTribusCollection
                .document(tribuKey)
                .collection(ADMIN_PERMISSION)
                .document(follower.getUidFollower())
                .delete()
                .addOnSuccessListener(aVoid -> {

                    mUsersCollection
                            .document(follower.getUidFollower())
                            .collection(TRIBUS_INVITATIONS)
                            .document(tribuKey)
                            .delete()
                            .addOnSuccessListener(aVoid12 -> {
                                showProgressDialog(false);
                                Toast.makeText(activity,
                                        activity.getResources().getString(R.string.toast_remove_request_successfully),
                                        Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                e.printStackTrace();
                                showProgressDialog(false);
                                Toast.makeText(activity, activity.getResources().getString(R.string.toast_error_remove_request),
                                        Toast.LENGTH_SHORT).show();

                            });

                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    showProgressDialog(false);
                    Toast.makeText(activity, activity.getResources().getString(R.string.toast_error_remove_request),
                            Toast.LENGTH_SHORT).show();

                });
    }


    public static void showDialogToAccept(Follower follower, User userFollower, String tribuKey, AppCompatActivity activity) {
        //INSTANCE OF PROGRESS DIALOG
        progress = new ProgressDialog(activity);
        progress.setCancelable(false);

        //CONFIGURATION OF DIALOG
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle(activity.getResources().getString(R.string.add_participant));

        builder.setMessage(activity.getResources().getString(R.string.accept_request)
                + userFollower.getName() + '"' + "?");

        String positiveText = activity.getResources().getString(R.string.yes);
        builder.setPositiveButton(positiveText, (dialog, which) -> {
            dialog.dismiss();
            showProgressDialog(true);
            sendFollowerToTribusFollower(follower, tribuKey, activity);

        });
        String negativeText = activity.getResources().getString(R.string.no);

        builder.setNegativeButton(negativeText, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private static void sendFollowerToTribusFollower(Follower follower, String tribuKey, AppCompatActivity activity) {

        mTribusCollection
                .document(tribuKey)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    Tribu tribu = documentSnapshot.toObject(Tribu.class);

                    Date date = new Date(System.currentTimeMillis());

                    Follower newFollowerTribu = new Follower();
                    newFollowerTribu.setAdmin(false);
                    newFollowerTribu.setUidFollower(follower.getUidFollower());
                    newFollowerTribu.setDate(date);


                    Tribu newFollower = new Tribu();
                    newFollower.setDate(date);
                    newFollower.setKey(tribuKey);
                    newFollower.setThematic(tribu.getProfile().getThematic());

                    mTribusCollection
                            .document(tribuKey)
                            .collection(TRIBUS_PARTICIPANTS)
                            .document(follower.getUidFollower())
                            .set(newFollowerTribu)
                            .addOnSuccessListener(aVoid -> {

                                mUsersCollection
                                        .document(follower.getUidFollower())
                                        .collection(PARTICIPATING)
                                        .document(tribuKey)
                                        .set(newFollower)
                                        .addOnSuccessListener(aVoid12 -> {
                                            removeFollowerOfAdminPermissionAfterAccepted(follower, tribuKey, activity);

                                        })
                                        .addOnFailureListener(e -> {
                                            e.printStackTrace();
                                            showProgressDialog(false);
                                            Toast.makeText(activity, activity.getResources().getString(R.string.toast_error_accept_request),
                                                    Toast.LENGTH_SHORT).show();

                                        });
                                ;

                            })
                            .addOnFailureListener(e -> {
                                e.printStackTrace();
                                showProgressDialog(false);
                                Toast.makeText(activity, activity.getResources().getString(R.string.toast_error_accept_request),
                                        Toast.LENGTH_SHORT).show();

                            });


                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    showProgressDialog(false);
                    Toast.makeText(activity, activity.getResources().getString(R.string.toast_error_accept_request),
                            Toast.LENGTH_SHORT).show();

                });



    }


    private static void removeFollowerOfAdminPermissionAfterAccepted(Follower follower, String tribuKey, AppCompatActivity activity) {

        mTribusCollection
                .document(tribuKey)
                .collection(ADMIN_PERMISSION)
                .document(follower.getUidFollower())
                .delete()
                .addOnSuccessListener(aVoid -> {

                    mUsersCollection
                            .document(follower.getUidFollower())
                            .collection(TRIBUS_INVITATIONS)
                            .document(tribuKey)
                            .delete()
                            .addOnSuccessListener(aVoid12 -> {
                                showProgressDialog(false);
                                Toast.makeText(activity,
                                        activity.getResources().getString(R.string.toast_accept_request_successfully),
                                        Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                e.printStackTrace();
                                showProgressDialog(false);
                                Toast.makeText(activity, activity.getResources().getString(R.string.toast_error_accept_request),
                                        Toast.LENGTH_SHORT).show();

                            });
                    ;


                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    showProgressDialog(false);
                    Toast.makeText(activity, activity.getResources().getString(R.string.toast_error_accept_request),
                            Toast.LENGTH_SHORT).show();

                });
        ;

    }

    private static void showProgressDialog(boolean load) {

        if (load) {
            progress.show();
        } else {
            progress.dismiss();
        }
    }

}
