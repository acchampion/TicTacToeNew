package com.wiley.fordummies.androidsdk.tictactoe.ui.fragment;

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

import com.wiley.fordummies.androidsdk.tictactoe.R;

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
    private static final String TAG = HelpWebViewFragment.class.getSimpleName();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_help_webview, container, false);

        WebView helpInWebView = v.findViewById(R.id.helpwithwebview);
        mProgressBar = v.findViewById(R.id.webviewprogress);
        mProgressBar.setMax(100);

        Activity activity = requireActivity();

		Bundle extras = activity.getIntent().getExtras();
		if (extras != null) {
			mUrl = extras.getString(ARG_URI);
			Timber.tag(TAG).d("Loading URL %s", mUrl);
		}

		WebView.setWebContentsDebuggingEnabled(true);
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
            AppCompatActivity activity = (AppCompatActivity) requireActivity();
            ActionBar actionBar = activity.getSupportActionBar();
			if (actionBar != null) {
				actionBar.setSubtitle(getResources().getString(R.string.help_webview));
			}
		}
        catch (NullPointerException npe) {
            Timber.tag(TAG).e("Could not set subtitle");
        }
    }

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mProgressBar = null;
	}
}
