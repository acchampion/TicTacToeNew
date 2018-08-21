package com.wiley.fordummies.androidsdk.tictactoe;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * Created by adamcchampion on 2017/08/19.
 */

public class GameSessionActivity extends SingleFragmentActivity {
    private GameSessionFragment mGameSessionFragment;

    @Override
    protected Fragment createFragment() {
        recoverFragment();
        return mGameSessionFragment;
    }

    public void humanTakesATurn(int posX, int posY) {
        recoverFragment();
        mGameSessionFragment.humanTakesATurn(posX, posY);
    }

    private void recoverFragment() {
        if (mGameSessionFragment == null) {
            // Did we already create a fragment? If so, pick the last created fragment where
            // the Tic-Tac-Toe game is being played.
            FragmentManager fm = getSupportFragmentManager();
            if (fm.getFragments().size() == 0) {
                mGameSessionFragment = new GameSessionFragment();
            }
            else {
                for (Fragment fragment: fm.getFragments()) {
                    if (fragment instanceof GameSessionFragment) {
                        mGameSessionFragment = (GameSessionFragment) fragment;
                    }
                }
            }
        }
    }
}
