package com.wiley.fordummies.androidsdk.tictactoe.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.wiley.fordummies.androidsdk.tictactoe.Game;
import com.wiley.fordummies.androidsdk.tictactoe.GameGrid;
import com.wiley.fordummies.androidsdk.tictactoe.R;
import com.wiley.fordummies.androidsdk.tictactoe.Square;
import com.wiley.fordummies.androidsdk.tictactoe.Symbol;
import com.wiley.fordummies.androidsdk.tictactoe.model.Settings;

import java.util.ArrayList;
import java.util.Random;

import timber.log.Timber;

/**
 * Fragment where user plays Tic-Tac-Toe.
 * <p>
 * Created by adamcchampion on 2017/08/19.
 */
public class GameSessionFragment extends Fragment {
	private static final int ANDROID_TIMEOUT_BASE = 500;
	private static final int ANDROID_TIMEOUT_SEED = 2000;

	private Board mBoard;
	public Game mActiveGame;
	private GameView mGameView;
	private int mScorePlayerOne = 0;
	private int mScorePlayerTwo = 0;
	private String mFirstPlayerName;
	private String mSecondPlayerName;

	private ViewGroup mContainer;
	private Bundle mSavedInstanceState;

	private static final String SCOREPLAYERONEKEY = "ScorePlayerOne";
	private static final String SCOREPLAYERTWOKEY = "ScorePlayerTwo";
	private static final String GAMEKEY = "Game";
	// private static final String TAG = GameSessionFragment.class.getSimpleName();

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Timber.d("onCreateView()");
		View v;

