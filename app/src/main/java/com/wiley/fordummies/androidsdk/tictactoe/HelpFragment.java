package com.wiley.fordummies.androidsdk.tictactoe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import timber.log.Timber;

/**
 * Created by adamcchampion on 2017/08/14.
 */

public class HelpFragment extends Fragment implements View.OnClickListener {
    private final String TAG = getClass().getSimpleName();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_help, container, false);

        Button btOK = v.findViewById(R.id.button_help_ok);
        btOK.setOnClickListener(this);
        Button wikipedia = v.findViewById(R.id.button_lookup_wikipedia);
        wikipedia.setOnClickListener(this);
        Button wikipediaWebView = v.findViewById(R.id.button_lookup_wikipedia_in_web_view);
        wikipediaWebView.setOnClickListener(this);

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
                    actionBar.setSubtitle(getResources().getString(R.string.help));
                }
            }
        }
        catch (NullPointerException npe) {
            Timber.e(TAG, "Could not set subtitle");
        }
    }

    private boolean hasNetworkConnection() {
        Activity activity = getActivity();

        if (activity != null) {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) activity.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                boolean isConnected;
                boolean isWifiAvailable = networkInfo.isAvailable();
                boolean isWifiConnected = networkInfo.isConnected();
                networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                boolean isMobileAvailable = networkInfo.isAvailable();
                boolean isMobileConnected = networkInfo.isConnected();
                isConnected = (isMobileAvailable && isMobileConnected) || (isWifiAvailable && isWifiConnected);
                return (isConnected);
            }
            return false;
        }
        else {
            return false;
        }
    }

    private void launchBrowser(String url) {
        Uri theUri = Uri.parse(url);
        Intent launchBrowserIntent = new Intent(Intent.ACTION_VIEW, theUri);
        startActivity(launchBrowserIntent);
    }

    private void launchWebView(String url) {
        Activity activity = getActivity();

        if (activity != null) {
            Intent launchWebViewIntent = new Intent(getActivity().getApplicationContext(), HelpWebViewActivity.class);
            launchWebViewIntent.putExtra("url", url);
            startActivity(launchWebViewIntent);
        }
    }

    private void noNetworkConnectionNotify() {
        FragmentManager manager = getFragmentManager();
        NoNetworkConnectionDialogFragment fragment = new NoNetworkConnectionDialogFragment();

        if (manager != null) {
            fragment.show(manager, "no_net_conn");
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_help_ok:
                Activity activity = getActivity();

                if (activity != null) {
                    activity.finishFromChild(getActivity());
                }

                break;
            case R.id.button_lookup_wikipedia:
                if (hasNetworkConnection()) {
                    launchBrowser("https://en.wikipedia.org/wiki/Tic-tac-toe");
                } else {
                    noNetworkConnectionNotify();
                }
                break;
            case R.id.button_lookup_wikipedia_in_web_view:
                if (hasNetworkConnection()) {
                    launchWebView("https://en.wikipedia.org/wiki/Tic-tac-toe");
                } else {
                    noNetworkConnectionNotify();
                }
                break;
        }
    }
}
