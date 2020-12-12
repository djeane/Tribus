package apptribus.com.tribus.activities.privacy_policy;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;


import apptribus.com.tribus.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PrivacyPolicyCheckUsernameActivity extends AppCompatActivity {

    //@BindView(R.id.wv_privacy_policy_check_username)
    //WebView mWebView;

    @BindView(R.id.arrow_back)
    ImageView mArrowBack;

    @BindView(R.id.tv57)
    TextView mTv57;

    //ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy_check_username);

        ButterKnife.bind(this);

        String lastUpdate = "Última atualização: 18 de novembro de 2017."; //index: 0 - 18

        SpannableString styledString = new SpannableString(lastUpdate);
        styledString.setSpan(
                new StyleSpan(Typeface.BOLD),
                0,
                18,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );



        //mWebView.loadUrl("file:///android_asset/privacy_policy_check_username.html");
        //mWebView.getSettings().setJavaScriptEnabled(true);


        //init();
        mTv57.setText(styledString);

        mArrowBack.setOnClickListener(v -> {
            finish();
        });


    }

    /*private void init(){
        mWebView.loadUrl("file:///android_asset/privacy_policy_check_username.html");
        mWebView.requestFocus();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Aguarde...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mWebView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                try {
                    progressDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }*/


    @Override
    public void onBackPressed() {
        finish();
    }

}
