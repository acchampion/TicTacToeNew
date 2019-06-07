package com.wiley.fordummies.androidsdk.tictactoe;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.plugins.building.BuildingPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.services.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.services.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.services.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.services.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.services.commons.models.Position;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Activity showing user's location on Mapbox Map.
 *
 * Source: Mapbox, https://github.com/mapbox/mapbox-android-demo (LocationPluginActivity)
 *
 * Created by adamcchampion on 2017/08/17.
 */

public class MapsActivity extends AppCompatActivity implements LocationEngineListener,
        PermissionsListener, View.OnClickListener {
    private final int ENABLE_GPS_REQUEST_CODE = 1;

    private PermissionsManager mPermissionsManager;
    private MapView mMapView;
    private MapboxMap mMapboxMap;
    private LocationLayerPlugin mLocationLayerPlugin;
    private LocationEngine mLocationEngine;

    private EditText mEditLocation;
    private String whereAmIString = null;

    private static final String WHERE_AM_I_STRING = "WhereAmIString";
    private final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the MapView.
        Mapbox.getInstance(this, getString(R.string.access_token));

        setContentView(R.layout.activity_maps);

        mMapView = findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(mapboxMap -> {
            mMapboxMap = mapboxMap;
            mMapboxMap.getUiSettings().setCompassEnabled(true);
            mMapboxMap.getUiSettings().setZoomControlsEnabled(true);
            mMapboxMap.getUiSettings().setZoomGesturesEnabled(true);
            enableLocationPlugin();
        });

        mEditLocation = findViewById(R.id.location);

        Button buttonLocate = findViewById(R.id.button_locate);
        buttonLocate.setOnClickListener(this);

        Button buttonFindMe = findViewById(R.id.button_findme);
        buttonFindMe.setOnClickListener(this);
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationPlugin() {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            mLocationLayerPlugin = new LocationLayerPlugin(mMapView, mMapboxMap, mLocationEngine);
            mLocationLayerPlugin.setLocationLayerEnabled(true);

            BuildingPlugin buildingPlugin = new BuildingPlugin(mMapView, mMapboxMap);
            buildingPlugin.setVisibility(true);
            buildingPlugin.setMinZoomLevel(15.0f);
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
        }
        catch (NullPointerException npe) {
            Timber.e(TAG, "Could not set subtitle");
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int editLocationWidth = displayMetrics.widthPixels - 350;
        if (mEditLocation != null) {
            mEditLocation.setWidth(Math.max(250, editLocationWidth));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mLocationLayerPlugin != null) {
            mLocationLayerPlugin.onStart();
        }
        mMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mLocationEngine != null) {
            mLocationEngine.removeLocationUpdates();
        }
        if (mLocationLayerPlugin != null) {
            mLocationLayerPlugin.onStop();
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
        if (whereAmIString != null)
            outState.putString(WHERE_AM_I_STRING, whereAmIString);
    }

    private void requestLocation() {
        Timber.d(TAG, "requestLocation()");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (lacksLocationPermission()) {
                int PERMISSION_REQUEST_LOCATION = 1;
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQUEST_LOCATION);
            }
            else {
                doRequestLocation();
            }
        }
        else {
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

        if (mMapboxMap != null) {
            if (mLocationLayerPlugin == null) {
                mLocationLayerPlugin = new LocationLayerPlugin(mMapView, mMapboxMap, mLocationEngine);
            }
        }
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.M)
    private LocationEngine initializeLocationEngine() {
        LocationEngine locationEngine = new LocationEngineProvider(this).obtainBestLocationEngineAvailable();
        locationEngine.setPriority(LocationEnginePriority.BALANCED_POWER_ACCURACY);
        locationEngine.setInterval(60000);
        locationEngine.activate();

        if (hasLocationPermission()) {
            Location lastLocation = locationEngine.getLastLocation();
            if (lastLocation != null) {
                setCameraPosition(lastLocation);
            } else {
                locationEngine.addLocationEngineListener(this);
            }
        }

        return locationEngine;
    }

    private void setCameraPosition(Location location) {
        mMapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 16));
    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    public void onConnected() {
        mLocationEngine.requestLocationUpdates();
    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    public void onLocationChanged(Location location) {
        if (location != null) {
            setCameraPosition(location);
            mLocationEngine.removeLocationEngineListener(this);

            MapboxGeocoding reverseGeocode = new MapboxGeocoding.Builder()
                    .setAccessToken(Mapbox.getAccessToken())
                    .setCoordinates(Position.fromCoordinates(location.getLongitude(), location.getLatitude()))
                    .setGeocodingType(GeocodingCriteria.TYPE_ADDRESS)
                    .build();

            reverseGeocode.enqueueCall(new Callback<GeocodingResponse>() {
                @Override
                public void onResponse(@NonNull Call<GeocodingResponse> call, @NonNull Response<GeocodingResponse> response) {
                    GeocodingResponse responseBody = response.body();
                    if (responseBody != null) {
                        List<CarmenFeature> results = responseBody.getFeatures();
                        if (results.size() > 0) {
                            // Log the first results position.
                            Position firstResultPos = results.get(0).asPosition();
                            Timber.d(TAG, "onResponse: %s", firstResultPos.toString());


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
                            Timber.d(TAG, "onResponse: No result found");
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<GeocodingResponse> call, @NonNull Throwable throwable) {
                    Timber.e(TAG, "Error receiving geocoding response");
                    throwable.printStackTrace();
                }
            });
        }
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Timber.d(TAG, "onExplanationNeeded()");
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationPlugin();
        } else {
            //FragmentManager fragmentManager = getSupportFragmentManager();
            //LocationDeniedDialogFragment deniedDialogFragment = new LocationDeniedDialogFragment();
            //deniedDialogFragment.show(fragmentManager, "location_denied");
            Timber.e(TAG, "User denied permission to get location");
            Toast.makeText(getApplicationContext(), R.string.location_permission_denied, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]grantResults) {
        if (mPermissionsManager != null) {
            mPermissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);

            // Direct users to turn on GPS (and send them to Location Settings if it's off).
            // Source: https://stackoverflow.com/questions/843675/how-do-i-find-out-if-the-gps-of-an-android-device-is-enabled
            final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(getApplicationContext(), "Please turn on GPS in the Settings app", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, ENABLE_GPS_REQUEST_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Fetch location if user allowed it.
        if (requestCode == ENABLE_GPS_REQUEST_CODE) {
            requestLocation();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_locate:
                try {
                    String locationName = mEditLocation.getText().toString();
                    MapboxGeocoding mapboxGeocoding = new MapboxGeocoding.Builder()
                            .setAccessToken(Mapbox.getAccessToken())
                            .setLocation(locationName)
                            .setGeocodingType(GeocodingCriteria.TYPE_ADDRESS)
                            .build();

                    mapboxGeocoding.enqueueCall(new Callback<GeocodingResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<GeocodingResponse> call, @NonNull Response<GeocodingResponse> response) {
                            //if (response != null) {
                                GeocodingResponse responseBody = response.body();
                                if (responseBody != null) {
                                    List<CarmenFeature> results = responseBody.getFeatures();
                                    if (results != null && results.size() > 0) {
                                        // Log the first results position.
                                        Position firstResultPos = results.get(0).asPosition();
                                        Timber.d(TAG, "onResponse: %s", firstResultPos.toString());


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
                                        Timber.d(TAG, "onResponse: No result found");
                                        Toast.makeText(MapsActivity.this, "No results found.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            //}
                        }

                        @Override
                        public void onFailure(@NonNull Call<GeocodingResponse> call, @NonNull Throwable throwable) {
                            Timber.e(TAG, "Error receiving geocoding response");
                            Toast.makeText(MapsActivity.this, "Geocoding error, please try again.",
                                    Toast.LENGTH_SHORT).show();
                            throwable.printStackTrace();
                        }
                    });
                } catch (Exception e) {
                    Timber.e(TAG, "Could not locate this address");
                    e.printStackTrace();
                }
                break;
            case R.id.button_findme:
                requestLocation();
                break;
        }
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        whereAmIString = savedInstanceState.getString(WHERE_AM_I_STRING);
        if (whereAmIString != null)
            mEditLocation.setText(whereAmIString);
    }
}
