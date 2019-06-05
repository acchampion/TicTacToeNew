package com.wiley.fordummies.androidsdk.tictactoe;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * Fragment that shows WebView for Tic-Tac-Toe on Wikipedia.
 *
 * Created by adamcchampion on 2017/08/14.
 */

public class HelpWebViewFragment extends Fragment implements View.OnClickListener {
    private String mUrl;
    private ProgressBar mProgressBar;

    private static final String ARG_URI = "url";
    private final String TAG = getClass().getSimpleName();

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    @SuppressWarnings({"LogNotTimber"})
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_help_webview, container, false);

        WebView helpInWebView = v.findViewById(R.id.helpwithwebview);
        mProgressBar = v.findViewById(R.id.webviewprogress);
        mProgressBar.setMax(100);

        View buttonExit = v.findViewById(R.id.button_exit);
        buttonExit.setOnClickListener(this);
        Activity activity = getActivity();

        if (activity != null) {
            Bundle extras = activity.getIntent().getExtras();
            if (extras != null) {
                mUrl = extras.getString(ARG_URI);
                Log.d(TAG, "Loading URL " + mUrl);
            }
        }

        WebView.setWebContentsDebuggingEnabled(true);
        helpInWebView.getSettings().setJavaScriptEnabled(true);
        helpInWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }
        });
        helpInWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView webView, int progress) {
                if (progress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                }
                else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(progress);
                }
            }
        });

        helpInWebView.loadUrl(mUrl);


        return v;
    }

    @Override
    @SuppressWarnings({"LogNotTimber"})
    public void onResume() {
        super.onResume();
        try {
            AppCompatActivity activity = (AppCompatActivity) getActivity();

            if (activity != null) {
                ActionBar actionBar = activity.getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setSubtitle(getResources().getString(R.string.help_webview));
                }
            }
        }
        catch (NullPointerException npe) {
            Log.e(TAG, "Could not set subtitle");
        }
    }


    @Override
    public void onClick(View view) {
        Activity activity = getActivity();

        if (activity != null) {
            switch (view.getId()) {
                case R.id.button_exit:
                    activity.finishFromChild(getActivity());
                    break;
            }
        }
    }
}
