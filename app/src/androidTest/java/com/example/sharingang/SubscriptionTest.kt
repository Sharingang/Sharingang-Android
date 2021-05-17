package com.example.sharingang

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.example.sharingang.ui.activities.MainActivity
import com.example.sharingang.utils.navigate_to
import com.example.sharingang.utils.withMenuIdOrText
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class SubscriptionTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    // We start with the main activity, and then navigate where we want
    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun subscriptionsAreEmptyBeforeSubscribing() {
        navigate_to(R.id.userProfileFragment)
        onView(
            withMenuIdOrText(
                R.id.subscription_list,
                R.string.see_subscriptions
            )
        ).perform(click())
        onView(withId(R.id.subscriptions)).check(matches(withText(R.string.not_subscribed)))
    }

    @Test
    fun canSeeSubscriptionsAfterSubscribing() {
        navigate_to(R.id.searchFragment)
        onView(withMenuIdOrText(R.id.menuSubscribe, R.string.subscribe)).perform(click())
        pressBack()
        navigate_to(R.id.userProfileFragment)
        onView(
            withMenuIdOrText(
                R.id.subscription_list,
                R.string.see_subscriptions
            )
        ).perform(click())
        onView(withId(R.id.subscriptions)).check(matches(not(withText(R.string.not_subscribed))))
    }
}
