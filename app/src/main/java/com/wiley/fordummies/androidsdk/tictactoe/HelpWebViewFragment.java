package com.wiley.fordummies.androidsdk.tictactoe;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import timber.log.Timber;

/**
 * Fragment that shows WebView for Tic-Tac-Toe on Wikipedia.
 *
 * Created by adamcchampion on 2017/08/14.
 */

public class HelpWebViewFragment extends Fragment  {
    private String mUrl;
    private ProgressBar mProgressBar;

    private static final String ARG_URI = "url";
    private final String TAG = getClass().getSimpleName();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_help_webview, container, false);

        WebView helpInWebView = v.findViewById(R.id.helpwithwebview);
        mProgressBar = v.findViewById(R.id.webviewprogress);
        mProgressBar.setMax(100);

        Activity activity = getActivity();

        if (activity != null) {
            Bundle extras = activity.getIntent().getExtras();
            if (extras != null) {
                mUrl = extras.getString(ARG_URI);
                Timber.d(TAG, "Loading URL %s", mUrl);
            }
        }

        WebView.setWebContentsDebuggingEnabled(true);
        // helpInWebView.getSettings().setJavaScriptEnabled(true);
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
            Timber.e(TAG, "Could not set subtitle");
        }
    }
}
