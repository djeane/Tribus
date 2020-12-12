package apptribus.com.tribus.activities.main_activity.fragment_timeline.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.main_activity.fragment_timeline.mvp.TimeLinePresenter;
import apptribus.com.tribus.activities.main_activity.fragment_timeline.mvp.TimeLineView;
import apptribus.com.tribus.pojo.CollectionTag;
import butterknife.BindView;
import butterknife.ButterKnife;

import static apptribus.com.tribus.util.Constantes.DATE;
import static apptribus.com.tribus.util.Constantes.GENERAL_TAGS;
import static apptribus.com.tribus.util.Constantes.TAG_COLLECTION;

public class MuralVerticalAdapter extends RecyclerView.Adapter<MuralVerticalAdapter.MuralVerticalViewHolder>{

    private Context mContext;
    private List<String> mThematicsList;
    private TimeLinePresenter mPresenter;

    //FIRESTORE INSTANCE
    private FirebaseFirestore mFirestore;

    //FIRESTORE REFERENCES
    private CollectionReference mTagCollection;

    public MuralVerticalAdapter(Context context, List<String> thematics,
                                TimeLinePresenter presenter){
        this.mContext = context;
        this.mThematicsList = thematics;
        this.mPresenter = presenter;

        mFirestore = FirebaseFirestore.getInstance();
        mTagCollection = mFirestore.collection(GENERAL_TAGS);

    }

    @NonNull
    @Override
    public MuralVerticalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_mural, parent, false);

        return new MuralVerticalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MuralVerticalViewHolder holder, int position) {

        String thematic = mThematicsList.get(position);

        holder.initMuralVerticalVH(thematic);
    }

    @Override
    public int getItemCount() {
        return mThematicsList.size();
    }



    public class MuralVerticalViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tv_thematic)
        TextView mTvThematic;

        @BindView(R.id.rv_row_mural)
        RecyclerView mRvRowMural;

        private List<CollectionTag> tags;
        private MuralHorizontalAdapter mHorizontalAdapter;


        public MuralVerticalViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            tags = new ArrayList<>();

            mHorizontalAdapter = new MuralHorizontalAdapter();
            mRvRowMural.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            mRvRowMural.setAdapter(mHorizontalAdapter);
        }

        private void initMuralVerticalVH(String thematic){

            mTagCollection
                    .document(thematic)
                    .collection(TAG_COLLECTION)
                    .orderBy(DATE, Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {

                            for (DocumentSnapshot tag : queryDocumentSnapshots){
                                tags.add(tag.toObject(CollectionTag.class));
                            }

                            setTvThematic(thematic);

                            mHorizontalAdapter.setMuralHorizontalAdapter(mContext, tags, mPresenter);
                            mHorizontalAdapter.notifyMuralHorizontalAdapter(tags);
                        }
                        else {

                            mTagCollection
                                    .document(thematic)
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        mTvThematic.setVisibility(View.GONE);
                                    })
                                    .addOnFailureListener(Throwable::printStackTrace);
                        }
                    })
                    .addOnFailureListener(Throwable::printStackTrace);

        }


        private void setTvThematic(String thematic){
            mTvThematic.setText(thematic);
        }
    }
}
