package apptribus.com.tribus.activities.main_activity.fragment_talks.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Collections;
import java.util.List;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.main_activity.fragment_talks.mvp.TalksFragmentPresenter;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by User on 5/23/2018.
 */

public class TalksFragmentCategoryAdapter extends RecyclerView.Adapter<TalksFragmentCategoryAdapter.TalksCategoryViewHolder>{


    private LayoutInflater mInflate;
    private List<String> mListCategories;
    private Context mContext;
    private int mInicialPosition = 0;
    private OnCategoryListener mListener;


    public TalksFragmentCategoryAdapter(Context context, List<String> listCategories, TalksFragmentPresenter presenter){
        this.mInflate = LayoutInflater.from(context);
        this.mListCategories = listCategories;
        this.mContext = context;

        if (presenter != null){
            mListener = presenter;
        }
    }

    @Override
    public TalksCategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = mInflate.inflate(R.layout.row_talks_category, parent, false);

        return new TalksCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TalksCategoryViewHolder holder, int position) {

        String currentTitle = mListCategories.get(position);
        holder.setCategoryTitle(currentTitle);
        if (position == mInicialPosition){
            holder.mBtnCategory.setBackground(mContext.getResources().getDrawable(R.drawable.button_thematics_pressed));
            holder.mBtnCategory.setTextColor(mContext.getResources().getColor(R.color.colorIcons));
        }
        else {
            holder.mBtnCategory.setBackground(mContext.getResources().getDrawable(R.drawable.button_thematics));
            holder.mBtnCategory.setTextColor(mContext.getResources().getColor(R.color.accent));
        }

    }

    @Override
    public int getItemCount() {
        return mListCategories.size();
    }

    public class TalksCategoryViewHolder extends RecyclerView.ViewHolder{

        private Context mContext;

        @BindView(R.id.item_category)
        CardView mCardView;

        @BindView(R.id.button_category)
        public Button mBtnCategory;


        public TalksCategoryViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            ButterKnife.bind(this, itemView);

        }

        private void setCategoryTitle(String categoryTitle) {
            mBtnCategory.setText(categoryTitle);

            mBtnCategory.setOnClickListener(view -> {
                mInicialPosition = getAdapterPosition();
                notifyDataSetChanged();
                mListener.onCategoryClickListener(categoryTitle);
            });
        }
    }

    public interface OnCategoryListener{
        void onCategoryClickListener(String categoryTitle);
    }
}
