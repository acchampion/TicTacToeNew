package com.wiley.fordummies.androidsdk.tictactoe.ui.activity;

import androidx.fragment.app.Fragment;

import com.wiley.fordummies.androidsdk.tictactoe.ui.fragment.ImagesFragment;

/**
 * Activity for showing and hosting images.
 *
 * Created by adamcchampion on 2017/08/12.
 */

public class ImagesActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new ImagesFragment();
    }
}
