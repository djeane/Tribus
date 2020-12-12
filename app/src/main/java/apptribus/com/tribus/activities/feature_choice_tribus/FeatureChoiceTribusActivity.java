package apptribus.com.tribus.activities.feature_choice_tribus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import javax.inject.Inject;

import apptribus.com.tribus.activities.feature_choice_tribus.dagger.DaggerFeatureChoiceTribusComponent;
import apptribus.com.tribus.activities.feature_choice_tribus.dagger.FeatureChoiceTribusModule;
import apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_container.mvp.FragmentContainerPresenter;
import apptribus.com.tribus.activities.feature_choice_tribus.fragments.fragment_feature.mvp.FragmentFeaturePresenter;
import apptribus.com.tribus.activities.feature_choice_tribus.mvp.FeatureChoiceTribusPresenter;
import apptribus.com.tribus.activities.feature_choice_tribus.mvp.FeatureChoiceTribusView;
import apptribus.com.tribus.application.dagger.TribusApplication;
import apptribus.com.tribus.util.ConnectivityReceiver;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;

public class FeatureChoiceTribusActivity extends AppCompatActivity
        implements ConnectivityReceiver.ConnectivityReceiverListener,
        FragmentFeaturePresenter.OnFragmentFeaturePresenterListener,
        FragmentContainerPresenter.OnFragmentContainerListener,
        FragmentFeaturePresenter.OnTvInfoNewParticipantListener {


    @Inject
    FeatureChoiceTribusView view;

    @Inject
    FeatureChoiceTribusPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerFeatureChoiceTribusComponent.builder()
                .appComponent(TribusApplication.get(this).component())
                .featureChoiceTribusModule(new FeatureChoiceTribusModule(this))
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
    protected void onResume() {
        super.onResume();
        //register connection status listener
        TribusApplication.getInstance().setConnectivityListener(this);
        presenter.onResume();
    }

    @Override
    protected void onStop() {
        presenter.onStop();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

        ShowSnackBarInfoInternet.showSnack(isConnected, view);
    }

    @Override
    public void getInstance(String feature) {
        presenter.getInstance(feature);
    }

    @Override
    public void startChatActivity(String tribuUniqueName, String topicKey, String tribuKey, String tribuName, String topicName) {
        presenter.startChatActivity(tribuUniqueName, topicKey, tribuKey, tribuName, topicName);
    }

    @Override
    public void onInfoNewParticipantListener(String mTribusKey) {
        presenter.openDetailTribuAddFollowers(mTribusKey);
    }
}
