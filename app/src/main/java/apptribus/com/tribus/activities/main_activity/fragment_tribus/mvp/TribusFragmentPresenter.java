package apptribus.com.tribus.activities.main_activity.fragment_tribus.mvp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import apptribus.com.tribus.activities.main_activity.fragment_tribus.adapter.TribusFragmentAdapter;
import apptribus.com.tribus.activities.main_activity.fragment_tribus.adapter.TribusFragmentCategoriesAdapter;
import apptribus.com.tribus.activities.main_activity.fragment_tribus.adapter.TribusThematicsAdapter;
import apptribus.com.tribus.pojo.Tribu;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static apptribus.com.tribus.util.Constantes.SOLICITACOES_ENVIADAS;
import static apptribus.com.tribus.util.Constantes.TRIBUS_ADICIONADAS;
import static apptribus.com.tribus.util.Constantes.TRIBUS_ENCERRADAS;
import static apptribus.com.tribus.util.Constantes.TRIBUS_REMOVIDAS;

public class TribusFragmentPresenter implements TribusFragmentAdapter.TribusFragmentAdapterItemClickListener,
        TribusFragmentCategoriesAdapter.OnTribusFragmentCategoriesAdapterListener,
        TribusThematicsAdapter.TribusThematicsAdapterClickListener {

    private final TribusFragmentView view;
    private final TribusFragmentModel model;
    private CompositeSubscription mSubscription = new CompositeSubscription();
    private TribuFragmentPresenterOpenFeatureChoiceListener mListener;

    private TribusFragmentCategoriesAdapter mTribusFragmentCategoryAdapter;
    private TribusThematicsAdapter mTribusThematicsAdapterFollowedTribus;
    private TribusThematicsAdapter mTribusThematicsAdapterRemovedTribus;
    private List<String> mListThematics;
    private List<Tribu> mListTribus;
    private List<Tribu> mListTribusByThematics;
    private TribusFragmentAdapter mTribuFragmentAdapter;
    private TribusFragmentAdapter mTribusFragmentAdapterByThematic;


    //LISTS
    private List<String> mListFollowedTribusThematics;
    private List<String> mListRemovedTribusThematics;
    private List<String> mListInvitedTribusThematics;
    private List<String> mListClosedTribusThematics;




    public TribusFragmentPresenter(TribusFragmentView view, TribusFragmentModel model) {
        this.view = view;
        this.model = model;
    }

    public void onStart(){

        if (mListThematics == null){
            mListThematics = new ArrayList<>();
        }
        if (mListTribus == null){
            mListTribus = new ArrayList<>();
        }
        if (mListTribusByThematics == null){
            mListTribusByThematics = new ArrayList<>();
        }

        if (mListFollowedTribusThematics == null){
            mListFollowedTribusThematics = new ArrayList<>();
        }
        if (mListRemovedTribusThematics == null){
            mListRemovedTribusThematics = new ArrayList<>();
        }
        if (mListInvitedTribusThematics == null){
            mListInvitedTribusThematics = new ArrayList<>();
        }
        if (mListClosedTribusThematics == null){
            mListClosedTribusThematics = new ArrayList<>();
        }


        setTribusFragmentCategoriesAdapter();


        mSubscription.add(getAllTribus());

        //mSubscription.add(getRemovedTribusThematics());
        mSubscription.add(getFollowedTribusThematics());


        loadMore();
    }


    //show list of categories (tribus adicionadas, removidas, encerradas...)
    private List<String> getTitle(){

        List<String> categoryTitles = new ArrayList<>();
        String[] titles = {TRIBUS_ADICIONADAS, SOLICITACOES_ENVIADAS, TRIBUS_REMOVIDAS, TRIBUS_ENCERRADAS};

        categoryTitles.addAll(Arrays.asList(titles));

        return categoryTitles;
    }
    private void setTribusFragmentCategoriesAdapter(){
        mTribusFragmentCategoryAdapter = new TribusFragmentCategoriesAdapter(view.mContext.getContext(), getTitle(), this);
        view.mRvListCategory.setAdapter(mTribusFragmentCategoryAdapter);


    }

    @Override
    public void onCategoryClickListener(String categoryTitle) {

        switch (categoryTitle){

            case TRIBUS_ADICIONADAS:

                mSubscription.add(getFollowedTribusThematics());
                //loadMoreTribusByThematic(thematicTitle);

                /*if (mListFollowedTribusThematics != null && !mListFollowedTribusThematics.isEmpty()) {
                    mTribusThematicsAdapterFollowedTribus = new TribusThematicsAdapter(view.mContext.getActivity(),
                          mListFollowedTribusThematics, this);
                    view.mRvListThematics.setAdapter(mTribusThematicsAdapterFollowedTribus);
                    view.mRvListThematics.setVisibility(VISIBLE);
                    view.mRelativeListThematics.setVisibility(VISIBLE);
                    //mTribusThematicsAdapterFollowedTribus.notifyDataSetChanged();

                    //hide visitbity of removed thematics
                    view.mRelativeListThematicsRemovedTribus.setVisibility(GONE);
                    view.mRvListThematicsRemovedTribus.setVisibility(GONE);
                    view.mTvInfoThematicsRemovedTribus.setVisibility(GONE);


                }
                else {
                    view.mRelativeListThematicsRemovedTribus.setVisibility(GONE);
                    view.mRvListThematicsRemovedTribus.setVisibility(GONE);
                    view.mTvInfoThematicsRemovedTribus.setVisibility(GONE);
                    view.mTvInfoThematics.setText("Não há tribus adicionadas!");
                    view.mTvInfoThematics.setVisibility(VISIBLE);
                    view.mRvListThematics.setVisibility(GONE);
                    view.mRelativeListThematics.setVisibility(VISIBLE);

                    //hide visitbity of removed thematics

                    //view.mRecyclerView.setVisibility(View.GONE);

                }*/

                break;

            case TRIBUS_REMOVIDAS:
                mSubscription.add(getRemovedTribusThematics());
                /*if (mListRemovedTribusThematics != null && !mListRemovedTribusThematics.isEmpty()) {
                    mTribusThematicsAdapterRemovedTribus = null;
                    mTribusThematicsAdapterRemovedTribus = new TribusThematicsAdapter(view.mContext.getActivity(),
                          mListRemovedTribusThematics, this);
                    view.mRvListThematicsRemovedTribus.setAdapter(mTribusThematicsAdapterRemovedTribus);
                    view.mRvListThematicsRemovedTribus.setVisibility(View.VISIBLE);
                    //mTribusThematicsAdapterFollowedTribus.notifyDataSetChanged();
                    //hide visitbity of removed thematics
                    view.mRelativeListThematics.setVisibility(GONE);
                    view.mRvListThematics.setVisibility(GONE);
                    view.mTvInfoThematics.setVisibility(GONE);
                    view.mRelativeListThematicsRemovedTribus.setVisibility(View.VISIBLE);


                }
                else {
                    view.mRelativeListThematics.setVisibility(GONE);
                    view.mRvListThematics.setVisibility(GONE);
                    view.mTvInfoThematics.setVisibility(GONE);
                    view.mRvListThematicsRemovedTribus.setVisibility(GONE);
                    view.mTvInfoThematicsRemovedTribus.setText("Não há tribus removidas");
                    view.mTvInfoThematicsRemovedTribus.setVisibility(VISIBLE);
                    view.mRelativeListThematicsRemovedTribus.setVisibility(View.VISIBLE);

                    //view.mRecyclerView.setVisibility(View.GONE);

                }*/

                break;

            case SOLICITACOES_ENVIADAS:
                if (mListInvitedTribusThematics != null && !mListInvitedTribusThematics.isEmpty()) {
                    //mTribusThematicsAdapterFollowedTribus = new TribusThematicsAdapter(view.mContext.getActivity(),
                    //      mListInvitedTribusThematics, this);
                    //view.mRvListThematics.setAdapter(mTribusThematicsAdapterFollowedTribus);
                    view.mRvListThematics.setVisibility(VISIBLE);
                    mTribusThematicsAdapterFollowedTribus.notifyDataSetChanged();
                }
                else {
                    view.mRelativeListThematicsRemovedTribus.setVisibility(GONE);
                    view.mRvListThematicsRemovedTribus.setVisibility(GONE);
                    view.mTvInfoThematicsRemovedTribus.setVisibility(GONE);

                    view.mTvInfoThematics.setText("Não há solicitações para seguir uma tribu!");
                    view.mTvInfoThematics.setVisibility(VISIBLE);
                    view.mRvListThematics.setVisibility(GONE);
                    view.mRelativeListThematics.setVisibility(VISIBLE);
                    //view.mRecyclerView.setVisibility(View.GONE);

                }

                break;


            case TRIBUS_ENCERRADAS:
                if (mListClosedTribusThematics != null && !mListClosedTribusThematics.isEmpty()) {
                    //mTribusThematicsAdapterFollowedTribus = new TribusThematicsAdapter(view.mContext.getActivity(),
                    //      mListClosedTribusThematics, this);
                    //view.mRvListThematics.setAdapter(mTribusThematicsAdapterFollowedTribus);
                    view.mRvListThematics.setVisibility(View.VISIBLE);
                    mTribusThematicsAdapterFollowedTribus.notifyDataSetChanged();
                }
                else {
                    view.mRelativeListThematicsRemovedTribus.setVisibility(GONE);
                    view.mRvListThematicsRemovedTribus.setVisibility(GONE);
                    view.mTvInfoThematicsRemovedTribus.setVisibility(GONE);

                    view.mTvInfoThematics.setText("Não há tribus encerradas!");
                    view.mTvInfoThematics.setVisibility(View.VISIBLE);
                    view.mRvListThematics.setVisibility(View.GONE);
                    view.mRelativeListThematics.setVisibility(VISIBLE);
                    //view.mRecyclerView.setVisibility(View.GONE);

                }

                break;

        }
    }

    //get thematics's list of followed tribus (Política, Séries, Saúde...)
    private Subscription getFollowedTribusThematics(){
        if (mListFollowedTribusThematics!= null){
            mListFollowedTribusThematics.clear();
        }
        return model.getFollowedTribusThematics(mListFollowedTribusThematics)
                .doOnNext(listTribusFollowedThematics  -> {
                    mListFollowedTribusThematics = listTribusFollowedThematics;

                })
                .subscribe(listTribusFollowedThematics -> {

                            view.mRelativeListThematics.setVisibility(VISIBLE);

                            if (listTribusFollowedThematics != null) {
                                mTribusThematicsAdapterFollowedTribus = null;
                                mTribusThematicsAdapterFollowedTribus = new TribusThematicsAdapter(view.mContext.getActivity(),
                                        listTribusFollowedThematics, this);
                                view.mRvListThematics.setAdapter(mTribusThematicsAdapterFollowedTribus);
                                view.mRvListThematics.setVisibility(VISIBLE);

                                //hide visitbity of removed thematics
                                view.mRelativeListThematicsRemovedTribus.setVisibility(GONE);
                                view.mRvListThematicsRemovedTribus.setVisibility(GONE);
                                view.mTvInfoThematicsRemovedTribus.setVisibility(GONE);

                                //view.mRecyclerView.setVisibility(View.VISIBLE);
                                //mTribusThematicsAdapterFollowedTribus.notifyDataSetChanged();
                                Log.e("getFollowedTribusThem: ", "setou mTribusThematicsAdapterFollowedTribus - TRIBUS FOLLOWED");
                                Log.e("listTribusFollowedTh: ", listTribusFollowedThematics.toString());

                            }
                            else {
                                //hide visitbity of removed thematics
                                view.mRelativeListThematicsRemovedTribus.setVisibility(GONE);
                                view.mRvListThematicsRemovedTribus.setVisibility(GONE);
                                view.mTvInfoThematicsRemovedTribus.setVisibility(GONE);

                                view.mRvListThematics.setVisibility(GONE);
                                view.mTvInfoThematics.setText("Não há tribus adicionadas!");
                                view.mTvInfoThematics.setVisibility(VISIBLE);


                                //view.mRecyclerView.setVisibility(View.GONE);
                                //Toast.makeText(view.mContext.getContext(), "Não há tribus adicionadas", Toast.LENGTH_SHORT).show();

                            }
                        },
                        Throwable::printStackTrace);
    }

    private Subscription getRemovedTribusThematics(){
        if (mListRemovedTribusThematics != null){
            mListRemovedTribusThematics.clear();
        }
        return model.getRemovedTribusThematics(mListRemovedTribusThematics)
                .doOnNext(listRemovedTribusThematics  -> {
                    mListRemovedTribusThematics = listRemovedTribusThematics;

                })
                .subscribe(listRemovedTribusThematics -> {

                            view.mRelativeListThematicsRemovedTribus.setVisibility(VISIBLE);

                            if (listRemovedTribusThematics != null) {
                                mTribusThematicsAdapterRemovedTribus = null;
                                mTribusThematicsAdapterRemovedTribus = new TribusThematicsAdapter(view.mContext.getActivity(),
                                        listRemovedTribusThematics, this);
                                view.mRvListThematicsRemovedTribus.setAdapter(mTribusThematicsAdapterRemovedTribus);
                                view.mRvListThematicsRemovedTribus.setVisibility(VISIBLE);
                                //view.mRecyclerView.setVisibility(View.VISIBLE);
                                //mTribusThematicsAdapterFollowedTribus.notifyDataSetChanged();

                                //hide visitbity of removed thematics
                                view.mRelativeListThematics.setVisibility(GONE);
                                view.mRvListThematics.setVisibility(GONE);
                                view.mTvInfoThematics.setVisibility(GONE);

                                Log.e("getFollowedTribusThem: ", "setou mTribusThematicsAdapterFollowedTribus - TRIBUS FOLLOWED");
                                Log.e("listTribusRemovedTh: ", mListRemovedTribusThematics.toString());

                            }
                            else {
                                view.mRelativeListThematics.setVisibility(GONE);
                                view.mRvListThematics.setVisibility(GONE);
                                view.mTvInfoThematics.setVisibility(GONE);
                                view.mRvListThematicsRemovedTribus.setVisibility(GONE);
                                view.mTvInfoThematicsRemovedTribus.setText("Não há tribus removidas");
                                view.mTvInfoThematicsRemovedTribus.setVisibility(VISIBLE);
                                //view.mRecyclerView.setVisibility(View.GONE);
                                //Toast.makeText(view.mContext.getContext(), "Não há tribus adicionadas", Toast.LENGTH_SHORT).show();
                            }
                        },
                        Throwable::printStackTrace);
    }

    //get all tribus for the first time the fragment is initialized
    private Subscription getAllTribus() {
        if (mListTribusByThematics != null){
            mListTribusByThematics.clear();
        }
        mListTribus.clear();
        return model.getAllTribus(mListTribus)
                .subscribe(tribus -> {
                    if (tribus != null && !tribus.isEmpty()) {
                        mListTribus = tribus;
                        view.mCoordinatorTribus.setVisibility(VISIBLE);
                        view.mCoordinatorTribus.bringToFront();
                        mTribuFragmentAdapter = new TribusFragmentAdapter(view.mContext.getActivity(),
                                mListTribus, view, this);
                        view.mRecyclerView.setAdapter(mTribuFragmentAdapter);
                        view.mProgressBarListTribus.setVisibility(GONE);
                        view.mCoordinatorOptionsThematics.setVisibility(GONE);
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

                    model.loadMoreTribus(mListTribus, mTribuFragmentAdapter, view.mProgressBarBottom);
                }
            }
        });

    }

    @Override
    public void tribusThematicsAdapterOnClickListener(String thematicTitle) {

        view.mCoordinatorOptionsThematics.setVisibility(VISIBLE);
        view.mCoordinatorOptionsThematics.bringToFront();

        mSubscription.add(getTribusByThematics(thematicTitle));
        loadMoreTribusByThematic(thematicTitle);
    }

    //get all tribus by specifically thematic
    private Subscription getTribusByThematics(String thematic) {
        if (mListTribus != null){
            mListTribus.clear();
        }
        mListTribusByThematics.clear();
        return model.getTribusByThematics(mListTribusByThematics, thematic)
                .subscribe(tribus -> {
                    if (tribus != null && !tribus.isEmpty()) {
                        mListTribusByThematics = tribus;
                        mTribusFragmentAdapterByThematic = new TribusFragmentAdapter(view.mContext.getActivity(),
                                mListTribusByThematics, view, this);
                        view.mRvTribusByThematics.setAdapter(mTribusFragmentAdapterByThematic);
                        view.mProgressBarThematics.setVisibility(GONE);
                        view.mCoordinatorTribus.setVisibility(GONE);

                        Log.e("tribus: ", "mListTribusByThematics NÃO É null nem empty" +  mListTribusByThematics.toString());
                        //view.mProgressBarThematics.setVisibility(GONE);
                    }
                    else {
                        Log.e("tribus: ", "mListTribusByThematics É null nem empty" +  mListTribusByThematics.toString());

                    }
                        },
                        Throwable::printStackTrace);


    }


    private void loadMoreTribusByThematic(String thematicTitle) {
        view.mRvTribusByThematics.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                if (reachedBottom) {
                    view.mProgressBarBottom.bringToFront();
                    view.mProgressBarBottom.setVisibility(VISIBLE);

                    model.loadMoreTribusByThematics(mListTribusByThematics, thematicTitle, mTribusFragmentAdapterByThematic, view);
                }
            }
        });

    }


    public void onPause(){

    }

    public void onStop(){
    }

    public void onDetach(){
        mSubscription.clear();
    }

    public void onAttach(Context context) {

        if (context instanceof TribuFragmentPresenterOpenFeatureChoiceListener) {
            mListener = (TribuFragmentPresenterOpenFeatureChoiceListener) context;
        }

    }



    public void onDestroyView(){
        mSubscription.clear();
    }

    @Override
    public void openFeatureChoiceActivity(String tribuKey, String tribuUniqueName) {
        mListener.openFeatureChoiceActivity(tribuKey, tribuUniqueName);
    }

    public interface TribuFragmentPresenterOpenFeatureChoiceListener{
        void openFeatureChoiceActivity(String tribuKey, String tribuUniqueName);
    }


































    /*private final TribusFragmentView view;
    private final TribusFragmentModel model;
    private CompositeSubscription mSubscription = new CompositeSubscription();
    private CompositeSubscription subscriptionCategories = new CompositeSubscription();
    private Tribu mTribu;
    private FirebaseRecyclerAdapter<Tribu, TribusFragmentViewHolder> mAdapter;
    private Tribu mTribuAdmin;
    private MessageUser mMessageUser;
    private TribusFragmentCategoriesAdapter mTribusFragmentCategoryAdapter;
    private TribusThematicsAdapter mTribusThematicsAdapterFollowedTribus;
    private List<String> mListRemovedTribusThematics = new ArrayList<>();
    private List<String> mListInvitedTribusThematics = new ArrayList<>();
    private List<String> mListClosedTribusThematics = new ArrayList<>();
    private List<String> mListFollowedTribusThematics = new ArrayList<>();



    //REFERENCES - FIRBASE (KEEP SYNCED)
    private static FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private static DatabaseReference mReferenceUsers = mDatabase.getReference().child(GENERAL_USERS);
    private static DatabaseReference mReferenceFollowers = mDatabase.getReference().child(FOLLOWERS);
    private static DatabaseReference mReferenceAdmin = mDatabase.getReference().child(ADMINS);
    private static DatabaseReference mReferenceTribus = mDatabase.getReference().child(GENERAL_TRIBUS);


    public TribusFragmentPresenter(TribusFragmentView view, TribusFragmentModel model) {
        this.view = view;
        this.model = model;
    }


    public void onStart(){
        //KEEP SYNCED
        mReferenceUsers.keepSynced(true);
        mReferenceFollowers.keepSynced(true);
        mReferenceAdmin.keepSynced(true);
        mReferenceTribus.keepSynced(true);


        mSubscription.add(observeModel());

        setTribusFragmentCategoriesAdapter();

        mSubscription.add(getFollowedTribusThematics());
        mSubscription.add(getInvitedTribusThematics());
        mSubscription.add(getRemovedTribusThematics());
        mSubscription.add(getEndedTribusThematics());

        //mSubscription.add(hasChildren());
        //mSubscription.add(populateRvThematics());



        if(view.mRecyclerView != null && mAdapter != null){
            mAdapter.notifyDataSetChanged();
        }


    }

    private List<String> getTitle(){

        List<String> categoryTitles = new ArrayList<>();
        String[] titles = {TRIBUS_ADICIONADAS, SOLICITACOES_ENVIADAS, TRIBUS_REMOVIDAS, TRIBUS_ENCERRADAS};

        categoryTitles.addAll(Arrays.asList(titles));

        return categoryTitles;
    }


    private void setTribusFragmentCategoriesAdapter(){
        mTribusFragmentCategoryAdapter = new TribusFragmentCategoriesAdapter(view.mContext.getContext(), getTitle(), this);
        view.mRvListCategory.setAdapter(mTribusFragmentCategoryAdapter);


    }


    @Override
    public void tribusThematicsAdapterOnClickListener(String title) {

    }


    //SUBSCRIPTION TO POPULATE RECYCLER VIEW WITH ALL TRIBUS
    private Subscription observeModel(){
        //return model.getCurrentUser()
                //.subscribe(
                    ///    this::setAdapter,
                  //      Throwable::printStackTrace
                //);
    }

    private void setAdapter(User user){
        //mAdapter = model.setAdapter(user);

        //if(view.mRecyclerView != null && mAdapter != null) {
          //  view.mRecyclerView.setAdapter(mAdapter);
        //}

        //view.mProgressBarListTribus.setVisibility(View.GONE);
    }



    private Subscription getFollowedTribusThematics(){
        return model.getFollowedTribusThematics(new ArrayList<>())
                .doOnNext(listTribusFollowedThematics  -> {
                    mListFollowedTribusThematics = listTribusFollowedThematics;

                })
                .subscribe(listTribusFollowedThematics -> {

                    if (listTribusFollowedThematics != null) {
                        mTribusThematicsAdapterFollowedTribus = new TribusThematicsAdapter(view.mContext.getActivity(),
                                listTribusFollowedThematics, this);
                        view.mRvListThematics.setAdapter(mTribusThematicsAdapterFollowedTribus);
                        view.mRvListThematics.setVisibility(View.VISIBLE);
                        //view.mRecyclerView.setVisibility(View.VISIBLE);
                        //mTribusThematicsAdapterFollowedTribus.notifyDataSetChanged();
                        Log.e("getFollowedTribusThem: ", "setou mTribusThematicsAdapterFollowedTribus - TRIBUS FOLLOWED");
                        Log.e("listTribusFollowedTh: ", listTribusFollowedThematics.toString());

                    }
                    else {
                        view.mRvListThematics.setVisibility(View.GONE);
                        view.mTvInfoThematics.setText("Não há tribus adicionadas!");
                        view.mTvInfoThematics.setVisibility(View.VISIBLE);
                        //view.mRecyclerView.setVisibility(View.GONE);
                        //Toast.makeText(view.mContext.getContext(), "Não há tribus adicionadas", Toast.LENGTH_SHORT).show();
                    }
                },
                        Throwable::printStackTrace);
    }

    private Subscription getInvitedTribusThematics(){
        return model.getInvitedTribusThematics(new ArrayList<>())
                .doOnNext(listTribusInvitedThematics -> {
                    mListInvitedTribusThematics = listTribusInvitedThematics;
                })
                .subscribe(listTribusInvitedThematics -> {
                    //if (listTribusInvitedThematics != null && !listTribusInvitedThematics.isEmpty()) {
                      //  mTribusThematicsAdapterFollowedTribus = new TribusThematicsAdapter(view.mContext.getActivity(), listTribusInvitedThematics);
                        //view.mRvListThematics.setAdapter(mTribusThematicsAdapterFollowedTribus);
                        //mTribusThematicsAdapterFollowedTribus.notifyDataSetChanged();
                        //Log.e("listTribusInvitedTh: ", listTribusInvitedThematics.toString());
                        //Log.e("getInvitedTribusThem: ", "setou mTribusThematicsAdapterFollowedTribus - TRIBUS INVITED");

                    }
                });
    }

    private Subscription getRemovedTribusThematics(){
        return model.getRemovedTribusThematics(new ArrayList<>())
                .doOnNext(listTribusRemovedThematics -> {
                    //mListRemovedTribusThematics = listTribusRemovedThematics;
                })
                .subscribe(listTribusRemovedThematics -> {
                    //if (listTribusRemovedThematics != null && !listTribusRemovedThematics.isEmpty()) {
                      //  mTribusThematicsAdapterFollowedTribus = new TribusThematicsAdapter(view.mContext.getActivity(), listTribusRemovedThematics);
                        //view.mRvListThematics.setAdapter(mTribusThematicsAdapterFollowedTribus);
                        //mTribusThematicsAdapterFollowedTribus.notifyDataSetChanged();
                        //Log.e("listTribusRemovedTh: ", listTribusRemovedThematics.toString());
                        //Log.e("getrRemovedTribusThem: ", "setou mTribusThematicsAdapterFollowedTribus - TRIBUS REMOVIDAS");
                    }

                });
    }

    private Subscription getEndedTribusThematics(){
        return model.getEndedTribusThematics(new ArrayList<>())
                .doOnNext(listTribusEndedThematics -> {
                    mListClosedTribusThematics = listTribusEndedThematics;
                })
                .subscribe(listTribusEndedThematics -> {
                    //if (listTribusEndedThematics != null && !listTribusEndedThematics.isEmpty()) {
                      //  mTribusThematicsAdapterFollowedTribus = new TribusThematicsAdapter(view.mContext.getActivity(), mListClosedTribusThematics);
                        //view.mRvListThematics.setAdapter(mTribusThematicsAdapterFollowedTribus);
                        //mTribusThematicsAdapterFollowedTribus.notifyDataSetChanged();
                        //Log.e("listTribusEndedThe: ", listTribusEndedThematics.toString());
                        //Log.e("getEndedTribusThem: ", "setou mTribusThematicsAdapterFollowedTribus - TRIBUS ENDED");
                    }

                });
    }


    @Override
    public void onCategoryClickListener(String categoryTitle) {

        switch (categoryTitle){

            case TRIBUS_ADICIONADAS:
                if (mListFollowedTribusThematics != null && !mListFollowedTribusThematics.isEmpty()) {
                    mTribusThematicsAdapterFollowedTribus = new TribusThematicsAdapter(view.mContext.getActivity(),
                            mListFollowedTribusThematics, this);
                    view.mRvListThematics.setAdapter(mTribusThematicsAdapterFollowedTribus);
                    view.mRvListThematics.setVisibility(View.VISIBLE);
                    mTribusThematicsAdapterFollowedTribus.notifyDataSetChanged();
                }
                else {
                    view.mTvInfoThematics.setText("Não há tribus adicionadas!");
                    view.mTvInfoThematics.setVisibility(View.VISIBLE);
                    view.mRvListThematics.setVisibility(View.GONE);
                    //view.mRecyclerView.setVisibility(View.GONE);

                }

                Log.e("tribus cat clicked: ", "TRIBUS_ADICIONADAS");
                break;

            case SOLICITACOES_ENVIADAS:
                if (mListInvitedTribusThematics != null && !mListInvitedTribusThematics.isEmpty()) {
                    mTribusThematicsAdapterFollowedTribus = new TribusThematicsAdapter(view.mContext.getActivity(),
                            mListInvitedTribusThematics, this);
                    view.mRvListThematics.setAdapter(mTribusThematicsAdapterFollowedTribus);
                    view.mRvListThematics.setVisibility(View.VISIBLE);
                    mTribusThematicsAdapterFollowedTribus.notifyDataSetChanged();
                }
                else {
                    view.mTvInfoThematics.setText("Não há solicitações para seguir uma mTribu!");
                    view.mTvInfoThematics.setVisibility(View.VISIBLE);
                    view.mRvListThematics.setVisibility(View.GONE);
                    //view.mRecyclerView.setVisibility(View.GONE);

                }

                Log.e("tribus cat clicked: ", "SOLICITACOES_ENVIADAS");

                break;

            case TRIBUS_REMOVIDAS:
                if (mListRemovedTribusThematics != null && !mListRemovedTribusThematics.isEmpty()) {
                    mTribusThematicsAdapterFollowedTribus = new TribusThematicsAdapter(view.mContext.getActivity(),
                            mListRemovedTribusThematics, this);
                    view.mRvListThematics.setAdapter(mTribusThematicsAdapterFollowedTribus);
                    view.mRvListThematics.setVisibility(View.VISIBLE);
                    mTribusThematicsAdapterFollowedTribus.notifyDataSetChanged();
                }
                else {
                    view.mTvInfoThematics.setText("Não há tribus removidas!");
                    view.mTvInfoThematics.setVisibility(View.VISIBLE);
                    view.mRvListThematics.setVisibility(View.GONE);
                    //view.mRecyclerView.setVisibility(View.GONE);

                }

                Log.e("tribus cat clicked: ", "TRIBUS_REMOVIDAS");

                break;

            case TRIBUS_ENCERRADAS:
                if (mListClosedTribusThematics != null && !mListClosedTribusThematics.isEmpty()) {
                    mTribusThematicsAdapterFollowedTribus = new TribusThematicsAdapter(view.mContext.getActivity(),
                            mListClosedTribusThematics, this);
                    view.mRvListThematics.setAdapter(mTribusThematicsAdapterFollowedTribus);
                    view.mRvListThematics.setVisibility(View.VISIBLE);
                    mTribusThematicsAdapterFollowedTribus.notifyDataSetChanged();
                }
                else {
                    view.mTvInfoThematics.setText("Não há tribus encerradas!");
                    view.mTvInfoThematics.setVisibility(View.VISIBLE);
                    view.mRvListThematics.setVisibility(View.GONE);
                    //view.mRecyclerView.setVisibility(View.GONE);

                }
                 Log.e("tribus cat clicked: ", "TRIBUS_ENCERRADAS");

                break;

        }
    }




































































    //OLD CODE
    private Subscription hasChildren(){
        return model.hasChildren()
                .subscribe(hasChildren -> {
                    if (!hasChildren){

                            view.mRecyclerView.setVisibility(View.GONE);
                            TextView tv = new TextView(view.getContext());
                            tv.setText(R.string.hasChildrenMainTribus);
                            tv.setId(R.id.text);
                            tv.setTextSize(16);
                            tv.setTextColor(view.getResources().getColor(R.color.colorPrimaryDark));

                            tv.setLayoutParams(new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT,
                                    CoordinatorLayout.LayoutParams.WRAP_CONTENT));
                            tv.setGravity(Gravity.CENTER_HORIZONTAL);

                            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) view.mCoordinatorTribus.getLayoutParams();

                            setViewMargins(view.getContext(), params, 0, 24, 0, 0, tv);

                            view.mCoordinatorTribus.addView(tv);


                    }
                    else {

                        if (view.mCoordinatorTribus.findViewById(R.id.text) != null) {
                            //view.mTvTribuWhichYouFollow.setVisibility(VISIBLE);
                            view.mRecyclerView.setVisibility(View.VISIBLE);
                            view.mCoordinatorTribus.removeView(view.mCoordinatorTribus.findViewById(R.id.text));


                        }

                    }
                },
                        Throwable::printStackTrace
                );
    }

    private void setViewMargins(Context con, ViewGroup.LayoutParams params,
                                int left, int top, int right, int bottom, View view) {

        final float scale = con.getResources().getDisplayMetrics().density;
        // convert the DP into pixel
        int pixel_left = (int) (left * scale + 0.5f);
        int pixel_top = (int) (top * scale + 0.5f);
        int pixel_right = (int) (right * scale + 0.5f);
        int pixel_bottom = (int) (bottom * scale + 0.5f);

        ViewGroup.MarginLayoutParams s = (ViewGroup.MarginLayoutParams) params;
        s.setMargins(pixel_left, pixel_top, pixel_right, pixel_bottom);

        view.setLayoutParams(params);
    }


    //SUBSCRIPTION TO POPULATE RECYCLER VIEW TO ADMIN
    private Subscription observeAdmin(){
        return model.getUser()
                .switchMap(model::getTribuAdmin)
                .subscribe(tribu -> {

                            mTribuAdmin = tribu;
                            if (mTribuAdmin != null) {
                                mTribu = mTribuAdmin;
                                //view.mIncludeCardViewTribuAdmin.setVisibility(VISIBLE);
                                //view.mViewRowTop.setVisibility(VISIBLE);
                                //view.mConstraintLayout.setVisibility(VISIBLE);
                                //view.mCircleImageTribu.setVisibility(VISIBLE);
                                //view.mTvNameTribu.setVisibility(VISIBLE);
                                //view.mTvUniqueName.setVisibility(VISIBLE);
                                //view.mTvMessage.setVisibility(VISIBLE);
                                //view.mTvTribuWhichYouAreAdmin.setVisibility(VISIBLE);

                                //view.mRelativeTribuAdmin.setVisibility(VISIBLE);
                                //setNumFollowers(mTribu);

                            }

                            //model.getMessagesTribuAdmin(mTribuAdmin, view);
                            model.getTopicMessages(mTribuAdmin, view);

                            //setNumberOfFollowers(mTribu);

                },
                        Throwable::printStackTrace
                );
    }




    public void onPause(){

    }

    public void onStop(){
        model.removeListeners();
        if(mAdapter != null){
            mAdapter.notifyDataSetChanged();
        }
    }

    public void onDetach(){
        if (mAdapter != null) {
            mAdapter.cleanup();
            mSubscription.clear();
            model.removeListeners();
            //subscriptionCategories.clear();
        }
    }




    private void openImageTribu() {

        //CREATE DIALOG TO SHOW NEW IMAGE
        //CONFIGURATION OF DIALOG
        AlertDialog.Builder builder = new AlertDialog.Builder(view.mContext.getActivity(), R.style.MyDialogTheme);
        LayoutInflater inflater = view.mContext.getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_image_tribu, null);
        SimpleDraweeView mSdImageTribu = dialogView.findViewById(R.id.sd_image_tribu);
        TextView mTvNameOfTribu = dialogView.findViewById(R.id.tv_name_of_tribu);
        TextView mTvUniqueName = dialogView.findViewById(R.id.tv_unique_name);
        TextView mTvAdminSince = dialogView.findViewById(R.id.tv_created_date);

        mTvNameOfTribu.setText(mTribu.getProfile().getNameTribu());
        mTvUniqueName.setText(mTribu.getProfile().getUniqueName());
        builder.setView(dialogView);

            String time = GetTimeAgo.getTimeAgo(mTribu.getProfile().getCreationDate(), view.mContext.getActivity());
            String append = "Criada ";
            String appendDate = append + time;
            mTvAdminSince.setText(appendDate);

        ControllerListener listener = new BaseControllerListener() {
            @Override
            public void onFailure(String id, Throwable throwable) {
                super.onFailure(id, throwable);
                //Log.d("Valor: ", "onFailure - View: " + throwable.toString());

            }

            @Override
            public void onIntermediateImageFailed(String id, Throwable throwable) {
                super.onIntermediateImageFailed(id, throwable);
            }

            @Override
            public void onFinalImageSet(String id, Object imageInfo, Animatable animatable) {
                super.onFinalImageSet(id, imageInfo, animatable);
            }

            @Override
            public void onIntermediateImageSet(String id, Object imageInfo) {
                super.onIntermediateImageSet(id, imageInfo);
            }

            @Override
            public void onRelease(String id) {
                super.onRelease(id);
            }

            @Override
            public void onSubmit(String id, Object callerContext) {
                super.onSubmit(id, callerContext);
                //Log.d("Valor: ", "onSubmit");

            }
        };
        DraweeController dc = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse(mTribu.getProfile().getImageUrl()))
                .setControllerListener(listener)
                .setOldController(mSdImageTribu.getController())
                .build();
        mSdImageTribu.setController(dc);

        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams wmlp = dialog.getWindow()
                .getAttributes();
        wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        wmlp.gravity = WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;
        dialog.getWindow().setGravity(wmlp.gravity);


        dialog.show();

    }


    public void onDestroyView(){
        if (mAdapter != null) {
            mAdapter.cleanup();
            mSubscription.clear();
            model.removeListeners();
            //subscriptionCategories.clear();
        }
    }
*/


}
