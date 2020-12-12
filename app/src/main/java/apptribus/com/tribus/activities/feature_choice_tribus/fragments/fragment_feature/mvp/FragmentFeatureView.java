package apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_feature.mvp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import apptribus.com.tribus.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.TRIBU_KEY;

@SuppressLint("ViewConstructor")
public class FragmentFeatureView extends RelativeLayout {

    @BindView(R.id.relative_features)
    RelativeLayout mRelativeFeatures;

    @BindView(R.id.rv_features)
    RecyclerView mRvFeature;

    @BindView(R.id.tv_info_new_participants)
    TextView mTvInfoNewParticipants;

    @BindView(R.id.relative_info_new_participants)
    RelativeLayout mRelativeInfoNewParticipants;

    public LinearLayoutManager mManager;

    public Fragment mContext;
    private Unbinder unbinder;
    public String mTribuKey;


    public FragmentFeatureView(Fragment fragment, String tribusKey) {
        super(fragment.getContext());
        mContext = fragment;

        inflate(fragment.getContext(), R.layout.fragment_features, this);
        unbinder = ButterKnife.bind(this);

        if (fragment.getArguments() != null) {
            Bundle bundle = fragment.getArguments();
            mTribuKey = bundle.getString(TRIBU_KEY);
        } else {

            mTribuKey = tribusKey;
            Log.e("tribus - erro: ", "O bundle é null FragmentFeature");
        }

        mManager = new LinearLayoutManager(fragment.getContext());
        mManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvFeature.setLayoutManager(mManager);
    }

    public Observable<Void> observableTvInfoNewParticipants() {
        return RxView.clicks(mTvInfoNewParticipants);
    }

    public void onDestroyView() {
        unbinder.unbind();
    }
}
