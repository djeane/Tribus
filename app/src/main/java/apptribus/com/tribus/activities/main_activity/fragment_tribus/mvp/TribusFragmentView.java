package apptribus.com.tribus.activities.main_activity.fragment_tribus.mvp;

import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import apptribus.com.tribus.R;
import apptribus.com.tribus.util.WrapContentLinearLayoutManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class TribusFragmentView extends CoordinatorLayout {


    @BindView(R.id.relative_main)
    RelativeLayout mRelativeMain;

    //CATEGORY
    @BindView(R.id.relative_tribus_category)
    RelativeLayout mRelativeTribusCategory;

    @BindView(R.id.rv_tribus_category)
    RecyclerView mRvListCategory;


    //THEMATICS - FOLLOWED TRIBUS
    @BindView(R.id.relative_list_thematics)
    RelativeLayout mRelativeListThematics;

    @BindView(R.id.rv_list_thematics)
    RecyclerView mRvListThematics;

    @BindView(R.id.tv_info_thematics)
    TextView mTvInfoThematics;


    //THEMATICS - REMOVED TRIBUS
    @BindView(R.id.relative_list_thematics_removed_tribus)
    RelativeLayout mRelativeListThematicsRemovedTribus;

    @BindView(R.id.rv_list_thematics_removed_tribus)
    RecyclerView mRvListThematicsRemovedTribus;

    @BindView(R.id.tv_info_thematics_removed_tribus)
    TextView mTvInfoThematicsRemovedTribus;


    //LIST TRIBUS FOR THE FIRST TIME AND FOLLOWED TRIBUS
    @BindView(R.id.layout_tribus_fragment)
    CoordinatorLayout mCoordinatorTribus;

    @BindView(R.id.rv_list_tribus_fragment)
    RecyclerView mRecyclerView;

    @BindView(R.id.progressbar)
    ProgressBar mProgressBarListTribus;


    @BindView(R.id.progressbar_bottom)
    public ProgressBar mProgressBarBottom;


    //TO SHOW TRIBUS BY THEMATIC SEARCH
    @BindView(R.id.coordinator_options_thematics)
    public CoordinatorLayout mCoordinatorOptionsThematics;

    @BindView(R.id.rv_tribus_thematics)
    public RecyclerView mRvTribusByThematics;

    @BindView(R.id.progressbar_thematics)
    public ProgressBar mProgressBarThematics;

    private LinearLayoutManager mLayoutManagerCategory;
    private LinearLayoutManager mLayoutManagerThematics;
    private LinearLayoutManager mLayoutManagerThematicsRemovedTribus;
    private LinearLayoutManager mLayoutManager;


    public Fragment mContext;
    private Unbinder unbinder;

    public TribusFragmentView(@NonNull Fragment fragment) {
        super(fragment.getContext());
        mContext = fragment;
        inflate(fragment.getActivity(), R.layout.fragment_tribus, this);
        unbinder = ButterKnife.bind(this);


        initViews();

    }


    private void initViews(){
        mLayoutManagerCategory = new WrapContentLinearLayoutManager(mContext.getActivity());
        mLayoutManagerCategory.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvListCategory.setLayoutManager(mLayoutManagerCategory);

        //layout manager to Thematics
        mLayoutManagerThematics = new WrapContentLinearLayoutManager(getContext());
        mLayoutManagerThematics.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvListThematics.setLayoutManager(mLayoutManagerThematics);

        //layout manager to Thematics
        mLayoutManagerThematicsRemovedTribus = new WrapContentLinearLayoutManager(getContext());
        mLayoutManagerThematicsRemovedTribus.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvListThematicsRemovedTribus.setLayoutManager(mLayoutManagerThematicsRemovedTribus);

        mLayoutManager = new WrapContentLinearLayoutManager(mContext.getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        //mRecyclerView.setHasFixedSize(true);

    }

    public void onDestroyView(){
        unbinder.unbind();
    }








    //OLD CODE
    /*@BindView(R.id.layout_tribus_fragment)
    CoordinatorLayout mCoordinatorTribus;

    @BindView(R.id.rv_list_tribus_fragment)
    RecyclerView mRecyclerView;

    @BindView(R.id.circle_image_of_tribu)
    public SimpleDraweeView mCircleImageTribu;

    @BindView(R.id.tv_name_of_tribu)
    public TextView mTvNameTribu;

    @BindView(R.id.tv_unique_name)
    public TextView mTvUniqueName;

    @BindView(R.id.tv_message)
    public TextView mTvMessage;

    @BindView(R.id.tv_num_topics)
    public TextView mTvNumTopics;

    @BindView(R.id.tv_date)
    public TextView mTvDate;

    @BindView(R.id.constraint_layout)
    ConstraintLayout mConstraintLayout;

    @BindView(R.id.view_row_top)
    View mViewRowTop;

    private NpaLinearLayoutManager mLayoutManager;

    public Fragment mContext;
    private Unbinder unbinder;


    public TribusFragmentView(@NonNull Fragment fragment) {
        super(fragment.getContext());
        mContext = fragment;
        inflate(fragment.getActivity(), R.layout.fragment_tribus, this);
        unbinder = ButterKnife.bind(this);

        mLayoutManager = new NpaLinearLayoutManager(fragment.getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);


    }

    public void onDestroyView(){
        unbinder.unbind();
    }*/


}
