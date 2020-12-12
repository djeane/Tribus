package apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_container;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_container.dagger.DaggerFragmentContainerComponent;
import apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_container.dagger.FragmentContainerModule;
import apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_container.mvp.FragmentContainerPresenter;
import apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_container.mvp.FragmentContainerView;
import apptribus.com.tribus.application.dagger.TribusApplication;

import static apptribus.com.tribus.util.Constantes.CAMPAIGN;
import static apptribus.com.tribus.util.Constantes.EVENT;
import static apptribus.com.tribus.util.Constantes.SHARED_MEDIA;
import static apptribus.com.tribus.util.Constantes.SURVEY;
import static apptribus.com.tribus.util.Constantes.TOPIC;
import static apptribus.com.tribus.util.Constantes.TRIBU_KEY;

public class FragmentContainer extends Fragment {

    @Inject
    FragmentContainerView view;

    @Inject
    FragmentContainerPresenter presenter;

    private static String mTribusKey;
    private static String mFeature;


    public static FragmentContainer getInstance(String feature, String tribuKey) {

        mTribusKey = tribuKey;
        mFeature = feature;

        FragmentContainer fragment;
        Bundle args = new Bundle();

        switch (feature) {
            case TOPIC:
                fragment = new FragmentContainer();
                args.putString(TOPIC, feature);
                args.putString(TRIBU_KEY, tribuKey);
                fragment.setArguments(args);
                return fragment;

            case SURVEY:
                fragment = new FragmentContainer();
                args.putString(SURVEY, feature);
                args.putString(TRIBU_KEY, tribuKey);
                fragment.setArguments(args);
                return fragment;

            case EVENT:
                fragment = new FragmentContainer();
                args.putString(EVENT, feature);
                args.putString(TRIBU_KEY, tribuKey);
                fragment.setArguments(args);
                return fragment;

            case CAMPAIGN:
                fragment = new FragmentContainer();
                args.putString(CAMPAIGN, feature);
                args.putString(TRIBU_KEY, tribuKey);
                fragment.setArguments(args);
                return fragment;

            case SHARED_MEDIA:
                fragment = new FragmentContainer();
                args.putString(SHARED_MEDIA, feature);
                args.putString(TRIBU_KEY, tribuKey);
                fragment.setArguments(args);
                return fragment;

            default:
                return null;


        }

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        DaggerFragmentContainerComponent
                .builder()
                .appComponent(TribusApplication.get(getActivity()).component())
                .fragmentContainerModule(new FragmentContainerModule(this, mTribusKey, mFeature))
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
    public void onPause() {
        presenter.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        presenter.onStop();
        super.onStop();
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
