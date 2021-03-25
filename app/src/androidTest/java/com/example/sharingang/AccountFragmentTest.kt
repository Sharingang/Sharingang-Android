package com.example.sharingang

import android.Manifest
import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.toPackage
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
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
    fun logoutButtonDoesNotAppearWhenLoggedOut() {
        onView(withId(R.id.gotoAccount)).check(matches(withText("Account")))
        onView(withId(R.id.gotoAccount)).perform(click())
        onView(withId(R.id.loginButton)).check(matches(withText("Log In")))
        onView(withId(R.id.logoutButton)).check(matches(not(isDisplayed())))
        onView(withId(R.id.account_status)).check(matches(withText("Status: Logged Out")))
    }

    @Test
    fun loginClick() {
        val device: UiDevice = UiDevice.getInstance(getInstrumentation())
        onView(withId(R.id.gotoAccount)).perform(click())
        onView(withId(R.id.loginButton)).perform(click())
        device.pressBack()
        onView(withId(R.id.loginButton)).check(matches(withText("Log In")))
    }


}