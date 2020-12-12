package apptribus.com.tribus.activities.main_activity.fragment_tribus.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Collections;
import java.util.List;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.main_activity.fragment_tribus.mvp.TribusFragmentPresenter;
import butterknife.BindView;
import butterknife.ButterKnife;

import static apptribus.com.tribus.util.Constantes.CAMPAIGN;
import static apptribus.com.tribus.util.Constantes.EVENT;
import static apptribus.com.tribus.util.Constantes.SHARED_MEDIA;
import static apptribus.com.tribus.util.Constantes.SURVEY;
import static apptribus.com.tribus.util.Constantes.TOPIC;

/**
 * Created by User on 5/2/2018.
 */

public class TribusFragmentCategoriesAdapter extends RecyclerView.Adapter<TribusFragmentCategoriesAdapter.TribusCategoryViewHolder> {

    private LayoutInflater mInflate;
    private List<String> mListCategories;
    private OnTribusFragmentCategoriesAdapterListener mOnCategoryListener;
    private int mInicialPosition = 0;
    private Context mContext;


    public TribusFragmentCategoriesAdapter(Context context, List<String> listCategories, TribusFragmentPresenter presenter) {
        this.mInflate = LayoutInflater.from(context);
        this.mListCategories = listCategories;
        this.mContext = context;

        if (presenter != null){
            mOnCategoryListener = presenter;
        }
    }

    @Override
    public TribusCategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = mInflate.inflate(R.layout.row_tribus_category, parent, false);

        return new TribusCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TribusCategoryViewHolder holder, int position) {

        String currentTitle = mListCategories.get(position);
        holder.setCategoryTitle(currentTitle);

        if (position == mInicialPosition){
            holder.mBtnCategory.setBackground(mContext.getResources().getDrawable(R.drawable.button_thematics_pressed));
            holder.mBtnCategory.setTextColor(mContext.getResources().getColor(R.color.colorIcons));
        }
        else {
            holder.mBtnCategory.setBackground(mContext.getResources().getDrawable(R.drawable.button_thematics));
            holder.mBtnCategory.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        }

    }

    @Override
    public int getItemCount() {
        return mListCategories.size();
    }


    public class TribusCategoryViewHolder extends RecyclerView.ViewHolder{

        private Context mContext;

        @BindView(R.id.item_category)
        CardView mCardView;

        @BindView(R.id.button_category)
        public Button mBtnCategory;


        public TribusCategoryViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            ButterKnife.bind(this, itemView);

        }

        private void setCategoryTitle(String categoryTitle) {
            mBtnCategory.setText(categoryTitle);

            mBtnCategory.setOnClickListener(view -> {
                mInicialPosition = getAdapterPosition();
                notifyDataSetChanged();
                mOnCategoryListener.onCategoryClickListener(categoryTitle);
            });
        }
    }

    public interface OnTribusFragmentCategoriesAdapterListener {
        void onCategoryClickListener(String categoryTitle);
    }
}
