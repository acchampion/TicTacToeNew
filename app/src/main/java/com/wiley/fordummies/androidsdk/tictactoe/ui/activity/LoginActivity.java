package com.wiley.fordummies.androidsdk.tictactoe.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnticipateInterpolator;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.wiley.fordummies.androidsdk.tictactoe.R;
import com.wiley.fordummies.androidsdk.tictactoe.ui.fragment.LoginFragment;

/**
 * Activity for user login.
 *
 * Created by adamcchampion on 2017/08/03.
 */

public class LoginActivity extends AppCompatActivity {

    protected Fragment createFragment() {
        return new LoginFragment();
    }

	@LayoutRes
	protected int getLayoutResId() {
		return R.layout.activity_fragment;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
		SplashScreen.installSplashScreen(this).setOnExitAnimationListener(splashScreenView -> {
					final ObjectAnimator slideUp = ObjectAnimator.ofFloat(
							splashScreenView,
							String.valueOf(View.TRANSLATION_Y),
							0f,
							splashScreenView.getIconView().getHeight()
					);
					slideUp.setInterpolator(new AnticipateInterpolator());
					slideUp.setDuration(200L);

					// Call SplashScreenView.remove at the end of your custom animation.
					slideUp.addListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							splashScreenView.remove();
						}
					});

					// Run your animation.
					slideUp.start();
				}
			);
		}
		super.onCreate(savedInstanceState);
		setContentView(getLayoutResId());

		FragmentManager fm = getSupportFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.fragment_container);

		if (fragment == null) {
			fragment = new LoginFragment();
			fm.beginTransaction()
					.add(R.id.fragment_container, fragment)
					.commit();
		}
	}
}
