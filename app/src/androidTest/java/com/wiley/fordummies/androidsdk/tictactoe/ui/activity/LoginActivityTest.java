package com.wiley.fordummies.androidsdk.tictactoe.ui.activity;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import android.view.View;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

	@Rule
	public ActivityScenarioRule<LoginActivity> mActivityScenarioRule =
			new ActivityScenarioRule<>(LoginActivity.class);

	@Test
	public void loginActivityTest() {
		ViewInteraction textView = onView(
				allOf(withText("Tic-Tac-Toe Awaits!"),
						withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.ScrollView.class))),
						isDisplayed()));
		textView.check(matches(withText("Tic-Tac-Toe Awaits!")));
	}
}
