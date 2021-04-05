package com.example.sharingang

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
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class AccountFragmentTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)


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
        device.findObject(UiSelector().textContains("ext")).clickAndWaitForNewWindow(1000)
        Thread.sleep(15000)
        val secondfield = device.findObject(UiSelector().className("android.widget.EditText"))
        secondfield.text = "sharingangtest2021"
        device.findObject(UiSelector().textContains("ext")).click()
        Thread.sleep(4000)
        device.findObject(UiSelector()).swipeUp(1000)
        device.findObject(UiSelector().className("android.widget.Button").instance(3)).clickAndWaitForNewWindow(20000)
        try {
            val scrollable = UiScrollable(UiSelector().scrollable(true))
            scrollable.scrollToEnd(100)
        } catch (e: UiObjectNotFoundException) {
            Thread.sleep(1000)
        }

        Thread.sleep(1000)
        device.findObject(UiSelector().className("android.widget.Button").instance(0)).click()
        Thread.sleep(5000)
        //device.findObject(UiSelector().className("android.widget.Button").instance(0)).click()
    }

    @Test
    fun loggedInScreenWhenLoggedIn() {
        onView(withId(R.id.gotoAccount)).perform(click())
        onView(withId(R.id.account_status)).check(matches(withText("Status: Logged in as \nSharingang Test")))
        onView(withId(R.id.loginButton)).check(matches(not(isDisplayed())))
        onView(withId(R.id.logoutButton)).check(matches(isDisplayed()))
        onView(withId(R.id.logoutButton)).perform(click())
        onView(withId(R.id.loginButton)).check(matches(isDisplayed()))
        onView(withId(R.id.logoutButton)).check(matches(not(isDisplayed())))
    }

}