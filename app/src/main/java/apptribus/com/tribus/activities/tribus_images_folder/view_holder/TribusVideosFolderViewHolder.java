package apptribus.com.tribus.activities.tribus_images_folder.view_holder;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;

import java.text.SimpleDateFormat;
import java.util.Date;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.chat_tribu.view_holder.UserMessageViewHolder;
import apptribus.com.tribus.activities.show_video.ShowVideoActivity;
import apptribus.com.tribus.pojo.MessageUser;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static apptribus.com.tribus.util.Constantes.TRIBU_UNIQUE_NAME;

/**
 * Created by User on 9/23/2017.
 */

public class TribusVideosFolderViewHolder extends RecyclerView.ViewHolder {

    private Context mContext;

    @BindView(R.id.frame_videos_folder)
    FrameLayout mFrameFolder;

    @BindView(R.id.relative_exoplayer)
    RelativeLayout mRelativeExoplayer;

    @BindView(R.id.video_frame)
    SimpleExoPlayerView mSimpleExoplayer;

    @BindView(R.id.loading_painel)
    RelativeLayout mRelativeLoadingPanel;

    @BindView(R.id.card_button_play)
    CardView mCardButtonPlay;

    @BindView(R.id.btn_play)
    ImageButton mBtnPlay;

    @BindView(R.id.card_duration)
    CardView mCardDuration;

    @BindView(R.id.tv_duration)
    TextView mTvDuration;

    @BindView(R.id.image_time)
    TextView mTvTime;

    private SimpleExoPlayer mExoplayer;

    public TribusVideosFolderViewHolder(View itemView) {
        super(itemView);
        mContext = itemView.getContext();
        ButterKnife.bind(this, itemView);

    }


    //INIT VIDEO
    private void initPlayer(Uri videoUri) {

        mSimpleExoplayer.requestFocus();

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        DefaultTrackSelector trackSelector;
        DataSource.Factory mediaDataSourceFactory = new DefaultDataSourceFactory(mContext,
                Util.getUserAgent(mContext, "Tribus"),
                (TransferListener<? super DataSource>) bandwidthMeter);
        ;

        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        mExoplayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector);
        mSimpleExoplayer.setPlayer(mExoplayer);
        mExoplayer.setPlayWhenReady(false);

        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource mediaSource = new ExtractorMediaSource(videoUri,
                mediaDataSourceFactory, extractorsFactory, null, null);
        mExoplayer.prepare(mediaSource);

    }


    //set talker since
    private void setTvTime(long timestamp) {
        //SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        SimpleDateFormat sfd = new SimpleDateFormat("dd/MM HH:mm");
        String time = sfd.format(new Date(timestamp));

        mTvTime.setText(time);
    }

    private void setTvDuration(String duration){
        mTvDuration.setText(duration);
    }

    public void initTribusVideoFolder(MessageUser message, String mTribusKey){
        setTvDuration(message.getVideo().getDuration());
        //setTvTime(message.getTimestampCreatedLong());
        Uri uri = Uri.parse(message.getVideo().getDownloadUri());
        initPlayer(uri);

        if(uri != null) {
            mSimpleExoplayer.setOnClickListener(v -> {
                openShowVideoActivity(message, mTribusKey);
            });

            mCardButtonPlay.setOnClickListener(v -> {
                openShowVideoActivity(message, mTribusKey);
            });

            mBtnPlay.setOnClickListener(v -> {
                openShowVideoActivity(message, mTribusKey);
            });
        }
    }

    private void openShowVideoActivity(MessageUser message, String mTribusKey) {

        Uri uri = Uri.parse(message.getVideo().getDownloadUri());

        Intent intent = new Intent(mContext, ShowVideoActivity.class);
        intent.setData(uri);
        intent.putExtra(TRIBU_UNIQUE_NAME, mTribusKey);
        mContext.startActivity(intent);

    }


}
