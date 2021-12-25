package com.wiley.fordummies.androidsdk.tictactoe.model;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class QueryPreferences {

	private static final String PREF_SEARCH_QUERY = "searchQuery";

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
}
