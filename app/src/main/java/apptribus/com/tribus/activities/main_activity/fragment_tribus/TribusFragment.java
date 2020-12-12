package apptribus.com.tribus.activities.main_activity.fragment_tribus;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import apptribus.com.tribus.activities.main_activity.fragment_tribus.dagger.DaggerTribusFragmentComponent;
import apptribus.com.tribus.activities.main_activity.fragment_tribus.dagger.TribusFragmentModule;
import apptribus.com.tribus.activities.main_activity.fragment_tribus.mvp.TribusFragmentPresenter;
import apptribus.com.tribus.activities.main_activity.fragment_tribus.mvp.TribusFragmentView;
import apptribus.com.tribus.application.dagger.TribusApplication;

public class TribusFragment extends Fragment {


    @Inject
    TribusFragmentView view;

    @Inject
    TribusFragmentPresenter presenter;


    public static TribusFragment getInstance(int position) {
        TribusFragment fragment = new TribusFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DaggerTribusFragmentComponent.builder()
                .appComponent(TribusApplication.get(getActivity()).component())
                .tribusFragmentModule(new TribusFragmentModule(this))
                .build().inject(this);
        return view;
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
