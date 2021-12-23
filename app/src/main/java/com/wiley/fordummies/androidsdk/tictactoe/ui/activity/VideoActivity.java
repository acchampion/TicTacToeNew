package com.wiley.fordummies.androidsdk.tictactoe.ui.activity;

import androidx.fragment.app.Fragment;

import com.wiley.fordummies.androidsdk.tictactoe.ui.fragment.VideoFragment;

/**
 * Created by adamcchampion on 2017/08/12.
 */

public class VideoActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new VideoFragment();
    }
}
