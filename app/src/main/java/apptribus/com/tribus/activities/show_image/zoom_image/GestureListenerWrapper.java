package apptribus.com.tribus.activities.show_image.zoom_image;

import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by User on 8/27/2017.
 */

public class GestureListenerWrapper extends GestureDetector.SimpleOnGestureListener {

    private GestureDetector.SimpleOnGestureListener mDelegate;
    private ZoomableDraweeView mDraweeView;

    public GestureListenerWrapper(ZoomableDraweeView view) {
        mDelegate = new GestureDetector.SimpleOnGestureListener();
        mDraweeView = view;
    }

    public void setListener(GestureDetector.SimpleOnGestureListener listener) {
        mDelegate = listener;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        mDelegate.onLongPress(e);
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return mDelegate.onScroll(e1, e2, distanceX, distanceY);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return mDelegate.onFling(e1, e2, velocityX, velocityY);
    }

    @Override
    public void onShowPress(MotionEvent e) {
        mDelegate.onShowPress(e);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return mDelegate.onDown(e);
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        mDraweeView.onDoubleTap(e);
        return mDelegate.onDoubleTap(e);
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return mDelegate.onDoubleTapEvent(e);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return mDelegate.onSingleTapConfirmed(e);
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return mDelegate.onSingleTapUp(e);
    }
}