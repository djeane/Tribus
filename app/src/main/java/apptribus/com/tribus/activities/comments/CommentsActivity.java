package apptribus.com.tribus.activities.comments;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import javax.inject.Inject;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.comments.dagger.CommentsModule;
import apptribus.com.tribus.activities.comments.dagger.DaggerCommentsComponent;
import apptribus.com.tribus.activities.comments.mvp.CommentsPresenter;
import apptribus.com.tribus.activities.comments.mvp.CommentsView;
import apptribus.com.tribus.application.dagger.TribusApplication;
import apptribus.com.tribus.pojo.Tribu;

public class CommentsActivity extends AppCompatActivity {

    @Inject
    CommentsView view;

    @Inject
    CommentsPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerCommentsComponent.builder()
                .appComponent(TribusApplication.get(this).component())
                .commentsModule(new CommentsModule(this))
                .build().inject(this);

        setContentView(view);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            View view = getCurrentFocus();
            if(view instanceof EditText){
                Rect outRect = new Rect();
                view.getGlobalVisibleRect(outRect);
                if(!outRect.contains((int)event.getRawX(), (int)event.getRawY())){
                    view.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }



    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        presenter.onRestart();
    }

    @Override
    protected void onPause() {
        presenter.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        presenter.onStop();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }
    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }
}
