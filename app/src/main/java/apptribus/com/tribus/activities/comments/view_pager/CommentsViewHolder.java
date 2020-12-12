package apptribus.com.tribus.activities.comments.view_pager;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.Date;

import apptribus.com.tribus.R;
import apptribus.com.tribus.util.GetTimeAgo;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by User on 7/24/2017.
 */

public class CommentsViewHolder extends RecyclerView.ViewHolder{

    private Context mContext;

    @BindView(R.id.circle_user_image_comment)
    public SimpleDraweeView mCircleUserImageComment;

    @BindView(R.id.tv_message_user_comment)
    TextView mTvCommment;

    @BindView(R.id.tv_username_comment)
    TextView mTvUsernameComment;

    @BindView(R.id.tv_comment_date)
    TextView mTvCommentDate;

    @BindView(R.id.icon_like)
    public ImageView mIconLike;

    @BindView(R.id.icon_dislike)
    public ImageView mIconDislike;

    @BindView(R.id.tv_num_likes)
    TextView mTvNumLikes;

    @BindView(R.id.tv_num_dislikes)
    TextView mTvNumDislikes;

    @BindView(R.id.btn_clear)
    public Button mBtnClear;

    @BindView(R.id.btn_edit)
    public Button mBtnEdit;

    public CommentsViewHolder(View itemView) {
        super(itemView);
        mContext = itemView.getContext();
        ButterKnife.bind(this, itemView);
    }


    //SET IMAGE OF USER
    public void setUsersImage(String url){

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
                //.setControllerListener(listener)
                .setOldController(mCircleUserImageComment.getController())
                .build();
        mCircleUserImageComment.setController(dc);

    }

    //SET USERNAME COMMENT
    public void setTvUsernameComment(String usernameComment){
        mTvUsernameComment.setText(usernameComment);
    }

    //SET COMMENT
    public void setTvComment(String comment){
        mTvCommment.setText(comment);
    }

    //SET DATE
    public void setTvCommentDate(Date date){
        String time = GetTimeAgo.getTimeAgo(date, mContext);
        mTvCommentDate.setText(time);
    }

    //SET NUM LIKES
    public void setTvNumLikes(int likes){
        mTvNumLikes.setText(String.valueOf(likes));
    }

    //SET NUM DISLIKES
    public void setmTvNumDislikes(int dislikes){
        mTvNumDislikes.setText(String.valueOf(dislikes));
    }

}
