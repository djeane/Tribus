package apptribus.com.tribus.activities.main_activity.fragment_timeline.mvp;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.main_activity.fragment_timeline.adapter.MuralVerticalAdapter;
import apptribus.com.tribus.activities.main_activity.fragment_timeline.adapter.TribusLineAdapter;
import apptribus.com.tribus.activities.main_activity.fragment_timeline.adapter.TribusLineThematicsAdapter;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.util.PresenceSystemAndLastSeen;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static android.view.View.*;

/**
 * Created by User on 5/27/2017.
 */

public class TimeLinePresenter implements TribusLineThematicsAdapter.OnClickThematicsTribusLineListener,
        TribusLineAdapter.TribusLineAdapterListener {

    private final TimeLineView view;
    private final TimeLineModel model;
    private CompositeSubscription mSubscription;
    private TribusLinePresenterListener mListener;

    private TribusLineThematicsAdapter mTribusLineThematicsAdapter;
    private TribusLineAdapter mTimeLineAdapter;
    private TribusLineAdapter mTimeLineAdapterByThematics;
    private List<String> mListThematics; //= new ArrayList<>();
    private List<Tribu> mListTribus; //= new ArrayList<>();
    private List<Tribu> mListTribusByThematics; //= new ArrayList<>();

    //to mural button
    private List<String> mListTagThematics;
    private MuralVerticalAdapter mMuralVerticalAdapter;

    private int mPositionIndexTribus;
    private int mPositionIndexTribusByThematic;
    private int mPositionIndexThematic;


    public TimeLinePresenter(TimeLineView view, TimeLineModel model) {
        this.view = view;
        this.model = model;
    }

    public void onStart() {

        PresenceSystemAndLastSeen.presenceSystem();

        mSubscription = new CompositeSubscription();

        if (mListThematics == null){
            mListThematics = new ArrayList<>();
        }
        if (mListTribus == null){
            mListTribus = new ArrayList<>();
        }
        if (mListTribusByThematics == null){
            mListTribusByThematics = new ArrayList<>();
        }

        //to mural button
        if (mListTagThematics == null){
            mListTagThematics = new ArrayList<>();
        }

        mSubscription.add(getThematicsToPopulateRv());
        mSubscription.add(getAllTribus());
        mSubscription.add(observeBtnWhatsGoingOn());
        mSubscription.add(getThematicsTag());
        //mSubscription.add(observeBtnWhatsGoingOn());
        mSubscription.add(observeBtnTribusLine());

        loadMore();

        ((LinearLayoutManager) view.mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(mPositionIndexTribus, 0);
        ((LinearLayoutManager) view.mRvTribusByThematics.getLayoutManager()).scrollToPositionWithOffset(mPositionIndexTribusByThematic, 0);
        ((LinearLayoutManager) view.mRvListThematics.getLayoutManager()).scrollToPositionWithOffset(mPositionIndexThematic, 0);

    }


    private Subscription getThematicsToPopulateRv() {
        mListThematics.clear();
        return model.getThematicsToPopulateRv(mListThematics, view)
                .subscribe(thematics -> {
                            mListThematics = thematics;
                            mTribusLineThematicsAdapter = new TribusLineThematicsAdapter(view, mListThematics, this);
                            view.mRvListThematics.setAdapter(mTribusLineThematicsAdapter);
                        },
                        Throwable::printStackTrace);

    }

    private Subscription getAllTribus() {
        if (mListTribusByThematics != null){
            mListTribusByThematics.clear();
        }
        mListTribus.clear();
        return model.getAllTribus(mListTribus)
                .subscribe(tribus -> {
                            mListTribus = tribus;
                            view.mCoordinatorTribusList.setVisibility(VISIBLE);
                            view.mCoordinatorTribusList.bringToFront();
                            mTimeLineAdapter = new TribusLineAdapter(view.mContext, mListTribus, view, this);
                            view.mRecyclerView.setAdapter(mTimeLineAdapter);
                            if (view.mProgressBar != null && view.mRecyclerView != null) {
                                view.mProgressBar.setVisibility(GONE);
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

                    model.loadMoreTribus(mListTribus, mTimeLineAdapter, view.mProgressBarBottom);
                }
            }
        });

    }

    private Subscription getThematicsTag(){
        mListTagThematics.clear();
        return model.getThematicsTag(mListTagThematics)
                .subscribe(tags -> {
                    view.mProgressBarContentTribus.setVisibility(GONE);

                    if (tags != null && !tags.isEmpty()) {
                        mListTagThematics = tags;
                        mMuralVerticalAdapter = new MuralVerticalAdapter(view.mContext.getActivity(), mListTagThematics, this);
                        view.mRvListMuralTribus.setAdapter(mMuralVerticalAdapter);
                        view.mRvListMuralTribus.setVisibility(VISIBLE);

                    }
                    else {
                        view.mRvListMuralTribus.setVisibility(GONE);
                        view.mTvNoTag.setVisibility(VISIBLE);
                    }
                });
    }

    private Subscription observeBtnWhatsGoingOn() {
        return view.observableBtnWhatsGoingOn()
                .subscribe(__ -> {
                    //view.mCoordinatorOptionsThematics.setVisibility(GONE);
                    //view.mCoordinatorTribusList.setVisibility(GONE);
                            mListener.btnMuralOnClickListenerHide();
                            view.mCoordinatorTribusList.setVisibility(GONE);
                            view.mCoordinatorOptionsThematics.setVisibility(GONE);
                            view.mCoordinatorContentTribus.setVisibility(VISIBLE);
                            view.mCoordinatorContentTribus.bringToFront();
                            view.mProgressBarBottom.setVisibility(GONE);
                            view.mBtnTribusLine.setTextColor(view.getResources().getColor(R.color.primary_light));
                            view.mViewTribusLine.setBackgroundColor(view.getResources().getColor(R.color.colorIcons));
                            view.mBtnWhatsGoingOn.setTextColor(view.getResources().getColor(R.color.accent));
                            view.mViewWhatsGoingOn.setBackgroundColor(view.getResources().getColor(R.color.accent));
                    //Toast.makeText(view.mContext.getActivity(), "Aguarde! Logo esta novidade estará disponível! ", Toast.LENGTH_SHORT).show();
                },
                        Throwable::printStackTrace);
    }

    private Subscription observeBtnTribusLine() {
        return view.observableBtnTribusLine()
                .subscribe(__ -> {
                            mListener.btnMuralOnClickListenerShow();
                            view.mCoordinatorTribusList.setVisibility(VISIBLE);
                            view.mCoordinatorTribusList.bringToFront();
                            view.mCoordinatorContentTribus.setVisibility(GONE);
                            view.mCoordinatorOptionsThematics.setVisibility(GONE);
                            view.mBtnWhatsGoingOn.setTextColor(view.getResources().getColor(R.color.primary_light));
                            view.mViewWhatsGoingOn.setBackgroundColor(view.getResources().getColor(R.color.colorIcons));
                            view.mBtnTribusLine.setTextColor(view.getResources().getColor(R.color.accent));
                            view.mViewTribusLine.setBackgroundColor(view.getResources().getColor(R.color.accent));
                        },
                        Throwable::printStackTrace);
    }

    public void onPause() {
        PresenceSystemAndLastSeen.lastSeen();

    }

    public void onStop() {
        //mListThematics.clear();
        //mListTribus.clear();
        //mListTribusByThematics.clear();

        mPositionIndexTribus = ((LinearLayoutManager) view.mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        mPositionIndexTribusByThematic = ((LinearLayoutManager) view.mRvTribusByThematics.getLayoutManager()).findFirstVisibleItemPosition();
        mPositionIndexThematic = ((LinearLayoutManager) view.mRvListThematics.getLayoutManager()).findFirstVisibleItemPosition();

        //PresenceSystemAndLastSeen.presenceSystem();
        //mSubscription.clear();
    }


    public void onResume() {

        PresenceSystemAndLastSeen.presenceSystem();

        //((LinearLayoutManager) view.mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(mPositionIndexTribus, 0);
        //((LinearLayoutManager) view.mRvTribusByThematics.getLayoutManager()).scrollToPositionWithOffset(mPositionIndexTribusByThematic, 0);
        //((LinearLayoutManager) view.mRvListThematics.getLayoutManager()).scrollToPositionWithOffset(mPositionIndexThematic, 0);

    }


    @Override
    public void onClickThematicsListener(String thematicTitle, Button mButtonThematics) {


        view.mCoordinatorOptionsThematics.setVisibility(VISIBLE);
        view.mCoordinatorOptionsThematics.bringToFront();
        view.mCoordinatorContentTribus.setVisibility(GONE);
        view.mBtnWhatsGoingOn.setTextColor(view.getResources().getColor(R.color.primary_light));
        view.mViewWhatsGoingOn.setBackgroundColor(view.getResources().getColor(R.color.colorIcons));
        view.mBtnTribusLine.setTextColor(view.getResources().getColor(R.color.accent));
        view.mViewTribusLine.setBackgroundColor(view.getResources().getColor(R.color.accent));

        mSubscription.add(getTribusByThematics(thematicTitle, mButtonThematics));
        loadMoreTribusByThematic(thematicTitle);
    }

    private Subscription getTribusByThematics(String thematic, Button mButtonThematics) {
        if (mListTribus != null){
            mListTribus.clear();
        }
        mListTribusByThematics.clear();
            return model.getTribusByThematics(mListTribusByThematics, thematic)
                    .subscribe(tribus -> {

                            mListTribusByThematics = tribus;
                            mTimeLineAdapterByThematics = new TribusLineAdapter(view.mContext, mListTribusByThematics, view, this);
                            view.mRvTribusByThematics.setAdapter(mTimeLineAdapterByThematics);
                            view.mProgressBarThematics.setVisibility(GONE);
                            mButtonThematics.setEnabled(true);
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

                    model.loadMoreTribusByThematics(mListTribusByThematics, thematicTitle, mTimeLineAdapterByThematics, view);
                }
            }
        });

    }


    public void onDestroyView() {
        mSubscription.clear();
    }

    public void onAttach(Context context) {

        if (context instanceof TribusLinePresenterListener) {
            mListener = (TribusLinePresenterListener) context;
        }

    }


    @Override
    public void openFeatureChoiceActivity(Tribu tribu) {
        mListener.openFeactureChoice(tribu);
    }

    @Override
    public void openProfileTribuUserActivity(Tribu tribu) {
        mListener.openProfileTribusUserActivity(tribu);
    }

    @Override
    public void openShareFragment(Tribu tribu) {
        mListener.openShareFragment(tribu);
    }

    public interface TribusLinePresenterListener{
        void openFeactureChoice(Tribu tribu);
        void openProfileTribusUserActivity(Tribu tribu);
        void openShareFragment(Tribu tribu);
        void btnMuralOnClickListenerShow();
        void btnMuralOnClickListenerHide();
    }
}
