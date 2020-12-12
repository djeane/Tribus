package apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_feature;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_feature.dagger.DaggerFragmentFeatureComponent;
import apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_feature.dagger.FragmentFeatureModule;
import apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_feature.mvp.FragmentFeaturePresenter;
import apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_feature.mvp.FragmentFeatureView;
import apptribus.com.tribus.application.dagger.TribusApplication;

import static apptribus.com.tribus.util.Constantes.TRIBU_KEY;

public class FragmentFeature extends Fragment {

    @Inject
    FragmentFeatureView view;

    @Inject
    FragmentFeaturePresenter presenter;

    private static String mTribusKey;

    public static FragmentFeature getInstance(String tribuKey) {
        mTribusKey = tribuKey;
        FragmentFeature mFragmentFeature = new FragmentFeature();
        Bundle args = new Bundle();
        args.putString(TRIBU_KEY, tribuKey);
        mFragmentFeature.setArguments(args);
        return new FragmentFeature();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        DaggerFragmentFeatureComponent.builder()
                .appComponent(TribusApplication.get(getActivity()).component())
                .fragmentFeatureModule(new FragmentFeatureModule(this, mTribusKey))
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
