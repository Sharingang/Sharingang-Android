package com.example.sharingang

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NewItemFragmentTest {
    // We start with the main activity, and then navigate where we want
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private val firstItem = "First Item"
    private val secondItem = "Second Item"

    @Test
    fun aDescriptionCanBeEnteredAndSeenOnMainActivity() {
        onView(withId(R.id.newItemButton)).perform(click())
        onView(withId(R.id.newItemPrompt)).check(matches(withText("New Item")))
        onView(withId(R.id.editItemDescription)).perform(
            typeText(firstItem),
            closeSoftKeyboard()
        )
        val button = onView(withId(R.id.createItemButton))
        button.check(matches(withText("Create Item")))
        button.perform(click())

        onView(withText(firstItem)).check(matches(isDisplayed()))

        onView(withId(R.id.newItemButton)).perform(click())
        onView(withId(R.id.editItemDescription)).perform(
            typeText(secondItem),
            closeSoftKeyboard()
        )
        onView(withId(R.id.createItemButton)).perform(click())

        onView(withText(firstItem)).check(matches(isDisplayed()))
        onView(withText(secondItem)).check(matches(isDisplayed()))
    }
}