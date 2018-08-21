package com.wiley.fordummies.androidsdk.tictactoe;

import android.support.v4.app.Fragment;

/**
 * Activity to show a WebView for Tic-Tac-Toe on Wikipedia.
 *
 * Created by adamcchampion on 2017/08/14.
 */

public class HelpWebViewActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new HelpWebViewFragment();
    }
}
