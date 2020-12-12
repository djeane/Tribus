package apptribus.com.tribus.activities.detail_talker.repository;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.detail_talker.mvp.DetailTalkerView;
import apptribus.com.tribus.activities.main_activity.MainActivity;
import apptribus.com.tribus.pojo.Talk;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;

import static apptribus.com.tribus.util.Constantes.CONTACTS;
import static apptribus.com.tribus.util.Constantes.CONTACTS_ACCEPTED;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.USER_INVITATIONS;
import static apptribus.com.tribus.util.Constantes.USER_PERMISSIONS;

public class DetailTalkerAPI {


    //INSTANCES - FIREBASE
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    //FIRESTORE INSTANCE
    private static FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();


    //FIRESTORE COLLECTIONS REFERENCES
    private static CollectionReference mUsersCollections = mFirestore.collection(GENERAL_USERS);

    //SHOW
    private static ProgressDialog progress;


    //REMOVE CONTACT IF PROFILE PUBLIC
    public static void showDialogToRemoveContact(AppCompatActivity activity, String contactId, String mFromChatTribus) {
        //INSTANCE OF PROGRESS DIALOG
        progress = new ProgressDialog(activity);
        progress.setCancelable(false);

        mUsersCollections
                .document(contactId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    User contact = documentSnapshot.toObject(User.class);

                    //CONFIGURATION OF DIALOG
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle(activity.getResources().getString(R.string.remove_contact));

                    builder.setMessage(activity.getResources().getString(R.string.remove_contact_question)
                            + '"'+ contact.getName() + '"' + "?");

                    String positiveText = activity.getResources().getString(R.string.yes);
                    builder.setPositiveButton(positiveText, (dialog, which) -> {
                        dialog.dismiss();
                        showProgressDialog(true);
                        removeContact(contact, activity, mFromChatTribus);

                    });
                    String negativeText = activity.getResources().getString(R.string.no);
                    builder.setNegativeButton(negativeText, (dialog, which) -> dialog.dismiss());

                    AlertDialog dialog = builder.create();
                    dialog.show();

                })
                .addOnFailureListener(Throwable::printStackTrace);
    }

    private static void removeContact(User contact, AppCompatActivity activity, String mFromChatTribus) {

        //REMOVING CONTACT FROM CONTACTS COLLECTIONS
        mUsersCollections
                .document(mAuth.getCurrentUser().getUid())
                .collection(CONTACTS)
                .document(contact.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {

                    mUsersCollections
                            .document(contact.getId())
                            .collection(CONTACTS)
                            .document(mAuth.getCurrentUser().getUid())
                            .delete()
                            .addOnSuccessListener(aVoid12 -> {

                                showProgressDialog(false);
                                showToast(activity, activity.getResources().getString(R.string.contact_removed_successfully));

                                if (mFromChatTribus != null) {
                                    activity.finish();
                                } else {
                                    backToMainActivity(activity);

                                }

                            })
                            .addOnFailureListener(e -> {
                                e.printStackTrace();
                                showProgressDialog(false);
                                showToast(activity, activity.getResources().getString(R.string.toast_error_remove_contact));

                            });
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    showProgressDialog(false);
                    showToast(activity, activity.getResources().getString(R.string.toast_error_remove_contact));

                });

    }

    private static void showToast(AppCompatActivity activity, String message){
        Toast.makeText(activity,
                message,
                Toast.LENGTH_SHORT).show();

    }


    //ADD CONTACT IF IS PROFILE PUBLIC
    public static void showDialogAddContact(String tribusKey, AppCompatActivity activity, String mContactId,
                                            DetailTalkerView view) {
        //INSTANCE OF PROGRESS DIALOG
        progress = new ProgressDialog(activity);
        progress.setCancelable(false);


        mUsersCollections
                .document(mContactId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    User contact = documentSnapshot.toObject(User.class);

                    //CONFIGURATION OF DIALOG
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle(activity.getResources().getString(R.string.add_contact));
                    builder.setMessage(activity.getResources().getString(R.string.accept_contact)
                            + '"'+ contact.getName() + '"' + activity.getResources().getString(R.string.as_a_contact));

                    String positiveText = activity.getResources().getString(R.string.yes);
                    builder.setPositiveButton(positiveText, (dialog, which) -> {
                        dialog.dismiss();
                        showProgressDialog(true);
                        addContact(tribusKey, contact, activity, view);

                    });
                    String negativeText = activity.getResources().getString(R.string.no);
                    builder.setNegativeButton(negativeText, (dialog, which) -> dialog.dismiss());

                    AlertDialog dialog = builder.create();
                    dialog.show();

                })
                .addOnFailureListener(Throwable::printStackTrace);

    }

    private static void addContact(String tribusKey, User contact, AppCompatActivity activity, DetailTalkerView view) {

        Date date = new Date(System.currentTimeMillis());

        Talk contactRight = new Talk();
        contactRight.setTribuKey(tribusKey);
        contactRight.setTalkerId(contact.getId());
        contactRight.setFromPermission(false);
        contactRight.setDateAccepted(date);

        Talk contactLeft = new Talk();
        contactLeft.setTalkerId(mAuth.getCurrentUser().getUid());
        contactLeft.setTribuKey(tribusKey);
        contactLeft.setFromPermission(false);
        contactLeft.setDateAccepted(date);

        mUsersCollections
                .document(mAuth.getCurrentUser().getUid())
                .collection(CONTACTS)
                .document(contact.getId())
                .set(contactRight)
                .addOnSuccessListener(aVoid -> {

                    mUsersCollections
                            .document(contact.getId())
                            .collection(CONTACTS)
                            .document(mAuth.getCurrentUser().getUid())
                            .set(contactLeft)
                            .addOnSuccessListener(aVoid1 -> {

                                showProgressDialog(false);
                                showToast(activity, activity.getResources().getString(R.string.contact_added));
                                view.mBtnRemoveTalker.setVisibility(View.VISIBLE);
                                view.mBtnAddTalker.setVisibility(View.GONE);
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


    //ADD TALKER IF INVITED AND PROFILE PRIVATE OR PROFILE PUBLIC
    public static void addContactIfInvitedAndProfilePrivate(String tribusKey, AppCompatActivity activity,
                                                            String mContactId, DetailTalkerView view) {


        if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
            ShowSnackBarInfoInternet.showToastInfoInternet(activity);
        } else {
            ShowSnackBarInfoInternet.showSnack(true, view);
            showDialogAddContactIfInvitedAndProfilePrivate(activity, mContactId, tribusKey, view);
        }
    }

    private static void showDialogAddContactIfInvitedAndProfilePrivate(AppCompatActivity activity, String contactId,
                                                                       String tribusKey, DetailTalkerView view) {
        //INSTANCE OF PROGRESS DIALOG
        progress = new ProgressDialog(activity);
        progress.setCancelable(false);

        mUsersCollections
                .document(mAuth.getCurrentUser().getUid())
                .collection(USER_PERMISSIONS)
                .document(contactId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {

                        Talk contactRight = documentSnapshot.toObject(Talk.class);

                        mUsersCollections
                                .document(contactRight.getTalkerId())
                                .get()
                                .addOnSuccessListener(documentSnapshot1 -> {

                                    User userContact = documentSnapshot.toObject(User.class);

                                    //CONFIGURATION OF DIALOG
                                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                    builder.setTitle(activity.getResources().getString(R.string.accept_invitation));

                                    builder.setMessage(activity.getResources().getString(R.string.accept_contact)
                                            +'"'+ userContact.getName() + '"' + activity.getResources().getString(R.string.as_a_contact));

                                    String positiveText = activity.getResources().getString(R.string.yes);
                                    builder.setPositiveButton(positiveText, (dialog, which) -> {
                                        dialog.dismiss();
                                        showProgressDialog(true);
                                        sendContactToContactsCollection(activity, userContact, contactRight, tribusKey, view);

                                    });
                                    String negativeText = activity.getResources().getString(R.string.no);
                                    builder.setNegativeButton(negativeText, (dialog, which) -> dialog.dismiss());

                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                })
                                .addOnFailureListener(Throwable::printStackTrace);
                    }
                })
                .addOnFailureListener(Throwable::printStackTrace);
    }

    private static void sendContactToContactsCollection(AppCompatActivity activity, User userContact,
                                                        Talk contactRight, String tribusKey, DetailTalkerView view) {

        Date date = new Date(System.currentTimeMillis());

        Talk otherContact = new Talk();
        otherContact.setTribuKey(tribusKey);
        otherContact.setTalkerId(userContact.getId());
        otherContact.setFromPermission(true);
        otherContact.setDateAccepted(date);

        Talk currentContact = new Talk();
        currentContact.setTalkerId(mAuth.getCurrentUser().getUid());
        currentContact.setTribuKey(tribusKey);
        currentContact.setFromPermission(true);
        currentContact.setDateAccepted(date);


        mUsersCollections
                .document(mAuth.getCurrentUser().getUid())
                .collection(CONTACTS)
                .document(contactRight.getTalkerId())
                .set(otherContact)
                .addOnSuccessListener(aVoid -> {

                    mUsersCollections
                            .document(contactRight.getTalkerId())
                            .collection(CONTACTS)
                            .document(mAuth.getCurrentUser().getUid())
                            .set(currentContact)
                            .addOnSuccessListener(aVoid12 -> {

                                mUsersCollections
                                        .document(mAuth.getCurrentUser().getUid())
                                        .collection(CONTACTS_ACCEPTED)
                                        .document(otherContact.getTalkerId())
                                        .set(currentContact)
                                        .addOnSuccessListener(aVoid13 -> {
                                            removeInvitationsAndPermissionAfterAccepted(contactRight, activity, view);
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

    private static void removeInvitationsAndPermissionAfterAccepted(Talk contact, AppCompatActivity activity, DetailTalkerView view) {

        mUsersCollections
                .document(mAuth.getCurrentUser().getUid())
                .collection(USER_PERMISSIONS)
                .document(contact.getTalkerId())
                .delete()
                .addOnSuccessListener(aVoid -> {

                    mUsersCollections
                            .document(contact.getTalkerId())
                            .collection(USER_INVITATIONS)
                            .document(mAuth.getCurrentUser().getUid())
                            .delete()
                            .addOnSuccessListener(aVoid12 -> {
                                showProgressDialog(false);

                                showToast(activity, activity.getResources().getString(R.string.contact_added));

                                view.mRelativeMiddle.setVisibility(View.VISIBLE);
                                view.mRelativeBottom.setVisibility(View.VISIBLE);
                                view.mRelativePrivate.setVisibility(View.GONE);
                                view.mLinearButtonsPublicAdd.setVisibility(View.VISIBLE);
                                view.mLinearButtonsPublicAccept.setVisibility(View.GONE);
                                view.mBtnRemoveTalker.setVisibility(View.VISIBLE);
                                view.mBtnAddTalker.setVisibility(View.GONE);

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

    //EXCLUDE INVITATION IF PROFILE PRIVATE
    public static void excludeInvitationIfProfilePrivateAndPublic(AppCompatActivity activity, String mContactId,
                                                                  DetailTalkerView view) {

        if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
            ShowSnackBarInfoInternet.showToastInfoInternet(activity);
        } else {
            ShowSnackBarInfoInternet.showSnack(true, view);
            showDialogToDenied(activity, mContactId, view);
        }
    }

    private static void showDialogToDenied(AppCompatActivity activity, String contactId,
                                           DetailTalkerView view) {
        //INSTANCE OF PROGRESS DIALOG
        progress = new ProgressDialog(activity);
        progress.setCancelable(false);

        mUsersCollections
                .document(contactId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    User userContact = documentSnapshot.toObject(User.class);

                    //CONFIGURATION OF DIALOG
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle(activity.getResources().getString(R.string.dont_add_contact));

                    builder.setMessage(activity.getResources().getString(R.string.removing_invitation)
                            + '"' + userContact.getName() + '"' + "?");

                    String positiveText = activity.getResources().getString(R.string.yes);
                    builder.setPositiveButton(positiveText, (dialog, which) -> {
                        dialog.dismiss();
                        showProgressDialog(true);
                        removeInvitationsAndPermissionAfterDenied(activity, userContact, view);

                    });
                    String negativeText = activity.getResources().getString(R.string.no);
                    builder.setNegativeButton(negativeText, (dialog, which) -> dialog.dismiss());

                    AlertDialog dialog = builder.create();
                    dialog.show();

                })
                .addOnFailureListener(Throwable::printStackTrace);

    }

    private static void removeInvitationsAndPermissionAfterDenied(AppCompatActivity activity, User userContact,
                                                                  DetailTalkerView view) {
        mUsersCollections
                .document(mAuth.getCurrentUser().getUid())
                .collection(USER_PERMISSIONS)
                .document(userContact.getId())
                .delete()
                .addOnSuccessListener(documentSnapshot -> {

                    mUsersCollections
                            .document(userContact.getId())
                            .collection(USER_INVITATIONS)
                            .document(mAuth.getCurrentUser().getUid())
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                showProgressDialog(false);
                                showToast(activity, activity.getResources().getString(R.string.invitation_removed));

                                if (userContact.isAccepted()) {
                                    view.mRelativeMiddle.setVisibility(View.VISIBLE);
                                    view.mRelativeBottom.setVisibility(View.VISIBLE);
                                    view.mRelativePrivate.setVisibility(View.GONE);
                                    view.mLinearButtonsPublicAdd.setVisibility(View.VISIBLE);
                                    view.mLinearButtonsPublicAccept.setVisibility(View.GONE);
                                    view.mBtnAddTalker.setVisibility(View.VISIBLE);
                                    view.mBtnRemoveTalker.setVisibility(View.GONE);
                                } else {
                                    view.mRelativeMiddle.setVisibility(View.GONE);
                                    view.mRelativeBottom.setVisibility(View.GONE);
                                    view.mRelativePrivate.setVisibility(View.VISIBLE);
                                    view.mLinearButtonsPrivateAdded.setVisibility(View.VISIBLE);
                                    view.mLinearButtonsPrivateAccept.setVisibility(View.GONE);
                                    view.mBtnPrivateAdded.setVisibility(View.VISIBLE);
                                    view.mBtnCancelPrivateAdded.setVisibility(View.GONE);
                                }

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


    //INVITE TALKER IF PROFILE PRIVATE
    public static void showDialogAddTalkerIfPrivate(String tribusKey, AppCompatActivity activity, String contactId,
                                                    DetailTalkerView view) {
        //INSTANCE OF PROGRESS DIALOG
        progress = new ProgressDialog(activity);
        progress.setCancelable(false);

        mUsersCollections
                .document(contactId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    User contact = documentSnapshot.toObject(User.class);

                    //CONFIGURATION OF DIALOG
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle(activity.getResources().getString(R.string.add_contact));

                    builder.setMessage(activity.getResources().getString(R.string.accept_contact)
                            + '"'+ contact.getName() + '"' + activity.getResources().getString(R.string.as_a_contact));

                    String positiveText = activity.getResources().getString(R.string.yes);
                    builder.setPositiveButton(positiveText, (dialog, which) -> {
                        dialog.dismiss();
                        showProgressDialog(true);
                        addContactIfPrivate(tribusKey, contact, activity, view);

                    });
                    String negativeText = activity.getResources().getString(R.string.no);
                    builder.setNegativeButton(negativeText, (dialog, which) -> dialog.dismiss());

                    AlertDialog dialog = builder.create();
                    dialog.show();

                })
                .addOnFailureListener(Throwable::printStackTrace);

    }

    private static void addContactIfPrivate(String tribusKey, User contactInvited, AppCompatActivity activity,
                                            DetailTalkerView view) {


        Date date = new Date(System.currentTimeMillis());

        Talk contactRight = new Talk();
        contactRight.setTribuKey(tribusKey);
        contactRight.setTalkerId(contactInvited.getId());
        contactRight.setFromPermission(true);
        contactRight.setDateInvitation(date);

        Talk contactLeft = new Talk();
        contactLeft.setTalkerId(mAuth.getCurrentUser().getUid());
        contactLeft.setTribuKey(tribusKey);
        contactLeft.setFromPermission(true);
        contactLeft.setDateInvitation(date);


        mUsersCollections
                .document(contactInvited.getId())
                .collection(USER_PERMISSIONS)
                .document(mAuth.getCurrentUser().getUid())
                .set(contactLeft)
                .addOnSuccessListener(aVoid -> {

                    mUsersCollections
                            .document(mAuth.getCurrentUser().getUid())
                            .collection(USER_INVITATIONS)
                            .document(contactInvited.getId())
                            .set(contactRight)
                            .addOnSuccessListener(aVoid1 -> {
                                showProgressDialog(false);
                                showToast(activity, activity.getResources().getString(R.string.toast_alert_invitation_sended));
                                view.mBtnPrivateAdded.setVisibility(View.GONE);
                                view.mBtnCancelPrivateAdded.setVisibility(View.VISIBLE);
                            })
                            .addOnFailureListener(e -> {
                                e.printStackTrace();
                                showProgressDialog(false);
                                showToast(activity, activity.getResources().getString(R.string.toast_erro_send_invitation));

                            });
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    showProgressDialog(false);
                    showToast(activity, activity.getResources().getString(R.string.toast_erro_send_invitation));

                });

    }


    //CANCEL INVITATION IF PROFILE PRIVATE
    public static void cancelInvitationTalkerPrivate(AppCompatActivity activity, String contactId,
                                                     DetailTalkerView view) {
        //CHECK INTERNET CONNECTION
        if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
            ShowSnackBarInfoInternet.showToastInfoInternet(activity);
        } else {
            ShowSnackBarInfoInternet.showSnack(true, view);
            openDialogCancelInvitation(activity, contactId, view);
        }
    }

    private static void openDialogCancelInvitation(AppCompatActivity activity, String contactId,
                                                   DetailTalkerView view) {

        progress = new ProgressDialog(activity);
        progress.setMessage(activity.getResources().getString(R.string.canceling_invitation));
        progress.setCanceledOnTouchOutside(false);


        mUsersCollections
                .document(contactId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    User userContact = documentSnapshot.toObject(User.class);

                    //CONFIGURATION OF DIALOG
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                    builder.setTitle(activity.getResources().getString(R.string.cancel_invitation));

                    builder.setMessage(activity.getResources().getString(R.string.cancel_invitation_message)
                            + '"' + userContact.getName() + "(" + userContact.getUsername()
                            + ") ?");

                    String positiveText = activity.getResources().getString(R.string.cancel_invitation_btn);

                    builder.setPositiveButton(positiveText, (dialog, which) -> {
                        dialog.dismiss();
                        showProgressDialog(true);
                        cancelInvitation(activity, contactId, view);
                    });

                    String negativeText = activity.getResources().getString(R.string.no);
                    builder.setNegativeButton(negativeText, (dialog, which) -> {
                        dialog.dismiss();
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();

                })
                .addOnFailureListener(Throwable::printStackTrace);

    }

    private static void cancelInvitation(AppCompatActivity activity, String contatoId,
                                         DetailTalkerView view) {


        mUsersCollections
                .document(mAuth.getCurrentUser().getUid())
                .collection(USER_INVITATIONS)
                .document(contatoId)
                .delete()
                .addOnSuccessListener(aVoid -> {

                    mUsersCollections
                            .document(contatoId)
                            .collection(USER_PERMISSIONS)
                            .document(mAuth.getCurrentUser().getUid())
                            .delete()
                            .addOnSuccessListener(aVoid12 -> {
                                showProgressDialog(false);
                                showToast(activity, activity.getResources().getString(R.string.invitation_canceled));
                                view.mRelativeMiddle.setVisibility(View.GONE);
                                view.mRelativeBottom.setVisibility(View.GONE);
                                view.mRelativePrivate.setVisibility(View.VISIBLE);
                                view.mLinearButtonsPrivateAdded.setVisibility(View.VISIBLE);
                                view.mLinearButtonsPrivateAccept.setVisibility(View.GONE);
                                view.mBtnPrivateAdded.setVisibility(View.VISIBLE);
                                view.mBtnCancelPrivateAdded.setVisibility(View.GONE);

                            })
                            .addOnFailureListener(e -> {
                                e.printStackTrace();
                                showProgressDialog(false);
                                showToast(activity, activity.getResources().getString(R.string.toast_error_canceling_invitation));

                            });

                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    showProgressDialog(false);
                    showToast(activity, activity.getResources().getString(R.string.toast_error_canceling_invitation));

                });

    }


    //SHOW PROGRESS
    private static void showProgressDialog(boolean load) {

        if (load) {
            progress.show();
        } else {
            progress.dismiss();
        }
    }


    public static void backToMainActivity(AppCompatActivity activity) {

        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }


}
