package apptribus.com.tribus.activities.detail_add_talker.repository;

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
import java.util.Date;
import java.util.List;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.detail_add_talker.adapter.DetailAddTalkerAdapter;
import apptribus.com.tribus.pojo.Talk;
import apptribus.com.tribus.pojo.User;
import rx.Observable;

import static android.view.View.GONE;
import static apptribus.com.tribus.util.Constantes.CONTACTS;
import static apptribus.com.tribus.util.Constantes.CONTACTS_ACCEPTED;
import static apptribus.com.tribus.util.Constantes.DATE;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.USER_INVITATIONS;
import static apptribus.com.tribus.util.Constantes.USER_PERMISSIONS;

public class DetailAddTalkerAPI {

    //INSTANCES - FIREBASE
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    //SHOW
    private static ProgressDialog progress;

    //FIRESTORE INSTANCE
    private static FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    //FIRESTORE COLLECTIONS REFERENCES
    private static CollectionReference mUsersCollection = mFirestore.collection(GENERAL_USERS);

    //OBJECTS
    private static DocumentSnapshot mLastContactVisible = null;
    private static List<DocumentSnapshot> mListDocSnapshotAllContacts;
    private static List<DocumentSnapshot> mListDocSnapshotAllContactsClear;



    public static Observable<List<Talk>> getAllContacts(List<Talk> contacts) {

        Query firstQuery = mUsersCollection
                .document(mAuth.getCurrentUser().getUid())
                .collection(USER_PERMISSIONS)
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

    public static void loadMoreContacts(List<Talk> contacts, DetailAddTalkerAdapter detailAddTalkerAdapter,
                                         ProgressBar mProgressBarBottom) {

        if (mLastContactVisible != null) {
            Query nextQuery = mUsersCollection
                    .document(mAuth.getCurrentUser().getUid())
                    .collection(USER_PERMISSIONS)
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

                            detailAddTalkerAdapter.notifyDataSetChanged();
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


    public static void showDialogToAcceptContact(Talk contact, User userContact, AppCompatActivity activity) {
        //INSTANCE OF PROGRESS DIALOG
        progress = new ProgressDialog(activity);
        progress.setCancelable(false);

        //CONFIGURATION OF DIALOG
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getResources().getString(R.string.add_contact));
        builder.setMessage(activity.getResources().getString(R.string.accept_contact)
               + '"' + userContact.getName() + '"' + activity.getResources().getString(R.string.as_a_contact));

        String positiveText = activity.getResources().getString(R.string.yes);
        builder.setPositiveButton(positiveText, (dialog, which) -> {
            dialog.dismiss();
            showProgressDialog(true);
            addContactIntoFirestore(contact, activity);

        });
        String negativeText = activity.getResources().getString(R.string.no);;
        builder.setNegativeButton(negativeText, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void showDialogToDenyInvitation(Talk contact, User userContact, AppCompatActivity activity) {
        //INSTANCE OF PROGRESS DIALOG
        progress = new ProgressDialog(activity);
        progress.setCancelable(false);

        //CONFIGURATION OF DIALOG
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getResources().getString(R.string.dont_add_contact));

        builder.setMessage(activity.getResources().getString(R.string.removing_invitation) + '"' + userContact.getName() + '"' + "?");

        String positiveText = activity.getResources().getString(R.string.yes);
        builder.setPositiveButton(positiveText, (dialog, which) -> {
            dialog.dismiss();
            showProgressDialog(true);
            removeInvitationsAndPermissionAfterDenied(contact, activity);

        });
        String negativeText = activity.getResources().getString(R.string.no);;
        builder.setNegativeButton(negativeText, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private static void showToast(AppCompatActivity activity, String message){
        Toast.makeText(activity,
                message,
                Toast.LENGTH_SHORT).show();
    }

    private static void removeInvitationsAndPermissionAfterDenied(Talk contact, AppCompatActivity activity) {

        mUsersCollection
                .document(mAuth.getCurrentUser().getUid())
                .collection(USER_PERMISSIONS)
                .document(contact.getTalkerId())
                .delete()
                .addOnSuccessListener(aVoid -> {

                    mUsersCollection
                            .document(contact.getTalkerId())
                            .collection(USER_INVITATIONS)
                            .document(mAuth.getCurrentUser().getUid())
                            .delete()
                            .addOnSuccessListener(aVoid12 -> {
                                showProgressDialog(false);
                                showToast(activity, activity.getResources().getString(R.string.invitation_removed));

                            })
                            .addOnFailureListener(e -> {
                                e.printStackTrace();
                                showProgressDialog(false);
                                showToast(activity, activity.getResources().getString(R.string.toast_error_remove_invitation));

                            });


                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    showProgressDialog(false);
                    showToast(activity, activity.getResources().getString(R.string.toast_error_remove_invitation));

                });

    }


    private static void addContactIntoFirestore(Talk contact, AppCompatActivity activity) {

        Date date = new Date(System.currentTimeMillis());

        Talk userContact = new Talk();
        userContact.setTribuKey(contact.getTribuKey());
        userContact.setTalkerId(contact.getTalkerId());
        userContact.setFromPermission(true);
        userContact.setDateAccepted(date);

        Talk currentContact = new Talk();
        currentContact.setTalkerId(mAuth.getCurrentUser().getUid());
        currentContact.setTribuKey(contact.getTribuKey());
        currentContact.setFromPermission(true);
        currentContact.setDateAccepted(date);


        mUsersCollection
                .document(mAuth.getCurrentUser().getUid())
                .collection(CONTACTS)
                .document(userContact.getTalkerId())
                .set(userContact)
                .addOnSuccessListener(aVoid -> {

                    mUsersCollection
                            .document(userContact.getTalkerId())
                            .collection(CONTACTS)
                            .document(mAuth.getCurrentUser().getUid())
                            .set(currentContact)
                            .addOnSuccessListener(aVoid12 -> {

                                mUsersCollection
                                        .document(mAuth.getCurrentUser().getUid())
                                        .collection(CONTACTS_ACCEPTED)
                                        .document(userContact.getTalkerId())
                                        .set(userContact)
                                        .addOnSuccessListener(aVoid13 -> {
                                            removeInvitationsAndPermissionAfterAccepted(contact, activity);
                                        })
                                        .addOnFailureListener(e -> {
                                            e.printStackTrace();
                                            showProgressDialog(false);
                                            showToast(activity, activity.getResources().getString(R.string.toast_error_add_contact));
                                        });

                            })
                            .addOnFailureListener(e -> {
                                e.printStackTrace();
                                showProgressDialog(false);
                                showToast(activity, activity.getResources().getString(R.string.toast_error_add_contact));
                            });

                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    showProgressDialog(false);
                    showToast(activity, activity.getResources().getString(R.string.toast_error_add_contact));
                });


    }


    private static void removeInvitationsAndPermissionAfterAccepted(Talk contact, AppCompatActivity activity) {


        mUsersCollection
                .document(mAuth.getCurrentUser().getUid())
                .collection(USER_PERMISSIONS)
                .document(contact.getTalkerId())
                .delete()
                .addOnSuccessListener(aVoid -> {

                    mUsersCollection
                            .document(contact.getTalkerId())
                            .collection(USER_INVITATIONS)
                            .document(mAuth.getCurrentUser().getUid())
                            .delete()
                            .addOnSuccessListener(aVoid12 -> {
                                showProgressDialog(false);
                                showToast(activity, activity.getResources().getString(R.string.contact_added));
                          })
                            .addOnFailureListener(e -> {
                                e.printStackTrace();
                                showProgressDialog(false);
                                showToast(activity, activity.getResources().getString(R.string.toast_error_add_contact));

                            });

                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    showProgressDialog(false);
                    showToast(activity, activity.getResources().getString(R.string.toast_error_add_contact));
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
