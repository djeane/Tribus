package apptribus.com.tribus.activities.blocked_talkers.view_holder;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import java.text.SimpleDateFormat;
import java.util.Date;

import apptribus.com.tribus.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BlockedTalkersViewHolder extends RecyclerView.ViewHolder {

    private Context mContext;

    @BindView(R.id.row_blocked_talkers)
    public CardView mCardBlockedTalkers;

    @BindView(R.id.circle_image_of_talker)
    public SimpleDraweeView mImageTalkers;

    @BindView(R.id.circle_image_of_tribu)
    public SimpleDraweeView mImageTribu;

    @BindView(R.id.tv_name_of_talker)
    public TextView mTvTalkersName;

    @BindView(R.id.tv_username_talker)
    public TextView mTvUsernameTalkers;

    @BindView(R.id.tv_name_of_tribu)
    public TextView mTvNameOfTribu;

    @BindView(R.id.tv_unique_name)
    public TextView mTvUniqueNameOfTribu;

    @BindView(R.id.tv_blocked_talker_since)
    public TextView mTvBlockedTalkerSince;

    @BindView(R.id.btn_unblock)
    public Button mBtnUnblock;


    public BlockedTalkersViewHolder(View itemView) {
        super(itemView);
        mContext = itemView.getContext();
        ButterKnife.bind(this, itemView);

    }

    //Talkers
    //set image
    public void setImageTalker(String url){

        ControllerListener listener = new BaseControllerListener() {
            @Override
            public void onFailure(String id, Throwable throwable) {
                super.onFailure(id, throwable);
                Log.d("Valor: ", "onFailure - id: " + id + "throwable: " + throwable);
            }

            @Override
            public void onIntermediateImageFailed(String id, Throwable throwable) {
                super.onIntermediateImageFailed(id, throwable);
                Log.d("Valor: ", "onIntermediateImageFailed - id: " + id + "throwable: " + throwable);
            }

            @Override
            public void onFinalImageSet(String id, Object imageInfo, Animatable animatable) {
                super.onFinalImageSet(id, imageInfo, animatable);
                Log.d("Valor: ", "onFinalImageSet - id: " + id + "imageInfo: " + imageInfo + "animatable: " + animatable);
            }

            @Override
            public void onIntermediateImageSet(String id, Object imageInfo) {
                super.onIntermediateImageSet(id, imageInfo);
                Log.d("Valor: ", "onIntermediateImageSet - id: " + id + "imageInfo: " + imageInfo);
            }

            @Override
            public void onRelease(String id) {
                super.onRelease(id);
                Log.d("Valor: ", "onRelease - id: " + id);
            }

            @Override
            public void onSubmit(String id, Object callerContext) {
                super.onSubmit(id, callerContext);
                Log.d("Valor: ", "onSubmit - id: " + id + "callerContext: " + callerContext);
            }
        };

        //SCRIPT - LARGURA DA IMAGEM
        //int w = 0;
        /*if (holder.mImageTribu.getLayoutParams().width == FrameLayout.LayoutParams.MATCH_PARENT
                || holder.mImageTribu.getLayoutParams().width == FrameLayout.LayoutParams.WRAP_CONTENT) {

            Display display = ((MainActivity) mContext).getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            try {
                w = size.x;
                Log.d("Valor: ", "Valor da largura(w) em onStart(FragmentPesquisarTribu): " + w);

            } catch (Exception e) {
                w = display.getWidth();
                e.printStackTrace();
            }
        }*/

        Uri uri = Uri.parse(url);
        DraweeController dc = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setControllerListener(listener)
                .setOldController(mImageTalkers.getController())
                .build();
        mImageTalkers.setController(dc);

    }

    //set username
    public void setTvUsernameTalkers(String usernameTalkers){
        mTvUsernameTalkers.setText(usernameTalkers);
    }

    //set Talker's name
    public void setTvTalkersName(String talkersName){
        mTvTalkersName.setText(talkersName);
    }

    //set blocked talker since
    public void setTvBlockedTalkerSince(long timestamp) {
        //SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        SimpleDateFormat sfd = new SimpleDateFormat("dd/MM HH:mm");
        String time = sfd.format(new Date(timestamp));

        String appendTime = "Contato bloqueado desde: " + time;
        Log.d("Valor: ", "appendTime: " + appendTime);

        mTvBlockedTalkerSince.setText(appendTime);
    }


    //Tribu
    //set image
    public void setImageTribu(String url){

        ControllerListener listener = new BaseControllerListener() {
            @Override
            public void onFailure(String id, Throwable throwable) {
                super.onFailure(id, throwable);
                Log.d("Valor: ", "onFailure - id: " + id + "throwable: " + throwable);
            }

            @Override
            public void onIntermediateImageFailed(String id, Throwable throwable) {
                super.onIntermediateImageFailed(id, throwable);
                Log.d("Valor: ", "onIntermediateImageFailed - id: " + id + "throwable: " + throwable);
            }

            @Override
            public void onFinalImageSet(String id, Object imageInfo, Animatable animatable) {
                super.onFinalImageSet(id, imageInfo, animatable);
                Log.d("Valor: ", "onFinalImageSet - id: " + id + "imageInfo: " + imageInfo + "animatable: " + animatable);
            }

            @Override
            public void onIntermediateImageSet(String id, Object imageInfo) {
                super.onIntermediateImageSet(id, imageInfo);
                Log.d("Valor: ", "onIntermediateImageSet - id: " + id + "imageInfo: " + imageInfo);
            }

            @Override
            public void onRelease(String id) {
                super.onRelease(id);
                Log.d("Valor: ", "onRelease - id: " + id);
            }

            @Override
            public void onSubmit(String id, Object callerContext) {
                super.onSubmit(id, callerContext);
                Log.d("Valor: ", "onSubmit - id: " + id + "callerContext: " + callerContext);
            }
        };

        //SCRIPT - LARGURA DA IMAGEM
        //int w = 0;
        /*if (holder.mImageTribu.getLayoutParams().width == FrameLayout.LayoutParams.MATCH_PARENT
                || holder.mImageTribu.getLayoutParams().width == FrameLayout.LayoutParams.WRAP_CONTENT) {

            Display display = ((MainActivity) mContext).getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            try {
                w = size.x;
                Log.d("Valor: ", "Valor da largura(w) em onStart(FragmentPesquisarTribu): " + w);

            } catch (Exception e) {
                w = display.getWidth();
                e.printStackTrace();
            }
        }*/

        Uri uri = Uri.parse(url);
        DraweeController dc = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setControllerListener(listener)
                .setOldController(mImageTribu.getController())
                .build();
        mImageTribu.setController(dc);

    }

    //set Tribu's name
    public void setTvNameOfTribu(String nameOfTribu){
        mTvNameOfTribu.setText(nameOfTribu);
    }

    //set Tribu's unique name
    public void setTvUniqueOfTribu(String uniqueNameOfTribu){
        mTvUniqueNameOfTribu.setText(uniqueNameOfTribu);
    }


}
