package com.example.sharingang

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class SellItemFragmentTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    // We start with the main activity, and then navigate where we want
    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private val itemTitle = "T"

    @Test
    fun anItemCanBeEditedAndSeenOnItemsListFragment() {
        Espresso.onView(ViewMatchers.withId(R.id.newItemButton)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.newItemPrompt))
            .check(ViewAssertions.matches(ViewMatchers.withText("New Item")))
        Espresso.onView(ViewMatchers.withId(R.id.editItemTitle)).perform(
            ViewActions.typeText(itemTitle),
            ViewActions.closeSoftKeyboard()
        )
        val buttonCreate = Espresso.onView(ViewMatchers.withId(R.id.createItemButton))
        buttonCreate.check(ViewAssertions.matches(ViewMatchers.withText("Create Item")))
        buttonCreate.perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText("Sell")).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.item_list_view_sell_button))
            .check(ViewAssertions.matches(ViewMatchers.withText("Unsell")))
    }
}