package com.example.sharingang


import android.Manifest
import android.os.Environment
import android.os.Environment.DIRECTORY_PICTURES
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
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
import java.io.File

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
    fun canOpenUserProfileFragment() {
        navigate_to(R.id.userProfileFragment)
        Thread.sleep(3000)
        val textView = onView(withId(R.id.nameText))
        textView.check(matches(withText(FakeCurrentUserProvider.fakeUser.name)))
        onView(withId(R.id.text_email)).check(matches(withText("test user email")))
        onView(withId(R.id.upf_topinfo)).check(matches(withText(
            "You need to be logged in to view User Profiles.")))
    }

    @Test
    fun pictureButtonsAreDisplayedCorrectly() {
        navigate_to(R.id.userProfileFragment)
        Thread.sleep(3000)
        onView(withId(R.id.btn_open_camera)).check(matches(withText("Open Camera")))
        onView(withId(R.id.btn_open_gallery)).check(matches(withText("Open Gallery")))
        onView(withId(R.id.btnApply)).check(matches(not(isDisplayed())))
    }


    @Test
    fun applyButtonIsDisplayedUponClickOnOpenGallery() {
        val device: UiDevice = UiDevice.getInstance(getInstrumentation())
        navigate_to(R.id.userProfileFragment)
        Thread.sleep(3000)
        onView(withId(R.id.btn_open_gallery)).perform(click())
        Thread.sleep(3000)
        device.pressBack()
        Thread.sleep(3000)
        onView(withId(R.id.btnApply)).check(matches(isDisplayed()))
        onView(withId(R.id.btnApply)).perform(click())
        Thread.sleep(3000)
        onView(withId(R.id.btnApply)).check(matches(not(isDisplayed())))
    }
}
