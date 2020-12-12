package apptribus.com.tribus.activities.show_survey;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import javax.inject.Inject;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.show_survey.dagger.DaggerShowSurveyComponent;
import apptribus.com.tribus.activities.show_survey.dagger.ShowSurveyModule;
import apptribus.com.tribus.activities.show_survey.mvp.ShowSurveyPresenter;
import apptribus.com.tribus.activities.show_survey.mvp.ShowSurveyView;
import apptribus.com.tribus.application.dagger.TribusApplication;
import butterknife.ButterKnife;

public class ShowSurveyActivity extends AppCompatActivity {

    @Inject
    ShowSurveyView view;

    @Inject
    ShowSurveyPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerShowSurveyComponent.builder()
                .appComponent(TribusApplication.get(this).component())
                .showSurveyModule(new ShowSurveyModule(this))
                .build().inject(this);

        setContentView(view);

    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    protected void onStop() {
        presenter.onStop();
        super.onStop();
    }

    @Override
    protected void onPause() {
        presenter.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        presenter.onRestart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }
}
