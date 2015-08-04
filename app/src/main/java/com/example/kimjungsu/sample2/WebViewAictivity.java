package com.example.kimjungsu.sample2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

public class WebViewAictivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_aictivity);

        WebView webView = (WebView) findViewById(R.id.webView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://m.naver.com");

    }
}
