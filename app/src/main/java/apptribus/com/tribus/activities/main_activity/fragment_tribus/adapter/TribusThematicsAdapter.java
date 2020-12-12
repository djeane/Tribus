package apptribus.com.tribus.activities.main_activity.fragment_tribus.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.Collections;
import java.util.List;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.main_activity.fragment_tribus.mvp.TribusFragmentPresenter;
import butterknife.BindView;
import butterknife.ButterKnife;

import static apptribus.com.tribus.util.Constantes.ALL;

/**
 * Created by User on 5/6/2018.
 */

public class TribusThematicsAdapter extends RecyclerView.Adapter<TribusThematicsAdapter.TribusThematicsViewHolder> {

    private Context mContext;
    private List<String> mTribusList;
    private int mInicialPosition = 0;
    private TribusThematicsAdapterClickListener mListener;


    public TribusThematicsAdapter(Context context, List<String> tribusList, TribusFragmentPresenter presenter){
        this.mContext = context;
        this.mTribusList = tribusList;

        if (presenter != null){
            mListener = presenter;
        }

    }


    @Override
    public TribusThematicsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_tribus_thematics, parent, false);
       return new TribusThematicsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TribusThematicsViewHolder holder, int position) {

        String tribusThematics = mTribusList.get(position);

        //holder.setTitleThematics(tribusThematics);

        /*if (position == 0){
            holder.setTitleThematics(ALL);
        }
        else {
            holder.setTitleThematics(tribusThematics);
        }*/

        holder.setTitleThematics(tribusThematics);

        if (position == mInicialPosition){
            holder.mBtnTribusThematics.setTextColor(mContext.getResources().getColor(R.color.accent));
            holder.mView2.setBackgroundColor(mContext.getResources().getColor(R.color.accent));
        }
        else {
            holder.mBtnTribusThematics.setTextColor(mContext.getResources().getColor(R.color.primary_light));
            holder.mView2.setBackgroundColor(mContext.getResources().getColor(R.color.colorIcons));
        }


    }

    @Override
    public int getItemCount() {
        return mTribusList.size();
    }



    public class TribusThematicsViewHolder extends RecyclerView.ViewHolder {


        private Context mContext;

        @BindView(R.id.item_tribu_thematics)
        LinearLayout mCardView;

        @BindView(R.id.button_tribu_thematics)
        Button mBtnTribusThematics;

        @BindView(R.id.view2)
        View mView2;


        public TribusThematicsViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();

            ButterKnife.bind(this, itemView);

        }

        private void setTitleThematics(String title){
            mBtnTribusThematics.setText(title);

            mBtnTribusThematics.setOnClickListener(v -> {
                mInicialPosition = getAdapterPosition();
                mBtnTribusThematics.setEnabled(false);
                notifyDataSetChanged();
                mListener.tribusThematicsAdapterOnClickListener(title);
            });

        }
    }

    public interface TribusThematicsAdapterClickListener{
        void tribusThematicsAdapterOnClickListener(String title);
    }
}
