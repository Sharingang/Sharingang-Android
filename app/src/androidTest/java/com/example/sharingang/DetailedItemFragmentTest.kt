package com.example.sharingang

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class DetailedItemFragmentTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    // We start with the main activity, and then navigate where we want
    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private val itemTitle = "T"
    private val itemDescription = "D"

    @Test
    fun anItemCanBeEditedAndSeenOnItemsListFragment() {
        navigate_to(R.id.newItemFragment)
        onView(withId(R.id.newItemPrompt))
            .check(matches(withText("New Item")))
        /*onView(withId(R.id.editItemTitle)).perform(
            typeText(itemTitle),
            closeSoftKeyboard()
        )*/
        val buttonCreate = onView(withId(R.id.createItemButton))
        buttonCreate.check(matches(withText("Create Item")))
        buttonCreate.perform(click())

        onView(withText(itemTitle)).perform(click())
/*
        onView(withId(R.id.itemTitle))
            .check(matches(withText(itemTitle)))*/
    }

    @Test
    fun canSeeCategoryInDetailedItemFragment() {
        val testTitle: String = "Book Item"
        val testCategory: String = "Book"
        navigate_to(R.id.newItemFragment)
        onView(withId(R.id.newItemPrompt)).check(matches(withText("New Item")))
        onView(withId(R.id.editItemTitle)).perform(
            typeText(testTitle),
            closeSoftKeyboard()
        )
        onView(withId(R.id.category_spinner)).perform(click())
        onView(withText(testCategory)).perform(click())
        onView(withId(R.id.createItemButton)).perform(click())

        onView(withText(testTitle)).perform(click())
        onView(withId(R.id.itemCategory)).check(matches(withText(testCategory)))
    }
}