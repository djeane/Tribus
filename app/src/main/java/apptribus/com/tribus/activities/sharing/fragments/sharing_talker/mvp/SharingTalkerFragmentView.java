package apptribus.com.tribus.activities.sharing.fragments.sharing_talker.mvp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import apptribus.com.tribus.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by User on 1/15/2018.
 */

public class SharingTalkerFragmentView extends RelativeLayout {

    @BindView(R.id.relative_talkers)
    RelativeLayout mRelativeTalkers;

    @BindView(R.id.rv_talkers)
    RecyclerView mRvTalkers;

    @BindView(R.id.tv_talkers)
    TextView mTvTalkers;

    public Fragment mFragmentContext;
    private Unbinder unbinder;
    public LinearLayoutManager mLayoutManagerTalkers;
    public String mLink;


    public SharingTalkerFragmentView(Fragment fragment) {
        super(fragment.getContext());
        mFragmentContext = fragment;

        inflate(fragment.getActivity(), R.layout.fragment_sharing_talker, this);
        unbinder = ButterKnife.bind(this);

        Bundle bundle = fragment.getArguments();
        mLink = bundle.getString("link");

        mLayoutManagerTalkers = new LinearLayoutManager(fragment.getContext());
        mLayoutManagerTalkers.setOrientation(LinearLayoutManager.VERTICAL);


        mRvTalkers.setLayoutManager(mLayoutManagerTalkers);
    }

    public void onDestroyView(){
        unbinder.unbind();
    }
}
