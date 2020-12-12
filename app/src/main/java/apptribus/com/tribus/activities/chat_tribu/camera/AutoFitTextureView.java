package apptribus.com.tribus.activities.chat_tribu.camera;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

/**
 * Created by User on 6/25/2017.
 */

public class AutoFitTextureView extends TextureView {

    private int mRatioWidth = 0;
    private int mRatioHeight = 0;


    public AutoFitTextureView(Context context) {
        this(context, null);
    }

    public AutoFitTextureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoFitTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void setAspectRatio(int width, int height){
        if(width < 0 || height < 0) {
            throw new IllegalArgumentException("O tamanho não pode ser menor que 0");
        }
        mRatioWidth = width;
        mRatioHeight = height;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if(mRatioWidth == 0 || mRatioHeight == 0){
            setMeasuredDimension(width, height);
        }
        else {
            if(width < height * mRatioWidth / mRatioHeight){
                setMeasuredDimension(width, width * mRatioHeight / mRatioWidth);
            }
            else {
                setMeasuredDimension(height * mRatioWidth / mRatioHeight, height);
            }
        }
    }
}
