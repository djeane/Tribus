package apptribus.com.tribus.activities.main_activity.fragment_talks.mvp;

import android.support.v4.app.Fragment;
import android.widget.ProgressBar;

import java.util.List;

import apptribus.com.tribus.activities.invitations_request_user.adapter.InvitationRequestUserAdapter;
import apptribus.com.tribus.activities.invitations_request_user.repository.InvitationsRequestUserAPI;
import apptribus.com.tribus.activities.main_activity.fragment_talks.adapter.ContactsAddedFragmentAdapter;
import apptribus.com.tribus.activities.main_activity.fragment_talks.adapter.ContactsInvitationsFragmentAdapter;
import apptribus.com.tribus.activities.main_activity.fragment_talks.adapter.ContactsRemoveFragmentAdapter;
import apptribus.com.tribus.activities.main_activity.fragment_talks.adapter.ContactsRequestFragmentAdapter;
import apptribus.com.tribus.activities.main_activity.fragment_talks.repository.TalksFragmentAPI;
import apptribus.com.tribus.pojo.Talk;
import apptribus.com.tribus.pojo.User;
import rx.Observable;

/**
 * Created by User on 6/13/2017.
 */

public class TalksFragmentModel {

    private final Fragment fragment;

    public TalksFragmentModel(Fragment fragment) {
        this.fragment = fragment;
    }



    //ADDED CONTACTS
    public Observable<List<Talk>> getAllContacts(List<Talk> contacts){
        return TalksFragmentAPI.getAllContacts(contacts, fragment);
    }

    public void loadMoreContacts(List<Talk> contacts, ContactsAddedFragmentAdapter mContactsAddedFragmentAdapter, ProgressBar mProgressBarBottom){
        TalksFragmentAPI.loadMoreContacts(contacts, mContactsAddedFragmentAdapter, mProgressBarBottom, fragment);
    }



    //REMOVE CONTACTS
    public Observable<List<Talk>> getAllRemoveContacts(List<Talk> contacts){
            return TalksFragmentAPI.getAllRemoveContacts(contacts, fragment);
    }

    public void loadMoreRemoveContacts(List<Talk> contacts, ContactsRemoveFragmentAdapter mContactsRemoveFragmentAdapter, ProgressBar mProgressBarBottom){

        TalksFragmentAPI.loadMoreRemoveContacts(contacts, mContactsRemoveFragmentAdapter, mProgressBarBottom, fragment);
    }

    public void showDialogToRemoveContact(User contact, ContactsRemoveFragmentAdapter mContactsRemoveAdapter){
        TalksFragmentAPI.showDialog(contact, fragment, mContactsRemoveAdapter);
    }



    //CONTACTS INVITATIONS
    public Observable<List<Talk>> getAllInvitations(List<Talk> contacts){
            return TalksFragmentAPI.getAllInvitations(contacts, fragment);

    }

    public void loadMoreInvitations(List<Talk> contacts, ContactsInvitationsFragmentAdapter mContactsInvitationsFragmentAdapter,
                                    ProgressBar mProgressBarBottom){

        TalksFragmentAPI.loadMoreInvitations(contacts, mContactsInvitationsFragmentAdapter, mProgressBarBottom, fragment);
    }

    public void showDialogToAcceptContact(Talk contact, User userContact){
        TalksFragmentAPI.showDialogToAcceptContact(contact, userContact, fragment);
    }

    public void showDialogToDenyInvitation(Talk contact, User userContact){
        TalksFragmentAPI.showDialogToDenyInvitation(contact, userContact, fragment);
    }


    //CONTACTS REQUEST
    public Observable<List<Talk>> getAllContactRequests(List<Talk> requests) {
        return TalksFragmentAPI.getAllContactRequests(requests, fragment);
    }

    public void loadMoreContactRequests(List<Talk> requests, ContactsRequestFragmentAdapter contactsRequestFragmentAdapter,
                                 ProgressBar mProgressBarBottom) {
        TalksFragmentAPI.loadMoreContactsRequests(requests, contactsRequestFragmentAdapter, mProgressBarBottom, fragment);
    }


    public void showDialogToCancelInvitation(User userContact) {
        TalksFragmentAPI.showDialogToCancelInvitation(userContact, fragment);
    }


}
