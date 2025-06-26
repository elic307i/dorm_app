package com.technion.dormsapp;

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
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;


@RunWith(AndroidJUnit4.class)
public class BorrowRequestUITest {

    @Rule
    public ActivityScenarioRule<BorrowRequest> activityRule =
            new ActivityScenarioRule<>(BorrowRequest.class);

    @Before
    public void setupPrefs() {
        Context context = ApplicationProvider.getApplicationContext();
        SharedPreferences prefs = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        prefs.edit().putInt("user_id", 123).putInt("room_id", 456).apply();  //
    }

    @Test
    public void testUIElementsDisplayed() {
        onView(withId(R.id.items)).check(matches(isDisplayed()));
        onView(withId(R.id.note)).check(matches(isDisplayed()));
        onView(withId(R.id.btnSubmit)).check(matches(isDisplayed()));
        onView(withId(R.id.btnReturn)).check(matches(isDisplayed()));
    }

    @Test
    public void testSubmitWithoutItemShowsError() {
        onView(withId(R.id.note)).perform(typeText("Test note"), closeSoftKeyboard());
        onView(withId(R.id.btnSubmit)).perform(click());
    }

    @Test
    public void testMissingRoomIdShowsError() {
        Context context = ApplicationProvider.getApplicationContext();
        SharedPreferences prefs = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        prefs.edit().remove("room_id").apply();

        onView(withId(R.id.btnSubmit)).perform(click());
    }

    @Test
    public void testValidSubmissionWithFakeItem() {
        onView(withId(R.id.note)).perform(typeText("Borrow a broom"), closeSoftKeyboard());
        onView(withId(R.id.btnSubmit)).perform(click());
    }
}
