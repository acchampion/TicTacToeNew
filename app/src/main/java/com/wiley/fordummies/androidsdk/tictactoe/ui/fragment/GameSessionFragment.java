package com.wiley.fordummies.androidsdk.tictactoe.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.rxjava3.RxDataStore;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.wiley.fordummies.androidsdk.tictactoe.Game;
import com.wiley.fordummies.androidsdk.tictactoe.GameGrid;
import com.wiley.fordummies.androidsdk.tictactoe.R;
import com.wiley.fordummies.androidsdk.tictactoe.Square;
import com.wiley.fordummies.androidsdk.tictactoe.Symbol;
import com.wiley.fordummies.androidsdk.tictactoe.model.Settings;
import com.wiley.fordummies.androidsdk.tictactoe.model.SettingsDataStoreHelper;
import com.wiley.fordummies.androidsdk.tictactoe.model.SettingsDataStoreSingleton;
import com.wiley.fordummies.androidsdk.tictactoe.ui.Board;
import com.wiley.fordummies.androidsdk.tictactoe.ui.GameView;
import com.wiley.fordummies.androidsdk.tictactoe.ui.activity.HelpActivity;

import java.util.List;
import java.util.Locale;
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

	private boolean mIsTestMode;
	private Board mBoard;
	public Game mActiveGame;
	private GameView mGameView;
	private int mScorePlayerOne = 0;
	private int mScorePlayerTwo = 0;
	private String mFirstPlayerName;
	private String mSecondPlayerName;

	private ViewGroup mContainer;
	private Bundle mSavedInstanceState;

	private SettingsDataStoreSingleton mDataStoreSingleton;
	private SettingsDataStoreHelper mDataStoreHelper;

	private final String TAG = getClass().getSimpleName();

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mDataStoreSingleton = SettingsDataStoreSingleton.getInstance(requireContext().getApplicationContext());
		RxDataStore<Preferences> mDataStore = mDataStoreSingleton.getDataStore();
		mDataStoreHelper = new SettingsDataStoreHelper(mDataStore);
		mIsTestMode = mDataStoreHelper.getBoolean("is_test_mode", false);
	}

	public void setTestMode(boolean testMode) {
		mDataStoreHelper.putBoolean("is_test_mode", testMode);
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Timber.tag(TAG).d("onCreateView()");
		View v;

		Activity activity = requireActivity();
		int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
		v = inflater.inflate(R.layout.fragment_game_session, container, false);

		mContainer = container;
		setRetainInstance(true);

		loadGameScores();

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
		mGameView.showScores(mActiveGame.getPlayerOneName(), mScorePlayerOne,
				mActiveGame.getPlayerTwoName(), mScorePlayerTwo);
		mGameView.setGameStatus(mActiveGame.getCurrentPlayerName() + " to play.");
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (savedInstanceState != null) {
			mSavedInstanceState = savedInstanceState;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		Timber.tag(TAG).d("onResume()");
		try {
			AppCompatActivity activity = (AppCompatActivity) requireActivity();
			ActionBar actionBar = activity.getSupportActionBar();
			if (actionBar != null) {
				actionBar.setSubtitle(getResources().getString(R.string.game));
			}

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
				activity.setShowWhenLocked(true);
			}
		} catch (NullPointerException npe) {
			Timber.tag(TAG).e("Could not set subtitle");
		}

		playNewGame();
	}

	@Override
	public void onStop() {
		super.onStop();
		Timber.tag(TAG).d("onStop()");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Timber.tag(TAG).d("onDestroyView()");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Timber.tag(TAG).d("onDestroy()");
	}

	private void playNewGame() {
		// If Android is the first player, give it its turn
		if (mActiveGame.getCurrentPlayerName().equals("Android")) {
			scheduleAndroidsTurn(mIsTestMode);
		}
	}

	private void setPlayers(Game theGame) {
		boolean humanPlaysFirst = mDataStoreHelper.getBoolean(Settings.Keys.OPT_PLAY_FIRST, Settings.Keys.OPT_PLAY_FIRST_DEF);

		if (humanPlaysFirst) {
			mFirstPlayerName = mDataStoreHelper.getString(Settings.Keys.OPT_NAME, Settings.Keys.OPT_NAME_DEFAULT);
			mSecondPlayerName = "Android";
		} else {
			mFirstPlayerName = "Android";
			mSecondPlayerName = mDataStoreHelper.getString(Settings.Keys.OPT_NAME, Settings.Keys.OPT_NAME_DEFAULT);
		}
		theGame.setPlayerNames(mFirstPlayerName, mSecondPlayerName);
	}

	public void scheduleAndroidsTurn(boolean isTestMode) {
		Timber.tag(TAG).d("Thread ID in scheduleAndroidsTurn: %s", Thread.currentThread().getId());
		mBoard.disableInput();
		if (!isTestMode) {
			Random randomNumber = new Random();
			Handler handler = new Handler(Looper.getMainLooper());
			handler.postDelayed(
					this::androidTakesATurn,
					ANDROID_TIMEOUT_BASE + randomNumber.nextInt(ANDROID_TIMEOUT_SEED)
			);
		} else {
			Timber.tag(TAG).d("In else() block, scheduleAndroidsTurn()");
			androidTakesATurn();
		}
	}

	private void androidTakesATurn() {
		int pickedX, pickedY;
		Timber.tag(TAG).d("Thread ID in androidTakesATurn: %s", Thread.currentThread().getId());

		GameGrid gameGrid = mActiveGame.getGameGrid();
		List<Square> emptyBlocks = gameGrid.getEmptySquares();
		int n = emptyBlocks.size();
		Random r = new Random();
		int randomIndex = r.nextInt(n);
		Square picked = emptyBlocks.get(randomIndex);
		mActiveGame.play(pickedX = picked.getX(), pickedY = picked.getY());
		mGameView.placeSymbol(pickedX);
		mBoard.enableInput();
		if (mActiveGame.isActive()) {
			mGameView.setGameStatus(mActiveGame.getCurrentPlayerName() + " to play.");
		} else {
			proceedToFinish();
		}
	}

	public void humanTakesATurn(int x, int y) {/* human's turn */
		Timber.tag(TAG).d("Thread ID in humanTakesATurn: %s", Thread.currentThread().getId());
		boolean successfulPlay = mActiveGame.play(x, y);
		if (successfulPlay) {
			mGameView.placeSymbol(x); /* Update the display */
			if (mActiveGame.isActive()) {
				mGameView.setGameStatus(mActiveGame.getCurrentPlayerName() + " to play.");
				scheduleAndroidsTurn(mIsTestMode);
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

	private void loadGameScores() {
		mScorePlayerOne = mDataStoreHelper.getInt(Settings.Keys.SCOREPLAYERONEKEY, 0);
		mScorePlayerTwo = mDataStoreHelper.getInt(Settings.Keys.SCOREPLAYERTWOKEY, 0);
	}

	private void saveGameScores() {
		String gameStr = "";
		if (mActiveGame != null) {
			gameStr = mActiveGame.toString();
		}

		Timber.tag(TAG).d("Player 1 score: %d; player 2 score: %d", mScorePlayerOne, mScorePlayerTwo);
		if (mDataStoreHelper.putInt(Settings.Keys.SCOREPLAYERONEKEY, mScorePlayerOne)) {
			Timber.tag(TAG).i("Wrote Player 1 score %d successfully to DataStore", mScorePlayerOne);
		} else {
			Timber.tag(TAG).e("Error writing Player 1 score to DataStore");
		}

		if (mDataStoreHelper.putInt(Settings.Keys.SCOREPLAYERTWOKEY, mScorePlayerTwo)) {
			Timber.tag(TAG).i("Wrote Player 2 score %d successfully to DataStore", mScorePlayerTwo);
		} else {
			Timber.tag(TAG).e("Error writing Player 2 score to DataStore");
		}

		Timber.tag(TAG).d("Game string: %s", gameStr);
		if (mDataStoreHelper.putString(Settings.Keys.GAMEKEY, gameStr)) {
			Timber.tag(TAG).i("Wrote game string %s to DataStore", gameStr);
		} else {
			Timber.tag(TAG).e("Error writing game string to DataStore");
		}

	}

	private void proceedToFinish() {
		String winningPlayerName;
		String alertMessage;
		if (mActiveGame.isWon()) {
			winningPlayerName = mActiveGame.getWinningPlayerName();
			alertMessage = winningPlayerName + " Wins!";
			mGameView.setGameStatus(alertMessage);
			accumulateScores(winningPlayerName);
			saveGameScores();

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
					Timber.tag(TAG + " PlayGameDialogYes ").d("Saving game scores");
					saveGameScores();
					LayoutInflater inflater = LayoutInflater.from(activity);
					if (mContainer != null) {
						Timber.tag(TAG).d("Calling setupBoard() again");
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
				.setNegativeButton("No", (dialog, which) -> {
					mScorePlayerOne = 0;
					mScorePlayerTwo = 0;
					mActiveGame = null;
					Timber.tag(TAG + " PlayGameDialogNo ").d("Saving game scores: zeroes");
					saveGameScores();
					activity.finish();
				})
				.show();

	}

	private void accumulateScores(String winningPlayerName) {
		Timber.tag(TAG).d("Scores: Player 1: %d; Player 2: %d", mScorePlayerOne, mScorePlayerTwo);
		if (winningPlayerName.equals(mFirstPlayerName))
			mScorePlayerOne++;
		else
			mScorePlayerTwo++;
	}

	private void sendScoresViaEmail() {
		String emailText = String.format(Locale.US, "%s score is %d and %s score is %d",
				mFirstPlayerName, mScorePlayerOne, mSecondPlayerName, mScorePlayerTwo);
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
				"Look at my AWESOME TicTacToe Score!");
		emailIntent.setType("plain/text");
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, emailText);
		startActivity(emailIntent);
	}

	private void sendScoresViaSms() {
		String smsText = String.format(Locale.US, "Look at my AWESOME Tic-Tac-Toe score! " +
				"%s score is %d and %s score is %d", mFirstPlayerName, mScorePlayerOne,
				mSecondPlayerName, mScorePlayerOne);
		Intent smsIntent = new Intent(Intent.ACTION_VIEW);
		smsIntent.putExtra("sms_body", smsText);
		smsIntent.setType("vnd.android-dir/mms-sms");
		startActivity(smsIntent);
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
			sendScoresViaSms();
			return true;
		} else if (itemId == R.id.menu_call) {
			callTicTacToeHelp();
			return true;
		} else {
			Timber.tag(TAG).e("Invalid menu item selected");
		}

		return false;
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	public int getPlayCount() {
		int playCount = 0;

		if (mActiveGame != null) {
			playCount = mActiveGame.getPlayCount();
		}

		return playCount;
	}

	public void resetPlayCount() {
		if (mActiveGame != null) {
			mActiveGame.resetPlayCount();
		}
	}
}
