package com.example.sharingang

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.PositionAssertions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class OrderItemsTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    // We start with the main activity, and then navigate where we want
    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private val first = "S"
    private val firstPrice = 10
    private val second = "D"
    private val secondPrice = 9
    private val third = "P"
    private val thirdPrice = 11

    private val book = "Book"
    private val electronics = "Electronics"

    private val orderByName = "Name"
    private val orderByPrice = "Price"
    private val orderByCategory = "Category"

    @Test
    fun itemsCanBeOrderedInDifferentWays() {
        // first item
        navigate_to(R.id.newEditFragment)
        onView(withId(R.id.itemTitle)).perform(
            typeText(first),
            closeSoftKeyboard()
        )
        onView(withId(R.id.category_spinner)).perform(click())
        onView(withText(book)).perform(click())
        onView(withId(R.id.itemPrice)).perform(
            typeText(firstPrice.toString()),
            closeSoftKeyboard()
        )
        onView(withId(R.id.createItemButton)).perform(click())

        // second item
        navigate_to(R.id.newEditFragment)
        onView(withId(R.id.itemTitle)).perform(
            typeText(second),
            closeSoftKeyboard()
        )
        onView(withId(R.id.category_spinner)).perform(click())
        onView(withText(electronics)).perform(click())
        onView(withId(R.id.itemPrice)).perform(
            typeText(secondPrice.toString()),
            closeSoftKeyboard()
        )
        onView(withId(R.id.createItemButton)).perform(click())

        // third item
        navigate_to(R.id.newEditFragment)
        onView(withId(R.id.itemTitle)).perform(
            typeText(third),
            closeSoftKeyboard()
        )
        onView(withId(R.id.category_spinner)).perform(click())
        onView(withText(book)).perform(click())
        onView(withId(R.id.itemPrice)).perform(
            typeText(thirdPrice.toString()),
            closeSoftKeyboard()
        )
        onView(withId(R.id.createItemButton)).perform(click())

        onView(withId(R.id.order_category_spinner)).perform(click())
        onView(withText(orderByName)).perform(click())
        onView(withId(R.id.start_ordering)).perform(click())
        Thread.sleep(500)
        onView(withText(second)).check(isCompletelyLeftOf(withText(third)))
        onView(withText(second)).check(isCompletelyAbove(withText(first)))

        onView(withId(R.id.order_category_spinner)).perform(click())
        onView(withText(orderByCategory)).perform(click())
        onView(withId(R.id.start_ordering)).perform(click())

        Thread.sleep(500)
        onView(withText(first)).check(isCompletelyAbove(withText(second)))
        onView(withText(third)).check(isCompletelyAbove(withText(second)))

        onView(withId(R.id.order_category_spinner)).perform(click())
        onView(withText(orderByPrice)).perform(click())

        onView(withId(R.id.order_ascending_descending)).perform(click())
        onView(withText("Descending")).perform(click())

        onView(withId(R.id.start_ordering)).perform(click())

        Thread.sleep(500)
        onView(withText(third)).check(isCompletelyAbove(withText(second)))
        onView(withText(first)).check(isCompletelyAbove(withText(second)))
    }
}