package apptribus.com.tribus.activities.main_activity.fragment_talks;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.main_activity.fragment_talks.dagger.DaggerTalksFragmentComponent;
import apptribus.com.tribus.activities.main_activity.fragment_talks.dagger.TalksFragmentModule;
import apptribus.com.tribus.activities.main_activity.fragment_talks.mvp.TalksFragmentPresenter;
import apptribus.com.tribus.activities.main_activity.fragment_talks.mvp.TalksFragmentView;
import apptribus.com.tribus.application.dagger.TribusApplication;

/**
 * Created by User on 5/25/2017.
 */

public class TalksFragment extends Fragment {

    @Inject
    TalksFragmentView view;

    @Inject
    TalksFragmentPresenter presenter;


    public static TalksFragment getInstance(int position) {
        TalksFragment fragment = new TalksFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        DaggerTalksFragmentComponent.builder()
                .appComponent(TribusApplication.get(getActivity()).component())
                .talksFragmentModule(new TalksFragmentModule(this))
                .build().inject(this);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onStart() {
        super.onStart();
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
        presenter.onPause();
        super.onPause();
    }

    @Override
    public void onDetach() {
        presenter.onDetach();
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        view.onDestroyView();
        presenter.onDestroyView();
        super.onDestroyView();
    }
}
