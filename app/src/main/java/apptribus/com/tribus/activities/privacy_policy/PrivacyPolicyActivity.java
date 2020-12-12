package apptribus.com.tribus.activities.privacy_policy;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.register_user.UserRegisterActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PrivacyPolicyActivity extends AppCompatActivity {

    @BindView(R.id.wv_privacy_policy)
    WebView mWebView;

    @BindView(R.id.arrow_back)
    ImageView mArrowBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_privacy_policy);

        ButterKnife.bind(this);

        mWebView.loadUrl("file:///android_asset/privacy_policy.html");


        mArrowBack.setOnClickListener(v -> {
            finish();
        });

    }


    @Override
    public void onBackPressed() {
        finish();
    }
}
