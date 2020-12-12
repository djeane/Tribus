package apptribus.com.tribus.activities.main_activity.fragment_timeline.view_holder;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Collections;
import java.util.List;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.main_activity.fragment_timeline.adapter.TribusLineAdapter;
import apptribus.com.tribus.activities.main_activity.fragment_timeline.mvp.TimeLineView;
import apptribus.com.tribus.pojo.Thematics;
import apptribus.com.tribus.pojo.Tribu;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.TRIBUS_THEMATICS;

/**
 * Created by User on 4/25/2018.
 */

public class ThematicsViewHolder extends RecyclerView.ViewHolder {

    private Context mContext;

    @BindView(R.id.item_thematics)
    CardView mCardViewThematics;

    @BindView(R.id.button_thematics)
    Button mButtonThematics;

    //REFERENCES FIREBASE
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mReferenceTribus;
    private DatabaseReference mReferenceThematics;
    private FirebaseRecyclerAdapter<Tribu, TimeLineViewHolder> mAdapter;
    private FirebaseFirestore mFirestore;
    private CollectionReference mTribusCollection;
    private Boolean isFirstPageFirstLoad = true;
    private DocumentSnapshot mLastTribuVisible;
    private List<Tribu> mTribusList;
    private TribusLineAdapter mTimeLineAdapter;


    public ThematicsViewHolder(View itemView) {
        super(itemView);
        mContext = itemView.getContext();
        ButterKnife.bind(this, itemView);
        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mReferenceTribus = mDatabase.getReference().child(GENERAL_TRIBUS);
        mReferenceThematics = mDatabase.getReference().child(TRIBUS_THEMATICS);
        mFirestore = FirebaseFirestore.getInstance();
        mTribusCollection = mFirestore.collection(GENERAL_TRIBUS);
        mTribusList = Collections.emptyList();
    }

    public void setTitleThematics(String thematic){
        mButtonThematics.setText(thematic);


    }



    public interface OnClickThematicsTribusLineListener{
        void onClickThematicsListener(String thematicTitle);
    }














