package com.example.sharingang

import android.Manifest
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
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
        navigate_to(R.id.newItemFragment)
        onView(withId(R.id.new_item_get_location)).perform(click())
        Thread.sleep(3000)
        onView(withId(R.id.createItemButton)).perform(click())
        navigate_to(R.id.mapFragment)
        val text = onView(withId(R.id.location_display))
        //text.check(matches(withText("")))
        Thread.sleep(6000)
        text.check(matches(withText(containsString("Your location"))))
        val device = UiDevice.getInstance(getInstrumentation())
        val marker = device.findObject(UiSelector().descriptionContains(""))
        marker.click()
    }
}
