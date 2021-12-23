package com.wiley.fordummies.androidsdk.tictactoe.ui.activity;

import androidx.fragment.app.Fragment;

import com.wiley.fordummies.androidsdk.tictactoe.ui.fragment.SensorsFragment;

/**
 * Created by adamcchampion on 2017/08/14.
 */

public class SensorsActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new SensorsFragment();
    }
}
