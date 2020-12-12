package apptribus.com.tribus.activities.invitation_request_tribu.repository;

import android.app.ProgressDialog;
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
import java.util.List;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.invitation_request_tribu.adapter.InvitationRequestTribuAdapter;
import apptribus.com.tribus.pojo.RequestTribu;
import apptribus.com.tribus.pojo.Tribu;
import rx.Observable;

import static android.view.View.GONE;
import static apptribus.com.tribus.util.Constantes.ADMIN_PERMISSION;
import static apptribus.com.tribus.util.Constantes.DATE;
import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.TRIBUS_INVITATIONS;

public class InvitationRequestTribuAPI {

    //FIRESTORE INSTANCE
    private static FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    //FIRESTORE COLLECTIONS REFERENCES
    private static CollectionReference mUsersCollection = mFirestore.collection(GENERAL_USERS);
    private static CollectionReference mTribusCollection = mFirestore.collection(GENERAL_TRIBUS);

    //INSTANCES - FIREBASE
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    //OBJECTS
    private static DocumentSnapshot mLastRequestTribuVisible = null;
    private static List<DocumentSnapshot> mListDocSnapshotAllRequestTribu;
    private static List<DocumentSnapshot> mListDocSnapshotAllRequestTribuClear;

    private static ProgressDialog progress;

    public static Observable<List<RequestTribu>> getAllRequestTribu(List<RequestTribu> requests) {

        Query firstQuery = mUsersCollection
                .document(mAuth.getCurrentUser().getUid())
                .collection(TRIBUS_INVITATIONS)
                .orderBy(DATE, Query.Direction.DESCENDING)
                .limit(4);

        if (mListDocSnapshotAllRequestTribu == null) {
            mListDocSnapshotAllRequestTribu = new ArrayList<>();
            mListDocSnapshotAllRequestTribuClear = new ArrayList<>();
        } else {
            mListDocSnapshotAllRequestTribu.clear();
            mListDocSnapshotAllRequestTribuClear.clear();
        }


        return Observable.create(subscriber ->

                firstQuery
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {

                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                requests.add(documentSnapshot.toObject(RequestTribu.class));
                                mListDocSnapshotAllRequestTribu.add(documentSnapshot);


                            }

                            mLastRequestTribuVisible = mListDocSnapshotAllRequestTribu.get(mListDocSnapshotAllRequestTribu.size() - 1);

                            subscriber.onNext(requests);
                        })
                        .addOnFailureListener(e -> {

                            subscriber.onNext(null);

                        }));

    }

    public static void loadMoreRequestTribu(List<RequestTribu> requests, InvitationRequestTribuAdapter invitationRequestTribuAdapter,
                                            ProgressBar mProgressBarBottom) {

        if (mLastRequestTribuVisible != null) {
            Query nextQuery = mUsersCollection
                    .document(mAuth.getCurrentUser().getUid())
                    .collection(TRIBUS_INVITATIONS)
                    .orderBy(DATE, Query.Direction.DESCENDING)
                    .startAfter(mLastRequestTribuVisible)
                    .limit(4);

            nextQuery
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            mListDocSnapshotAllRequestTribuClear.clear();

                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                if (!mListDocSnapshotAllRequestTribu.contains(documentSnapshot)) {

                                    mListDocSnapshotAllRequestTribu.add(documentSnapshot);
                                    mListDocSnapshotAllRequestTribuClear.add(documentSnapshot);
                                }
                            }

                            mLastRequestTribuVisible = mListDocSnapshotAllRequestTribu
                                    .get(mListDocSnapshotAllRequestTribu.size() - 1);


                            for (DocumentSnapshot documentSnapshot : mListDocSnapshotAllRequestTribuClear) {

                                requests.add(documentSnapshot.toObject(RequestTribu.class));


                            }

                            invitationRequestTribuAdapter.notifyDataSetChanged();
                            mProgressBarBottom.setVisibility(GONE);

                        } else {
                            mProgressBarBottom.setVisibility(GONE);
                            mLastRequestTribuVisible = null;

                        }

                    })
                    .addOnFailureListener(e -> {
                        mProgressBarBottom.setVisibility(GONE);
                    });

        } else {
            mProgressBarBottom.setVisibility(GONE);
        }
    }


    public static void openDialogToCancelRequestToTribu(Tribu tribu, AppCompatActivity activity) {

        progress = new ProgressDialog(activity);
        progress.setMessage(activity.getResources().getString(R.string.progress_message_cancel_invitation));
        progress.setCanceledOnTouchOutside(false);

        //CONFIGURATION OF DIALOG
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getResources().getString(R.string.cancel_tribu_request));

        builder.setMessage(activity.getResources().getString(R.string.cancel_tribu_request_question)
                + " " + tribu.getProfile().getNameTribu() + " ?");

        String positiveText = activity.getResources().getString(R.string.btn_cancel_request);
        builder.setPositiveButton(positiveText, (dialog, which) -> {
            dialog.dismiss();
            showProgressDialog(true);
            cancelInvitation(tribu, activity);


        });

        String negativeText = activity.getResources().getString(R.string.no);
        builder.setNegativeButton(negativeText, (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private static void cancelInvitation(Tribu tribu, AppCompatActivity activity) {

        mUsersCollection
                .document(mAuth.getCurrentUser().getUid())
                .collection(TRIBUS_INVITATIONS)
                .document(tribu.getKey())
                .delete()
                .addOnSuccessListener(aVoid -> {

                    mTribusCollection
                            .document(tribu.getKey())
                            .collection(ADMIN_PERMISSION)
                            .document(mAuth.getCurrentUser().getUid())
                            .delete()
                            .addOnSuccessListener(aVoid1 -> {
                                showProgressDialog(false);
                                Toast.makeText(activity,
                                        activity.getResources().getString(R.string.toast_cancel_request_successfully),
                                        Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                e.printStackTrace();
                                showProgressDialog(false);
                                Toast.makeText(activity,
                                        activity.getResources().getString(R.string.toast_error_cancel_request),
                                        Toast.LENGTH_SHORT).show();
                            });


                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    showProgressDialog(false);
                    Toast.makeText(activity,
                            activity.getResources().getString(R.string.toast_error_cancel_request),
                            Toast.LENGTH_SHORT).show();
                });


    }

    private static void showProgressDialog(boolean load) {
        if (load) {
            progress.show();
        } else {
            progress.dismiss();
        }
    }

}
