package com.wiley.fordummies.androidsdk.tictactoe;

import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.view.MotionEvent;
import android.view.View;

import org.junit.Test;

/**
 * Test that Tic-Tac-Toe's Board UI works.
 * <p>
 * Source: https://stackoverflow.com/questions/30908969/android-writing-test-cases-for-fragments
 * <p>
 * Created by adamcchampion on 2017/08/20.
 */

public class GameSessionFragmentTest extends ActivityInstrumentationTestCase2<GameSessionActivity> {
    private GameSessionActivity mGameSessionActivity;
    private GameSessionFragment mGameSessionFragment;
    private Board mBoard;

    private final float x[] = {(float) 56.0, (float) 143.0, (float) 227.0};
    private final float y[] = {(float) 56.0, (float) 143.0, (float) 227.0};

    public GameSessionFragmentTest() {
        super(GameSessionActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mGameSessionActivity = getActivity();
        mGameSessionFragment = new GameSessionFragment();
        mGameSessionActivity.getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, mGameSessionFragment, null)
                .commit();

        // Wait for the Activity to become idle so we don't have null Fragment references.
        getInstrumentation().waitForIdleSync();

        setActivityInitialTouchMode(false);
        if (mGameSessionFragment != null) {
            View fragmentView = mGameSessionFragment.getView();
            if (fragmentView != null) {
                mBoard = fragmentView.findViewById(R.id.board);
                mGameSessionFragment.mActiveGame = new Game();
            }
        }
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
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
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
                assertEquals(mGameSessionFragment.getPlayCount(), 0);
            }
        });
    }

    @UiThreadTest
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
        assertEquals(mGameSessionFragment.getPlayCount(), 0);
    }

    protected void tearDown() throws Exception {
        mGameSessionActivity.finish();
        super.tearDown();
    }
}
