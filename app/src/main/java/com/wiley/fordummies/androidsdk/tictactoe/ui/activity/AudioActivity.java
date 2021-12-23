package com.wiley.fordummies.androidsdk.tictactoe.ui.activity;

import androidx.fragment.app.Fragment;

import com.wiley.fordummies.androidsdk.tictactoe.ui.fragment.AudioFragment;

/**
 * Activity for handling audio.
 *
 * Created by adamcchampion on 2017/08/12.
 */

public class AudioActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new AudioFragment();
    }
}
