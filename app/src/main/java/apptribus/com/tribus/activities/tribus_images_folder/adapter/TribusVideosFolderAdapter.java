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
import apptribus.com.tribus.activities.show_image.ShowImageActivity;
import apptribus.com.tribus.activities.show_video.ShowVideoActivity;
import apptribus.com.tribus.activities.tribus_images_folder.view_holder.TribusImagesFolderViewHolder;
import apptribus.com.tribus.activities.tribus_images_folder.view_holder.TribusVideosFolderViewHolder;
import apptribus.com.tribus.pojo.MessageUser;

import static apptribus.com.tribus.util.Constantes.TRIBU_UNIQUE_NAME;

/**
 * Created by User on 9/23/2017.
 */

public class TribusVideosFolderAdapter extends RecyclerView.Adapter<TribusVideosFolderViewHolder> {

    private AppCompatActivity mContext;
    private List<MessageUser> mMessageList;
    private String mTribusKey;

    public TribusVideosFolderAdapter(AppCompatActivity mContext, List<MessageUser> mMessageList, String mTribusKey){
        this.mContext = mContext;
        this.mMessageList = mMessageList;
        this.mTribusKey = mTribusKey;
    }


    @Override
    public TribusVideosFolderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_tribus_videos_folder, parent, false);

        return new TribusVideosFolderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TribusVideosFolderViewHolder viewHolder, int position) {

        MessageUser message = mMessageList.get(position);

        viewHolder.initTribusVideoFolder(message, mTribusKey);
        /*viewHolder.setTvDuration(message.getVideo().getDuration());
        viewHolder.setTvTime(message.getTimestampCreatedLong());
        Uri uri = Uri.parse(message.getVideo().getDownloadUri());
        viewHolder.initPlayer(uri);

        if(uri != null) {
            viewHolder.mSimpleExoplayer.setOnClickListener(v -> {
                openShowVideoActivity(message);
            });

            viewHolder.mCardButtonPlay.setOnClickListener(v -> {
                openShowVideoActivity(message);
            });

            viewHolder.mBtnPlay.setOnClickListener(v -> {
                openShowVideoActivity(message);
            });
        }*/
    }

    private void openShowVideoActivity(MessageUser message) {

        Uri uri = Uri.parse(message.getVideo().getDownloadUri());

        Intent intent = new Intent(mContext, ShowVideoActivity.class);
        intent.setData(uri);
        intent.putExtra(TRIBU_UNIQUE_NAME, mTribusKey);
        mContext.startActivity(intent);

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }
}
