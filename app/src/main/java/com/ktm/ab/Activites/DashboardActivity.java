package com.ktm.ab.Activites;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ktm.ab.R;
import com.ktm.ab.Util.Constants;
import com.ktm.ab.Util.SharedDataUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DashboardActivity extends AppCompatActivity {

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mWebView = (WebView) findViewById(R.id.web);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
//        mWebView.getSettings().setSupportZoom(true);
        mWebView.addJavascriptInterface(new WebAppInterface(this), "Android");
        mWebView.loadUrl("file:///" + getFilesDir() + "/" + SharedDataUtils.getStringFields(this, Constants.Pref.CURRENT_PATH) + "/index.html");
        mWebView.setWebViewClient(new WebClient());

        if (Build.VERSION.SDK_INT >= 19) {
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

    }


    public class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void ServiceLocator() {
//            Toast.makeText(getApplicationContext(), "Click is Demo", Toast.LENGTH_LONG).show();
            startActivity(new Intent(mContext, ServiceCenterSearchActivity.class));
        }

        @JavascriptInterface
        public String getUserName() {
//            Toast.makeText(getApplicationContext(), "Click is Username", Toast.LENGTH_LONG).show();
            return SharedDataUtils.getStringFields(getApplicationContext(), Constants.Pref.CUSTOMER_NAME);
        }
    }

    public class WebClient extends WebViewClient {
        private RelativeLayout progressBar;

        public WebClient() {
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.e("Url ", "Url =-=-==-=-=-=-=-=- " + url);
            if (url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                startActivity(intent);
                return true;
            } else if (url.startsWith("http:") || url.startsWith("https:")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            } else if (url.startsWith("mailto:")) {
                // TODO : handle mail url
                return true;
            } else {
                mWebView.loadUrl(url);
            }
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {

        }
    }

}
