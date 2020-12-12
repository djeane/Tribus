package apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_feature.mvp;

import android.content.Context;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_feature.adapter.FragmentFeatureAdapter;
import apptribus.com.tribus.pojo.Tribu;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static android.view.View.*;

public class FragmentFeaturePresenter implements FragmentFeatureAdapter.FragmentAdapterOnClickListener {

    private final FragmentFeatureView view;
    private final FragmentFeatureModel model;
    private CompositeSubscription subscription = new CompositeSubscription();
    private Tribu mTribu;
    private FragmentFeatureAdapter mAdapter;
    private OnFragmentFeaturePresenterListener onFragmentFeaturePresenterListener;
    private OnTvInfoNewParticipantListener onTvInfoNewParticipantListener;


    public FragmentFeaturePresenter(FragmentFeatureView view, FragmentFeatureModel model) {
        this.view = view;
        this.model = model;
    }

    public void onStart() {

        subscription.add(getFollowersInvitationNum());
        subscription.add(observeTvInfoClickListener());
        setFragmentFeatureAdapter();
    }


    private void setFragmentFeatureAdapter() {
        mAdapter = new FragmentFeatureAdapter(view.mContext.getActivity(), view.mTribuKey, this);
        view.mRvFeature.setAdapter(mAdapter);
    }

    private Subscription getFollowersInvitationNum() {
        return model.getTribu(view.mTribuKey)
                .concatMap(tribu -> {
                    mTribu = tribu;
                    return model.getFollowersInvitationNum(view.mTribuKey);
                })
                .subscribe(numFollowers -> {

                            if (mTribu.getProfile().isPublic()) {
                                view.mRelativeInfoNewParticipants.setVisibility(GONE);

                            } else {
                                if (numFollowers != 0) {
                                    view.mRelativeInfoNewParticipants.setVisibility(VISIBLE);
                                    view.mTvInfoNewParticipants.setTextColor(view.getResources().getColor(R.color.red));

                                    String append;

                                    if (numFollowers == 1) {
                                        append = view.mContext.getResources().getString(R.string.touch_to_add)
                                                + " " + String.valueOf(numFollowers) + " " + view.mContext.getResources()
                                                .getString(R.string.one_participant);
                                    } else {
                                        append = view.mContext.getResources().getString(R.string.touch_to_add)
                                                + " " + String.valueOf(numFollowers) + " " + view.mContext.getResources()
                                                .getString(R.string.more_than_one_participant);
                                    }

                                    view.mTvInfoNewParticipants.setText(append);
                                } else {
                                    view.mRelativeInfoNewParticipants.setVisibility(VISIBLE);
                                    view.mTvInfoNewParticipants.setTextColor(view.getResources().getColor(R.color.primary_dark));
                                    view.mTvInfoNewParticipants.setText(view.mContext.getResources().getString(R.string.no_new_participant));
                                }
                            }

                            view.mRelativeFeatures.setVisibility(VISIBLE);
                        },
                        Throwable::printStackTrace
                );

    }

    private Subscription observeTvInfoClickListener() {
        return view.observableTvInfoNewParticipants()
                .subscribe(__ -> {
                    onTvInfoNewParticipantListener.onInfoNewParticipantListener(view.mTribuKey);
                });
    }

    public void onStop() {

    }

    public void onPause() {

    }

    public void onDetach() {

    }

    public void onAttach(Context context) {

        if (context instanceof FragmentFeaturePresenter.OnFragmentFeaturePresenterListener) {
            onFragmentFeaturePresenterListener = (FragmentFeaturePresenter.OnFragmentFeaturePresenterListener) context;
        }

        if (context instanceof FragmentFeaturePresenter.OnTvInfoNewParticipantListener) {
            onTvInfoNewParticipantListener = (FragmentFeaturePresenter.OnTvInfoNewParticipantListener) context;
        }
    }


    public void onDestroyView() {
        subscription.clear();
    }

    @Override
    public void onFeatureClickListener(String feature) {
        onFragmentFeaturePresenterListener.getInstance(feature);
    }


    public interface OnFragmentFeaturePresenterListener {
        void getInstance(String feature);
    }

    //to open add followers activity when textview is clicked
    public interface OnTvInfoNewParticipantListener {
        void onInfoNewParticipantListener(String tribuKey);
    }

}
