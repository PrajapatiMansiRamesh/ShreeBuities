package com.tecmanic.gogrocer.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;

import com.tecmanic.gogrocer.R;

public class GameWebActivity extends AppCompatActivity {

    WebSettings webSettings;
    String bankUrl = "";
    private WebView web_view;
    private ContentLoadingProgressBar progress_id;
    private View bg_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_web);
        web_view = findViewById(R.id.web_view);
        bg_back = findViewById(R.id.bg_back);
        progress_id = findViewById(R.id.progress_id);
        webSettings = web_view.getSettings();
        webSettings.setJavaScriptEnabled(true);
        bg_back.setVisibility(View.VISIBLE);
        progress_id.show();
        web_view.loadUrl("https://www.gamezop.com/?id=e2b2jv9S2");
        web_view.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                bg_back.setVisibility(View.GONE);
                progress_id.hide();
                super.onPageFinished(view, url);

//                bankUrl = url;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                bg_back.setVisibility(View.GONE);
                progress_id.hide();
                super.onReceivedError(view, request, error);
            }

        });

    }

    @Override
    public void onBackPressed() {
        if (this.web_view.canGoBack()) {
            this.web_view.goBack();
        } else {
            new AlertDialog.Builder(GameWebActivity.this)
                    .setTitle("Game Alert!")
                    .setMessage("Are you sure want to exit from game.")
                    .setCancelable(false)
                    .setPositiveButton("OK", (dialog, which) -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            finishAndRemoveTask();
                        } else {
                            finish();
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();

        }

    }
}