		Activity activity = requireActivity();
		int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
		if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
			v = inflater.inflate(R.layout.fragment_gamesession_land, container, false);
		} else {
			v = inflater.inflate(R.layout.fragment_game_session, container, false);
		}

		mContainer = container;
		setRetainInstance(true);

		if (savedInstanceState != null) {
			mScorePlayerOne = savedInstanceState.getInt(SCOREPLAYERONEKEY);
			mScorePlayerTwo = savedInstanceState.getInt(SCOREPLAYERTWOKEY);
		}

		setupBoard(v);

		setHasOptionsMenu(true);

		return v;
	}

	private void setupBoard(View v) {
		mBoard = v.findViewById(R.id.board);
		TextView turnStatusView = v.findViewById(R.id.gameInfo);
		TextView scoreView = v.findViewById(R.id.scoreboard);
		mActiveGame = new Game();
		GameGrid gameGrid = mActiveGame.getGameGrid();

		mBoard.setGrid(gameGrid);
		mGameView = new GameView();
		mGameView.setGameViewComponents(mBoard, turnStatusView, scoreView);
		setPlayers(mActiveGame);
		mGameView.showScores(mActiveGame.getPlayerOneName(), mScorePlayerOne, mActiveGame.getPlayerTwoName(), mScorePlayerTwo);
		mGameView.setGameStatus(mActiveGame.getCurrentPlayerName() + " to play.");
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mSavedInstanceState = savedInstanceState;
	}

	@Override
	public void onResume() {
		super.onResume();
		Timber.d("onResume()");
		try {
			AppCompatActivity activity = (AppCompatActivity) requireActivity();
			ActionBar actionBar = activity.getSupportActionBar();
			if (actionBar != null) {
				actionBar.setSubtitle(getResources().getString(R.string.game));
			}

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
				activity.setShowWhenLocked(true);
			} else {
				activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
				activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
			}
		} catch (NullPointerException npe) {
			Timber.e("Could not set subtitle");
		}

		playNewGame();
	}

	@Override
	public void onStop() {
		super.onStop();
		Timber.d("onStop()");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Timber.d("onDestroyView()");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Timber.d("onDestroy()");
	}

	private void playNewGame() {
		// If Android is the first player, give it its turn
		if (mActiveGame.getCurrentPlayerName().equals("Android")) scheduleAndroidsTurn();
	}

	private void setPlayers(Game theGame) {
		Activity activity = requireActivity();
		if (Settings.doesHumanPlayFirst(activity)) {
			mFirstPlayerName = Settings.getName(activity);
			mSecondPlayerName = "Android";
		} else {
			mFirstPlayerName = "Android";
			mSecondPlayerName = Settings.getName(activity);
		}
		theGame.setPlayerNames(mFirstPlayerName, mSecondPlayerName);
	}

	public void scheduleAndroidsTurn() {
		Timber.d("Thread ID in scheduleAndroidsTurn: %s", Thread.currentThread().getId());
		mBoard.disableInput();
		boolean testMode = false;
		if (!testMode) {
			Random randomNumber = new Random();
			Handler handler = new Handler();
			handler.postDelayed(
					this::androidTakesATurn,
					ANDROID_TIMEOUT_BASE + randomNumber.nextInt(ANDROID_TIMEOUT_SEED)
			);
		} else {
			androidTakesATurn();
		}
	}

	private void androidTakesATurn() {
		int pickedX, pickedY;
		Timber.d("Thread ID in androidTakesATurn: %s", Thread.currentThread().getId());

		GameGrid gameGrid = mActiveGame.getGameGrid();
		ArrayList<Square> emptyBlocks = gameGrid.getEmptySquares();
		int n = emptyBlocks.size();
		Random r = new Random();
		int randomIndex = r.nextInt(n);
		Square picked = emptyBlocks.get(randomIndex);
		mActiveGame.play(pickedX = picked.getX(), pickedY = picked.getY());
		mGameView.placeSymbol(pickedX, pickedY);
		mBoard.enableInput();
		if (mActiveGame.isActive()) {
			mGameView.setGameStatus(mActiveGame.getCurrentPlayerName() + " to play.");
		} else {
			proceedToFinish();
		}
	}

	void humanTakesATurn(int x, int y) {/* human's turn */
		Timber.d("Thread ID in humanTakesATurn: %s", Thread.currentThread().getId());
		boolean successfulPlay = mActiveGame.play(x, y);
		if (successfulPlay) {
			mGameView.placeSymbol(x, y); /* Update the display */
			if (mActiveGame.isActive()) {
				mGameView.setGameStatus(mActiveGame.getCurrentPlayerName() + " to play.");
				scheduleAndroidsTurn();
			} else {
				proceedToFinish();
			}
		}
	}

	private void quitGame() {
		AppCompatActivity activity = (AppCompatActivity) requireActivity();
		FragmentManager fm = activity.getSupportFragmentManager();
		AbandonGameDialogFragment abandonGameDialogFragment = new AbandonGameDialogFragment();
		abandonGameDialogFragment.show(fm, "abandon_game");
	}

	private void proceedToFinish() {
		String winningPlayerName;
		String alertMessage;
		if (mActiveGame.isWon()) {
			winningPlayerName = mActiveGame.getWinningPlayerName();
			alertMessage = winningPlayerName + " Wins!";
			mGameView.setGameStatus(alertMessage);
			accumulateScores(winningPlayerName);

			mGameView.showScores(mFirstPlayerName, mScorePlayerOne, mSecondPlayerName, mScorePlayerTwo);

		} else if (mActiveGame.isDrawn()) {
			alertMessage = "DRAW!";
			mGameView.setGameStatus(alertMessage);
		} else {
			// Control flow should never reach this block, but if it does, show a default text string.
			alertMessage = "Info";
		}
		final Activity activity = requireActivity();
		new AlertDialog.Builder(activity)
				.setTitle(alertMessage)
				.setMessage("Play another game?")
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton("Yes", (dialog, which) -> {
					LayoutInflater inflater = LayoutInflater.from(activity);
					if (mContainer != null) {
						Timber.d("Calling setupBoard() again");
						onSaveInstanceState(mSavedInstanceState);
						activity.recreate();
						onCreateView(inflater, mContainer, mSavedInstanceState);
					}

					if (mBoard != null) {
						Symbol blankSymbol = Symbol.SymbolBlankCreate();
						for (int x = 0; x < GameGrid.SIZE; x++) {
							for (int y = 0; y < GameGrid.SIZE; y++) {
								mActiveGame.getGameGrid().setValueAtLocation(x, y, blankSymbol);
							}
						}
					}
					playNewGame();
				})
				.setNegativeButton("No", (dialog, which) -> activity.finish())
				.show();

	}

	private void accumulateScores(String winningPlayerName) {
		if (winningPlayerName.equals(mFirstPlayerName))
			mScorePlayerOne++;
		else
			mScorePlayerTwo++;
	}

	private void sendScoresViaEmail() {
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
				"Look at my AWESOME TicTacToe Score!");
		emailIntent.setType("plain/text");
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
				mFirstPlayerName + " score is  " + mScorePlayerOne +
						" and " +
						mSecondPlayerName + " score is  " + mScorePlayerTwo);
		startActivity(emailIntent);
	}

	private void sendScoresViaSMS() {
		Intent SMSIntent = new Intent(Intent.ACTION_VIEW);
		SMSIntent.putExtra("sms_body",
				"Look at my AWESOME TicTacToe Score!" +
						mFirstPlayerName + " score is  " + mScorePlayerOne +
						" and " +
						mSecondPlayerName + " score is  " + mScorePlayerTwo);
		SMSIntent.setType("vnd.android-dir/mms-sms");
		startActivity(SMSIntent);
	}

	private void callTicTacToeHelp() {
		Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
		String phoneNumber = "842-822-4357"; // TIC TAC HELP
		String uri = "tel:" + phoneNumber.trim();
		phoneIntent.setData(Uri.parse(uri));
		startActivity(phoneIntent);
	}

	@Override
	public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_ingame, menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		final int itemId = item.getItemId();
		final Activity activity = requireActivity();
		final Context appContext = activity.getApplicationContext();

		if (itemId == R.id.menu_help) {
			startActivity(new Intent(appContext, HelpActivity.class));
			return true;
		} else if (itemId == R.id.menu_exit) {
			quitGame();
			return true;
		} else if (itemId == R.id.menu_email) {
			sendScoresViaEmail();
			return true;
		} else if (itemId == R.id.menu_sms) {
			sendScoresViaSMS();
			return true;
		} else if (itemId == R.id.menu_call) {
			callTicTacToeHelp();
			return true;
		} else {
			Timber.e("Invalid menu item selected");
		}

		return false;
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		Bundle instanceState;

		if (outState == null) {
			instanceState = new Bundle();
		} else {
			instanceState = outState;
		}
		// Save session score
		instanceState.putInt(SCOREPLAYERONEKEY, mScorePlayerOne);
		instanceState.putInt(SCOREPLAYERTWOKEY, mScorePlayerTwo);
		// Save turn
		instanceState.putString(GAMEKEY, mActiveGame.toString());
		//Save board
	}

	public int getPlayCount() {
		int playCount = 0;

		if (mActiveGame != null) {
			playCount = mActiveGame.getPlayCount();
		}

		return playCount;
	}
}
