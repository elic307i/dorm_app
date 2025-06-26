package com.technion.dormsapp;

import android.view.View;

import androidx.test.espresso.Root;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.*;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.*;

@RunWith(AndroidJUnit4.class)
public class MainActivityUITest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testLoginFieldsVisible() {
        onView(withId(R.id.UserName)).check(matches(isDisplayed()));
        onView(withId(R.id.password)).check(matches(isDisplayed()));
        onView(withId(R.id.btnStudent)).check(matches(isDisplayed()));
    }

    @Test
    public void testUserInputAndLoginClick() {
        onView(withId(R.id.UserName)).perform(typeText("student1"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText("password123"), closeSoftKeyboard());
        onView(withId(R.id.btnStudent)).perform(click());
        // This test assumes real login works. To reliably test this, use MockWebServer.
    }

    // Custom Toast matcher
    public static class ToastMatcher extends TypeSafeMatcher<Root> {
        @Override
        public void describeTo(Description description) {
            description.appendText("is a toast");
        }

        @Override
        @SuppressWarnings("deprecation")
        public boolean matchesSafely(Root root) {
            int type = root.getWindowLayoutParams().get().type;
            if ((type == android.view.WindowManager.LayoutParams.TYPE_TOAST)) {
                View decorView = root.getDecorView();
                return decorView.getWindowToken() == decorView.getApplicationWindowToken();
            }
            return false;
        }
    }
}
