package com.example.sharingang

import android.Manifest
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class MainActivityTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun clickingOnButtonStartsMapFragment(){
        onView(withId(R.id.go_to_map)).perform(click())
        onView(withId(R.id.location_display)).check(matches(withText("")))
    }
    @Test
    fun clickingOnButtonStartsNewItemFragment() {
        onView(withId(R.id.newItemButton)).perform(click())
        onView(withId(R.id.newItemPrompt))
            .check(matches(withText("New Item")))
    }
}