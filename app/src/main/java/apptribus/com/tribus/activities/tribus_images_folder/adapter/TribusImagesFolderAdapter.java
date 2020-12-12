package apptribus.com.tribus.activities.tribus_images_folder.adapter;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.chat_tribu.view_holder.UserMessageViewHolder;
import apptribus.com.tribus.activities.show_image.ShowImageActivity;
import apptribus.com.tribus.activities.tribus_images_folder.view_holder.TribusImagesFolderViewHolder;
import apptribus.com.tribus.pojo.MessageUser;

import static apptribus.com.tribus.util.Constantes.TRIBU_UNIQUE_NAME;

/**
 * Created by User on 9/23/2017.
 */

public class TribusImagesFolderAdapter extends RecyclerView.Adapter<TribusImagesFolderViewHolder> {

    private AppCompatActivity mContext;
    private List<MessageUser> mMessageList;
    private String mTribusKey;

    public TribusImagesFolderAdapter(AppCompatActivity mContext, List<MessageUser> mMessageList, String mTribusKey){
        this.mContext = mContext;
        this.mMessageList = mMessageList;
        this.mTribusKey = mTribusKey;
    }


    @Override
    public TribusImagesFolderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_tribus_images_folder, parent, false);

        return new TribusImagesFolderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TribusImagesFolderViewHolder viewHolder, int position) {

        MessageUser message = mMessageList.get(position);

        viewHolder.initTribusImageFolderVH(message, mTribusKey);

        /*viewHolder.setImage(message.getImage().getDownloadUri());
        viewHolder.setTvTime(message.getTimestampCreatedLong());

        viewHolder.mSpImage.setOnClickListener(v -> {
            openShowImageActivity(message);
        });*/

    }

    private void openShowImageActivity(MessageUser message) {

        Uri uri = Uri.parse(message.getImage().getDownloadUri());

        Intent intent = new Intent(mContext, ShowImageActivity.class);
        intent.setData(uri);
        intent.putExtra(TRIBU_UNIQUE_NAME, mTribusKey);
        mContext.startActivity(intent);

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }
}
