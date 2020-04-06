package com.wiley.fordummies.androidsdk.tictactoe;

import android.view.MotionEvent;
import android.view.View;

import androidx.test.annotation.UiThreadTest;
import androidx.test.rule.ActivityTestRule;

import com.wiley.fordummies.androidsdk.tictactoe.ui.Board;
import com.wiley.fordummies.androidsdk.tictactoe.ui.GameSessionActivity;
import com.wiley.fordummies.androidsdk.tictactoe.ui.GameSessionFragment;

import org.junit.Test;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test that Tic-Tac-Toe's Board UI works.
 * <p>
 * Source: https://stackoverflow.com/questions/30908969/android-writing-test-cases-for-fragments
 * <p>
 * Created by adamcchampion on 2017/08/20.
 */

public class GameSessionActivityTest extends ActivityTestRule<GameSessionActivity> {
    private GameSessionActivity mGameSessionActivity;
    private GameSessionFragment mGameSessionFragment;
    private Board mBoard;

    private final float[] x = {(float) 56.0, (float) 143.0, (float) 227.0};
    private final float[] y = {(float) 56.0, (float) 143.0, (float) 227.0};

    public GameSessionActivityTest() {
        super(GameSessionActivity.class);

        launchActivity(getActivityIntent());
        mGameSessionActivity = getActivity();
        mGameSessionFragment = mGameSessionActivity.getFragmentForTest();

        // Wait for the Activity to become idle so we don't have null Fragment references.
        getInstrumentation().waitForIdleSync();

        if (mGameSessionFragment != null) {
            View fragmentView = mGameSessionFragment.getView();
            if (fragmentView != null) {
                mBoard = fragmentView.findViewById(R.id.board);
                mGameSessionFragment.mActiveGame = new Game();
            }
        }
    }

    @Override
    protected void beforeActivityLaunched() {
        super.beforeActivityLaunched();


    }

    @Test
    public void testPreconditions() {
        assertNotNull(mGameSessionActivity);
        assertNotNull(mGameSessionFragment);
        assertNotNull(mBoard);
    }

    @Test
    public void testUI() {
        System.out.println("Thread ID in testUI:" + Thread.currentThread().getId());
        getInstrumentation().waitForIdleSync();
        getActivity().runOnUiThread((Runnable) () -> {
			System.out.println("Thread ID in TestUI.run:" + Thread.currentThread().getId());

			mBoard.requestFocus();

			MotionEvent newMotionEvent = MotionEvent.obtain((long) 1,
				(long) 1,
				MotionEvent.ACTION_DOWN,
				(float) 53.0,
				(float) 53.0,
				0);
			mBoard.dispatchTouchEvent(newMotionEvent);
			mGameSessionFragment.scheduleAndroidsTurn();
			assertEquals(mGameSessionFragment.getPlayCount(), 1);
		});
    }

    @UiThreadTest
    @Test
    public void testUIThreadTest() {
        System.out.println("Thread ID in testUI:" + Thread.currentThread().getId());
        mBoard.requestFocus();
        int i;
        for (i = 0; i < 3; i++) {
            MotionEvent newMotionEvent = MotionEvent.obtain((long) 1,
                    (long) 1,
                    MotionEvent.ACTION_DOWN,
                    x[i],
                    y[i],
                    0);
            mBoard.dispatchTouchEvent(newMotionEvent);
        }
        assertEquals(mGameSessionFragment.getPlayCount(), 1);
    }

    protected void afterActivityFinished() {
        super.afterActivityFinished();
        if (!getActivity().isFinishing()) {
            mGameSessionActivity.finish();
        }
    }
}
