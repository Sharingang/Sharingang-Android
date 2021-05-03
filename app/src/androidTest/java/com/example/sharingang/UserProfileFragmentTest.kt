package com.example.sharingang


import android.Manifest
import androidx.test.espresso.Espresso
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
    private val fakeText = "Fake"

    @Test
    fun canOpenUserProfileFragment() {
        navigate_to(R.id.userProfileFragment)
        val textView = onView(withId(R.id.nameText))
        textView.check(matches(withText(FakeCurrentUserProvider.fakeUser1.name)))
        onView(withId(R.id.text_email)).check(matches(withText("test-user@example.com")))
        onView(withId(R.id.upf_topinfo)).check(matches(withText(
            "You need to be logged in to view your User Profile.")))
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
    fun aUserCanSeeTheirItems() {
        navigate_to(R.id.newEditFragment)
        onView(withId(R.id.newItemPrompt)).check(matches(withText("New Item")))

        onView(withId(R.id.itemTitle)).perform(
            typeText(fakeText),
            closeSoftKeyboard()
        )
        val button = onView(withId(R.id.createItemButton))
        button.check(matches(withText("Create Item")))
        button.perform(click())
        waitAfterSaveItem()

        navigate_to(R.id.userProfileFragment)

        val textView = onView(withId(R.id.nameText))
        textView.check(matches(withText(FakeCurrentUserProvider.fakeUser1.name)))
        onView(withText(fakeText)).check(matches(isDisplayed()))
    }

    @Test
    fun aUserCanLogout() {
        FakeCurrentUserProvider.instance = 1
        navigate_to(R.id.userProfileFragment)
        Thread.sleep(2000)
        onView(withId(R.id.btn_logout)).perform(click())
        Thread.sleep(2000)
        Espresso.pressBack()
        navigate_to(R.id.newEditFragment)
        onView(withId(R.id.itemTitle)).perform(
            typeText("TestItem"),
            closeSoftKeyboard()
        )
        Thread.sleep(2000)
        onView(withId(R.id.createItemButton)).perform(click())
        waitAfterSaveItem()
        FakeCurrentUserProvider.instance = 2
        onView(withId(R.id.item_list_view_title)).perform(click())
        Thread.sleep(2000)
        onView(withId(R.id.itemPostedBy)).perform(click())
        Thread.sleep(2000)
        onView(withId(R.id.btn_login)).check(matches(not(isDisplayed())))
        onView(withId(R.id.btn_logout)).check(matches(not(isDisplayed())))
        Espresso.pressBack()
        Espresso.pressBack()
        FakeCurrentUserProvider.instance = 0
        Thread.sleep(3000)
        onView(withId(R.id.item_list_view_title)).perform(click())
        Thread.sleep(2000)
        onView(withId(R.id.itemPostedBy)).perform(click())
        Thread.sleep(3000)
        onView(withId(R.id.btn_logout)).check(matches(not(isDisplayed())))
        onView(withId(R.id.btn_login)).check(matches(not(isDisplayed())))
        onView(withId(R.id.btn_report)).check(matches(not(isDisplayed())))
    }

    @Test
    fun aUserCanCancelLogin() {
        FakeCurrentUserProvider.instance = 1
        navigate_to(R.id.userProfileFragment)
        Thread.sleep(2000)
        onView(withId(R.id.btn_logout)).perform(click())
        Thread.sleep(2000)
        onView(withId(R.id.btn_login)).perform(click())
        Thread.sleep(1000)
        val device = UiDevice.getInstance(getInstrumentation())
        device.pressBack()
        device.pressBack()
        onView(withId(R.id.btn_login)).check(matches(isDisplayed()))
    }
}
