package com.example.sharingang


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
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
        FakeCurrentUserProvider.instance = FakeCurrentUserProvider.Instance.FAKE_USER_2
        navigate_to(R.id.newEditFragment)
        Thread.sleep(500)
        onView(withId(R.id.itemTitle)).perform(
            typeText("TestItem"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.createItemButton)).perform(click())
        waitAfterSaveItem()
        FakeCurrentUserProvider.instance = FakeCurrentUserProvider.Instance.FAKE_USER_1
        onView(withId(R.id.item_list_view_title)).perform(click())
        onView(withId(R.id.itemPostedBy)).perform(click())
        Thread.sleep(500)
        onView(withId(R.id.btn_report)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_report)).perform(click())
        onView(withId(R.id.text_reportedUsername)).check(matches(withText("Reporting Test User 2")))
        onView(withId(R.id.radio_username)).check(matches(withText("Inappropriate Username")))
        onView(withId(R.id.radio_profile_picture)).check(matches(withText("Inappropriate Profile Picture")))
        onView(withId(R.id.radio_item)).check(matches(withText("Inappropriate Item")))
        onView(withId(R.id.radio_other)).check(matches(withText("Other")))
        onView(withId(R.id.button_ok)).check(matches(not(isEnabled())))
        onView(withId(R.id.radio_item)).perform(click())
        onView(withId(R.id.button_ok)).check(matches(isEnabled()))
        onView(withId(R.id.button_cancel)).check(matches(isEnabled()))
        onView(withId(R.id.button_cancel)).check(matches(withText("Cancel")))
        onView(withId(R.id.button_cancel)).perform(click())
        onView(withId(R.id.btn_report)).check(matches(isDisplayed()))
    }

    @Test
    fun reportCanBeSent() {
        FakeCurrentUserProvider.instance = FakeCurrentUserProvider.Instance.FAKE_USER_2
        navigate_to(R.id.newEditFragment)
        Thread.sleep(500)
        onView(withId(R.id.itemTitle)).perform(
            typeText("TestItem"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.createItemButton)).perform(click())
        waitAfterSaveItem()
        FakeCurrentUserProvider.instance = FakeCurrentUserProvider.Instance.FAKE_USER_1
        onView(withId(R.id.item_list_view_title)).perform(click())
        onView(withId(R.id.itemPostedBy)).perform(click())
        Thread.sleep(500)
        onView(withId(R.id.btn_report)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_report)).perform(click())
        onView(withId(R.id.text_reportedUsername)).check(matches(withText("Reporting Test User 2")))
        onView(withId(R.id.report_description)).check(matches(withHint("Provide a description for the report (optional)")))
        onView(withId(R.id.radio_other)).perform(click())
        onView(withId(R.id.button_ok)).check(matches(isEnabled()))
        onView(withId(R.id.button_ok)).check(matches(withText("OK")))
        onView(withId(R.id.button_ok)).perform(click())
    }

    @Test
    fun aUserCannotReportThemselves() {
        FakeCurrentUserProvider.instance = FakeCurrentUserProvider.Instance.FAKE_USER_1
        navigate_to(R.id.userProfileFragment)
        onView(withId(R.id.btn_report)).check(matches(not(isDisplayed())))
    }


}
