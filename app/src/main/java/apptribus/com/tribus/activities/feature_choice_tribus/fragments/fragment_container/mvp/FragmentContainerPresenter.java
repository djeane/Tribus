package apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_container.mvp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_container.adapter.FragmentContainerSurveysAdapter;
import apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_container.adapter.FragmentContainerTopicsAdapter;
import apptribus.com.tribus.pojo.ConversationTopic;
import apptribus.com.tribus.pojo.Survey;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static apptribus.com.tribus.util.Constantes.SURVEY;
import static apptribus.com.tribus.util.Constantes.TOPIC;

public class FragmentContainerPresenter implements FragmentContainerTopicsAdapter.OnShowTopicsViewHolderListener {

    private final FragmentContainerView view;
    private final FragmentContainerModel model;
    private CompositeSubscription subscription = new CompositeSubscription();
    private OnFragmentContainerListener onFragmentContainerListener;
    private List<ConversationTopic> mListTopics;
    private List<Survey> mListSurveys;
    private FragmentContainerTopicsAdapter mFragmentContainerTopicsAdapter;
    private FragmentContainerSurveysAdapter mFragmentContainerSurveysAdapter;


    public FragmentContainerPresenter(FragmentContainerView view, FragmentContainerModel model) {
        this.view = view;
        this.model = model;
    }

    public void onStart() {

        if (mListTopics == null) {
            mListTopics = new ArrayList<>();
        }

        if (mListSurveys == null) {
            mListSurveys = new ArrayList<>();
        }

        if (view.mRvContainer != null && (view.mTribusFeature == null || view.mTribusFeature.equals(TOPIC))) {
            subscription.add(getAllTopics());
        } else if (view.mRvContainer != null && (view.mTribusFeature == null || view.mTribusFeature.equals(SURVEY))) {
            subscription.add(getAllSurveys());
        }

        loadMore();

    }


    private Subscription getAllTopics() {
        mListTopics.clear();
        return model.getAllTopics(mListTopics, view.mTribusKey)
                .subscribe(topics -> {
                            if (topics == null || topics.isEmpty()) {
                                view.mCoordinatorRecycler.setVisibility(GONE);
                                view.mTvNoTopics.setVisibility(VISIBLE);
                                view.mTvNoSurveys.setVisibility(GONE);

                            } else {
                                view.mCoordinatorRecycler.setVisibility(VISIBLE);
                                view.mTvNoTopics.setVisibility(GONE);
                                view.mTvNoSurveys.setVisibility(GONE);
                                mListTopics = topics;

                                mFragmentContainerTopicsAdapter = new FragmentContainerTopicsAdapter(view.mContext,
                                        mListTopics, view, this, view.mTribusKey);
                                view.mRvContainer.setAdapter(mFragmentContainerTopicsAdapter);
                                view.mProgressBar.setVisibility(GONE);
                                view.mProgressBarBottom.setVisibility(GONE);
                            }

                        },
                        Throwable::printStackTrace);

    }

    private Subscription getAllSurveys() {
        mListSurveys.clear();
        return model.getAllSurveys(mListSurveys, view.mTribusKey)
                .subscribe(surveys -> {
                            if (surveys == null || surveys.isEmpty()) {
                                view.mCoordinatorRecycler.setVisibility(GONE);
                                view.mTvNoTopics.setVisibility(GONE);
                                view.mTvNoSurveys.setVisibility(VISIBLE);

                            } else {
                                view.mCoordinatorRecycler.setVisibility(VISIBLE);
                                view.mTvNoTopics.setVisibility(GONE);
                                view.mTvNoSurveys.setVisibility(GONE);
                                mListSurveys = surveys;

                                mFragmentContainerSurveysAdapter = new FragmentContainerSurveysAdapter(view.mContext,
                                        mListSurveys, view, view.mTribusKey);
                                view.mRvContainer.setAdapter(mFragmentContainerSurveysAdapter);
                                view.mProgressBar.setVisibility(GONE);
                                view.mProgressBarBottom.setVisibility(GONE);
                            }

                        },
                        Throwable::printStackTrace);

    }

    private void loadMore() {
        view.mRvContainer.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                if (reachedBottom) {
                    view.mProgressBarBottom.bringToFront();
                    view.mProgressBarBottom.setVisibility(VISIBLE);

                    if (view.mTribusFeature.equals(TOPIC)) {
                        model.loadMoreTopics(mListTopics, mFragmentContainerTopicsAdapter, view.mProgressBarBottom, view.mTribusKey);
                    } else if (view.mTribusFeature.equals(SURVEY)) {
                        model.loadMoreSurveys(mListSurveys, mFragmentContainerSurveysAdapter, view.mProgressBarBottom, view.mTribusKey);
                    }

                }
            }
        });

    }


    public void onPause() {

    }

    public void onStop() {

    }

    public void onDetach() {

    }

    public void onAttach(Context context) {

        if (context instanceof FragmentContainerPresenter.OnFragmentContainerListener) {
            onFragmentContainerListener = (FragmentContainerPresenter.OnFragmentContainerListener) context;
        }
    }


    public void onDestroyView() {
        subscription.clear();
    }

    @Override
    public void itemTopicClickListener(String tribuUniqueName, String topicKey, String tribuKey, String tribuName, String topicName) {
        onFragmentContainerListener.startChatActivity(tribuUniqueName, topicKey, tribuKey, tribuName, topicName);
    }

    public interface OnFragmentContainerListener {
        void startChatActivity(String tribuUniqueName, String topicKey, String tribuKey, String tribuName, String topicName);
    }

}
