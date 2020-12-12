package apptribus.com.tribus.activities.main_activity.fragment_timeline.mvp;

import android.support.v4.app.Fragment;
import android.widget.ProgressBar;

import java.util.List;

import apptribus.com.tribus.activities.main_activity.fragment_timeline.adapter.TribusLineAdapter;
import apptribus.com.tribus.activities.main_activity.fragment_timeline.repository.TimeLineAPI;
import apptribus.com.tribus.pojo.MessageUser;
import apptribus.com.tribus.pojo.Tribu;
import rx.Observable;


/**
 * Created by User on 5/27/2017.
 */

public class TimeLineModel {

    private final Fragment fragment;

    public TimeLineModel(Fragment fragment) {
        this.fragment = fragment;
    }


    public Observable<List<String>> getThematicsToPopulateRv(List<String> thematics, TimeLineView view){
        return TimeLineAPI.getThematicsToPopulateRv(thematics, view);
    }

    public Observable<List<Tribu>> getAllTribus(List<Tribu> tribus){
        return TimeLineAPI.getAllTribus(tribus);
    }

    public void loadMoreTribus(List<Tribu> tribus, TribusLineAdapter mTimeLineAdapter, ProgressBar mProgressBarBottom){
        TimeLineAPI.loadMoreTribus(tribus, mTimeLineAdapter, mProgressBarBottom);
    }

    public Observable<List<Tribu>> getTribusByThematics(List<Tribu> tribus, String thematic){
        return TimeLineAPI.getTribusByThematics(tribus, thematic);
    }


    public void loadMoreTribusByThematics(List<Tribu> tribus, String thematic, TribusLineAdapter mTimeLineAdapter, TimeLineView view){
        TimeLineAPI.loadMoreTribusByThematics(tribus, thematic, mTimeLineAdapter, view);
    }

    public Observable<List<String>> getThematicsTag(List<String> tagThematics){
        return TimeLineAPI.getThematicsTag(tagThematics);
    }

    /*public void getTribusByThematics(List<Tribu> tribus, String thematic, TimeLineView view, TimeLineAdapter mTimeLineAdapterByThematics){
        TimeLineAPI.getTribusByThematics(tribus, thematic, view, mTimeLineAdapterByThematics);
    }*/


}
