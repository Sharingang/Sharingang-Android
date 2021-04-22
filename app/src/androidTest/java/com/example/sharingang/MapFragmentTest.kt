package com.example.sharingang

import android.Manifest
import android.app.Activity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
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
    private val waitingTime = 8000L

    @Test
    fun itemsAddedAreDisplayedOnTheMap() {
        navigate_to(R.id.editItemFragment)
        onView(withId(R.id.itemTitle)).perform(
            ViewActions.typeText(itemTitle),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.item_get_location)).perform(click())
        Thread.sleep(waitingTime)
        onView(withId(R.id.createItemButton)).perform(click())
        navigate_to(R.id.mapFragment)
        onView(withId(R.id.map_start_search)).perform(click())
        onView(withId(R.id.searchText)).perform(
            ViewActions.typeText(itemTitle),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.sflSearchButton)).perform(click())
        navigate_up()
        onView(withId(R.id.map_get_my_location)).perform(click())
        Thread.sleep(waitingTime)
        var activity: Activity? = null
        activityRule.scenario.onActivity {
            activity = it
        }
        // This part gets the height of the status bar, in order to find the center of the map
        var result = 0
        val resourceId: Int =
            activity!!.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = activity!!.resources.getDimensionPixelSize(resourceId)
        }
        val device = UiDevice.getInstance(getInstrumentation())
        device.click(device.displayWidth / 2, device.displayHeight / 2 + result)
        Thread.sleep(1000)
        onView(withId(R.id.itemTitle)).check(matches(withText(itemTitle)))
    }

    @Test
    fun clickingOnButtonResetsCamera() {
        navigate_to(R.id.mapFragment)
        Thread.sleep(waitingTime)
        onView(withId(R.id.map_get_my_location)).perform(click())
    }
}
