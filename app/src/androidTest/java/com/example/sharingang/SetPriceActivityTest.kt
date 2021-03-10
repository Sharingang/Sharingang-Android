package com.example.sharingang

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Exception


@RunWith(AndroidJUnit4::class)
class SetPriceActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)


    @Test
    fun priceEnteredIsSeenOnSummary() {
        val currency = SetPriceActivity.CHOSEN_CURRENCY
        val price = 12.34
        onView(withId(R.id.buttonSetPrice)).perform(click())
        onView(withId(R.id.editTextSetPrice)).perform(
            typeText(price.toString()),
            closeSoftKeyboard()
        )
        val button = onView(withId(R.id.buttonOK))
        button.check(matches(withText("OK")))
        button.perform(click())
        onView(withId(R.id.textViewSetPriceSummary)).check(matches(withText("The price you set is: $price $currency.")))
    }

    @Test
    fun noPriceEntersZeroDollarsIntoSummary() {
        val currency = SetPriceActivity.CHOSEN_CURRENCY
        onView(withId(R.id.buttonSetPrice)).perform(click())
        val button = onView(withId(R.id.buttonOK))
        button.perform(click())
        onView(withId(R.id.textViewSetPriceSummary)).check(matches(withText("The price you set is: 0.0 $currency.")))
    }
}