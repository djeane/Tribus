package apptribus.com.tribus.activities.main_activity.fragment_timeline;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import apptribus.com.tribus.activities.main_activity.fragment_timeline.dagger.DaggerTimeLineFragmentComponent;
import apptribus.com.tribus.activities.main_activity.fragment_timeline.dagger.TimeLineFragmentModule;
import apptribus.com.tribus.activities.main_activity.fragment_timeline.mvp.TimeLinePresenter;
import apptribus.com.tribus.activities.main_activity.fragment_timeline.mvp.TimeLineView;
import apptribus.com.tribus.application.dagger.TribusApplication;

/**
 * Created by User on 5/25/2017.
 */

public class TimeLineFragment extends Fragment{

    @Inject
    TimeLineView view;

    @Inject
    TimeLinePresenter presenter;

    public static TimeLineFragment getInstance(int position) {
        TimeLineFragment fragment = new TimeLineFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DaggerTimeLineFragmentComponent.builder()
                .appComponent(TribusApplication.get(getActivity()).component())
                .timeLineFragmentModule(new TimeLineFragmentModule(this))
                .build().inject(this);

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        //presenter.setAdapter();
        presenter.onStart();
        presenter.onAttach(this.getContext());
    }

    @Override
    public void onStop() {
        presenter.onStop();
        super.onStop();
    }

    @Override
    public void onPause() {
        //presenter.onPause();
        presenter.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
    }


    @Override
    public void onDestroyView() {
        view.onDestroyView();
        presenter.onDestroyView();
        super.onDestroyView();
    }
}
