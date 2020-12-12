package apptribus.com.tribus.activities.block_user.repository;

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
import apptribus.com.tribus.activities.block_user.adapter.BlockUserAdapter;
import apptribus.com.tribus.pojo.Talk;
import apptribus.com.tribus.pojo.User;
import rx.Observable;

import static android.view.View.GONE;
import static apptribus.com.tribus.util.Constantes.CONTACTS;
import static apptribus.com.tribus.util.Constantes.DATE;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;


public class BlockUserAPI {


    //FIRESTORE INSTANCE
    private static FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    //FIRESTORE COLLECTIONS REFERENCES
    private static CollectionReference mUsersCollection = mFirestore.collection(GENERAL_USERS);


    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static ProgressDialog progress;
    private static DocumentSnapshot mLastContactVisible = null;
    private static List<DocumentSnapshot> mListDocSnapshotAllContacts;
    private static List<DocumentSnapshot> mListDocSnapshotAllContactsClear;


    //get all followers
    public static Observable<List<Talk>> getAllContacts(List<Talk> contacts) {

        Query firstQuery = mUsersCollection
                .document(mAuth.getCurrentUser().getUid())
                .collection(CONTACTS)
                .orderBy(DATE, Query.Direction.DESCENDING)
                .limit(4);

        if (mListDocSnapshotAllContacts == null) {
            mListDocSnapshotAllContacts = new ArrayList<>();
            mListDocSnapshotAllContactsClear = new ArrayList<>();
        }
        else {
            mListDocSnapshotAllContacts.clear();
            mListDocSnapshotAllContactsClear.clear();
        }



        return Observable.create(subscriber ->

                firstQuery
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {

                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                Talk contact = documentSnapshot.toObject(Talk.class);

                                contacts.add(contact);
                                mListDocSnapshotAllContacts.add(documentSnapshot);


                            }

                            mLastContactVisible = mListDocSnapshotAllContacts.get(mListDocSnapshotAllContacts.size() - 1);

                            subscriber.onNext(contacts);
                        })
                        .addOnFailureListener(e -> {

                            subscriber.onNext(null);

                        }));

    }

    //load more all tribus
    public static void loadMoreContacts(List<Talk> contacts, BlockUserAdapter blockUserAdapter,
                                         ProgressBar mProgressBarBottom) {

        if (mLastContactVisible != null) {
            Query nextQuery = mUsersCollection
                    .document(mAuth.getCurrentUser().getUid())
                    .collection(CONTACTS)
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

                                Talk contact = documentSnapshot.toObject(Talk.class);

                                    contacts.add(contact);


                            }

                            blockUserAdapter.notifyDataSetChanged();
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


    public static void showDialog(User contact, AppCompatActivity activity) {

        //INSTANCE OF PROGRESS DIALOG
        progress = new ProgressDialog(activity);
        progress.setCancelable(false);
        String message = activity.getResources().getString(R.string.progress_message_remove_user);
        progress.setMessage(message);

        //CONFIGURATION OF DIALOG
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getResources().getString(R.string.dialog_remove_talker_title));
        builder.setMessage(activity.getResources().getString(R.string.dialog_remove_talker_message_part_1)
                + contact.getName() + activity.getResources().getString(R.string.dialog_remove_talker_message_part_2));

        String positiveText = activity.getResources().getString(R.string.positive_button_remove_user);
        builder.setPositiveButton(positiveText, (dialog, which) -> {
            dialog.dismiss();
            showProgressDialog(true);
            removeContact(contact, activity);

        });
        String negativeText = activity.getResources().getString(R.string.negative_button_remove_user);
        builder.setNegativeButton(negativeText, (dialog, which) -> dialog.dismiss());

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

    private static void removeContact(User contact, AppCompatActivity activity) {

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
                                Toast.makeText(activity,
                                        activity.getResources().getString(R.string.toast_user_removed),
                                        Toast.LENGTH_SHORT)
                                        .show();

                            })
                            .addOnFailureListener(e -> {
                                e.printStackTrace();
                                showProgressDialog(false);
                                Toast.makeText(activity, activity.getResources().getString(R.string.toast_error_remove_contact),
                                        Toast.LENGTH_SHORT).show();
                            });

                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    showProgressDialog(false);
                    Toast.makeText(activity, activity.getResources().getString(R.string.toast_error_remove_contact),
                            Toast.LENGTH_SHORT).show();
                });



    }




    /*private void removeAndBlockTalker(Talk talker, AppCompatActivity activity, long talkerKey) {
        if (talkerKey != 0) {
            //REMOVES TALKER TO CURRENT USER NODE
            mRefTalkers
                    .child(mAuth.getCurrentUser().getUid())
                    .child(talker.getTalkerId())
                    .removeValue()
                    .addOnSuccessListener(aVoid -> {
                        //REMOVES CURRENT USER TO TALKER NODE
                        mRefTalkers
                                .child(talker.getTalkerId())
                                .child(mAuth.getCurrentUser().getUid())
                                .removeValue()
                                .addOnSuccessListener(aVoid1 -> {

                                    Talk newTalker = new Talk(talker.getNameTribu(), talker.getUniqueNameTribu(), talker.getTalkerId());
                                    //BLOCKS CURRENT TALKER FOR THE CURRENT USER
                                    mRefBlockedTalkers
                                            .child(mAuth.getCurrentUser().getUid())
                                            .child(talker.getTalkerId())
                                            .setValue(newTalker)
                                            .addOnSuccessListener(aVoid2 -> {
                                                Toast.makeText(activity.getApplicationContext(), "Usuário removido e bloqueado com sucesso!", Toast.LENGTH_SHORT)
                                                        .show();
                                                backToMainActivity(activity);
                                            })
                                            .addOnFailureListener(Throwable::getLocalizedMessage);
                                })
                                .addOnFailureListener(Throwable::getLocalizedMessage);
                    })
                    .addOnFailureListener(Throwable::getLocalizedMessage);

        }
    }

    private static void backToMainActivity(AppCompatActivity activity) {
        activity.finish();

    }*/

}
