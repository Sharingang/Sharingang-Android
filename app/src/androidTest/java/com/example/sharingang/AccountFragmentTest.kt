package com.example.sharingang

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers.not
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
        navigate_to(R.id.accountFragment)
        onView(withId(R.id.loginButton)).check(matches(withText("Log In")))
        onView(withId(R.id.logoutButton)).check(matches(not(isDisplayed())))
        onView(withId(R.id.account_status)).check(matches(withText("Status: Logged Out")))
    }
}