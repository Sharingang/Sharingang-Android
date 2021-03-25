package com.example.sharingang

import android.content.Intent
import android.Manifest
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class NewItemFragmentTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    // We start with the main activity, and then navigate where we want
    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule(order = 2)
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    @get:Rule(order = 3)
    var mActivityTestRule = IntentsTestRule(MainActivity::class.java)

    private val firstItem = "First Item"
    private val secondItem = "Second Item"

    @Test
    fun aDescriptionCanBeEnteredAndSeenOnMainActivity() {
        savePickedImage(mActivityTestRule.activity)
        val imgGalleryResult = createImageGallerySetResultStub(mActivityTestRule.activity)
        Intents.intending(IntentMatchers.hasAction(Intent.ACTION_GET_CONTENT)).respondWith(imgGalleryResult)

        onView(withId(R.id.newItemButton)).perform(click())
        onView(withId(R.id.newItemPrompt)).check(matches(withText("New Item")))

        onView(withId(R.id.new_item_image)).perform(click())
        onView(withId(R.id.new_item_image)).check(matches(hasContentDescription()))
        onView(withId(R.id.editItemTitle)).perform(
            typeText(firstItem),
            closeSoftKeyboard()
        )
        val button = onView(withId(R.id.createItemButton))
        button.check(matches(withText("Create Item")))
        button.perform(click())

        onView(withText(firstItem)).check(matches(isDisplayed()))

        onView(withId(R.id.newItemButton)).perform(click())
        onView(withId(R.id.editItemTitle)).perform(
            typeText(secondItem),
            closeSoftKeyboard()
        )
        onView(withId(R.id.createItemButton)).perform(click())

        onView(withText(firstItem)).check(matches(isDisplayed()))
        onView(withText(secondItem)).check(matches(isDisplayed()))
    }

    @Test
    fun clickingOnGetLocationDisplaysLocation() {
        onView(withId(R.id.newItemButton)).perform(click())
        val button = onView(withId(R.id.new_item_get_location))
        button.check(matches(withText("Get Location")))
        button.perform(click())
        Thread.sleep(5000)
        onView(withId(R.id.write_latitude)).check(matches(not(withText(""))))
        onView(withId(R.id.write_longitude)).check(matches(not(withText(""))))
    }

    @Test
    fun aLocationCanBeWrittenInNewItemFragment() {
        onView(withId(R.id.newItemButton)).perform(click())
        onView(withId(R.id.write_latitude)).perform(
            typeText("45.01"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.write_longitude)).perform(
            typeText("5.014"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.createItemButton)).perform(click())
    }
}