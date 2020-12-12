package apptribus.com.tribus.activities.sharing.fragments.sharing_tribus.mvp;

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

public class SharingTribusFragmentView extends RelativeLayout {

    @BindView(R.id.relative_tribus)
    RelativeLayout mRelativeTribus;

    @BindView(R.id.rv_tribus)
    RecyclerView mRvTribus;

    @BindView(R.id.tv_tribus)
    TextView mTvTribus;

    public LinearLayoutManager mLayoutManagerTribus;
    public Fragment mFragmentContext;
    private Unbinder unbinder;
    public String mLink;

    public SharingTribusFragmentView(Fragment fragment) {
        super(fragment.getContext());
        mFragmentContext = fragment;

        inflate(fragment.getActivity(), R.layout.fragment_sharing_tribu, this);
        unbinder = ButterKnife.bind(this);

        mLayoutManagerTribus = new LinearLayoutManager(fragment.getContext());
        mLayoutManagerTribus.setOrientation(LinearLayoutManager.VERTICAL);

        mRvTribus.setLayoutManager(mLayoutManagerTribus);

    }

    public void onDestroyView(){
        unbinder.unbind();
    }
}
