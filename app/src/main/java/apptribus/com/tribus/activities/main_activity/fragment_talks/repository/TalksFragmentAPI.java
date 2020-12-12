package apptribus.com.tribus.activities.main_activity.fragment_talks.repository;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.invitations_request_user.adapter.InvitationRequestUserAdapter;
import apptribus.com.tribus.activities.main_activity.fragment_talks.adapter.ContactsAddedFragmentAdapter;
import apptribus.com.tribus.activities.main_activity.fragment_talks.adapter.ContactsInvitationsFragmentAdapter;
import apptribus.com.tribus.activities.main_activity.fragment_talks.adapter.ContactsRemoveFragmentAdapter;
import apptribus.com.tribus.activities.main_activity.fragment_talks.adapter.ContactsRequestFragmentAdapter;
import apptribus.com.tribus.pojo.Talk;
import apptribus.com.tribus.pojo.User;
import rx.Observable;

import static android.view.View.GONE;
import static apptribus.com.tribus.util.Constantes.CONTACTS;
import static apptribus.com.tribus.util.Constantes.CONTACTS_ACCEPTED;
import static apptribus.com.tribus.util.Constantes.DATE;
import static apptribus.com.tribus.util.Constantes.DATE_INVITATION;
import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.USER_INVITATIONS;
import static apptribus.com.tribus.util.Constantes.USER_PERMISSIONS;

/**
 * Created by User on 6/13/2017.
 */

public class TalksFragmentAPI {

    //FIREBASE INSTANCES
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();


    private static ProgressDialog progress;

    //FIRESTORE INSTANCE
    private static FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    //FIRESTORE COLLECTIONS REFERENCES
    private static CollectionReference mTribusCollection = mFirestore.collection(GENERAL_TRIBUS);
    private static CollectionReference mUsersCollection = mFirestore.collection(GENERAL_USERS);


    //OBJECTS- ADDED CONTACTS
    private static DocumentSnapshot mLastContactVisible = null;
    private static List<DocumentSnapshot> mListDocSnapshotAllContacts;
    private static List<DocumentSnapshot> mListDocSnapshotAllContactsClear;

    //OBJECTS - REMOVE CONTACTS
    private static DocumentSnapshot mLastRemoveContactVisible = null;
    private static List<DocumentSnapshot> mListDocSnapshotAllRemoveContacts;
    private static List<DocumentSnapshot> mListDocSnapshotAllRemoveContactsClear;

    //OBJECTS - CONTACTS INVITATION
    private static DocumentSnapshot mLastInvitationVisible = null;
    private static List<DocumentSnapshot> mListDocSnapshotAllInvitations;
    private static List<DocumentSnapshot> mListDocSnapshotAllInvitationsClear;

    //OBJECTS - CONTACTS REQUEST
    private static DocumentSnapshot mLastContactRequestsVisible = null;
    private static List<DocumentSnapshot> mListDocSnapshotAllContactRequests;
    private static List<DocumentSnapshot> mListDocSnapshotAllContactRequestsClear;


