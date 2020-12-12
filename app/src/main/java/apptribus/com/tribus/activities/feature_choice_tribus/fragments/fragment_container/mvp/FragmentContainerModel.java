package apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_container.mvp;

import android.support.v4.app.Fragment;
import android.widget.ProgressBar;

import java.util.List;

import apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_container.adapter.FragmentContainerSurveysAdapter;
import apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_container.adapter.FragmentContainerTopicsAdapter;
import apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_container.repository.FragmentContainerAPI;
import apptribus.com.tribus.pojo.ConversationTopic;
import apptribus.com.tribus.pojo.Survey;
import apptribus.com.tribus.util.firestore.FirestoreService;
import rx.Observable;

public class FragmentContainerModel {

    private final Fragment fragment;

    private FirestoreService mFirestoreService;

    public FragmentContainerModel(Fragment fragment) {
        this.fragment = fragment;
        mFirestoreService = new FirestoreService(fragment.getActivity());
    }

    public Observable<List<ConversationTopic>> getAllTopics(List<ConversationTopic> topics, String tribuKey) {
        return FragmentContainerAPI.getAllTopics(topics, tribuKey);
    }

    public void loadMoreTopics(List<ConversationTopic> topics, FragmentContainerTopicsAdapter fragmentContainerTopicsAdapter,
                               ProgressBar mProgressBarBottom, String tribuKey) {
        FragmentContainerAPI.loadMoreTopics(topics, fragmentContainerTopicsAdapter, mProgressBarBottom, tribuKey);
    }

    public Observable<List<Survey>> getAllSurveys(List<Survey> surveys, String tribuKey) {
        return FragmentContainerAPI.getAllSurveys(surveys, tribuKey);
    }

    public void loadMoreSurveys(List<Survey> surveys, FragmentContainerSurveysAdapter fragmentContainerSurveysAdapter,
                                ProgressBar mProgressBarBottom, String tribuKey) {
        FragmentContainerAPI.loadMoreSurveys(surveys, fragmentContainerSurveysAdapter, mProgressBarBottom, tribuKey);
    }

}
