package com.example.sharingang

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.sharingang.ui.activities.MainActivity
import com.example.sharingang.utils.navigate_to
import com.example.sharingang.utils.waitAfterSaveItem
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@HiltAndroidTest

class SearchFragmentTest {


    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    private val firstTitle: String = "Harry Potter"
    private val secondTitle: String = "Inception"
    private val thirdTitle: String = "iPad"
    private val fourthTitle: String = "Harry Potter 2"

    private val commonString: String = "Harry Potter"

    private val bookCategory: String = "Book"
    private val electronicsCategory: String = "Electronics"

    // We start with the main activity, and then navigate where we want
    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun canSortByCategoriesOnly() {
        addItemsToInventory()
        navigate_to(R.id.searchFragment)
        onView(withId(R.id.searchCategorySpinner)).perform(click())
        onView(withText(bookCategory)).perform(click())
        onView(withId(R.id.sflSearchButton)).perform(click())

        onView(withText(firstTitle)).check(matches(isDisplayed()))
        onView(withText(firstTitle)).check(matches(isDisplayed()))

        onView(withId(R.id.searchCategorySpinner)).perform(click())
        onView(withText(electronicsCategory)).perform(click())
        onView(withId(R.id.sflSearchButton)).perform(click())

        onView(withText(thirdTitle)).check(matches(isDisplayed()))
    }


    @Test
    fun canSortByItemsOnly() {
        addItemsToInventory()
        navigate_to(R.id.searchFragment)
        onView(withId(R.id.searchText)).perform(
            typeText(fourthTitle),
            closeSoftKeyboard()
        )
        onView(withId(R.id.sflSearchButton)).perform(click())

        onView(withId(R.id.searchText)).perform(
            typeText(" "),
            closeSoftKeyboard()
        )
        onView(withText(fourthTitle)).check(matches(isDisplayed()))
    }

    @Test
    fun canSortByItemsAndCategoryAndClear() {
        addItemsToInventory()
        navigate_to(R.id.searchFragment)
        onView(withId(R.id.searchText)).perform(
            typeText(commonString),
            closeSoftKeyboard()
        )
        onView(withId(R.id.searchCategorySpinner)).perform(click())

        onView(withText(bookCategory)).perform(click())
        onView(withId(R.id.sflSearchButton)).perform(click())

        onView(withId(R.id.searchText)).perform(
            typeText(" "),
            closeSoftKeyboard()
        )

        onView(withText(firstTitle)).check(matches(isDisplayed()))
        onView(withText(fourthTitle)).check(matches(isDisplayed()))

        onView(withId(R.id.clearSearchButton)).perform(click())
        try {
            //This should fail
            onView(withText(firstTitle)).check(matches(isDisplayed()))
            //if it doesn't this will fail and the exception won't be caught
            onView(withId(R.id.searchText)).check(matches(withText("hello")))
        } catch (e: NoMatchingViewException) {
            //means we have succeeded
        }
    }

    @Test
    fun canSubUnsubToCategory() {
        navigate_to(R.id.searchFragment)
        onView(withId(R.id.menuSubscribe)).perform(click())
        onView(withId(R.id.menuUnsubscribe)).check(matches(isDisplayed()))
        onView(withId(R.id.menuUnsubscribe)).perform(click())
        onView(withId(R.id.menuSubscribe)).check(matches(isDisplayed()))
    }

    private fun addItemsToInventory() {
        addSingleItemToDB(firstTitle, bookCategory)
        addSingleItemToDB(secondTitle, bookCategory)
        addSingleItemToDB(thirdTitle, electronicsCategory)
        addSingleItemToDB(fourthTitle, bookCategory)
    }

    private fun addSingleItemToDB(name: String, category: String) {
        navigate_to(R.id.newEditFragment)
        onView(withId(R.id.itemTitle)).perform(
            typeText(name),
            closeSoftKeyboard()
        )

        onView(withId(R.id.category_spinner)).perform(click())
        onView(withText(category)).perform(click())

        onView(withId(R.id.createItemButton)).perform(click())
        waitAfterSaveItem()
    }
}
