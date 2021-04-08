package com.example.sharingang

import android.content.SharedPreferences
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.*
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers.not
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runner.manipulation.Alphanumeric
import org.junit.runners.MethodSorters


@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class AccountFragmentTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun loginButtonMatchesText() {
        onView(withId(R.id.gotoAccount)).check(matches(withText("Account")))
        onView(withId(R.id.gotoAccount)).perform(click())
        onView(withId(R.id.account_status)).check(matches(withText("Status: Logged Out")))
        onView(withId(R.id.logoutButton)).check(matches(not(isDisplayed())))
        onView(withId(R.id.loginButton)).check(matches(isDisplayed()))
    }



    @Test
    fun loginClick() {
        onView(withId(R.id.gotoAccount)).perform(click())
        onView(withId(R.id.loginButton)).perform(click())
        Thread.sleep(2000)
        val device: UiDevice = UiDevice.getInstance(getInstrumentation())
        //device.pressBack()
        //onView(withId(R.id.loginButton)).check(matches(withText("Log In")))
        val createAct = device.findObject(UiSelector().className("android.widget.EditText"))
        createAct.click()
        createAct.text = "sharingang.test"
        device.findObject(UiSelector().textContains("ext")).click()
        Thread.sleep(2000)
        val secondfield = device.findObject(UiSelector().className("android.widget.EditText"))
        secondfield.text = "sharingangtest2021"
        device.findObject(UiSelector().textContains("ext")).click()
        Thread.sleep(2000)
        device.findObject(UiSelector()).swipeUp(500)
        device.findObject(UiSelector().className("android.widget.Button").instance(3)).clickAndWaitForNewWindow(20000)
        try {
            val scrollable = UiScrollable(UiSelector().scrollable(true))
            scrollable.scrollToEnd(100)
        } catch (e: UiObjectNotFoundException) {
            Thread.sleep(1000)
        }

        Thread.sleep(1000)
        device.findObject(UiSelector().className("android.widget.Button").instance(0)).click()
        Thread.sleep(2000)
        device.pressBack()
        onView(withId(R.id.userProfileButton)).perform(click())
        onView(withId(R.id.nameText)).check(matches(withText("Sharingang Test")))
    }

    @Test
    fun loginScreenMatchesWhenLoggedIn() {
        onView(withId(R.id.gotoAccount)).perform(click())
        onView(withId(R.id.account_status)).check(matches(withText("Status: Logged in as \nSharingang Test")))
        onView(withId(R.id.loginButton)).check(matches(not(isDisplayed())))
        onView(withId(R.id.logoutButton)).check(matches(isDisplayed()))
        onView(withId(R.id.logoutButton)).perform(click())
        onView(withId(R.id.loginButton)).check(matches(isDisplayed()))
        onView(withId(R.id.logoutButton)).check(matches(not(isDisplayed())))
    }

    @Test
    fun logoutUpdatesUserProfile() {
        onView(withId(R.id.userProfileButton)).perform(click())
        onView(withId(R.id.nameText)).check(matches(withText("")))
    }

}