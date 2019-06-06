package com.wiley.fordummies.androidsdk.tictactoe;

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

import java.util.Hashtable;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by adamcchampion on 2017/08/14.
 */
public class SensorsFragment extends Fragment implements SensorEventListener {
    private SensorManager mSensorManager;
    private List<Sensor> mSensorList;
    private Hashtable<String, float[]> lastSensorValues = new Hashtable<>();

    private final String TAG = getClass().getSimpleName();
    private static final float TOLERANCE = (float) 10.0;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sensor_list, container, false);

        Activity activity = getActivity();
        RecyclerView sensorRecyclerView = v.findViewById(R.id.sensor_recycler_view);

        if (activity != null) {
            sensorRecyclerView.setLayoutManager(new LinearLayoutManager(activity));

            mSensorManager = (SensorManager) activity.getSystemService(SENSOR_SERVICE);
            if (mSensorManager != null) {
                mSensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);
                SensorAdapter adapter = new SensorAdapter(mSensorList);
                sensorRecyclerView.setAdapter(adapter);
                sensorRecyclerView.setItemAnimator(new DefaultItemAnimator());
            }
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        Timber.d(TAG, "onResume()");
        try {
            AppCompatActivity activity = (AppCompatActivity) getActivity();

            if (activity != null) {
                ActionBar actionBar = activity.getSupportActionBar();

                if (actionBar != null) {
                    actionBar.setSubtitle(getResources().getString(R.string.sensors));
                }
            }
        }
        catch (NullPointerException npe) {
            Timber.e(TAG, "Could not set subtitle");
        }

        // Start listening to sensor updates
        for (Sensor sensor : mSensorList) {
            mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        Timber.d(TAG, "Entering onPause()");
        super.onPause();
        // Stop updates when paused
        mSensorManager.unregisterListener(this);
        Timber.d(TAG, "Leaving onPause()");
    }


    private String getSensorDescription(Sensor sensor) {
        return "Sensor: " + sensor.getName() + "; Ver :" + sensor.getVersion() + "; Range: " +
                sensor.getMaximumRange() + "; Power: " + sensor.getPower() +
                "; Res: " + sensor.getResolution();
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
            }
            else if (distanceOfThisValue != 0.0) {
                percentageChange = change * (float) 100.0 / distanceOfThisValue;
            }
            else {
                percentageChange = (float) 0.0; // both distances are zero
            }

        }
        Timber.d(TAG, "--- EVENT Raw Values ---\n" + sensorName + "\n" +
                "Distance  Last= >" + distanceOfLastValue + "<\n" +
                "Distance  This= >" + distanceOfThisValue + "<\n" +
                "Change = >" + change + "<\n" +
                "Percent = >" + percentageChange + "%\n" +
                "Last value = " + lastValueString + "<\n" +
                sensorEventString);
        if (lastValue == null ||
                percentageChange > TOLERANCE) {
            Timber.d(TAG + sensorName,
                    "--- Event Changed --- \n" +
                            "Change = >" + change + "<\n" +
                            "Percent = >" + percentageChange + "%\n" +
                            sensorEventString);
        }
    }

    private String sensorEventToString(SensorEvent event) {
        StringBuilder builder = new StringBuilder();
        builder.append("Sensor: ");
        builder.append(event.sensor.getName());
        builder.append("\nAccuracy: ");
        builder.append(event.accuracy);
        builder.append("\nTimestamp: ");
        builder.append(event.timestamp);
        builder.append("\nValues:\n");
        for (int i = 0; i < event.values.length; i++) {
            builder.append("   [");
            builder.append(i);
            builder.append("] = ");
            builder.append(event.values[i]);
        }
        builder.append("\n");
        return builder.toString();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private class SensorHolder extends RecyclerView.ViewHolder {
        private String mDescriptionStr;
        private TextView mSensorInfoTextView;

        SensorHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_sensor, parent, false));

            mSensorInfoTextView = itemView.findViewById(R.id.sensor_data);
        }

        void bind(Sensor sensor) {
            mDescriptionStr = getSensorDescription(sensor);
            mSensorInfoTextView.setText(mDescriptionStr);
        }
    }

    private class SensorAdapter extends RecyclerView.Adapter<SensorHolder> {
        private List<Sensor> mSensorList;

        SensorAdapter(List<Sensor> sensorList) {
            mSensorList = sensorList;
        }

        @NonNull
        @Override
        public SensorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new SensorHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull SensorHolder holder, int position) {
            Sensor sensor = SensorsFragment.this.mSensorList.get(position);
            holder.bind(sensor);
        }

        @Override
        public int getItemCount() {
            return mSensorList.size();
        }
    }
}
