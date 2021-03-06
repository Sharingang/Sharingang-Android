package com.example.sharingang

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NewItemActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(NewItemActivity::class.java)

    private val expectedString = "Hello World!"

    @Test
    fun newItemPromptIsVisible() {
        onView(withId(R.id.newItemPrompt)).check(matches(withText("New Item")))
    }

    @Test
    fun aDescriptionCanBeEnteredAndSeenOnMainActivity() {
        Intents.init()
        onView(withId(R.id.editItemDescription)).perform(
            typeText(expectedString),
            closeSoftKeyboard()
        )
        val button = onView(withId(R.id.createItemButton))
        button.check(matches(withText("Create Item")))
        button.perform(click())
        Intents.intended(IntentMatchers.hasComponent(MainActivity::class.qualifiedName))
        onView(withId(R.id.lastDescription)).check(matches(withText(expectedString)))
        Intents.release()
    }
}