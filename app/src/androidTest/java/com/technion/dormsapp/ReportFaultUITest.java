package com.technion.dormsapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import static org.hamcrest.Matchers.*;



@RunWith(AndroidJUnit4.class)
public class ReportFaultUITest {

    @Rule
    public ActivityScenarioRule<ReportFault> activityRule =
            new ActivityScenarioRule<>(ReportFault.class);

    @Before
    public void setupPrefs() {
        // Simulate a logged-in user with a room assignment
        Context context = ApplicationProvider.getApplicationContext();
        SharedPreferences prefs = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        prefs.edit()
                .putInt("user_id", 123)
                .putInt("room_id", 456)
                .apply();
    }

    @Test
    public void testReportFormVisible() {
        onView(withId(R.id.itemType)).check(matches(isDisplayed()));
        onView(withId(R.id.faultDescription)).check(matches(isDisplayed()));
        onView(withId(R.id.urgencySpinner)).check(matches(isDisplayed()));
        onView(withId(R.id.btnSubmit)).check(matches(isDisplayed()));
    }

    @Test
    public void testSubmitFormWithValidInput() {
        // Select fault type (skip "בחר סוג תקלה")
        onView(withId(R.id.itemType)).perform(click());
        onData(not(equalTo("בחר סוג תקלה"))).atPosition(1).perform(click());

        // Type description
        onView(withId(R.id.faultDescription)).perform(typeText("Something is broken"), closeSoftKeyboard());

        // Select urgency
        onView(withId(R.id.urgencySpinner)).perform(click());
        onData(not(equalTo("בחר דחיפות"))).atPosition(1).perform(click());

        // Click submit (we won't check the result here)
        onView(withId(R.id.btnSubmit)).perform(click());
    }
}
