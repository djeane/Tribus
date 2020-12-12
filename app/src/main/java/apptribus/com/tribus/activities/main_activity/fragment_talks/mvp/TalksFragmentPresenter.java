package apptribus.com.tribus.activities.main_activity.fragment_talks.mvp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.main_activity.fragment_talks.adapter.ContactsAddedFragmentAdapter;
import apptribus.com.tribus.activities.main_activity.fragment_talks.adapter.ContactsInvitationsFragmentAdapter;
import apptribus.com.tribus.activities.main_activity.fragment_talks.adapter.ContactsRemoveFragmentAdapter;
import apptribus.com.tribus.activities.main_activity.fragment_talks.adapter.ContactsRequestFragmentAdapter;
import apptribus.com.tribus.activities.main_activity.fragment_talks.adapter.TalksFragmentCategoryAdapter;
import apptribus.com.tribus.pojo.Talk;
import apptribus.com.tribus.pojo.User;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static apptribus.com.tribus.util.Constantes.CONTATOS_ADICIONADOS;
import static apptribus.com.tribus.util.Constantes.CONVITES_ENVIADOS;
import static apptribus.com.tribus.util.Constantes.CONVITES_RECEBIDOS;
import static apptribus.com.tribus.util.Constantes.REMOVER_CONTATOS;

