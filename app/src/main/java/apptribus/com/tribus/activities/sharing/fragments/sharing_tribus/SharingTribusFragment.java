package apptribus.com.tribus.activities.sharing.fragments.sharing_tribus;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import apptribus.com.tribus.activities.sharing.fragments.sharing_tribus.dagger.DaggerSharingTribusFragmentComponent;
import apptribus.com.tribus.activities.sharing.fragments.sharing_tribus.dagger.SharingTribusFragmentModule;
import apptribus.com.tribus.activities.sharing.fragments.sharing_tribus.mvp.SharingTribusFragmentPresenter;
import apptribus.com.tribus.activities.sharing.fragments.sharing_tribus.mvp.SharingTribusFragmentView;
import apptribus.com.tribus.application.dagger.TribusApplication;

/**
 * Created by User on 1/15/2018.
 */

public class SharingTribusFragment extends Fragment{

    @Inject
    SharingTribusFragmentView view;

    @Inject
    SharingTribusFragmentPresenter presenter;

    private String mLink;

    public static SharingTribusFragment getInstance() {
        return new SharingTribusFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        DaggerSharingTribusFragmentComponent.builder()
                .appComponent(TribusApplication.get(getActivity()).component())
                .sharingTribusFragmentModule(new SharingTribusFragmentModule(this))
                .build().inject(this);

        mLink = this.getArguments().getString("link");

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.onStart(mLink);
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
