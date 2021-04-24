package com.example.sharingang


import android.Manifest
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.CoreMatchers.not
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class ReportFragmentTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    // We start with the main activity, and then navigate where we want
    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun reportCanBeCancelled() {
        navigate_to(R.id.userProfileFragment)
        Thread.sleep(500)
        onView(withId(R.id.btn_report)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_report)).perform(click())
        onView(withId(R.id.text_reportedUsername)).check(matches(withText("Reporting Test User")))
        onView(withId(R.id.button_ok)).check(matches(not(isEnabled())))
        onView(withId(R.id.radio_item)).perform(click())
        onView(withId(R.id.button_ok)).check(matches(isEnabled()))
        onView(withId(R.id.button_cancel)).check(matches(isEnabled()))
        onView(withId(R.id.button_cancel)).perform(click())
        onView(withId(R.id.btn_report)).check(matches(isDisplayed()))
    }

    @Test
    fun reportCanBeSent() {
        navigate_to(R.id.userProfileFragment)
        Thread.sleep(500)
        onView(withId(R.id.btn_report)).perform(click())
        onView(withId(R.id.radio_other)).perform(click())
        onView(withId(R.id.button_ok)).perform(click())
    }


}