public class TalksFragmentPresenter implements ContactsAddedFragmentAdapter.ContactsAddedAdapterClickListener,
        ContactsRemoveFragmentAdapter.OnBlockUserAdapterListener, TalksFragmentCategoryAdapter.OnCategoryListener,
        ContactsInvitationsFragmentAdapter.ContactsInvitationdFragmentAdapterListener,
        ContactsRequestFragmentAdapter.InvitationRequestUserListener {

    private final TalksFragmentView view;
    private final TalksFragmentModel model;
    private CompositeSubscription mSubscription = new CompositeSubscription();
    private TalksFragmentPresenterClickListener mListener;
    private TalksFramentContactsRemovePresenterClickListener mListenerContactsRemove;
    private List<Talk> mListContacts;
    private List<Talk> mListRequests;
    private List<Talk> mListInvitations;
    private List<Talk> mListRemoveContacts;;


    private TalksFragmentCategoryAdapter mTalksFragmentCategoryAdapter;
    private ContactsAddedFragmentAdapter mContactAddedAdapter;
    private ContactsRemoveFragmentAdapter mContactsRemoveAdapter;
    private ContactsInvitationsFragmentAdapter mContactsInvitationsAdapter;
    private ContactsRequestFragmentAdapter mContactsRequestAdapter;

    public TalksFragmentPresenter(TalksFragmentView view, TalksFragmentModel model) {
        this.view = view;
        this.model = model;
    }


    public void onStart() {

        if (mListContacts == null) {
            mListContacts = new ArrayList<>();
        }

        if (mListRemoveContacts == null) {
            mListRemoveContacts = new ArrayList<>();
        }
        if (mListInvitations == null) {
            mListInvitations = new ArrayList<>();
        }
        if (mListRequests == null) {
            mListRequests = new ArrayList<>();
        }


        mSubscription.add(getAllContacts());
        mSubscription.add(observeBtnTalksUpdates());
        mSubscription.add(observeBtnTalksMessages());

        setTalksFragmentCategoriesAdapter();

        loadMore();

    }

    private void setTalksFragmentCategoriesAdapter() {
        mTalksFragmentCategoryAdapter = new TalksFragmentCategoryAdapter(view.mFragment.getContext(), getTitle(), this);
        view.mRvTalkersCategory.setAdapter(mTalksFragmentCategoryAdapter);
        Log.e("talks catAdapter: ", "setTalksFragmentCategoriesAdapter");

    }

    private Subscription getAllContacts() {
        view.mRelativeListOptions.setVisibility(VISIBLE);
        view.mRelativeListOptions.bringToFront();
        view.mCoordinatorTalks.setVisibility(VISIBLE);
        view.mCoordinatorTalks.bringToFront();
        mListContacts.clear();
        return model.getAllContacts(mListContacts)
                .subscribe(tribus -> {
                            if (tribus != null && !tribus.isEmpty()) {
                                mListContacts = tribus;
                                mContactAddedAdapter = new ContactsAddedFragmentAdapter(view.mFragment.getActivity(),
                                        mListContacts, view, this);
                                view.mRecyclerView.setAdapter(mContactAddedAdapter);
                                view.mProgressBar.setVisibility(GONE);
                                //view.mCoordinatorOptionsThematics.setVisibility(GONE);
                            } else {
                                view.mRelativeNoContacts.setVisibility(VISIBLE);
                                view.mRelativeNoContacts.bringToFront();
                                //view.mTvNoContact.setVisibility(VISIBLE);
                                view.mTvNoContact.setText("Você não tem contatos adicionados no momento.");
                            }
                        },
                        Throwable::printStackTrace);

    }

    private void loadMore() {
        view.mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                if (reachedBottom) {
                    view.mProgressBarBottom.bringToFront();
                    view.mProgressBarBottom.setVisibility(VISIBLE);

                    model.loadMoreContacts(mListContacts, mContactAddedAdapter, view.mProgressBarBottom);
                }
            }
        });

    }


    private Subscription observeBtnTalksUpdates() {
        return view.observableBtnTalksUpdates()
                .subscribe(__ -> {
                    view.mBtnTalksMessages.setTextColor(view.getResources().getColor(R.color.primary_light));
                    view.mViewTalksMessages.setBackgroundColor(view.getResources().getColor(R.color.colorIcons));
                    view.mBtnTalksUpdates.setTextColor(view.getResources().getColor(R.color.accent));
                    view.mViewTalksUpdates.setBackgroundColor(view.getResources().getColor(R.color.accent));
                    Toast.makeText(view.mFragment.getActivity(), "Aguarde! Logo esta novidade estará disponível! ", Toast.LENGTH_SHORT).show();
                });
    }

    private Subscription observeBtnTalksMessages() {
        return view.observableBtnTalksMessages()
                .subscribe(__ -> {
                    view.mBtnTalksUpdates.setTextColor(view.getResources().getColor(R.color.primary_light));
                    view.mViewTalksUpdates.setBackgroundColor(view.getResources().getColor(R.color.colorIcons));
                    view.mBtnTalksMessages.setTextColor(view.getResources().getColor(R.color.accent));
                    view.mViewTalksMessages.setBackgroundColor(view.getResources().getColor(R.color.accent));
                });
    }


    private List<String> getTitle() {

        List<String> categoryTitles = new ArrayList<>();
        String[] titles = {CONTATOS_ADICIONADOS, CONVITES_RECEBIDOS, CONVITES_ENVIADOS, REMOVER_CONTATOS};

        categoryTitles.addAll(Arrays.asList(titles));

        return categoryTitles;
    }

    public void onPause() {

    }

    public void onStop() {

    }

    public void onDetach() {
        mSubscription.clear();
    }

    public void onDestroyView() {
        mSubscription.clear();
    }

    public void onAttach(Context context) {

        if (context instanceof TalksFragmentPresenterClickListener) {
            mListener = (TalksFragmentPresenterClickListener) context;
        }

        if (context instanceof TalksFramentContactsRemovePresenterClickListener) {
            mListenerContactsRemove = (TalksFramentContactsRemovePresenterClickListener) context;
        }

    }

    @Override
    public void openChatUser(String contactId) {
        mListener.talksFragmentPresenterItemOnClickListener(contactId);
    }

    @Override
    public void btnRemoveContactOnClickListener(User contact) {
        model.showDialogToRemoveContact(contact, mContactsRemoveAdapter);
    }

    @Override
    public void openContactProfiletOnClickListener(Talk contact) {
        mListenerContactsRemove.talksFramentContactsRemovePresenterItemOnClickListener(contact);
    }


    @Override
    public void onCategoryClickListener(String categoryTitle) {

        switch (categoryTitle){

            case "Contatos adicionados":
                if (mListRemoveContacts != null) {
                    mListRemoveContacts.clear();
                }
                if (mListInvitations != null){
                    mListInvitations.clear();

                }
                if (mListRequests != null) {
                    mListRequests.clear();
                }
                mListContacts.clear();
                mSubscription.add(getAllContacts());
                loadMore();
                break;

            case "Remover contatos":
                if (mListContacts != null) {
                    mListContacts.clear();
                }
                if (mListInvitations != null){
                    mListInvitations.clear();

                }
                if (mListRequests != null) {
                    mListRequests.clear();
                }
                mListRemoveContacts.clear();
                mSubscription.add(getRemoveContacts());
                loadMoreRemoveContacts();
                break;

            case "Convites recebidos":
                if (mListContacts != null) {
                    mListContacts.clear();
                }
                if (mListRemoveContacts != null){
                    mListRemoveContacts.clear();

                }
                if (mListRequests != null) {
                    mListRequests.clear();
                }
                mListInvitations.clear();
                mSubscription.add(getAllInvitations());
                loadMoreInvitations();
                break;

            case "Convites enviados":
                if (mListContacts != null) {
                    mListContacts.clear();
                }
                if (mListRemoveContacts != null){
                    mListRemoveContacts.clear();

                }
                if (mListInvitations != null) {
                    mListInvitations.clear();
                }
                mListRequests.clear();
                mSubscription.add(getAllContactRequests());
                loadMoreContactRequests();
                break;
        }

    }

    //REMOVE CONTACTS
    private Subscription getRemoveContacts() {
        view.mCoordinatorRemoveContacts.setVisibility(VISIBLE);
        view.mCoordinatorRemoveContacts.bringToFront();
        return model.getAllRemoveContacts(mListRemoveContacts)
                .subscribe(contatos -> {
                            if (contatos != null && !contatos.isEmpty()) {
                                mListRemoveContacts = contatos;
                                mContactsRemoveAdapter = new ContactsRemoveFragmentAdapter(view.mContext,
                                        mListRemoveContacts, view, this);
                                view.mRvRemoveContacts.setAdapter(mContactsRemoveAdapter);
                                view.mProgressBarRemoveContacts.setVisibility(GONE);
                            } else {
                                view.mRelativeNoContacts.setVisibility(VISIBLE);
                                view.mRelativeNoContacts.bringToFront();
                                //view.mTvNoContact.setVisibility(VISIBLE);
                                view.mTvNoContact.setText("Não há contatos para remover.");
                            }

                        },
                        Throwable::printStackTrace);

    }

    private void loadMoreRemoveContacts() {
        view.mRvRemoveContacts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                if (reachedBottom) {
                    view.mProgressBarBottom.bringToFront();
                    view.mProgressBarBottom.setVisibility(VISIBLE);

                    model.loadMoreRemoveContacts(mListRemoveContacts, mContactsRemoveAdapter, view.mProgressBarBottom);
                }
            }
        });

    }



    //CONTACTS INVITATIONS
    private Subscription getAllInvitations() {
        view.mCoordinatorContactsInvitations.setVisibility(VISIBLE);
        view.mCoordinatorContactsInvitations.bringToFront();
        return model.getAllInvitations(mListInvitations)
                .subscribe(invitations -> {
                            if (invitations != null && !invitations.isEmpty()) {
                                mListInvitations = invitations;
                                mContactsInvitationsAdapter = new ContactsInvitationsFragmentAdapter(view.mContext,
                                        mListInvitations, view, this);
                                view.mRvContactsInvitations.setAdapter(mContactsInvitationsAdapter);
                                view.mProgressBarContactsInvitations.setVisibility(GONE);
                            } else {
                                view.mRelativeNoContacts.setVisibility(VISIBLE);
                                view.mRelativeNoContacts.bringToFront();
                                //view.mTvNoContact.setVisibility(VISIBLE);
                                view.mTvNoContact.setText("Não há convites recebidos no momento.");
                            }

                        },
                        Throwable::printStackTrace);

    }


    private void loadMoreInvitations() {
        view.mRvContactsInvitations.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                if (reachedBottom) {
                    view.mProgressBarBottom.bringToFront();
                    view.mProgressBarBottom.setVisibility(VISIBLE);

                    model.loadMoreInvitations(mListInvitations, mContactsInvitationsAdapter, view.mProgressBarBottom);
                }
            }
        });

    }



    //CONTACTS REQUEST
    private Subscription getAllContactRequests() {
        view.mCoordinatorContactsRequests.setVisibility(VISIBLE);
        view.mCoordinatorContactsRequests.bringToFront();
        return model.getAllContactRequests(mListRequests)
                .subscribe(requests -> {
                            if (requests != null && !requests.isEmpty()) {
                                mListRequests = requests;
                                mContactsRequestAdapter = new ContactsRequestFragmentAdapter(view.mContext,
                                        mListRequests, view, this);
                                view.mRvContactsRequests.setAdapter(mContactsRequestAdapter);
                                view.mProgressBarContactsRequests.setVisibility(GONE);
                            } else {
                                view.mRelativeNoContacts.setVisibility(VISIBLE);
                                view.mRelativeNoContacts.bringToFront();
                                //view.mTvNoContact.setVisibility(VISIBLE);
                                view.mTvNoContact.setText("Não há convites enviados no momento.");
                            }

                        },
                        Throwable::printStackTrace);

    }


    private void loadMoreContactRequests() {
        view.mRvContactsRequests.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                if (reachedBottom) {
                    view.mProgressBarBottom.bringToFront();
                    view.mProgressBarBottom.setVisibility(VISIBLE);

                    model.loadMoreContactRequests(mListRequests, mContactsRequestAdapter, view.mProgressBarBottom);
                }
            }
        });

    }


    @Override
    public void btnAcceptContactOnClickListener(Talk contact, User userContact) {
        model.showDialogToAcceptContact(contact, userContact);
    }

    @Override
    public void btnDenyInvitationOnClickListener(Talk contact, User userContact) {
        model.showDialogToDenyInvitation(contact, userContact);
    }

    @Override
    public void openUserProfileOnClickListener(Talk contact) {
        mListenerContactsRemove.talksFramentContactsRemovePresenterItemOnClickListener(contact);
    }

    @Override
    public void btnCancelInvitation(User userContact) {
        model.showDialogToCancelInvitation(userContact);
    }

    @Override
    public void openUserProfile(Talk contact) {
        mListenerContactsRemove.talksFramentContactsRemovePresenterItemOnClickListener(contact);
    }


    public interface TalksFragmentPresenterClickListener {
        void talksFragmentPresenterItemOnClickListener(String contactId);
    }

    public interface TalksFramentContactsRemovePresenterClickListener {
        void talksFramentContactsRemovePresenterItemOnClickListener(Talk contact);
    }

}
