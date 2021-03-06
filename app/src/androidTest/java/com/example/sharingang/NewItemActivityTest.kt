package com.example.sharingang

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
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

    @Test
    fun newItemPromptIsVisible() {
        onView(withId(R.id.newItemPrompt)).check(matches(withText("New Item")))
    }
}