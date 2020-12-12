package apptribus.com.tribus.activities.main_activity.fragment_talks.mvp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import apptribus.com.tribus.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;

public class TalksFragmentView extends CoordinatorLayout {

    @BindView(R.id.relative_main)
    RelativeLayout mRelativeMain;

    @BindView(R.id.relative_no_contacts)
    RelativeLayout mRelativeNoContacts;

    @BindView(R.id.tv_no_contact)
    TextView mTvNoContact;

    @BindView(R.id.relative_talks_category)
    RelativeLayout mRelativeTalkersCategory;

    @BindView(R.id.rv_talks_category)
    RecyclerView mRvTalkersCategory;

    @BindView(R.id.relative_list_options)
    RelativeLayout mRelativeListOptions;

    @BindView(R.id.linear_options)
    LinearLayout mLinearOptions;

    @BindView(R.id.btn_talks_messages)
    Button mBtnTalksMessages;

    @BindView(R.id.view_talks_messages)
    View mViewTalksMessages;

    @BindView(R.id.btn_talks_updates)
    Button mBtnTalksUpdates;

    @BindView(R.id.view_talks_updates)
    View mViewTalksUpdates;

    @BindView(R.id.progressbar)
    ProgressBar mProgressBar;

    @BindView(R.id.layout_talks_fragment)
    CoordinatorLayout mCoordinatorTalks;

    @BindView(R.id.rv_list_talks_fragment)
    RecyclerView mRecyclerView;

    @BindView(R.id.progressbar_bottom)
    public ProgressBar mProgressBarBottom;

    //CATEGORY REMOVE CONTACTS
    @BindView(R.id.layout_talks_fragment_remove_contacts)
    CoordinatorLayout mCoordinatorRemoveContacts;

    @BindView(R.id.rv_list_talks_fragment_remove_contacts)
    RecyclerView mRvRemoveContacts;

    @BindView(R.id.progressbar_remove_contacts)
    ProgressBar mProgressBarRemoveContacts;


    //CATEGORY CONTACTS INVITATIONS
    @BindView(R.id.layout_talks_fragment_contacts_invitations)
    CoordinatorLayout mCoordinatorContactsInvitations;

    @BindView(R.id.rv_list_talks_fragment_contacts_invitations)
    RecyclerView mRvContactsInvitations;

    @BindView(R.id.progressbar_contacts_invitations)
    ProgressBar mProgressBarContactsInvitations;

    //CATEGORY CONTACTS INVITATIONS
    @BindView(R.id.layout_talks_fragment_contacts_requests)
    CoordinatorLayout mCoordinatorContactsRequests;

    @BindView(R.id.rv_list_talks_fragment_contacts_requests)
    RecyclerView mRvContactsRequests;

    @BindView(R.id.progressbar_contacts_requests)
    ProgressBar mProgressBarContactsRequests;


    private LinearLayoutManager mManager;
    private LinearLayoutManager mManagerTalksCategory;
    private LinearLayoutManager mManagerRemoveContacts;
    private LinearLayoutManager mManagerContactsInvitations;
    private LinearLayoutManager mManagerContactsRequests;
    private Unbinder unbinder;
    public Fragment mFragment;
    public Context mContext;

    public TalksFragmentView(@NonNull Fragment fragment) {
        super(fragment.getActivity());
        mFragment = fragment;
        mContext = fragment.getActivity();
        inflate(fragment.getActivity(), R.layout.fragment_talks, this);
        unbinder = ButterKnife.bind(this);

        initiateViews(fragment);

    }


    private void initiateViews(Fragment fragment){

        //CONTACTS LIST
        mManager = new LinearLayoutManager(fragment.getActivity());
        mManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mManager);
        //mRecyclerView.setHasFixedSize(true);

        //CATEGORY LIST
        mManagerTalksCategory = new LinearLayoutManager(fragment.getActivity());
        mManagerTalksCategory.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvTalkersCategory.setLayoutManager(mManagerTalksCategory);

        //REMOVE CONTACTS
        mManagerRemoveContacts = new LinearLayoutManager(fragment.getActivity());
        mManagerRemoveContacts.setOrientation(LinearLayoutManager.VERTICAL);
        mRvRemoveContacts.setLayoutManager(mManagerRemoveContacts);

        //CONTACTS INVITATIONS
        mManagerContactsInvitations = new LinearLayoutManager(fragment.getActivity());
        mManagerContactsInvitations.setOrientation(LinearLayoutManager.VERTICAL);
        mRvContactsInvitations.setLayoutManager(mManagerContactsInvitations);

        //CONTACTS REQUESTS
        mManagerContactsRequests = new LinearLayoutManager(fragment.getActivity());
        mManagerContactsRequests.setOrientation(LinearLayoutManager.VERTICAL);
        mRvContactsRequests.setLayoutManager(mManagerContactsRequests);

    }


    public Observable<Void> observableBtnTalksMessages(){
        return RxView.clicks(mBtnTalksMessages);
    }

    public Observable<Void> observableBtnTalksUpdates(){
        return RxView.clicks(mBtnTalksUpdates);
    }


    public void onDestroyView(){
        unbinder.unbind();
    }










    /*
    @BindView(R.id.layout_talks_fragment)
    CoordinatorLayout mCoordinatorTalks;

    @BindView(R.id.rv_list_talks_fragment)
    RecyclerView mRecyclerView;

    private NpaLinearLayoutManager mLayoutManager;
    private Unbinder unbinder;

    public TalksFragmentView(@NonNull Fragment fragment) {
        super(fragment.getActivity());
        inflate(fragment.getActivity(), R.layout.fragment_talks, this);
        unbinder = ButterKnife.bind(this);

        mLayoutManager = new NpaLinearLayoutManager(fragment.getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
    }


    public void onDestroyView(){
        unbinder.unbind();
    }

    */

}
