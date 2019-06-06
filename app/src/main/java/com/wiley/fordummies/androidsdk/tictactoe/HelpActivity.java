package com.wiley.fordummies.androidsdk.tictactoe;

import androidx.fragment.app.Fragment;

/**
 * Created by adamcchampion on 2017/08/14.
 */

public class HelpActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new HelpFragment();
    }
}
