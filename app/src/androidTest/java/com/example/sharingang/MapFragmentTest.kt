package com.example.sharingang

import android.Manifest
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.core.StringContains.containsString
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class MapFragmentTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    val grantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

    @Test
    fun itemsAddedAreDisplayedOnTheMap() {
        onView(withId(R.id.newItemButton)).perform(click())
        onView(withId(R.id.write_latitude)).perform(
            ViewActions.typeText("37.4"),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.write_longitude)).perform(
            ViewActions.typeText("4.143"),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.createItemButton)).perform(click())
        onView(withId(R.id.go_to_map)).perform(click())
        val text = onView(withId(R.id.location_display))
        //text.check(matches(withText("")))
        Thread.sleep(6000)
        text.check(matches(withText(containsString("Your location"))))
    }
}
