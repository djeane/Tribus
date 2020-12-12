package apptribus.com.tribus.activities.invitations_request_user.repository;

import android.app.ProgressDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.invitations_request_user.adapter.InvitationRequestUserAdapter;
import apptribus.com.tribus.pojo.Talk;
import apptribus.com.tribus.pojo.User;
import rx.Observable;

import static android.view.View.GONE;
import static apptribus.com.tribus.util.Constantes.DATE;
import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.USERS_TALKERS_INVITATIONS;
import static apptribus.com.tribus.util.Constantes.USERS_TALKERS_PERMISSIONS;
import static apptribus.com.tribus.util.Constantes.USER_INVITATIONS;
import static apptribus.com.tribus.util.Constantes.USER_PERMISSIONS;

public class InvitationsRequestUserAPI {

    //INSTANCES - FIREBASE
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    //FIRESTORE INSTANCE
    private static FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    //FIRESTORE COLLECTIONS REFERENCES
    private static CollectionReference mUsersCollection = mFirestore.collection(GENERAL_USERS);

    private static ProgressDialog progress;

    //OBJECTS
    private static DocumentSnapshot mLastContactVisible = null;
    private static List<DocumentSnapshot> mListDocSnapshotAllContacts;
    private static List<DocumentSnapshot> mListDocSnapshotAllContactsClear;


    public static Observable<List<Talk>> getAllContacts(List<Talk> contacts) {

        Query firstQuery = mUsersCollection
                .document(mAuth.getCurrentUser().getUid())
                .collection(USER_INVITATIONS)
                .orderBy(DATE, Query.Direction.DESCENDING)
                .limit(4);

        if (mListDocSnapshotAllContacts == null) {
            mListDocSnapshotAllContacts = new ArrayList<>();
            mListDocSnapshotAllContactsClear = new ArrayList<>();
        } else {
            mListDocSnapshotAllContacts.clear();
            mListDocSnapshotAllContactsClear.clear();
        }


        return Observable.create(subscriber ->

                firstQuery
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {

                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                contacts.add(documentSnapshot.toObject(Talk.class));
                                mListDocSnapshotAllContacts.add(documentSnapshot);


                            }

                            mLastContactVisible = mListDocSnapshotAllContacts.get(mListDocSnapshotAllContacts.size() - 1);

                            subscriber.onNext(contacts);
                        })
                        .addOnFailureListener(e -> {

                            subscriber.onNext(null);

                        }));

    }

    public static void loadMoreContacts(List<Talk> contacts, InvitationRequestUserAdapter invitationRequestUserAdapter,
                                        ProgressBar mProgressBarBottom) {

        if (mLastContactVisible != null) {
            Query nextQuery = mUsersCollection
                    .document(mAuth.getCurrentUser().getUid())
                    .collection(USER_INVITATIONS)
                    .orderBy(DATE, Query.Direction.DESCENDING)
                    .startAfter(mLastContactVisible)
                    .limit(4);

            nextQuery
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            mListDocSnapshotAllContactsClear.clear();

                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                if (!mListDocSnapshotAllContacts.contains(documentSnapshot)) {


                                    mListDocSnapshotAllContacts.add(documentSnapshot);
                                    mListDocSnapshotAllContactsClear.add(documentSnapshot);


                                }
                            }

                            mLastContactVisible = mListDocSnapshotAllContacts
                                    .get(mListDocSnapshotAllContacts.size() - 1);


                            for (DocumentSnapshot documentSnapshot : mListDocSnapshotAllContactsClear) {

                                contacts.add(documentSnapshot.toObject(Talk.class));


                            }

                            invitationRequestUserAdapter.notifyDataSetChanged();
                            mProgressBarBottom.setVisibility(GONE);

                        } else {
                            mProgressBarBottom.setVisibility(GONE);
                            mLastContactVisible = null;

                        }

                    })
                    .addOnFailureListener(e -> {
                        mProgressBarBottom.setVisibility(GONE);
                    });

        } else {
            mProgressBarBottom.setVisibility(GONE);
        }
    }


    public static void openDialogToCancelInvitation(User contact, AppCompatActivity activity) {
        progress = new ProgressDialog(activity);
        progress.setCanceledOnTouchOutside(false);
        progress.setMessage(activity.getResources().getString(R.string.progress_message_cancel_invitation_user));


        //CONFIGURATION OF DIALOG
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getResources().getString(R.string.cancel_invitation));

        builder.setMessage(activity.getResources().getString(R.string.cancel_invitation_message)
                + contact.getName() + "(" + contact.getUsername()
                + ")?");

        String positiveText = activity.getResources().getString(R.string.cancel_invitation_btn);
        builder.setPositiveButton(positiveText, (dialog, which) -> {
            dialog.dismiss();
            showProgressDialog(true);
            cancelInvitation(contact, activity);


        });

        String negativeText = activity.getResources().getString(R.string.no);
        builder.setNegativeButton(negativeText, (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private static void cancelInvitation(User contact, AppCompatActivity activity) {

        mUsersCollection
                .document(mAuth.getCurrentUser().getUid())
                .collection(USER_INVITATIONS)
                .document(contact.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {

                    mUsersCollection
                            .document(contact.getId())
                            .collection(USER_PERMISSIONS)
                            .document(mAuth.getCurrentUser().getUid())
                            .delete()
                            .addOnSuccessListener(aVoid12 -> {
                                showProgressDialog(false);
                                Toast.makeText(activity,
                                        activity.getResources().getString(R.string.invitation_canceled),
                                        Toast.LENGTH_SHORT).show();

                            })
                            .addOnFailureListener(e -> {
                                e.printStackTrace();
                                showProgressDialog(false);
                                Toast.makeText(activity,
                                        activity.getResources().getString(R.string.toast_error_canceling_invitation),
                                        Toast.LENGTH_SHORT).show();

                            });

                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    showProgressDialog(false);
                    Toast.makeText(activity,
                            activity.getResources().getString(R.string.toast_error_canceling_invitation),
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
