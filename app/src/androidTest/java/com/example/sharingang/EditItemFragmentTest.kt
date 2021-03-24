package com.example.sharingang

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class EditItemFragmentTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    // We start with the main activity, and then navigate where we want
    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule(order = 2)
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )

    @get:Rule(order = 3)
    var mActivityTestRule = IntentsTestRule(MainActivity::class.java)

    private val item = "Test"
    private val editedItem = "Edited"

    @Test
    fun canEditItemCategory() {
        onView(withId(R.id.newItemButton)).perform(click())
        onView(withId(R.id.newItemPrompt)).check(matches(withText("New Item")))
        onView(withId(R.id.editItemTitle)).perform(
            typeText("Book_item"),
            closeSoftKeyboard()
        )

        onView(withId(R.id.category_spinner)).perform(click())
        onView(withText("Book")).perform(click())

        onView(withId(R.id.createItemButton)).perform(click())

        onView(withText("Book_item")).check(matches(isDisplayed()))
        onView(withId(R.id.item_list_view_edit_btn)).check(matches(withText("Edit")))
            .perform(click())

        onView(withId(R.id.editItemPrompt)).check(matches(withText("Edit Item")))
        onView(withId(R.id.category_spinner)).check(matches(withSpinnerText("Book")))
        onView(withId(R.id.category_spinner)).perform(click())
        onView(withText("Games")).perform(click())

        onView(withId(R.id.editItemButton)).perform(click())
        onView(withId(R.id.item_list_view_edit_btn)).check(matches(withText("Edit")))
            .perform(click())
        onView(withId(R.id.category_spinner)).check(matches(withSpinnerText("Games")))
    }


    @Test
    fun anItemCanBeEditedAndSeenOnItemsListFragment() {
        savePickedImage(mActivityTestRule.activity)
        val imgGalleryResult = createImageGallerySetResultStub(mActivityTestRule.activity)
        intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(imgGalleryResult)

        onView(withId(R.id.newItemButton)).perform(click())
        onView(withId(R.id.newItemPrompt))
            .check(matches(withText("New Item")))
        onView(withId(R.id.editItemTitle)).perform(
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

        onView(withId(R.id.edit_item_image)).perform(click())
        onView(withId(R.id.edit_item_image)).check(matches(hasContentDescription()))

        onView(withId(R.id.editItemTitle)).perform(
            typeText(editedItem),
            closeSoftKeyboard()
        )

        onView(withId(R.id.editItemButton)).perform(click())

        onView(withText(item + editedItem))
            .check(matches(isDisplayed()))
    }

    @Test
    fun aLocationCanBeWrittenInEditFragment() {
        onView(withId(R.id.newItemButton)).perform(click())
        onView(withId(R.id.createItemButton)).perform(click())
        onView(withId(R.id.item_list_view_edit_btn)).perform(click())
        onView(withId(R.id.edit_latitude)).perform(
            typeText("45.01"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.edit_longitude)).perform(
            typeText("5.014"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.editItemButton)).perform(click())
    }
}