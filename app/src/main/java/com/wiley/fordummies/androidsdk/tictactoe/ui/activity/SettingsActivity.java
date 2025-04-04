package com.wiley.fordummies.androidsdk.tictactoe.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

import com.wiley.fordummies.androidsdk.tictactoe.R;
import com.wiley.fordummies.androidsdk.tictactoe.ui.fragment.SettingsFragment;

/**
 * Created by adamcchampion on 2017/08/13.
 */

public class SettingsActivity extends AppCompatActivity {

    protected Fragment createFragment() {
        return new SettingsFragment();
    }

    @LayoutRes
    protected int getLayoutResId() {
        return R.layout.activity_fragment;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(getLayoutResId());

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            Fragment preferenceFragment = createFragment();
            fm.beginTransaction()
                    .replace(R.id.fragment_container, preferenceFragment)
                    .commit();
        }

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
    }

    @Override
    protected void onStart() {
        super.onStart();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fragment_container), new OnApplyWindowInsetsListener() {
            @NonNull
            @Override
            public WindowInsetsCompat onApplyWindowInsets(@NonNull View v, @NonNull WindowInsetsCompat insets) {
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();

                var sysBarsCutoutGestures = insets.getInsets(WindowInsetsCompat.Type.systemBars() |
                        WindowInsetsCompat.Type.displayCutout() | WindowInsetsCompat.Type.systemGestures());
                v.setPadding(sysBarsCutoutGestures.left, sysBarsCutoutGestures.top,
                        sysBarsCutoutGestures.right, sysBarsCutoutGestures.bottom);
                return WindowInsetsCompat.CONSUMED;
            }
        });
    }
}
