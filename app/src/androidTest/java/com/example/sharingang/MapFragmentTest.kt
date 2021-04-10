package com.example.sharingang

import android.Manifest
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
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


    private val itemTitle = "T"

    @Test
    fun itemsAddedAreDisplayedOnTheMap() {
        navigate_to(R.id.newItemFragment)
        onView(withId(R.id.editItemTitle)).perform(
            ViewActions.typeText(itemTitle),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.new_item_get_location)).perform(click())
        Thread.sleep(4000)
        onView(withId(R.id.createItemButton)).perform(click())
        navigate_to(R.id.mapFragment)
        Thread.sleep(6000)
        val device = UiDevice.getInstance(getInstrumentation())
        val marker = device.findObject(UiSelector().descriptionContains(""))
        marker.click()
        onView(withText(itemTitle)).check(matches(isDisplayed()));
    }

    @Test
    fun clickingOnButtonResetsCamera() {
        navigate_to(R.id.mapFragment)
        Thread.sleep(6000)
        onView(withId(R.id.map_get_my_location)).perform(click())
    }
}
