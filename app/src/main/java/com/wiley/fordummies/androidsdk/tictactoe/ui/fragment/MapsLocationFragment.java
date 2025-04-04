package com.wiley.fordummies.androidsdk.tictactoe.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.ImageHolder;
import com.mapbox.maps.MapView;
import com.mapbox.maps.MapboxMap;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.LocationPuck2D;
import com.mapbox.maps.plugin.Plugin;
import com.mapbox.maps.plugin.gestures.GesturesPlugin;
import com.mapbox.maps.plugin.gestures.OnMoveListener;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener;
import com.mapbox.maps.plugin.locationcomponent.generated.LocationComponentSettings;
import com.wiley.fordummies.androidsdk.tictactoe.R;

import java.util.List;

import timber.log.Timber;

/**
 * Created by acc on June 30, 2023.
 *
 * I modified this example based on <a href="https://docs.mapbox.com/android/maps/examples/location-tracking/">a
 *   Mapbox example app/a>.
 */
public class MapsLocationFragment extends Fragment implements PermissionsListener,
		OnIndicatorBearingChangedListener, OnIndicatorPositionChangedListener, OnMoveListener, MenuProvider {

	private final PermissionsManager mPermissionsManager = new PermissionsManager(this);
	private MapView mMapView;
	private LocationPuck2D mLocationPuck;
	private LocationComponentPlugin mLocationPlugin;
	private GesturesPlugin mGesturesPlugin;
	private final String TAG = getClass().getSimpleName();

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View v = inflater.inflate(R.layout.fragment_maps_location, container, false);

		mMapView = (MapView) v.findViewById(R.id.map_view_location);
		MapboxMap mMapboxMap = mMapView.getMapboxMap();
		setupMap(mMapboxMap);

		return v;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		final MenuHost menuHost = requireActivity();
		menuHost.addMenuProvider(this, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mGesturesPlugin.removeOnMoveListener(this);
		mGesturesPlugin = null;
		mLocationPlugin.removeOnIndicatorBearingChangedListener(this);
		mLocationPlugin.removeOnIndicatorPositionChangedListener(this);
		mLocationPlugin = null;
		MapboxMap map = mMapView.getMapboxMap();
		mMapView = null;

		final MenuHost menuHost = requireActivity();
		menuHost.removeMenuProvider(this);
	}

	@Override
	public void onExplanationNeeded(@NonNull List<String> permissionsToExplain) {
		final Context ctx = requireContext();
		Toast.makeText(ctx, "You must enable location permissions", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onPermissionResult(boolean granted) {
		if (granted) {
			Timber.d(TAG, "Location permission granted");
			Toast.makeText(requireContext(), "User granted location permission", Toast.LENGTH_SHORT).show();
			initLocation();
			setupGesturesListener();
		} else {
			Timber.d(TAG, "User denied location permission");
			Toast.makeText(requireContext(), "User granted location permission", Toast.LENGTH_SHORT).show();
			onCameraTrackingDismissed();
		}
	}

	private void setupMap(MapboxMap map) {
		CameraOptions cameraOptions = new CameraOptions.Builder()
				.zoom(14.0)
				.build();
		map.setCamera(cameraOptions);
		map.loadStyle(Style.STANDARD);
		initLocation();
		setupGesturesListener();
	}


	private void initLocation() {
		if (mLocationPuck == null) {
			mLocationPuck = new LocationPuck2D();
			mLocationPuck.setBearingImage(ImageHolder.from(R.drawable.mapbox_user_puck_icon));
			mLocationPuck.setShadowImage(ImageHolder.from(R.drawable.mapbox_user_icon_shadow));
		}

		mLocationPlugin = mMapView.getPlugin(Plugin.MAPBOX_LOCATION_COMPONENT_PLUGIN_ID);
		if (mLocationPlugin != null) {
			final LocationComponentSettings.Builder builder = new LocationComponentSettings.Builder(mLocationPuck);
			builder.setEnabled(true);
			mLocationPlugin.updateSettings(theBuilder -> null);
			mLocationPlugin.addOnIndicatorBearingChangedListener(this);
			mLocationPlugin.addOnIndicatorPositionChangedListener(this);
		}
	}

	private void setupGesturesListener() {
		mGesturesPlugin = mMapView.getPlugin(Plugin.MAPBOX_GESTURES_PLUGIN_ID);
		if (mGesturesPlugin != null) {
			mGesturesPlugin.addOnMoveListener(this);
		}
	}

	@Override
	public boolean onMove(@NonNull MoveGestureDetector moveGestureDetector) {
		return false;
	}

	@Override
	public void onMoveBegin(@NonNull MoveGestureDetector moveGestureDetector) {
		onCameraTrackingDismissed();
	}

	@Override
	public void onMoveEnd(@NonNull MoveGestureDetector moveGestureDetector) {
		// Nothing here
	}

	@Override
	public void onIndicatorBearingChanged(double v) {

	}

	@Override
	public void onIndicatorPositionChanged(@NonNull Point point) {
		MapboxMap map = mMapView.getMapboxMap();
		CameraOptions.Builder builder = new CameraOptions.Builder();
		map.setCamera(builder.center(point).build());
		mGesturesPlugin.setFocalPoint(map.pixelForCoordinate(point));
	}



	private void onCameraTrackingDismissed() {
		Timber.tag(TAG).d("onCameraTrackingDismissed()");
		Toast.makeText(requireActivity(), "onCameraTrackingDismissed", Toast.LENGTH_SHORT).show();
		if (mLocationPlugin != null) {
			mLocationPlugin.removeOnIndicatorPositionChangedListener(this);
			mLocationPlugin.removeOnIndicatorBearingChangedListener(this);
		}
		if (mGesturesPlugin != null) {
			mGesturesPlugin.removeOnMoveListener(this);
		}
	}

	@Override
	public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
		menuInflater.inflate(R.menu.menu_maps, menu);
	}

	@Override
	public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
		final int itemId = menuItem.getItemId();
		final Activity activity = requireActivity();
		if (itemId == R.id.menu_showcurrentlocation) {
			if (!PermissionsManager.areLocationPermissionsGranted(requireContext())) {
				mPermissionsManager.requestLocationPermissions(activity);
			} else {
				MapboxMap map = mMapView.getMapboxMap();
				setupMap(map);
			}
		}
		return false;
	}
}
