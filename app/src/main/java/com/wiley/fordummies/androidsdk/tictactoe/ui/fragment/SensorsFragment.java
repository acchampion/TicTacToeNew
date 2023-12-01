package com.wiley.fordummies.androidsdk.tictactoe.ui.fragment;

import static android.content.Context.SENSOR_SERVICE;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wiley.fordummies.androidsdk.tictactoe.R;

import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public class SensorsFragment extends Fragment implements SensorEventListener  {
	private SensorManager mSensorManager;
	private List<Sensor> mSensorList;

	private final Hashtable<String, float[]> lastSensorValues = new Hashtable<>();

	private static final float TOLERANCE = (float) 10.0;
	private final String TAG = getClass().getSimpleName();

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_sensor_table_list, container, false);

		Activity activity = requireActivity();
		RecyclerView sensorRecyclerView = v.findViewById(R.id.sensor_recyclerview_table);
		sensorRecyclerView.setLayoutManager(new LinearLayoutManager(activity));

		mSensorManager = (SensorManager) activity.getSystemService(SENSOR_SERVICE);
		if (mSensorManager != null) {
			mSensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);
			SensorAdapter adapter = new SensorAdapter(mSensorList);
			sensorRecyclerView.setAdapter(adapter);
			sensorRecyclerView.setItemAnimator(new DefaultItemAnimator());
		}

		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		Timber.tag(TAG).d("onResume()");
		try {
			AppCompatActivity activity = (AppCompatActivity) requireActivity();
			ActionBar actionBar = activity.getSupportActionBar();

			if (actionBar != null) {
				actionBar.setSubtitle(getResources().getString(R.string.sensors));
			}
		} catch (NullPointerException npe) {
			Timber.tag(TAG).e("Could not set subtitle");
		}

		// Start listening to sensor updates
		for (Sensor sensor : mSensorList) {
			mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
		}
	}

	@Override
	public void onPause() {
		Timber.tag(TAG).d("Entering onPause()");
		super.onPause();
		// Stop updates when paused
		for (Sensor sensor : mSensorList) {
			mSensorManager.unregisterListener(this, sensor);
		}
		Timber.tag(TAG).d("Leaving onPause()");
	}


	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {
		String sensorName = sensorEvent.sensor.getName();
		String lastValueString = "No previous value";
		String sensorEventString = sensorEventToString(sensorEvent);
		float percentageChange = (float) 1000.0 + TOLERANCE; // Some value greater than tolerance
		float distanceOfLastValue = (float) 0.0;
		float distanceOfThisValue = (float) 0.0;
		float change = (float) 0.0;

		float[] lastValue = lastSensorValues.get(sensorName);
		lastSensorValues.remove(sensorName); // Hash table is "open" and can store multiple entries for the same key
		lastSensorValues.put(sensorName, sensorEvent.values.clone()); // update the value
		if (lastValue != null) {
			// Compute distance of new value, change and percentage change
			StringBuilder builder = new StringBuilder();
			distanceOfLastValue = (float) 0.0;
			for (int i = 0; i < sensorEvent.values.length; i++) {
				distanceOfLastValue = distanceOfLastValue + (float) Math.pow(lastValue[i], 2);
				distanceOfThisValue = distanceOfThisValue + (float) Math.pow(sensorEvent.values[i], 2);
				change = change + (float) Math.pow((sensorEvent.values[i] - lastValue[i]), 2);
				builder.append("   [");
				builder.append(i);
				builder.append("] = ");
				builder.append(lastValue[i]);
			}
			lastValueString = builder.toString();
			change = (float) Math.sqrt(change);
			distanceOfLastValue = (float) Math.sqrt(distanceOfLastValue);
			distanceOfThisValue = (float) Math.sqrt(distanceOfThisValue);

			if (distanceOfLastValue != 0.0) {
				percentageChange = change * (float) 100.0 / distanceOfLastValue;
			} else if (distanceOfThisValue != 0.0) {
				percentageChange = change * (float) 100.0 / distanceOfThisValue;
			} else {
				percentageChange = (float) 0.0; // both distances are zero
			}

		}
		Timber.tag(TAG).d("EVENT: Raw Values: %s; Distance Last: %f; Distance This: %f; Change: %f; Percent: %f%%; Last value: %s; %s",
				sensorName, distanceOfLastValue, distanceOfThisValue, change, percentageChange,
				lastValueString, sensorEventString);
		if (lastValue == null || percentageChange > TOLERANCE) {
			Timber.tag(TAG).d("--- Event Changed --- : Change: %f; Percent: %f%%; %s",
					change, percentageChange, sensorEventString);
		}
	}

	private String sensorEventToString(SensorEvent event) {
		StringBuilder builder = new StringBuilder();
		builder.append("Sensor: ");
		builder.append(event.sensor.getName());
		builder.append("; Accuracy: ");
		builder.append(event.accuracy);
		builder.append("; Timestamp: ");
		builder.append(event.timestamp);
		builder.append("; Values: ");
		for (int i = 0; i < event.values.length; i++) {
			builder.append("   [");
			builder.append(i);
			builder.append("] = ");
			builder.append(event.values[i]);
		}
		return builder.toString();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int i) {
		Timber.tag(TAG).d("onAccuracyChanged(): Accuracy of %s changed to %d", sensor.toString(), i);
	}

	private static class SensorHolder extends RecyclerView.ViewHolder {
		private final TextView mSensorNameTv, mSensorVersionTv, mSensorRangeTv, mSensorPowerTv, mSensorResTv;

		SensorHolder(LayoutInflater inflater, ViewGroup parent) {
			super(inflater.inflate(R.layout.list_item_sensor, parent, false));
			mSensorNameTv = itemView.findViewById(R.id.sensor_name);
			mSensorVersionTv = itemView.findViewById(R.id.sensor_version);
			mSensorRangeTv = itemView.findViewById(R.id.sensor_range);
			mSensorPowerTv = itemView.findViewById(R.id.sensor_power);
			mSensorResTv = itemView.findViewById(R.id.sensor_res);
		}

		void bind(Sensor sensor) {
			String name = String.format(Locale.getDefault(), "%-30s", sensor.getName().trim());
			mSensorNameTv.setText(name);
			final int version = sensor.getVersion();
			mSensorVersionTv.setText(String.format(Locale.getDefault(), "%,d", version));
			final float range = sensor.getMaximumRange();
			final float power = sensor.getPower();
			final float resolution = sensor.getResolution();
			mSensorRangeTv.setText(String.format(Locale.getDefault(), "%.6g", range));
			mSensorPowerTv.setText(String.format(Locale.getDefault(), "%.6f", power));
			mSensorResTv.setText(String.format(Locale.getDefault(), "%.6f", resolution));
		}
	}

	private class SensorAdapter extends RecyclerView.Adapter<SensorHolder> {
		private final List<Sensor> mSensorList;

		SensorAdapter(List<Sensor> sensorList) {
			mSensorList = sensorList;
		}

		@NonNull
		@Override
		public SensorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			LayoutInflater inflater = LayoutInflater.from(requireActivity());
			return new SensorHolder(inflater, parent);
		}

		@Override
		public void onBindViewHolder(@NonNull SensorHolder holder, int position) {
			Sensor sensor = mSensorList.get(position);
			holder.bind(sensor);
		}

		@Override
		public int getItemCount() {
			return mSensorList.size();
		}
	}
}
