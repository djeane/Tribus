package apptribus.com.tribus.activities.main_activity.fragment_timeline.mvp;

import android.annotation.SuppressLint;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import apptribus.com.tribus.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;

@SuppressLint("ViewConstructor")
public class TimeLineView extends RelativeLayout{

    @BindView(R.id.coordinator_tribus_list)
    public CoordinatorLayout mCoordinatorTribusList;

    @BindView(R.id.rv_list)
    public RecyclerView mRecyclerView;

    @BindView(R.id.progressbar)
    public ProgressBar mProgressBar;

    @BindView(R.id.progressbar_bottom)
    public ProgressBar mProgressBarBottom;

    @BindView(R.id.rv_list_thematics)
    public RecyclerView mRvListThematics;

    //TO SHOW TRIBUS BY THEMATIC SEARCH
    @BindView(R.id.coordinator_options_thematics)
    public CoordinatorLayout mCoordinatorOptionsThematics;

    @BindView(R.id.rv_tribus_thematics)
    public RecyclerView mRvTribusByThematics;

    @BindView(R.id.progressbar_thematics)
    public ProgressBar mProgressBarThematics;

    @BindView(R.id.linear_options)
    public LinearLayout mLinearOptions;


    //TribusLine
    @BindView(R.id.btn_tribus_line)
    Button mBtnTribusLine;

    @BindView(R.id.view_tribus_line)
    View mViewTribusLine;


    //What's going on...
    @BindView(R.id.btn_whats_going_on)
    Button mBtnWhatsGoingOn;

    @BindView(R.id.view_whats_going_on)
    View mViewWhatsGoingOn;


    //Mural
    @BindView(R.id.coordinator_content_tribus)
    CoordinatorLayout mCoordinatorContentTribus;

    @BindView(R.id.rv_list_content_tribus)
    RecyclerView mRvListMuralTribus;

    @BindView(R.id.progressbar_content_tribus)
    ProgressBar mProgressBarContentTribus;

    @BindView(R.id.tv_no_tag)
    TextView mTvNoTag;


    public LinearLayoutManager mLayoutManager;
    public LinearLayoutManager mLmThematics;
    public LinearLayoutManager mLmMural;
    public Fragment mContext;

    //for show list of tribus by thematic search
    public LinearLayoutManager mLmTribusByThematic;

    private Unbinder unbinder;

    //INITIALIZE VIEWS AND RECYCLERVIEW
    public TimeLineView(Fragment fragment) {
        super(fragment.getActivity());
        mContext = fragment;
        inflate(fragment.getActivity(), R.layout.fragment_timeline, this);
        unbinder = ButterKnife.bind(this);

        initViews();
        setRecyclerView();


    }

    private void initViews(){
        mBtnWhatsGoingOn.setTextColor(mContext.getResources().getColor(R.color.primary_light));
        mViewWhatsGoingOn.setBackgroundColor(mContext.getResources().getColor(R.color.colorIcons));
        mBtnTribusLine.setTextColor(mContext.getResources().getColor(R.color.accent));
        mViewTribusLine.setBackgroundColor(mContext.getResources().getColor(R.color.accent));
    }

    private void setRecyclerView(){

        mLayoutManager = new LinearLayoutManager(mContext.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mLmThematics = new LinearLayoutManager(mContext.getContext());
        mLmThematics.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvListThematics.setLayoutManager(mLmThematics);

        mLmTribusByThematic = new LinearLayoutManager(mContext.getContext());
        mRvTribusByThematics.setLayoutManager(mLmTribusByThematic);

        mLmMural = new LinearLayoutManager(mContext.getContext());
        mLmMural.setOrientation(LinearLayoutManager.VERTICAL);
        mRvListMuralTribus.setLayoutManager(mLmMural);

        //mRvListThematics.setNestedScrollingEnabled(false);
        //mRecyclerView.setNestedScrollingEnabled(false);

    }


    public Observable<Void> observableBtnWhatsGoingOn(){
        return RxView.clicks(mBtnWhatsGoingOn);
    }

    public Observable<Void> observableBtnTribusLine(){
        return RxView.clicks(mBtnTribusLine);
    }


    public void onDestroyView(){
        unbinder.unbind();
    }


}
