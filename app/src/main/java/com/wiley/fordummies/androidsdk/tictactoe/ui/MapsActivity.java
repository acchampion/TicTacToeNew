package com.wiley.fordummies.androidsdk.tictactoe.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.maps.UiSettings;
import com.mapbox.services.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.services.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.services.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.services.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.services.commons.models.Position;
import com.mapzen.android.lost.api.LocationRequest;
import com.wiley.fordummies.androidsdk.tictactoe.BuildConfig;
import com.wiley.fordummies.androidsdk.tictactoe.R;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Activity showing user's location on Mapbox Map.
 * <p>
 * Source: Mapbox, https://github.com/mapbox/mapbox-android-demo (LocationPluginActivity)
 * <p>
 * Created by adamcchampion on 2017/08/17.
 */

public class MapsActivity extends AppCompatActivity implements LocationEngineCallback<LocationEngineResult>,
		PermissionsListener, View.OnClickListener {
	private final int ENABLE_GPS_REQUEST_CODE = 1;

	private PermissionsManager mPermissionsManager;
	private MapView mMapView;
	private MapboxMap mMapboxMap;
	private LocationEngine mLocationEngine;

	private EditText mEditLocation;
	private String whereAmIString = null;

	private static final String WHERE_AM_I_STRING = "WhereAmIString";
	private static final String TAG = MapsActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Mapbox access token is configured here. This needs to be called either in your application
		// object or in the same activity which contains the MapView.
		Mapbox.getInstance(this, BuildConfig.MapboxAccessToken);

		setContentView(R.layout.activity_maps);

		mMapView = findViewById(R.id.map_view);
		mMapView.onCreate(savedInstanceState);
		mMapView.getMapAsync(mapboxMap -> {
			mMapboxMap = mapboxMap;
			mMapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
				UiSettings uiSettings = mMapboxMap.getUiSettings();
				uiSettings.setCompassEnabled(true);
				uiSettings.setZoomGesturesEnabled(true);
				enableLocationComponent();
			});
		});

		mEditLocation = findViewById(R.id.location);

		Button buttonLocate = findViewById(R.id.button_locate);
		buttonLocate.setOnClickListener(this);

		Button buttonFindMe = findViewById(R.id.button_findme);
		buttonFindMe.setOnClickListener(this);
	}

	@SuppressWarnings({"MissingPermission"})
	private void enableLocationComponent() {
		// Check if permissions are enabled and if not request
		if (PermissionsManager.areLocationPermissionsGranted(this)) {
			LocationComponent locationComponent = mMapboxMap.getLocationComponent();
			LocationComponentActivationOptions.Builder builder =
					new LocationComponentActivationOptions.Builder(this,
							Objects.requireNonNull(mMapboxMap.getStyle()));
			builder.useDefaultLocationEngine(true);
			LocationComponentActivationOptions options = builder.build();
			locationComponent.activateLocationComponent(options);
		} else {
			mPermissionsManager = new PermissionsManager(this);
			mPermissionsManager.requestLocationPermissions(this);
		}
	}

	// Add the MapView lifecycle to the activity's lifecycle methods
	@Override
	public void onResume() {
		super.onResume();
		mMapView.onResume();

		try {
			ActionBar actionBar = getSupportActionBar();
			if (actionBar != null) {
				actionBar.setSubtitle(getResources().getString(R.string.where_am_i));
			}
		} catch (NullPointerException npe) {
			Timber.tag(TAG).e("Could not set subtitle");
		}

		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
		int editLocationWidth = displayMetrics.widthPixels - 350;
		if (mEditLocation != null) {
			mEditLocation.setWidth(Math.max(250, editLocationWidth));
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		mMapView.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mLocationEngine != null) {
			mLocationEngine.removeLocationUpdates(this);
		}
		mMapView.onStop();
	}

	@Override
	public void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mMapView.onLowMemory();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
		if (whereAmIString != null)
			outState.putString(WHERE_AM_I_STRING, whereAmIString);
	}

	private void requestLocation() {
		Timber.tag(TAG).d("requestLocation()");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (lacksLocationPermission()) {
				int PERMISSION_REQUEST_LOCATION = 1;
				requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
						PERMISSION_REQUEST_LOCATION);
			} else {
				doRequestLocation();
			}
		} else {
			doRequestLocation();
		}
	}

	@RequiresApi(api = Build.VERSION_CODES.M)
	private boolean lacksLocationPermission() {
		return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED;
	}

	@RequiresApi(api = Build.VERSION_CODES.M)
	private boolean hasLocationPermission() {
		return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
				== PackageManager.PERMISSION_GRANTED;
	}

	private void doRequestLocation() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			mLocationEngine = initializeLocationEngine();
		}
	}

	@SuppressLint("MissingPermission")
	@RequiresApi(api = Build.VERSION_CODES.M)
	private LocationEngine initializeLocationEngine() {
		LocationEngine locationEngine = LocationEngineProvider.getBestLocationEngine(this);
		LocationEngineRequest.Builder locRequestBuilder = new LocationEngineRequest.Builder(60000);
		locRequestBuilder.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
		LocationEngineRequest locRequest = locRequestBuilder.build();

		if (hasLocationPermission()) {
			locationEngine.requestLocationUpdates(locRequest, this, Looper.getMainLooper());
		}

		return locationEngine;
	}

	private void setCameraPosition(Location location) {
		mMapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
				new LatLng(location.getLatitude(), location.getLongitude()), 16));
	}

	@Override
	public void onExplanationNeeded(List<String> permissionsToExplain) {
		Timber.tag(TAG).d("onExplanationNeeded()");
	}

	@Override
	public void onPermissionResult(boolean granted) {
		if (granted) {
			enableLocationComponent();
		} else {
			//FragmentManager fragmentManager = getSupportFragmentManager();
			//LocationDeniedDialogFragment deniedDialogFragment = new LocationDeniedDialogFragment();
			//deniedDialogFragment.show(fragmentManager, "location_denied");
			Timber.tag(TAG).e("User denied permission to get location");
			Toast.makeText(getApplicationContext(), R.string.location_permission_denied, Toast.LENGTH_LONG).show();
			finish();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (mPermissionsManager != null) {
			mPermissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);

			// Direct users to turn on GPS (and send them to Location Settings if it's off).
			// Source: https://stackoverflow.com/questions/843675/how-do-i-find-out-if-the-gps-of-an-android-device-is-enabled
			final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			if (manager != null) {
				if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
					Toast.makeText(getApplicationContext(), "Please turn on GPS in the Settings app", Toast.LENGTH_LONG).show();
					Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivityForResult(intent, ENABLE_GPS_REQUEST_CODE);
				}
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// Fetch location if user allowed it.
		if (requestCode == ENABLE_GPS_REQUEST_CODE) {
			requestLocation();
		}
	}

	@Override
	public void onClick(View view) {
		final int viewId = view.getId();

		if (viewId == R.id.button_locate) {
			try {
				String locationName = mEditLocation.getText().toString();
				MapboxGeocoding mapboxGeocoding = new MapboxGeocoding.Builder()
						.setAccessToken(Mapbox.getAccessToken())
						.setLocation(locationName)
						.setGeocodingType(GeocodingCriteria.TYPE_ADDRESS)
						.build();

				mapboxGeocoding.enqueueCall(new Callback<>() {
					@Override
					public void onResponse(@NonNull Call<GeocodingResponse> call, @NonNull Response<GeocodingResponse> response) {
						//if (response != null) {
						GeocodingResponse responseBody = response.body();
						if (responseBody != null) {
							List<CarmenFeature> results = responseBody.getFeatures();
							if (results != null && results.size() > 0) {
								// Log the first results position.
								Position firstResultPos = results.get(0).asPosition();
								Timber.tag(TAG).d("onResponse: %s", firstResultPos.toString());


								if (mMapboxMap != null) {
									LatLng latLng = new LatLng(firstResultPos.getLatitude(), firstResultPos.getLongitude());

									CameraPosition cameraPosition = new CameraPosition.Builder()
											.target(latLng)
											.zoom(15.0)
											.build();
									mMapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
								}
							} else {
								// No result for your request were found.
								Timber.tag(TAG).d("onResponse: No result found");
								Toast.makeText(MapsActivity.this, "No results found.", Toast.LENGTH_SHORT).show();
							}
						}
						//}
					}

					@Override
					public void onFailure(@NonNull Call<GeocodingResponse> call, @NonNull Throwable throwable) {
						Timber.tag(TAG).e("Error receiving geocoding response");
						Toast.makeText(MapsActivity.this, "Geocoding error, please try again.",
								Toast.LENGTH_SHORT).show();
						throwable.printStackTrace();
					}
				});
			} catch (Exception e) {
				Timber.tag(TAG).e("Could not locate this address");
				e.printStackTrace();
			}
		} else if (viewId == R.id.button_findme) {
			requestLocation();
		}
	}

	protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		whereAmIString = savedInstanceState.getString(WHERE_AM_I_STRING);
		if (whereAmIString != null)
			mEditLocation.setText(whereAmIString);
	}

	@Override
	public void onSuccess(LocationEngineResult result) {
		if (result != null) {
			Location location = result.getLastLocation();
			if (location != null) {
				setCameraPosition(location);
				mLocationEngine.removeLocationUpdates(this);

				MapboxGeocoding reverseGeocode = new MapboxGeocoding.Builder()
						.setAccessToken(Mapbox.getAccessToken())
						.setCoordinates(Position.fromCoordinates(location.getLongitude(), location.getLatitude()))
						.setGeocodingType(GeocodingCriteria.TYPE_ADDRESS)
						.build();

				reverseGeocode.enqueueCall(new Callback<>() {
					@Override
					public void onResponse(@NonNull Call<GeocodingResponse> call, @NonNull Response<GeocodingResponse> response) {
						GeocodingResponse responseBody = response.body();
						if (responseBody != null) {
							List<CarmenFeature> results = responseBody.getFeatures();
							if (results.size() > 0) {
								// Log the first results position.
								Position firstResultPos = results.get(0).asPosition();
								Timber.tag(TAG).d("onResponse: %s", firstResultPos.toString());


								if (mMapboxMap != null) {
									LatLng latLng = new LatLng(firstResultPos.getLatitude(), firstResultPos.getLongitude());

									CameraPosition cameraPosition = new CameraPosition.Builder().
											target(latLng)
											.build();
									mMapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
									mEditLocation.setText(firstResultPos.toString());
								}
							} else {
								// No result for your request were found.
								Timber.tag(TAG).d("onResponse: No result found");
							}
						}
					}

					@Override
					public void onFailure(@NonNull Call<GeocodingResponse> call, @NonNull Throwable throwable) {
						Timber.tag(TAG).e("Error receiving geocoding response");
						throwable.printStackTrace();
					}
				});
			}
		}
	}

	@Override
	public void onFailure(@NonNull Exception exception) {

	}
}
