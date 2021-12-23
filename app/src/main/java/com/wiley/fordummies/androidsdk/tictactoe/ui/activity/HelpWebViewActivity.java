package com.wiley.fordummies.androidsdk.tictactoe.ui.activity;

import androidx.fragment.app.Fragment;

import com.wiley.fordummies.androidsdk.tictactoe.ui.fragment.HelpWebViewFragment;

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
