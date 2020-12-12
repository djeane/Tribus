package apptribus.com.tribus.activities.main_activity.fragment_timeline.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;
import java.util.List;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.main_activity.fragment_timeline.mvp.TimeLinePresenter;
import apptribus.com.tribus.activities.main_activity.fragment_timeline.mvp.TimeLineView;
import apptribus.com.tribus.activities.main_activity.fragment_timeline.view_holder.TimeLineViewHolder;
import apptribus.com.tribus.pojo.Tribu;
import butterknife.BindView;
import butterknife.ButterKnife;

import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.TRIBUS_THEMATICS;

public class TribusLineThematicsAdapter extends RecyclerView.Adapter<TribusLineThematicsAdapter.ThematicsViewHolder> {

    private TimeLineView mContext;
    private List<String> mListThematics;
    private OnClickThematicsTribusLineListener mOnClickThematicsTribusLineListener;
    private int mInicialPosition = 0;

    public TribusLineThematicsAdapter(TimeLineView context, List<String> thematics, TimeLinePresenter presenter) {
        this.mContext = context;
        this.mListThematics = thematics;
        if (presenter != null){
            mOnClickThematicsTribusLineListener = presenter;
        }
    }

    @NonNull
    @Override
    public ThematicsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_thematics, parent, false);

        return new ThematicsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ThematicsViewHolder holder, int position) {

        String thematic = mListThematics.get(position);

        /*if (position == 0){
            holder.setTitleThematics(ALL);
        }
        else {
            holder.setTitleThematics(thematic);
        }*/
        holder.setTitleThematics(thematic);

        if (position == mInicialPosition){
            holder.mButtonThematics.setBackground(mContext.getResources().getDrawable(R.drawable.button_thematics_pressed));
            holder.mButtonThematics.setTextColor(mContext.getResources().getColor(R.color.colorIcons));
        }
        else {
            holder.mButtonThematics.setBackground(mContext.getResources().getDrawable(R.drawable.button_thematics));
            holder.mButtonThematics.setTextColor(mContext.getResources().getColor(R.color.accent));
        }


    }

    @Override
    public int getItemCount() {
        return mListThematics.size();
    }


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

        public void setTitleThematics(String thematic) {
            mButtonThematics.setText(thematic);

            mButtonThematics.setOnClickListener(v -> {
                mInicialPosition = getAdapterPosition();
                mButtonThematics.setEnabled(false);
                notifyDataSetChanged();
                mOnClickThematicsTribusLineListener.onClickThematicsListener(thematic, mButtonThematics);
            });

        }

    }

    public interface OnClickThematicsTribusLineListener {
        void onClickThematicsListener(String thematicTitle, Button mButtonThematics);
    }

}