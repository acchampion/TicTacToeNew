package com.wiley.fordummies.androidsdk.tictactoe.ui.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.wiley.fordummies.androidsdk.tictactoe.BuildConfig;
import com.wiley.fordummies.androidsdk.tictactoe.R;

public class MapsActivity extends AppCompatActivity {

	private PermissionsManager mPermissionsManager;
	private MapView mMapView;

	private EditText mEditLocation;
	private String mWhereAmIString = "";

	private static final String WHERE_AM_I_STRING = "WhereAmIString";

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Mapbox access token is configured here. This needs to be called either in your application
		// object or in the same activity which contains the MapView.
		Mapbox.getInstance(this, BuildConfig.MapboxAccessToken);

		setContentView(R.layout.activity_maps);

		mMapView = findViewById(R.id.map_view);
		if (mMapView != null) {
			mMapView.onCreate(savedInstanceState);
		}

		mEditLocation = findViewById(R.id.location);

		Button buttonLocate = findViewById(R.id.button_locate);
		// buttonLocate.setOnClickListener(this);

		Button buttonFindMe = findViewById(R.id.button_findme);
		// buttonFindMe.setOnClickListener(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		mMapView.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
		mMapView.onStop();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mMapView.onLowMemory();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
	}
}
