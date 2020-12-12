package apptribus.com.tribus.activities.send_video.interfaces;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.send_video.SendVideoActivity;
import apptribus.com.tribus.activities.send_video.mvp.SendVideoView;

/**
 * Created by User on 7/13/2017.
 */

public interface SendVideoRenderBuilderInterface {
    void buildRenderers(SendVideoActivity player);
    void cancel();
}
