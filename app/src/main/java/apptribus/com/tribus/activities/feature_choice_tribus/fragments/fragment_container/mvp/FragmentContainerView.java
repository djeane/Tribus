package apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_container.mvp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ProgressBar;
import android.widget.TextView;

import apptribus.com.tribus.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

@SuppressLint("ViewConstructor")
public class FragmentContainerView extends CoordinatorLayout {

    @BindView(R.id.coordinator_recycler)
    CoordinatorLayout mCoordinatorRecycler;

    @BindView(R.id.rv_container)
    RecyclerView mRvContainer;

    @BindView(R.id.progressbar)
    ProgressBar mProgressBar;

    @BindView(R.id.progressbar_bottom)
    ProgressBar mProgressBarBottom;

    @BindView(R.id.tv_no_topics)
    TextView mTvNoTopics;

    @BindView(R.id.tv_no_surveys)
    TextView mTvNoSurveys;

    public Context mContext;
    private Unbinder mUnbinder;
    private LinearLayoutManager mManager;
    public String mTribusKey;
    public String mTribusFeature;

    public Fragment mFragment;

    public FragmentContainerView(Fragment fragment, String tribusKey, String tribusFeature) {
        super(fragment.getActivity());

        mFragment = fragment;

        mContext = fragment.getContext();

        inflate(mContext, R.layout.fragment_container, this);

        mUnbinder = ButterKnife.bind(this);


        /*if (fragment.getArguments() != null){
            mBundle = fragment.getArguments();
            this.mTribusKey = mBundle.getString(TRIBU_KEY);
        }*/

        initParams(tribusKey, tribusFeature);

        initViews();
    }

    private void initParams(String tribusKey, String tribusFeature) {
        this.mTribusKey = tribusKey;
        this.mTribusFeature = tribusFeature;

    }

    private void initViews() {
        mManager = new LinearLayoutManager(mContext);
        mRvContainer.setLayoutManager(mManager);
    }

    public void onDestroyView() {
        mUnbinder.unbind();
    }
}
