package apptribus.com.tribus.activities.main_activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import javax.inject.Inject;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.main_activity.dagger.DaggerMainComponent;
import apptribus.com.tribus.activities.main_activity.dagger.MainModule;
import apptribus.com.tribus.activities.main_activity.fragment_talks.mvp.TalksFragmentPresenter;
import apptribus.com.tribus.activities.main_activity.fragment_timeline.mvp.TimeLinePresenter;
import apptribus.com.tribus.activities.main_activity.fragment_tribus.mvp.TribusFragmentPresenter;
import apptribus.com.tribus.activities.main_activity.mpv.MainPresenter;
import apptribus.com.tribus.activities.main_activity.mpv.MainView;
import apptribus.com.tribus.application.dagger.TribusApplication;
import apptribus.com.tribus.pojo.Talk;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.util.ConnectivityReceiver;
import apptribus.com.tribus.util.PresenceSystemAndLastSeen;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements ConnectivityReceiver.ConnectivityReceiverListener,
        TribusFragmentPresenter.TribuFragmentPresenterOpenFeatureChoiceListener,
        TalksFragmentPresenter.TalksFragmentPresenterClickListener,
        TalksFragmentPresenter.TalksFramentContactsRemovePresenterClickListener,
        TimeLinePresenter.TribusLinePresenterListener {


    @Inject
    MainView view;

    @Inject
    MainPresenter presenter;

    @BindView(R.id.fab)
    public FloatingActionButton mFab;

    @BindView(R.id.coordinator_main)
    CoordinatorLayout mCoordinatorMain;


    /*//@BindView(R.id.toolbar_main)
    //public Toolbar mToolbarMain;

    //@BindView(R.id.bottom_navigation_view)
    //public BottomNavigationView mBottomNavigationItemView;



    //private MenuItem mAddTalker;
    //private MenuItem mInvitationsRequest;
    //private MenuItem mRemoveTalkers;
    //private MenuItem mRequestPermissionTribu;
    //private MenuItem mPrivacyPolicy;


    //private Drawer mDrawerNavigation;
    //private TimeLineFragment mTimeLineFragment = null;
    //private TribusFragment mTribusFragment;
    //private TalksFragment mTalksFragment;
    //private SearchView mSearchView;
    //private MenuItem mSearchable;
    //private User mUser;
    //private FirebaseDatabase mDatabase;
    //private FirebaseAuth mAuth;
    //private DatabaseReference mReferenceUsers;
    private DatabaseReference mRefChatTalker;
    private StorageReference mRefStorageUser;
    private FirebaseStorage mStorage;
    //private File mImageFolder;

    //private String mUpdateLastMessage;
    //private String mTalkerId;
    //public static boolean isOpen;

    //private String mUid;

    //private CompositeSubscription subscription = new CompositeSubscription();

    //FragmentTransaction mTransaction2;
    //FragmentManager mFm1;
    //FragmentManager mFm2;
    //FragmentTransaction mTransaction1;*/




    public static void start(AppCompatActivity context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerMainComponent.builder()
                .appComponent(TribusApplication.get(this).component())
                .mainModule(new MainModule(this))
                .build().inject(this);

        setContentView(view);
        ButterKnife.bind(this);

        /*mUpdateLastMessage = getIntent().getStringExtra(UPDATE_LAST_MESSAGE);
        mTalkerId = getIntent().getStringExtra(CONTACT_ID);

        mToolbarMain.setTitle("Tribus");
        setSupportActionBar(mToolbarMain);
        //presenter.observeSubscriptionModel();
        ShowSnackBarInfoInternet.checkConnection(mCoordinatorMain);

        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mReferenceUsers = mDatabase.getReference().child(GENERAL_USERS);
        mRefChatTalker = mDatabase.getReference().child(CHAT_TALKER);
        mRefStorageUser = mStorage.getReference().child(IMAGES_USERS);*/

        /*if (mAuth.getCurrentUser() != null){
            getCurrentUser();
        }*/

        //setBottomNavigationItem();

        /*mFm1 = getSupportFragmentManager();
        mFm2 = getSupportFragmentManager();
        mTransaction1 = mFm1.beginTransaction();

        //TO SHOW DEFAULT FRAGMENT WHEN ACTIVITY STARTS
        if (mTimeLineFragment == null) {
            mTimeLineFragment = TimeLineFragment.getInstance(0);
            mTransaction1.replace(R.id.frame_list, mTimeLineFragment).commit();
            mTribusFragment = null;
            mTalksFragment = null;


        }

        if (mAddTalker != null) {
            mAddTalker.setVisible(false);
            mInvitationsRequest.setVisible(false);
            mRemoveTalkers.setVisible(false);
            mRequestPermissionTribu.setVisible(false);
        }

        mDrawerNavigation = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolbarMain)
                .withDisplayBelowStatusBar(true)
                .withCloseOnClick(true)
                .withActionBarDrawerToggleAnimated(true)
                .withActionBarDrawerToggle(true)
                .withSavedInstance(savedInstanceState)
                .withSelectedItem(0)
                .addDrawerItems(new PrimaryDrawerItem().withName("Todas as Tribus")
                        .withOnDrawerItemClickListener((view2, position, drawerItem) -> {

                        mTransaction2 = null;
                            mTransaction2 = mFm2.beginTransaction();

                            mToolbarMain.setTitle("Todas as Tribus");
                        mFab.setVisibility(View.VISIBLE);

                        if (mAddTalker != null) {
                            mAddTalker.setVisible(false);
                            mInvitationsRequest.setVisible(false);
                            mRemoveTalkers.setVisible(false);
                            mRequestPermissionTribu.setVisible(false);
                        }

                        //if (mTimeLineFragment == null) {
                        mTimeLineFragment = TimeLineFragment.getInstance(0);
                        mTransaction2.replace(R.id.frame_list, mTimeLineFragment).commit();
                        mTribusFragment = null;
                        mTalksFragment = null;
                        mBottomNavigationItemView.setSelectedItemId(R.id.pesquisar_tribu);

                        //view.mBottomNavigationItemView.setSelectedItemId(R.id.pesquisar_tribu);

                        if (mDrawerNavigation.isDrawerOpen()) {
                            mDrawerNavigation.closeDrawer();
                        }
                        // } else {
                        //if (mDrawerNavigation.isDrawerOpen()) {
                        //mDrawerNavigation.closeDrawer();
                        //}
                        //}

                        return true;
                        //}
                        })
                )
                .withSliderBackgroundDrawable(getResources().getDrawable(R.drawable.background_white))
                .build();*/

        //CHECK INTERNET CONNECTION
        //setOverflowButtonColor(getResources().getColor(R.color.colorIcons));


    }



    @Override
    protected void onStart() {
        super.onStart();

        /*if (mUser != null && mUser.getThumb() == null){
            createThumbImage(mUser.getImageUrl());
        }

        mReferenceUsers.keepSynced(true);
        mRefChatTalker.keepSynced(true);

        PresenceSystemAndLastSeen.presenceSystem();

        FirebaseMessaging.getInstance().subscribeToTopic(NEW_TRIBUS);

        subscription.add(observeFAB());
        subscription.add(observeModel());

        isOpen = true;*/

        //mRefAdmin.keepSynced(true);
        //mRefChatTalker.keepSynced(true);

        presenter.onStart();
        //presenter.observeModel();
        //presenter.observeUser();
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (menu != null) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_main_activity, menu);

            mSearchable = menu.findItem(R.id.action_searchable_activity);
            mAddTalker = menu.findItem(R.id.action_add_talker);
            mInvitationsRequest = menu.findItem(R.id.action_invitations_request);
            mRemoveTalkers = menu.findItem(R.id.action_remove_talkers);
            mRequestPermissionTribu = menu.findItem(R.id.action_invitations_request_tribu);
            mPrivacyPolicy = menu.findItem(R.id.action_privacy_policy);

            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

            mSearchView = (SearchView) mSearchable.getActionView();

            if (mSearchView != null) {
                mSearchView.setMaxWidth(Integer.MAX_VALUE);
                mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
                mSearchView.setQueryHint(getResources().getString(R.string.search_hint));

            }

            return true;
        } else {
            return false;
        }

    }*/

    /*@SuppressLint("RestrictedApi")
    private void setBottomNavigationItem() {

        if (mPrivacyPolicy != null) {
            mPrivacyPolicy.setVisible(true);

            //SET UP BOTTOM NAVIGATION VIEW - IF IS NOT ADMIN
            mBottomNavigationItemView.setOnNavigationItemSelectedListener(itemBottomNav -> {

                FragmentManager fm1 = this.getSupportFragmentManager();
                FragmentTransaction transaction1 = fm1.beginTransaction();

                switch (itemBottomNav.getItemId()) {
                    case R.id.pesquisar_tribu:

                        //ShowSnackBarInfoInternet.showToastInfoInternet(view.mContext);
                        mFab.setVisibility(VISIBLE);

                        //SET VISIBILITY OF FAB
                        //view.mAppBar.setExpanded(true);
                        mToolbarMain.setTitle("Tribus");
                        mToolbarMain.setCollapsible(false);
                        //if (view.mSearchable != null) {
                        mSearchable.setVisible(true);
                        //}
                        if (mTimeLineFragment == null) {
                            mTimeLineFragment = TimeLineFragment.getInstance(0);
                            transaction1.replace(R.id.frame_list, mTimeLineFragment).commit();
                            mTribusFragment = null;
                            mTalksFragment = null;
                            //view.mProfileItem.setVisible(true);
                            //if (view.mAddTalker != null) {
                            mAddTalker.setVisible(false);
                            mInvitationsRequest.setVisible(false);
                            mRemoveTalkers.setVisible(false);
                            //view.mBlockedTalkers.setVisible(false);
                            mRequestPermissionTribu.setVisible(false);
                            //}
                        }

                        return true;

                    case R.id.adicionar_tribu:

                        //SET VISIBILITY OF FAB
                        mFab.setVisibility(GONE);
                        //view.mAppBar.setExpanded(true);
                        mToolbarMain.setCollapsible(false);
                        if (mSearchable != null) {
                            mSearchable.setVisible(false);
                        }

                        if (mTribusFragment == null) {
                            mTribusFragment = TribusFragment.getInstance(1);
                            transaction1.replace(R.id.frame_list, mTribusFragment).commit();
                            mTimeLineFragment = null;
                            mTalksFragment = null;
                            //view.mProfileItem.setVisible(false);
                            //if (view.mAddTalker != null) {
                            mAddTalker.setVisible(false);
                            mInvitationsRequest.setVisible(false);
                            mRemoveTalkers.setVisible(false);
                            //view.mBlockedTalkers.setVisible(false);
                            mRequestPermissionTribu.setVisible(true);
                            //}
                        }

                        return true;

                    case R.id.conversas:
                        //SET VISIBILITY OF FAB
                        mFab.setVisibility(GONE);

                        //view.mAppBar.setExpanded(true);
                        mToolbarMain.setCollapsible(false);
                        if (mSearchable != null) {
                            mSearchable.setVisible(false);
                        }
                        if (mTalksFragment == null) {
                            mTalksFragment = TalksFragment.getInstance(2);
                            transaction1.replace(R.id.frame_list, mTalksFragment).commit();
                            mTimeLineFragment = null;
                            mTribusFragment = null;
                            //view.mProfileItem.setVisible(false);
                            //if (view.mAddTalker != null) {
                            mAddTalker.setVisible(true);
                            mRemoveTalkers.setVisible(true);
                            mInvitationsRequest.setVisible(true);
                            //view.mBlockedTalkers.setVisible(true);
                            mRequestPermissionTribu.setVisible(false);
                            //}
                        }

                        return true;
                }
                return false;

            });
        }
        //}
    }*/

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        Intent intent;

        switch (id) {
            case R.id.action_add_talker:
                intent = new Intent(this, DetailAddTalkerActivity.class);
                startActivity(intent);
                //presenter.openAddTalker();
                //model.openAddTalker();
                break;

            case R.id.action_invitations_request:
                intent = new Intent(this, InvitationRequestUserActivity.class);
                startActivity(intent);
                //presenter.openInvitationsRequest();
                break;

            case R.id.action_remove_talkers:
                intent = new Intent(this, BlockUserActivity.class);
                startActivity(intent);
                //presenter.openBlockUser();
                break;

            case R.id.action_invitations_request_tribu:
                intent = new Intent(this, InvitationRequestTribuActivity.class);
                startActivity(intent);
                //presenter.openRequestPermissionTribu();
                break;

            case R.id.action_privacy_policy:
                intent = new Intent(this, PrivacyPolicyCheckUsernameActivity.class);
                startActivity(intent);
                //presenter.openPrivacyPolicyActivity();
                break;

            case R.id.action_share:
                if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                    ShowSnackBarInfoInternet.showToastInfoInternet(this);
                    break;
                } else {
                    openShareFragmentToApp();
                    //presenter.openShareFragmetToApp();
                    break;
                }
        }

        return true;
    }*/

    private void openShareFragmentToApp() {
        try {
            String packageName = this.getPackageName();
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_SUBJECT, "Tribus");
            intent.setType("text/plain");

            String strShareMessage = "\nEi, eu tô participando do TRIBUS! Instala ele aí! Você vai gostar!\n\n";

            strShareMessage = strShareMessage + "https://play.google.com/store/apps/details?id=" + packageName;

            intent.putExtra(Intent.EXTRA_TEXT, strShareMessage);
            startActivity(intent);

        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(this, "Não foi encontrada uma tela para compartilhamento.", Toast.LENGTH_LONG)
                    .show();
        }
    }

    /*private Subscription observeModel() {
        return getCurrentUser()
                //.debounce(10000, TimeUnit.MILLISECONDS) // mudei isso para rodar no tablet
                //.observeOn(AndroidSchedulers.mainThread()) // mudei isso para rodar no tablet
                .subscribe(user -> {
                            mUser = user;
                            //setFields(user);
                            //if (view.mToolbarMain != null) {
                            setBottomNavigationItem();
                            //}
                        },
                        Throwable::printStackTrace
                );
    }*/


    /*private Observable<User> getCurrentUser() {

        if (mAuth.getCurrentUser() != null) {
            mUid = mAuth.getCurrentUser().getUid();
        }

        return Observable.create(subscriber -> mReferenceUsers
                .child(mUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mUser = dataSnapshot.getValue(User.class);
                        subscriber.onNext(mUser);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        if (databaseError != null) {
                            databaseError.toException().printStackTrace();
                        }
                    }
                }));

    }*/


    @Override
    protected void onRestart() {
        super.onRestart();
        PresenceSystemAndLastSeen.presenceSystem();
        presenter.onRestart();
    }

    @Override
    protected void onPause() {
        presenter.onPause();
        PresenceSystemAndLastSeen.lastSeen();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //register connection status listener
        TribusApplication.getInstance().setConnectivityListener(this);
        PresenceSystemAndLastSeen.presenceSystem();


        //updateLastMessage();
        presenter.onResume();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onStop() {
        presenter.onStop();
        //isOpen = false;
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        //subscription.clear();
        super.onDestroy();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        ShowSnackBarInfoInternet.showSnack(isConnected, mFab);
    }

    @Override
    public void openFeatureChoiceActivity(String tribuKey, String tribuUniqueName) {
        presenter.openFeatureChoiceActivity(tribuKey, tribuUniqueName);
    }

    @Override
    public void talksFragmentPresenterItemOnClickListener(String contactId) {
        presenter.openChatUserActivity(contactId);
    }

    @Override
    public void talksFramentContactsRemovePresenterItemOnClickListener(Talk contact) {
        presenter.openDetailContactActivity(contact);
    }

    @Override
    public void openFeactureChoice(Tribu tribu) {
        presenter.openFeatureChoiceActivity(tribu.getKey(), tribu.getProfile().getUniqueName());
    }

    @Override
    public void openProfileTribusUserActivity(Tribu tribu) {
        presenter.openProfileTribuUserActivity(tribu);
    }

    @Override
    public void openShareFragment(Tribu tribu) {
        presenter.openShareFragmentToCard(tribu);
    }

    @Override
    public void btnMuralOnClickListenerShow() {
        presenter.btnFabShow();
    }
    @Override
    public void btnMuralOnClickListenerHide() {
        presenter.btnFabHide();
    }

    /*private void createThumbImage(String imageUrl){

        Uri image = Uri.parse(imageUrl);

        StorageReference imageRef = mStorage.getReferenceFromUrl(imageUrl);

        File jpegFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        mImageFolder = new File(jpegFile, "Tribus Imagens");
        if (!mImageFolder.exists()) {
            mImageFolder.mkdirs();
        }

        String currentTimestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String prepend = "JPEG_" + currentTimestamp + "_";
        File imageFile = null;

        try {
            imageFile = File.createTempFile(prepend, ".jpg", mImageFolder);
            File finalImageFile = imageFile;

            imageRef
                    .getFile(imageFile)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Local temp file has been created

                        File tumbPath = new File(finalImageFile.getPath());

                        Bitmap thumbBitmap = null;

                        try {
                            thumbBitmap = new Compressor(this)
                                    .setMaxWidth(200)
                                    .setMaxHeight(200)
                                    .setQuality(75)
                                    .compressToBitmap(tumbPath);

                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                            byte[] thumbData = byteArrayOutputStream.toByteArray();


                            StorageReference mReferenceImageUser = mRefStorageUser
                                    .child(mAuth.getCurrentUser().getUid())
                                    .child(mUser.getUsername())
                                    .child("thumb");
                            UploadTask task = mReferenceImageUser.putBytes(thumbData);

                            if (task != null) {
                                task.addOnSuccessListener(taskSnapshot2 -> {
                                    String uri = taskSnapshot2.getDownloadUrl().toString();
                                    Map<String, Object> updateUser = new HashMap<>();
                                    updateUser.put("thumb", uri);

                                    mReferenceUsers
                                            .child(mAuth.getCurrentUser().getUid())
                                            .updateChildren(updateUser)
                                            .addOnFailureListener(Throwable::getLocalizedMessage);

                                }).addOnFailureListener(Throwable::getLocalizedMessage);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }).addOnFailureListener(Throwable::getLocalizedMessage);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    /*public Observable<Void> observableFAB() {
        return RxView.clicks(mFab);
    }

    //Shows dialog when FAB is clicked
    @SuppressLint("ResourceType")
    private Subscription observeFAB() {
        return observableFAB()
                .filter(__ -> {
                    if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                        ShowSnackBarInfoInternet.showToastInfoInternet(this);
                        return false;
                    } else {
                        ShowSnackBarInfoInternet.showSnack(true, mCoordinatorMain);
                        return true;
                    }
                })
                .subscribe(
                        __ -> CreateTribuActivity.start(this),
                        Throwable::printStackTrace
                );
    }*/

    /*public void updateLastMessage() {

        if (mTalkerId != null) {
            if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                return;

            }
            else {
                mRefChatTalker
                        .child(mAuth.getCurrentUser().getUid())
                        .child(mTalkerId)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (dataSnapshot.hasChildren()) {
                                    //ChatTalker chatTalker = dataSnapshot.getValue(ChatTalker.class);
                                    //UPDATE ONLINE
                                    Map<String, Object> updateChatTalker = new HashMap<>();
                                    updateChatTalker.put("unreadMessages", 0);
                                    //updateChatTalker.put("talkerIsOnline", mCurrentUser.isOnlineInChat());

                                    mRefChatTalker
                                            .child(mAuth.getCurrentUser().getUid())
                                            .child(mTalkerId)
                                            .updateChildren(updateChatTalker);

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                if (databaseError != null) {
                                    databaseError.toException().printStackTrace();
                                }
                            }
                        });

            }
        }
    }*/
}
