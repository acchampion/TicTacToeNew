package com.wiley.fordummies.androidsdk.tictactoe.model;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class QueryPreferences {

	private static final String PREF_SEARCH_QUERY = "searchQuery";
	private static final String PREF_LAST_RESULT_ID = "lastResultId";
	private static final String PREF_IS_POLLING = "isPolling";

	public static String getStoredQuery(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString(PREF_SEARCH_QUERY, "");
	}

	public static void setStoredQuery(Context context, String query) {
		PreferenceManager.getDefaultSharedPreferences(context)
				.edit()
				.putString(PREF_SEARCH_QUERY, query)
				.apply();
	}

	public static String getLastResultId(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getString(PREF_LAST_RESULT_ID, "");
	}

	public static void setLastResultId(Context context, String lastResultId) {
		PreferenceManager.getDefaultSharedPreferences(context)
				.edit()
				.putString(PREF_LAST_RESULT_ID, lastResultId)
				.apply();
	}

	public static boolean isPolling(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean(PREF_IS_POLLING, false);
	}

	public static void setPolling(Context context, boolean isOn) {
		PreferenceManager.getDefaultSharedPreferences(context)
				.edit()
				.putBoolean(PREF_IS_POLLING, isOn)
				.apply();
	}
}
