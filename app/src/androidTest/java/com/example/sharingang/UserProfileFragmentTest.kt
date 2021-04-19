package com.example.sharingang


import android.Manifest
import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.toPackage
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class UserProfileFragmentTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    // We start with the main activity, and then navigate where we want
    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule(order = 2)
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

    @Test
    fun canOpenSearchFragment() {
        navigate_to(R.id.userProfileFragment)

        val textView = onView(withId(R.id.nameText))
        textView.check(matches(withText(FakeCurrentUserProvider.fakeUser.name)))
    }

    @Test
    fun pictureButtonsAreDisplayedCorrectly() {
        navigate_to(R.id.userProfileFragment)
        onView(withId(R.id.btn_open_camera)).check(matches(withText("Open Camera")))
        onView(withId(R.id.btn_open_gallery)).check(matches(withText("Open Gallery")))
        onView(withId(R.id.btnApply)).check(matches(not(isDisplayed())))
    }



    @Test
    fun applyButtonIsDisplayedUponClickOnOpenGallery() {
        val device: UiDevice = UiDevice.getInstance(getInstrumentation())
        navigate_to(R.id.userProfileFragment)
        onView(withId(R.id.btn_open_gallery)).perform(click())
        device.pressBack()
        onView(withId(R.id.btnApply)).check(matches(isDisplayed()))
        onView(withId(R.id.btnApply)).perform(click())
        onView(withId(R.id.btnApply)).check(matches(not(isDisplayed())))
    }

    @Test
    fun applyButtonApplies() {
        val device: UiDevice = UiDevice.getInstance(getInstrumentation())
        navigate_to(R.id.userProfileFragment)
        onView(withId(R.id.btn_open_gallery)).perform(click())
        device.pressBack()
        onView(withId(R.id.btnApply)).check(matches(isDisplayed()))
        UserProfileFragment.isTestCase = true
        onView(withId(R.id.btnApply)).perform(click())
        onView(withId(R.id.btnApply)).check(matches(not(isDisplayed())))
        UserProfileFragment.isTestCase = false
    }
}
