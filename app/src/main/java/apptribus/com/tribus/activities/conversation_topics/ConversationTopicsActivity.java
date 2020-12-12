package apptribus.com.tribus.activities.conversation_topics;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import javax.inject.Inject;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.conversation_topics.dagger.ConversationTopicsModule;
import apptribus.com.tribus.activities.conversation_topics.dagger.DaggerConversationTopicsComponent;
import apptribus.com.tribus.activities.conversation_topics.mvp.ConversationTopicsPresenter;
import apptribus.com.tribus.activities.conversation_topics.mvp.ConversationTopicsView;
import apptribus.com.tribus.application.dagger.TribusApplication;

public class ConversationTopicsActivity extends AppCompatActivity {

    @Inject
    ConversationTopicsView view;

    @Inject
    ConversationTopicsPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerConversationTopicsComponent.builder()
                .appComponent(TribusApplication.get(this).component())
                .conversationTopicsModule(new ConversationTopicsModule(this))
                .build().inject(this);


        setContentView(view);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    protected void onPause() {
        presenter.onPause();
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        presenter.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    protected void onStop() {
        presenter.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }
}
