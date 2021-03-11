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
class EditItemFragmentTest {
    // We start with the main activity, and then navigate where we want
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private val item = "Test"
    private val editedItem = "Edited"

    @Test
    fun anItemCanBeEditedAndSeenOnItemsListFragment() {
        onView(withId(R.id.newItemButton)).perform(click())
        onView(withId(R.id.newItemPrompt))
            .check(matches(withText("New Item")))
        onView(withId(R.id.editItemDescription)).perform(
            typeText(item),
            closeSoftKeyboard()
        )
        val buttonCreate = onView(withId(R.id.createItemButton))
        buttonCreate.check(matches(withText("Create Item")))
        buttonCreate.perform(click())

        onView(withText(item))
            .check(matches(isDisplayed()))

        val buttonEdit = onView(withId(R.id.item_list_view_edit_btn))
        buttonEdit.check(matches(withText("Edit")))
        buttonEdit.perform(click())

        onView(withId(R.id.editItemPrompt))
            .check(matches(withText("Edit Item")))

        onView(withId(R.id.editItemDescription)).perform(
            typeText(editedItem),
            closeSoftKeyboard()
        )

        onView(withId(R.id.editItemButton)).perform(click())

        onView(withText(item + editedItem))
            .check(matches(isDisplayed()))
    }
}