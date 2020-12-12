package apptribus.com.tribus.util;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import static android.view.View.X;
import static android.view.View.Y;

/**
 * Created by User on 11/16/2017.
 */

public class NpaLinearLayoutManager extends LinearLayoutManager {

    private Context mContext;

    @Override
    public boolean supportsPredictiveItemAnimations() {
            return false;
    }

    public NpaLinearLayoutManager(Context context) {
        super(context);
        mContext = context;
    }

    public NpaLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public NpaLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
