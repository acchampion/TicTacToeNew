package com.wiley.fordummies.androidsdk.tictactoe;

import androidx.fragment.app.Fragment;

/**
 * Activity for user to select options.
 *
 * Created by adamcchampion on 2017/08/05.
 */

public class GameOptionsActivity extends SingleFragmentActivity {
    protected final String TAG = getClass().getSimpleName();

    @Override
    protected Fragment createFragment() {
        return new GameOptionsFragment();
    }
}
