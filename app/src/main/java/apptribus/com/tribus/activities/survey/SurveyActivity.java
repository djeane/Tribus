package apptribus.com.tribus.activities.survey;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import javax.inject.Inject;

import apptribus.com.tribus.activities.survey.dagger.DaggerSurveyNewComponent;
import apptribus.com.tribus.activities.survey.dagger.SurveyNewModule;
import apptribus.com.tribus.activities.survey.mvp.SurveyNewPresenter;
import apptribus.com.tribus.activities.survey.mvp.SurveyNewView;
import apptribus.com.tribus.application.dagger.TribusApplication;
import apptribus.com.tribus.util.ConnectivityReceiver;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;

public class SurveyActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{

    @Inject
    SurveyNewView view;

    @Inject
    SurveyNewPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerSurveyNewComponent.builder()
                .appComponent(TribusApplication.get(this).component())
                .surveyNewModule(new SurveyNewModule(this))
                .build().inject(this);

        setContentView(view);

        //CHECK INTERNET CONNECTION
        ShowSnackBarInfoInternet.checkConnection(view);

    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
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
    protected void onRestart() {
        super.onRestart();
        presenter.onRestart();
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //CHECK INTERNET CONNECTION
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        ShowSnackBarInfoInternet.showSnack(isConnected, view);
    }

}