    public static Observable<List<Talk>> getAllContacts(List<Talk> contacts, Fragment fragment) {

        Query firstQuery = mUsersCollection
                .document(mAuth.getCurrentUser().getUid())
                .collection(CONTACTS)
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
                    .addSnapshotListener(fragment.getActivity(), (queryDocumentSnapshots, e) -> {

                       if (e != null){
                           subscriber.onNext(null);
                       }

                       if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()){

                           for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()){

                               Talk contact = dc.getDocument().toObject(Talk.class);

                               switch (dc.getType()){
                                   case ADDED:
                                       contacts.add(contact);
                                       mListDocSnapshotAllContacts.add(dc.getDocument());

                                       break;
                               }

                           }

                           mLastContactVisible = mListDocSnapshotAllContacts.get(mListDocSnapshotAllContacts.size() - 1);

                           subscriber.onNext(contacts);
                       }
                       else {
                           subscriber.onNext(null);
                       }


                    }));

                /*firstQuery
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {

                            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {

                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                    contacts.add(documentSnapshot.toObject(Talk.class));
                                    mListDocSnapshotAllContacts.add(documentSnapshot);


                                }

                                mLastContactVisible = mListDocSnapshotAllContacts.get(mListDocSnapshotAllContacts.size() - 1);

                                subscriber.onNext(contacts);
                            }
                            else {
                                subscriber.onNext(null);
                            }
                        })
                        .addOnFailureListener(e -> {

                            subscriber.onNext(null);

                        }));*/

    }

    public static void loadMoreContacts(List<Talk> contacts, ContactsAddedFragmentAdapter contactsAddedFragmentAdapter,
                                        ProgressBar mProgressBarBottom, Fragment fragment) {

        if (mLastContactVisible != null) {
            Query nextQuery = mUsersCollection
                    .document(mAuth.getCurrentUser().getUid())
                    .collection(CONTACTS)
                    .startAfter(mLastContactVisible)
                    .limit(4);


            nextQuery
                    .addSnapshotListener(fragment.getActivity(), (queryDocumentSnapshots, e) -> {

                        if (e != null){
                            mProgressBarBottom.setVisibility(GONE);
                            return;
                        }

                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()){

                            mListDocSnapshotAllContactsClear.clear();

                            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()){

                                switch (dc.getType()){
                                    case ADDED:
                                        if (!mListDocSnapshotAllContacts.contains(dc.getDocument())) {
                                            mListDocSnapshotAllContacts.add(dc.getDocument());
                                            mListDocSnapshotAllContactsClear.add(dc.getDocument());

                                        }

                                        break;

                                }

                            }

                            for (DocumentSnapshot documentSnapshot : mListDocSnapshotAllContactsClear) {

                                contacts.add(documentSnapshot.toObject(Talk.class));


                            }

                            mLastContactVisible = mListDocSnapshotAllContacts
                                    .get(mListDocSnapshotAllContacts.size() - 1);
                            contactsAddedFragmentAdapter.notifyDataSetChanged();
                            mProgressBarBottom.setVisibility(GONE);

                        }
                        else {
                            mProgressBarBottom.setVisibility(GONE);
                            mLastContactVisible = null;

                        }

                    });

            /*nextQuery
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

                            contactsAddedFragmentAdapter.notifyDataSetChanged();
                            mProgressBarBottom.setVisibility(GONE);

                        } else {
                            mProgressBarBottom.setVisibility(GONE);
                            mLastContactVisible = null;

                        }

                    })
                    .addOnFailureListener(e -> {
                        mProgressBarBottom.setVisibility(GONE);
                    });*/

        } else {
            mProgressBarBottom.setVisibility(GONE);
        }
    }



    //REMOVE CONTACT
    public static Observable<List<Talk>> getAllRemoveContacts(List<Talk> contacts, Fragment fragment) {

        Query firstQuery = mUsersCollection
                .document(mAuth.getCurrentUser().getUid())
                .collection(CONTACTS)
                .limit(4);

        if (mListDocSnapshotAllRemoveContacts == null) {
            mListDocSnapshotAllRemoveContacts = new ArrayList<>();
            mListDocSnapshotAllRemoveContactsClear = new ArrayList<>();
        } else {
            mListDocSnapshotAllRemoveContacts.clear();
            mListDocSnapshotAllRemoveContactsClear.clear();
        }


        return Observable.create(subscriber ->

                firstQuery
                        .addSnapshotListener(fragment.getActivity(), (queryDocumentSnapshots, e) -> {

                            if (e != null){
                                subscriber.onNext(null);
                            }

                            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()){

                                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()){

                                    Talk contact = dc.getDocument().toObject(Talk.class);

                                    switch (dc.getType()){
                                        case ADDED:
                                            contacts.add(contact);
                                            mListDocSnapshotAllRemoveContacts.add(dc.getDocument());

                                            break;

                                    }

                                }

                                mLastRemoveContactVisible = mListDocSnapshotAllRemoveContacts.get(mListDocSnapshotAllRemoveContacts.size() - 1);

                                subscriber.onNext(contacts);
                            }
                            else {
                                subscriber.onNext(null);
                            }


                        }));

                /*firstQuery
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {

                            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                    contacts.add(documentSnapshot.toObject(Talk.class));
                                    mListDocSnapshotAllRemoveContacts.add(documentSnapshot);


                                }

                                mLastRemoveContactVisible = mListDocSnapshotAllRemoveContacts
                                        .get(mListDocSnapshotAllRemoveContacts.size() - 1);

                                subscriber.onNext(contacts);
                            }
                            else {
                                subscriber.onNext(null);
                            }
                        })
                        .addOnFailureListener(e -> {

                            subscriber.onNext(null);

                        }));*/

    }

    public static void loadMoreRemoveContacts(List<Talk> contacts, ContactsRemoveFragmentAdapter contactsRemoveFragmentAdapter,
                                              ProgressBar mProgressBarBottom, Fragment fragment) {

        if (mLastRemoveContactVisible != null) {
            Query nextQuery = mUsersCollection
                    .document(mAuth.getCurrentUser().getUid())
                    .collection(CONTACTS)
                    .startAfter(mLastRemoveContactVisible)
                    .limit(4);

            nextQuery
                    .addSnapshotListener(fragment.getActivity(), (queryDocumentSnapshots, e) -> {

                        if (e != null){
                            mProgressBarBottom.setVisibility(GONE);
                            return;
                        }

                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()){

                            mListDocSnapshotAllRemoveContactsClear.clear();

                            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()){

                                switch (dc.getType()){
                                    case ADDED:
                                        if (!mListDocSnapshotAllRemoveContacts.contains(dc.getDocument())) {


                                            mListDocSnapshotAllRemoveContacts.add(dc.getDocument());
                                            mListDocSnapshotAllRemoveContactsClear.add(dc.getDocument());


                                        }
                                        break;

                                    case REMOVED:
                                        contactsRemoveFragmentAdapter.notifyDataSetChanged();
                                        break;

                                }

                            }

                            for (DocumentSnapshot documentSnapshot : mListDocSnapshotAllRemoveContactsClear) {

                                contacts.add(documentSnapshot.toObject(Talk.class));


                            }

                            mLastRemoveContactVisible = mListDocSnapshotAllRemoveContacts
                                    .get(mListDocSnapshotAllRemoveContacts.size() - 1);
                            contactsRemoveFragmentAdapter.notifyDataSetChanged();
                            mProgressBarBottom.setVisibility(GONE);

                        }
                        else {
                            mProgressBarBottom.setVisibility(GONE);
                            mLastRemoveContactVisible = null;

                        }

                    });


            /*nextQuery
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            mListDocSnapshotAllRemoveContactsClear.clear();

                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                if (!mListDocSnapshotAllRemoveContacts.contains(documentSnapshot)) {


                                    mListDocSnapshotAllRemoveContacts.add(documentSnapshot);
                                    mListDocSnapshotAllRemoveContactsClear.add(documentSnapshot);


                                }
                            }

                            mLastRemoveContactVisible = mListDocSnapshotAllRemoveContacts
                                    .get(mListDocSnapshotAllRemoveContacts.size() - 1);


                            for (DocumentSnapshot documentSnapshot : mListDocSnapshotAllRemoveContactsClear) {

                                contacts.add(documentSnapshot.toObject(Talk.class));


                            }

                            contactsRemoveFragmentAdapter.notifyDataSetChanged();
                            mProgressBarBottom.setVisibility(GONE);

                        } else {
                            mProgressBarBottom.setVisibility(GONE);
                            mLastRemoveContactVisible = null;

                        }

                    })
                    .addOnFailureListener(e -> {
                        mProgressBarBottom.setVisibility(GONE);
                    });*/

        } else {
            mProgressBarBottom.setVisibility(GONE);
        }
    }

    public static void showDialog(User contact, Fragment fragment, ContactsRemoveFragmentAdapter mContactsRemoveAdapter) {

        //INSTANCE OF PROGRESS DIALOG
        progress = new ProgressDialog(fragment.getActivity());
        progress.setCancelable(false);
        String message = fragment.getResources().getString(R.string.progress_message_remove_user);
        progress.setMessage(message);

        //CONFIGURATION OF DIALOG
        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getActivity());
        builder.setTitle(fragment.getResources().getString(R.string.dialog_remove_talker_title));
        builder.setMessage(fragment.getResources().getString(R.string.dialog_remove_talker_message_part_1)
                + contact.getName() + fragment.getResources().getString(R.string.dialog_remove_talker_message_part_2));

        String positiveText = fragment.getResources().getString(R.string.positive_button_remove_user);
        builder.setPositiveButton(positiveText, (dialog, which) -> {
            dialog.dismiss();
            showProgressDialog(true);
            removeContact(contact, fragment, mContactsRemoveAdapter);

        });
        String negativeText = fragment.getResources().getString(R.string.negative_button_remove_user);
        builder.setNegativeButton(negativeText, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private static void showProgressDialog(boolean load) {

        if (load) {
            progress.show();
        } else {
            progress.dismiss();
        }
    }

    private static void removeContact(User contact, Fragment fragment, ContactsRemoveFragmentAdapter mContactsRemoveAdapter) {

        mUsersCollection
                .document(mAuth.getCurrentUser().getUid())
                .collection(CONTACTS)
                .document(contact.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {

                    mUsersCollection
                            .document(contact.getId())
                            .collection(CONTACTS)
                            .document(mAuth.getCurrentUser().getUid())
                            .delete()
                            .addOnSuccessListener(aVoid12 -> {
                                showProgressDialog(false);
                                Toast.makeText(fragment.getActivity(),
                                        fragment.getResources().getString(R.string.toast_user_removed),
                                        Toast.LENGTH_SHORT)
                                        .show();
                                mContactsRemoveAdapter.notifyDataSetChanged();

                            })
                            .addOnFailureListener(e -> {
                                e.printStackTrace();
                                showProgressDialog(false);
                                Toast.makeText(fragment.getActivity(), fragment.getResources().getString(R.string.toast_error_remove_contact),
                                        Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    showProgressDialog(false);
                    Toast.makeText(fragment.getActivity(), fragment.getResources().getString(R.string.toast_error_remove_contact),
                            Toast.LENGTH_SHORT).show();
                });



    }




    //CONTACTS INVITATIONS
    public static Observable<List<Talk>> getAllInvitations(List<Talk> contacts, Fragment fragment) {

        Query firstQuery = mUsersCollection
                .document(mAuth.getCurrentUser().getUid())
                .collection(USER_PERMISSIONS)
                .orderBy(DATE_INVITATION, Query.Direction.DESCENDING)
                .limit(4);

        if (mListDocSnapshotAllInvitations == null) {
            mListDocSnapshotAllInvitations = new ArrayList<>();
            mListDocSnapshotAllInvitationsClear = new ArrayList<>();
        } else {
            mListDocSnapshotAllInvitations.clear();
            mListDocSnapshotAllInvitationsClear.clear();
        }


        return Observable.create(subscriber ->

                firstQuery
                        .addSnapshotListener(fragment.getActivity(), (queryDocumentSnapshots, e) -> {

                            if (e != null){
                                subscriber.onNext(null);
                            }

                            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()){

                                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()){

                                    Talk contact = dc.getDocument().toObject(Talk.class);

                                    switch (dc.getType()){
                                        case ADDED:
                                            contacts.add(contact);
                                            mListDocSnapshotAllInvitations.add(dc.getDocument());

                                            break;
                                    }

                                }

                                mLastInvitationVisible = mListDocSnapshotAllInvitations.get(mListDocSnapshotAllInvitations.size() - 1);

                                subscriber.onNext(contacts);
                            }
                            else {
                                subscriber.onNext(null);
                            }


                        }));


        /*firstQuery
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {

                            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                    contacts.add(documentSnapshot.toObject(Talk.class));
                                    mListDocSnapshotAllInvitations.add(documentSnapshot);


                                }

                                mLastInvitationVisible = mListDocSnapshotAllInvitations.get(mListDocSnapshotAllInvitations.size() - 1);

                                subscriber.onNext(contacts);
                            }
                            else {
                                subscriber.onNext(contacts);
                            }
                        })
                        .addOnFailureListener(e -> {

                            subscriber.onNext(null);

                        }));*/

    }

    public static void loadMoreInvitations(List<Talk> contacts, ContactsInvitationsFragmentAdapter contactsInvitationsFragmentAdapter,
                                           ProgressBar mProgressBarBottom, Fragment fragment) {

        if (mLastInvitationVisible != null) {
            Query nextQuery = mUsersCollection
                    .document(mAuth.getCurrentUser().getUid())
                    .collection(USER_PERMISSIONS)
                    .orderBy(DATE_INVITATION, Query.Direction.DESCENDING)
                    .startAfter(mLastInvitationVisible)
                    .limit(4);

            nextQuery
                    .addSnapshotListener(fragment.getActivity(), (queryDocumentSnapshots, e) -> {

                        if (e != null){
                            mProgressBarBottom.setVisibility(GONE);
                            return;
                        }

                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()){

                            mListDocSnapshotAllInvitationsClear.clear();

                            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()){

                                switch (dc.getType()){
                                    case ADDED:
                                        if (!mListDocSnapshotAllInvitations.contains(dc.getDocument())) {

                                            mListDocSnapshotAllInvitations.add(dc.getDocument());
                                            mListDocSnapshotAllInvitationsClear.add(dc.getDocument());

                                        }
                                        break;

                                    case REMOVED:
                                        contactsInvitationsFragmentAdapter.notifyDataSetChanged();
                                        break;


                                }

                            }

                            for (DocumentSnapshot documentSnapshot : mListDocSnapshotAllInvitationsClear) {

                                contacts.add(documentSnapshot.toObject(Talk.class));

                            }

                            mLastInvitationVisible = mListDocSnapshotAllInvitations
                                    .get(mListDocSnapshotAllInvitations.size() - 1);
                            contactsInvitationsFragmentAdapter.notifyDataSetChanged();
                            mProgressBarBottom.setVisibility(GONE);

                        }
                        else {
                            mProgressBarBottom.setVisibility(GONE);
                            mLastInvitationVisible = null;

                        }

                    });

            /*nextQuery
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            mListDocSnapshotAllInvitationsClear.clear();

                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                if (!mListDocSnapshotAllInvitations.contains(documentSnapshot)) {


                                    mListDocSnapshotAllInvitations.add(documentSnapshot);
                                    mListDocSnapshotAllInvitationsClear.add(documentSnapshot);


                                }
                            }

                            mLastInvitationVisible = mListDocSnapshotAllInvitations
                                    .get(mListDocSnapshotAllInvitations.size() - 1);


                            for (DocumentSnapshot documentSnapshot : mListDocSnapshotAllInvitationsClear) {

                                contacts.add(documentSnapshot.toObject(Talk.class));


                            }

                            contactsInvitationsFragmentAdapter.notifyDataSetChanged();
                            mProgressBarBottom.setVisibility(GONE);

                        } else {
                            mProgressBarBottom.setVisibility(GONE);
                            mLastContactVisible = null;

                        }

                    })
                    .addOnFailureListener(e -> {
                        mProgressBarBottom.setVisibility(GONE);
                    });*/

        } else {
            mProgressBarBottom.setVisibility(GONE);
        }
    }


    public static void showDialogToAcceptContact(Talk contact, User userContact, Fragment fragment) {
        //INSTANCE OF PROGRESS DIALOG
        progress = new ProgressDialog(fragment.getActivity());
        progress.setCancelable(false);

        //CONFIGURATION OF DIALOG
        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getActivity());
        builder.setTitle(fragment.getResources().getString(R.string.add_contact));
        builder.setMessage(fragment.getResources().getString(R.string.accept_contact)
                + '"' + userContact.getName() + '"' + fragment.getResources().getString(R.string.as_a_contact));

        String positiveText = fragment.getResources().getString(R.string.yes);
        builder.setPositiveButton(positiveText, (dialog, which) -> {
            dialog.dismiss();
            showProgressDialog(true);
            addContactIntoFirestore(contact, fragment);

        });
        String negativeText = fragment.getResources().getString(R.string.no);;
        builder.setNegativeButton(negativeText, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void showDialogToDenyInvitation(Talk contact, User userContact, Fragment fragment) {
        //INSTANCE OF PROGRESS DIALOG
        progress = new ProgressDialog(fragment.getActivity());
        progress.setCancelable(false);

        //CONFIGURATION OF DIALOG
        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getActivity());
        builder.setTitle(fragment.getResources().getString(R.string.dont_add_contact));

        builder.setMessage(fragment.getResources().getString(R.string.removing_invitation)
                + '"' + userContact.getName() + '"' + "?");

        String positiveText = fragment.getResources().getString(R.string.yes);
        builder.setPositiveButton(positiveText, (dialog, which) -> {
            dialog.dismiss();
            showProgressDialog(true);
            removeInvitationsAndPermissionAfterDenied(contact, fragment);

        });
        String negativeText = fragment.getResources().getString(R.string.no);;
        builder.setNegativeButton(negativeText, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private static void showToast(Context context, String message){
        Toast.makeText(context,
                message,
                Toast.LENGTH_SHORT).show();
    }

    private static void removeInvitationsAndPermissionAfterDenied(Talk contact, Fragment fragment) {

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
                                showToast(fragment.getActivity(), fragment.getResources().getString(R.string.invitation_removed));

                            })
                            .addOnFailureListener(e -> {
                                e.printStackTrace();
                                showProgressDialog(false);
                                showToast(fragment.getActivity(), fragment.getResources().getString(R.string.toast_error_remove_invitation));

                            });


                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    showProgressDialog(false);
                    showToast(fragment.getActivity(), fragment.getResources().getString(R.string.toast_error_remove_invitation));

                });

    }


    private static void addContactIntoFirestore(Talk contact, Fragment fragment) {

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
                                            removeInvitationsAndPermissionAfterAccepted(contact, fragment);
                                        })
                                        .addOnFailureListener(e -> {
                                            e.printStackTrace();
                                            showProgressDialog(false);
                                            showToast(fragment.getActivity(), fragment.getResources().getString(R.string.toast_error_add_contact));
                                        });

                            })
                            .addOnFailureListener(e -> {
                                e.printStackTrace();
                                showProgressDialog(false);
                                showToast(fragment.getActivity(), fragment.getResources().getString(R.string.toast_error_add_contact));
                            });

                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    showProgressDialog(false);
                    showToast(fragment.getActivity(), fragment.getResources().getString(R.string.toast_error_add_contact));
                });


    }


    private static void removeInvitationsAndPermissionAfterAccepted(Talk contact, Fragment fragment) {


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
                                showToast(fragment.getActivity(), fragment.getResources().getString(R.string.contact_added));
                            })
                            .addOnFailureListener(e -> {
                                e.printStackTrace();
                                showProgressDialog(false);
                                showToast(fragment.getActivity(), fragment.getResources().getString(R.string.toast_error_add_contact));

                            });

                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    showProgressDialog(false);
                    showToast(fragment.getActivity(), fragment.getResources().getString(R.string.toast_error_add_contact));
                });

    }



    //CONTACTS REQUEST
    public static Observable<List<Talk>> getAllContactRequests(List<Talk> requests, Fragment fragment) {

        Query firstQuery = mUsersCollection
                .document(mAuth.getCurrentUser().getUid())
                .collection(USER_INVITATIONS)
                .orderBy(DATE_INVITATION, Query.Direction.DESCENDING)
                .limit(4);

        if (mListDocSnapshotAllContactRequests == null) {
            mListDocSnapshotAllContactRequests = new ArrayList<>();
            mListDocSnapshotAllContactRequestsClear = new ArrayList<>();
        } else {
            mListDocSnapshotAllContactRequests.clear();
            mListDocSnapshotAllContactRequestsClear.clear();
        }


        return Observable.create(subscriber ->

                firstQuery
                        .addSnapshotListener(fragment.getActivity(), (queryDocumentSnapshots, e) -> {

                            if (e != null){
                                subscriber.onNext(null);
                            }

                            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()){

                                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()){

                                    Talk contact = dc.getDocument().toObject(Talk.class);

                                    switch (dc.getType()){
                                        case ADDED:
                                            requests.add(contact);
                                            mListDocSnapshotAllContactRequests.add(dc.getDocument());

                                            break;
                                    }

                                }

                                mLastContactRequestsVisible = mListDocSnapshotAllContactRequests.get(mListDocSnapshotAllContactRequests.size() - 1);

                                subscriber.onNext(requests);
                            }
                            else {
                                subscriber.onNext(null);
                            }


                        }));


        /*firstQuery
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {

                            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                    requests.add(documentSnapshot.toObject(Talk.class));
                                    mListDocSnapshotAllContactRequests.add(documentSnapshot);


                                }

                                mLastContactRequestsVisible = mListDocSnapshotAllContactRequests.get(mListDocSnapshotAllContactRequests.size() - 1);

                                subscriber.onNext(requests);
                            }
                            else {
                                subscriber.onNext(null);
                            }
                        })
                        .addOnFailureListener(e -> {
                            subscriber.onNext(null);
                        }));*/

    }

    public static void loadMoreContactsRequests(List<Talk> requests, ContactsRequestFragmentAdapter contactsRequestFragmentAdapter,
                                                ProgressBar mProgressBarBottom, Fragment fragment) {

        if (mLastContactRequestsVisible!= null) {
            Query nextQuery = mUsersCollection
                    .document(mAuth.getCurrentUser().getUid())
                    .collection(USER_INVITATIONS)
                    .orderBy(DATE_INVITATION, Query.Direction.DESCENDING)
                    .startAfter(mLastContactRequestsVisible)
                    .limit(4);


            nextQuery
                    .addSnapshotListener(fragment.getActivity(), (queryDocumentSnapshots, e) -> {

                        if (e != null){
                            mProgressBarBottom.setVisibility(GONE);
                            return;
                        }

                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()){

                            mListDocSnapshotAllContactRequestsClear.clear();

                            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()){

                                switch (dc.getType()){
                                    case ADDED:
                                        if (!mListDocSnapshotAllContactRequests.contains(dc.getDocument())) {


                                            mListDocSnapshotAllContactRequests.add(dc.getDocument());
                                            mListDocSnapshotAllContactRequestsClear.add(dc.getDocument());


                                        }
                                        break;

                                    case REMOVED:
                                        contactsRequestFragmentAdapter.notifyDataSetChanged();
                                        break;


                                }

                            }


                            for (DocumentSnapshot documentSnapshot : mListDocSnapshotAllContactRequestsClear) {

                                requests.add(documentSnapshot.toObject(Talk.class));

                            }
                            mLastContactRequestsVisible = mListDocSnapshotAllContactRequests
                                    .get(mListDocSnapshotAllContactRequests.size() - 1);
                            contactsRequestFragmentAdapter.notifyDataSetChanged();
                            mProgressBarBottom.setVisibility(GONE);

                        }
                        else {
                            mProgressBarBottom.setVisibility(GONE);
                            mLastContactRequestsVisible = null;

                        }

                    });


            /*nextQuery
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            mListDocSnapshotAllContactRequestsClear.clear();

                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                if (!mListDocSnapshotAllContactRequests.contains(documentSnapshot)) {


                                    mListDocSnapshotAllContactRequests.add(documentSnapshot);
                                    mListDocSnapshotAllContactRequestsClear.add(documentSnapshot);


                                }
                            }

                            mLastContactRequestsVisible = mListDocSnapshotAllContactRequests
                                    .get(mListDocSnapshotAllContactRequests.size() - 1);


                            for (DocumentSnapshot documentSnapshot : mListDocSnapshotAllContactRequestsClear) {

                                requests.add(documentSnapshot.toObject(Talk.class));

                            }

                            contactsRequestFragmentAdapter.notifyDataSetChanged();
                            mProgressBarBottom.setVisibility(GONE);

                        } else {
                            mProgressBarBottom.setVisibility(GONE);
                            mLastContactVisible = null;

                        }

                    })
                    .addOnFailureListener(e -> {
                        mProgressBarBottom.setVisibility(GONE);
                    });*/

        } else {
            mProgressBarBottom.setVisibility(GONE);
        }
    }

    public static void showDialogToCancelInvitation(User contact, Fragment fragment) {
        progress = new ProgressDialog(fragment.getActivity());
        progress.setCanceledOnTouchOutside(false);
        progress.setMessage(fragment.getResources().getString(R.string.progress_message_cancel_invitation_user));


        //CONFIGURATION OF DIALOG
        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getActivity());
        builder.setTitle(fragment.getResources().getString(R.string.cancel_invitation));

        builder.setMessage(fragment.getResources().getString(R.string.cancel_invitation_message)
                + contact.getName() + "(" + contact.getUsername()
                + ")?");

        String positiveText = fragment.getResources().getString(R.string.cancel_invitation_btn);
        builder.setPositiveButton(positiveText, (dialog, which) -> {
            dialog.dismiss();
            showProgressDialog(true);
            cancelInvitation(contact, fragment);


        });

        String negativeText = fragment.getResources().getString(R.string.no);
        builder.setNegativeButton(negativeText, (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private static void cancelInvitation(User contact, Fragment fragment) {

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
                                Toast.makeText(fragment.getActivity(),
                                        fragment.getResources().getString(R.string.invitation_canceled),
                                        Toast.LENGTH_SHORT).show();

                            })
                            .addOnFailureListener(e -> {
                                e.printStackTrace();
                                showProgressDialog(false);
                                Toast.makeText(fragment.getActivity(),
                                        fragment.getResources().getString(R.string.toast_error_canceling_invitation),
                                        Toast.LENGTH_SHORT).show();

                            });

                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    showProgressDialog(false);
                    Toast.makeText(fragment.getActivity(),
                            fragment.getResources().getString(R.string.toast_error_canceling_invitation),
                            Toast.LENGTH_SHORT).show();

                });
    }

}