    //SET THEMATIC
    public void initThematicsViewHolder(String thematic, TimeLineView view) {

        mButtonThematics.setText(thematic);

        mButtonThematics.setOnClickListener(v -> {
            mButtonThematics.setPressed(true);
            getTribusFromThematics(thematic, view);
        });

        view.mRvTribusByThematics.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                if (reachedBottom) {
                    view.mProgressBarBottom.setVisibility(View.VISIBLE);

                    loadMoreTribus(mTribusList, mTimeLineAdapter, view.mProgressBarBottom, thematic);
                }
            }
        });

    }




    //OLD METHOD TO GET THEMATICS
    public void getTribusFromThematics(String thematic, TimeLineView view){

        view.mProgressBarThematics.setVisibility(View.VISIBLE);

        Query firstQuery = mTribusCollection
                .whereEqualTo("profile.thematic", thematic)
                .orderBy("timestampCreated.timestamp", Query.Direction.DESCENDING)
                .limit(3);

                firstQuery
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {

                            if (isFirstPageFirstLoad) {
                                mLastTribuVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                            }

                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){

                                    mTribusList.add(documentSnapshot.toObject(Tribu.class));


                            }

                            isFirstPageFirstLoad = false;

                            if (mTimeLineAdapter != null){
                                mTimeLineAdapter.notifyDataSetChanged();
                            }
                            else {
                                mTimeLineAdapter = new TribusLineAdapter(view.mContext, mTribusList, view, null);
                            }
                            view.mCoordinatorTribusList.setVisibility(View.GONE);
                            view.mRecyclerView.setVisibility(View.GONE);
                            view.mProgressBar.setVisibility(View.GONE);
                            view.mProgressBarBottom.setVisibility(GONE);

                            view.mLinearOptions.setVisibility(View.VISIBLE);
                            view.mCoordinatorOptionsThematics.setVisibility(View.VISIBLE);
                            view.mProgressBarThematics.setVisibility(View.GONE);
                            view.mRvTribusByThematics.setVisibility(View.VISIBLE);



                        })
                        .addOnFailureListener(Throwable::getMessage);

    }

    public void loadMoreTribus(List<Tribu> tribus, TribusLineAdapter mTimeLineAdapter, ProgressBar mProgressBarBottom,
                               String thematic) {

        Query nextQuery = mTribusCollection
                .whereEqualTo("profile.thematic", thematic)
                .orderBy("timestampCreated.timestamp", Query.Direction.DESCENDING)
                .startAfter(mLastTribuVisible)
                .limit(3);

        nextQuery
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    if (!queryDocumentSnapshots.isEmpty()) {

                        mLastTribuVisible = queryDocumentSnapshots
                                .getDocuments()
                                .get(queryDocumentSnapshots.size() - 1);

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                            tribus.add(documentSnapshot.toObject(Tribu.class));
                        }

                        mTimeLineAdapter.notifyDataSetChanged();
                        mProgressBarBottom.setVisibility(GONE);
                    }
                    else {
                        mProgressBarBottom.setVisibility(GONE);
                    }

                })
                .addOnFailureListener(Throwable::getMessage);
    }




    public void initThematicsVH(Thematics thematicsOb, String thematic, TimeLineView view, Fragment fragment,
                                final FirebaseRecyclerAdapter<Thematics, ThematicsViewHolder> adapter, int positionViewHoolder) {

        //setThematic(thematic, positionViewHoolder, view);

        mButtonThematics.setOnClickListener(v -> {


            view.mCoordinatorTribusList.setVisibility(View.GONE);
            view.mRecyclerView.setVisibility(View.GONE);
            view.mProgressBar.setVisibility(View.GONE);

            view.mCoordinatorOptionsThematics.setVisibility(View.VISIBLE);
            view.mProgressBarThematics.setVisibility(View.VISIBLE);

            if (mAdapter != null) {
                mAdapter = null;
            }
            setNewAdapter(view, 2, thematic, fragment);
        });

    }







    private void setNewAdapter(TimeLineView view, int i, String thematic, Fragment fragment) {

        mAdapter = new FirebaseRecyclerAdapter<Tribu, TimeLineViewHolder>(
                Tribu.class,
                R.layout.tribus_item,
                TimeLineViewHolder.class,
                mReferenceTribus.orderByChild("thematic")
        ) {
            @Override
            protected void populateViewHolder(TimeLineViewHolder viewHolder, Tribu model, int position) {

                if (model.getProfile().getThematic().equals(thematic)) {

                    viewHolder.initTimeLineVH(model, view, fragment);


                } else {

                    viewHolder.mCardView.setVisibility(View.GONE);
                    viewHolder.mRelativeMain.setVisibility(View.GONE);
                    viewHolder.mRelativeTop.setVisibility(View.GONE);
                    viewHolder.mIvTopic.setVisibility(View.GONE);
                    viewHolder.mTvTopicNumber.setVisibility(View.GONE);
                    viewHolder.mIvSurvey.setVisibility(View.GONE);
                    viewHolder.mTvSurveyNumber.setVisibility(View.GONE);
                    viewHolder.mTvNumParticipants.setVisibility(View.GONE);
                    viewHolder.mIvShare.setVisibility(View.GONE);
                    viewHolder.mLinearMiddle.setVisibility(View.GONE);
                    viewHolder.mRelativeSdv.setVisibility(View.GONE);
                    viewHolder.mCoordinator.setVisibility(View.GONE);
                    viewHolder.mImageTribu.setVisibility(View.GONE);
                    viewHolder.mBtnParticipar.setVisibility(View.GONE);
                    viewHolder.mIvLocked.setVisibility(View.GONE);
                    viewHolder.mCircleImageAdmin.setVisibility(View.GONE);
                    viewHolder.mTvName.setVisibility(View.GONE);
                    viewHolder.mTvUsername.setVisibility(View.GONE);
                    viewHolder.mTvTribusName.setVisibility(View.GONE);
                    viewHolder.mTvTribusThematic.setVisibility(View.GONE);
                    viewHolder.mTvTribusDescription.setVisibility(View.GONE);
                    viewHolder.mView1.setVisibility(View.GONE);
                    viewHolder.mRelativeButton.setVisibility(View.GONE);
                    viewHolder.mLinearLayout.setVisibility(View.GONE);
                    viewHolder.mView2.setVisibility(View.GONE);

                }
            }

        };

        view.mRvTribusByThematics.setAdapter(mAdapter);

        view.mProgressBarThematics.setVisibility(View.GONE);
        view.mRvTribusByThematics.setVisibility(View.VISIBLE);

    }

}
