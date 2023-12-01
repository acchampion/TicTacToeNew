package com.wiley.fordummies.androidsdk.tictactoe.model;

/**
 * Class for reading settings from the process's DataStore.
 *
 * Created by adamcchampion on 2017/08/14.
 */

public class Settings {
	public final static String NAME = "settings.db";
	public final static String ERROR = "error";

	public static class Keys {
		public final static String OPT_NAME = "name";
		public static final String OPT_NAME_DEFAULT = "";
		public final static String OPT_PLAY_FIRST = "human_starts";
		public final static boolean OPT_PLAY_FIRST_DEF = true;
		public static final String SCOREPLAYERONEKEY = "ScorePlayerOne";
		public static final String SCOREPLAYERTWOKEY = "ScorePlayerTwo";
		public static final String GAMEKEY = "Game";
		public static final String PREF_SEARCH_QUERY = "searchQuery";
		public static final String PREF_LAST_RESULT_ID = "lastResultId";
		public static final String PREF_IS_POLLING = "isPolling";

	}
}
