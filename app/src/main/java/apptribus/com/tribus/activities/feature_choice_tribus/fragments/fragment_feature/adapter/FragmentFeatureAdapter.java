package apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_feature.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_feature.mvp.FragmentFeaturePresenter;
import butterknife.BindView;
import butterknife.ButterKnife;

import static apptribus.com.tribus.util.Constantes.CAMPAIGN;
import static apptribus.com.tribus.util.Constantes.EVENT;
import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.SHARED_MEDIA;
import static apptribus.com.tribus.util.Constantes.SURVEY;
import static apptribus.com.tribus.util.Constantes.SURVEYS;
import static apptribus.com.tribus.util.Constantes.TOPIC;
import static apptribus.com.tribus.util.Constantes.TOPICS;
import static apptribus.com.tribus.util.Constantes.TRIBUS_SURVEY;
import static apptribus.com.tribus.util.Constantes.TRIBUS_TOPICS;

public class FragmentFeatureAdapter extends RecyclerView.Adapter<FragmentFeatureAdapter.FragmentFeatureViewHolder> {

    private LayoutInflater mLayoutInflater;
    private String mTribusKey;
    private FragmentAdapterOnClickListener onFeatureListener;
    private Context mContext;
    private int mInicialPosition = 0;


    public FragmentFeatureAdapter(Context context, String tribusKey, FragmentFeaturePresenter presenter) {
        this.mTribusKey = tribusKey;

        mContext = context;

        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (presenter != null) {
            onFeatureListener = presenter;
        }
    }


    @Override
    public FragmentFeatureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.row_tribus_features, parent, false);
        return new FragmentFeatureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FragmentFeatureViewHolder holder, int position) {

        holder.getTitleFeatures();


        if (position == mInicialPosition){
            holder.mmBtnTribusFeature.setBackground(mContext.getResources().getDrawable(R.drawable.button_thematics_pressed));
            holder.mmBtnTribusFeature.setTextColor(mContext.getResources().getColor(R.color.colorIcons));
        }
        else {
            holder.mmBtnTribusFeature.setBackground(mContext.getResources().getDrawable(R.drawable.button_thematics));
            holder.mmBtnTribusFeature.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        }


    }

    @Override
    public int getItemCount() {
        return 5;
    }


    public class FragmentFeatureViewHolder extends RecyclerView.ViewHolder {


        private Context mContext;

        @BindView(R.id.item_tribu_features)
        LinearLayout mCardView;

        @BindView(R.id.button_tribu_features)
        Button mmBtnTribusFeature;

        //FIRESTORE INSTANCE
        private FirebaseFirestore mFirestore;

        //FIRESTORE COLLECTIONS REFERENCES
        private CollectionReference mTribusCollection;


        //FIREBASE
        private FirebaseAuth mAuth;


        public FragmentFeatureViewHolder(View itemView) {
            super(itemView);

            mContext = itemView.getContext();
            ButterKnife.bind(this, itemView);

            mAuth = FirebaseAuth.getInstance();
            mFirestore = FirebaseFirestore.getInstance();
            mTribusCollection = mFirestore.collection(GENERAL_TRIBUS);

        }

        private void setTitleFeatures(long numTopics, long numSurveys) {

            switch (getAdapterPosition()) {
                case 0:
                    String appendTitleTopics = mContext.getResources().getString(R.string.topics)
                            + String.valueOf(numTopics) + ")";
                    mmBtnTribusFeature.setText(appendTitleTopics);
                    mmBtnTribusFeature.setOnClickListener(v -> {
                        mInicialPosition = getAdapterPosition();
                        notifyDataSetChanged();
                        if (onFeatureListener != null) {
                            onFeatureListener.onFeatureClickListener(TOPIC);
                        }

                    });
                    break;

                case 1:
                    String appendTitleSurveys = mContext.getResources().getString(R.string.surveys)
                            + String.valueOf(numSurveys) + ")";
                    mmBtnTribusFeature.setText(appendTitleSurveys);
                    mmBtnTribusFeature.setOnClickListener(v -> {
                        mInicialPosition = getAdapterPosition();
                        notifyDataSetChanged();
                        if (onFeatureListener != null) {
                            onFeatureListener.onFeatureClickListener(SURVEY);
                        }

                    });
                    break;

                case 2:
                    String appendTitleEvents = mContext.getResources().getString(R.string.events);
                            /*+ String.valueOf(numTopics) + ")";*/
                    mmBtnTribusFeature.setText(appendTitleEvents);
                    mmBtnTribusFeature.setOnClickListener(v -> {
                        mInicialPosition = getAdapterPosition();
                        notifyDataSetChanged();
                        if (onFeatureListener != null) {
                            onFeatureListener.onFeatureClickListener(EVENT);
                        }

                    });
                    break;

                case 3:
                    String appendTitleCampaigns = mContext.getResources().getString(R.string.campaigns);
                            /*+ String.valueOf(numTopics) + ")";*/
                    mmBtnTribusFeature.setText(appendTitleCampaigns);
                    mmBtnTribusFeature.setOnClickListener(v -> {
                        mInicialPosition = getAdapterPosition();
                        notifyDataSetChanged();
                        if (onFeatureListener != null) {
                            onFeatureListener.onFeatureClickListener(CAMPAIGN);
                        }

                    });
                    break;

                case 4:
                    String appendTitleSharedMedia = mContext.getResources().getString(R.string.shared_media);
                            /*+ String.valueOf(numTopics) + ")";*/
                    mmBtnTribusFeature.setText(appendTitleSharedMedia);
                    mmBtnTribusFeature.setOnClickListener(v -> {
                        mInicialPosition = getAdapterPosition();
                        notifyDataSetChanged();
                        if (onFeatureListener != null) {
                            onFeatureListener.onFeatureClickListener(SHARED_MEDIA);
                        }

                    });
                    break;
            }
        }


        private void getTitleFeatures() {

            mTribusCollection
                    .document(mTribusKey)
                    .collection(TOPICS)
                    .get()
                    .addOnSuccessListener(queryTopics -> {

                        mTribusCollection
                                .document(mTribusKey)
                                .collection(SURVEYS)
                                .get()
                                .addOnSuccessListener(querySurveys -> {

                                    setTitleFeatures(queryTopics.getDocuments().size(),
                                            querySurveys.getDocuments().size());

                                })
                                .addOnFailureListener(Throwable::printStackTrace);


                    })
                    .addOnFailureListener(Throwable::printStackTrace);


        }
    }

    public interface FragmentAdapterOnClickListener {
        void onFeatureClickListener(String feature);
    }
}
