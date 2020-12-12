package apptribus.com.tribus.activities.change_admin.repository;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.change_admin.adapter.ChangeAdminAdapter;
import apptribus.com.tribus.activities.main_activity.MainActivity;
import apptribus.com.tribus.pojo.Admin;
import apptribus.com.tribus.pojo.Follower;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import rx.Observable;

import static android.view.View.GONE;
import static apptribus.com.tribus.util.Constantes.ADMIN;
import static apptribus.com.tribus.util.Constantes.DATE;
import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.TRIBUS_PARTICIPANTS;

public class ChangeAdminAPI {

    //INSTANCES - FIREBASE
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    //FIRESTORE INSTANCE
    private static FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    //FIRESTORE COLLECTIONS REFERENCES
    private static CollectionReference mTribusCollection = mFirestore.collection(GENERAL_TRIBUS);

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

                            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                    Follower follower = documentSnapshot.toObject(Follower.class);

                                    if (!follower.isAdmin()) {

                                        followers.add(documentSnapshot.toObject(Follower.class));
                                        mListDocSnapshotAllFollowers.add(documentSnapshot);
                                    }


                                }


                                if (!mListDocSnapshotAllFollowers.isEmpty()) {
                                    mLastFollowerVisible = mListDocSnapshotAllFollowers.get(mListDocSnapshotAllFollowers.size() - 1);

                                    subscriber.onNext(followers);
                                }
                                else {
                                    subscriber.onNext(null);
                                }
                            }
                            else {
                                subscriber.onNext(null);
                            }
                        })
                        .addOnFailureListener(e -> {

                            subscriber.onNext(null);

                        }));

    }

    public static void loadMoreFollowers(List<Follower> followers, ChangeAdminAdapter changeAdminAdapter,
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

                                    Follower follower = documentSnapshot.toObject(Follower.class);

                                    if (!follower.isAdmin()) {

                                        mListDocSnapshotAllFollowers.add(documentSnapshot);
                                        mListDocSnapshotAllFollowersClear.add(documentSnapshot);

                                    }
                                }
                            }


                            for (DocumentSnapshot documentSnapshot : mListDocSnapshotAllFollowersClear) {

                                followers.add(documentSnapshot.toObject(Follower.class));


                            }

                            mLastFollowerVisible = mListDocSnapshotAllFollowers
                                    .get(mListDocSnapshotAllFollowers.size() - 1);


                            changeAdminAdapter.notifyDataSetChanged();
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


    public static void openDialogChangeAdmin(User follower, String tribuKey, AppCompatActivity activity) {


        progress = new ProgressDialog(activity);
        progress.setMessage(activity.getResources().getString(R.string.progress_update_admin_title));
        progress.setCanceledOnTouchOutside(false);

        mTribusCollection
                .document(tribuKey)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    Tribu tribu = documentSnapshot.toObject(Tribu.class);

                    String[] firstName = follower.getName().split(" ");
                    String appendNameAndUsername = firstName[0] + " (" + follower.getUsername() + ")";


                    //CONFIGURATION OF DIALOG
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle(activity.getResources().getString(R.string.dialog_replace_admin_title_part_1) + " "
                            + appendNameAndUsername + " " +
                            activity.getResources().getString(R.string.dialog_replace_admin_title_part_2));

                    builder.setMessage(activity.getResources().getString(R.string.dialog_replace_admin_message));

                    String positiveText = activity.getResources().getString(R.string.positive_button_replace_admin);
                    builder.setPositiveButton(positiveText, (dialog, which) -> {
                        dialog.dismiss();
                        showProgressDialog(true);
                        updateAdmin(follower, tribu, activity);


                    });

                    String negativeText = activity.getResources().getString(R.string.negative_button_replace_admin);
                    builder.setNegativeButton(negativeText, (dialog, which) -> {
                        dialog.dismiss();
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();

                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(activity,
                            activity.getResources().getString(R.string.toast_error_change_admin), Toast.LENGTH_SHORT).show();
                });


    }

    private static void updateAdmin(User follower, Tribu tribu, AppCompatActivity activity) {

        Admin followerAdmin = new Admin();
        Date date = new Date(System.currentTimeMillis());
        followerAdmin.setUidAdmin(tribu.getAdmin().getUidAdmin());
        followerAdmin.setDate(date);
        followerAdmin.setTribuKey(tribu.getKey());

        mTribusCollection
                .document(tribu.getKey())
                .collection(ADMIN)
                .document(follower.getId())
                .set(followerAdmin)
                .addOnSuccessListener(aVoid -> {

                    mTribusCollection
                            .document(tribu.getKey())
                            .collection(ADMIN)
                            .document(mAuth.getCurrentUser().getUid())
                            .delete()
                            .addOnSuccessListener(aVoid12 -> {
                                changeAdminFollower(follower, activity, tribu.getKey());
                            })
                            .addOnFailureListener(e -> {
                                e.printStackTrace();
                                showProgressDialog(false);
                                Toast.makeText(activity,
                                        activity.getResources().getString(R.string.toast_error_change_admin), Toast.LENGTH_SHORT).show();

                            });

                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    showProgressDialog(false);
                    Toast.makeText(activity,
                            activity.getResources().getString(R.string.toast_error_change_admin), Toast.LENGTH_SHORT).show();
                });

    }

    private static void changeAdminFollower(User newAdmin, AppCompatActivity activity, String tribuKey) {

        //UPDATE TRIBUS-FOLLOWERS OLD ADMIN
        Map<String, Object> updateTribusFollowersOldAdmin = new HashMap<>();
        updateTribusFollowersOldAdmin.put("admin", false);

        //UPDATE TRIBUS-FOLLOWERS NEW ADMIN
        Map<String, Object> updateTribusFollowersnNewAdmin = new HashMap<>();
        updateTribusFollowersnNewAdmin.put("admin", true);

        mTribusCollection
                .document(tribuKey)
                .collection(TRIBUS_PARTICIPANTS)
                .document(newAdmin.getId())
                .update(updateTribusFollowersnNewAdmin)
                .addOnSuccessListener(aVoid -> {

                    mTribusCollection
                            .document(tribuKey)
                            .collection(TRIBUS_PARTICIPANTS)
                            .document(mAuth.getCurrentUser().getUid())
                            .update(updateTribusFollowersOldAdmin)
                            .addOnSuccessListener(aVoid1 -> {

                                showProgressDialog(false);
                                Toast.makeText(activity,
                                        activity.getResources().getString(R.string.negative_button_replace_admin),
                                        Toast.LENGTH_SHORT)
                                        .show();
                                openMainActivity(activity);

                            })
                            .addOnFailureListener(e -> {
                                e.printStackTrace();
                                showProgressDialog(false);
                                Toast.makeText(activity,
                                        activity.getResources().getString(R.string.toast_error_change_admin), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    showProgressDialog(false);
                    Toast.makeText(activity,
                            activity.getResources().getString(R.string.toast_error_change_admin), Toast.LENGTH_SHORT).show();
                });


    }

    private static void openMainActivity(AppCompatActivity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    //SHOW PROGRESS
    private static void showProgressDialog(boolean load) {
        if (load) {
            progress.show();
        } else {
            progress.dismiss();
        }
    }

}
