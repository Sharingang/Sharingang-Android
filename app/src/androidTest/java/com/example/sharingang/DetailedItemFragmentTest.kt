package com.example.sharingang

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
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
        onView(withId(R.id.editItemTitle)).perform(
            typeText(itemTitle),
            closeSoftKeyboard()
        )
        onView(withId(R.id.editItemDescription)).perform(
            typeText(itemDescription),
            closeSoftKeyboard()
        )
        val buttonCreate = onView(withId(R.id.createItemButton))
        buttonCreate.check(matches(withText("Create Item")))
        buttonCreate.perform(click())

        onView(withText(itemTitle)).perform(click())

        onView(withId(R.id.itemTitle))
            .check(matches(withText(itemTitle)))

        onView(withId(R.id.itemDescription))
            .check(matches(withText(itemDescription)))
    }

    @Test
    fun canSeeCategoryInDetailedItemFragment() {
        val testTitle = "Book Item"
        val testCategory = "Book"
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

    @Test
    fun anItemBeSharedFromDetailedItemFragment() {
        navigate_to(R.id.newItemFragment)
        onView(withId(R.id.newItemPrompt))
            .check(matches(withText("New Item")))
        onView(withId(R.id.editItemTitle)).perform(
            typeText(itemTitle),
            closeSoftKeyboard()
        )
        val buttonCreate = onView(withId(R.id.createItemButton))
        buttonCreate.check(matches(withText("Create Item")))
        buttonCreate.perform(click())

        onView(withText(itemTitle)).perform(click())

        onView(withId(R.id.itemTitle))
            .check(matches(withText(itemTitle)))

        Intents.init()
        onView(withId(R.id.shareButton)).perform(click())

        // The ACTION_SHARE intent is inside an ACTION_CHOOSER intent
        intended(hasAction(Intent.ACTION_CHOOSER))
        val actionChooserIntent = Intents.getIntents().firstOrNull()
        val sendIntent = actionChooserIntent?.extras?.get(Intent.EXTRA_INTENT) as Intent?
        val link = sendIntent?.extras?.getString(Intent.EXTRA_TEXT)
        Intents.release()

        val linkPrefix = "https://sharingang.page.link?apn=com.example.sharingang&link=https%3A%2F%2Fsharingang.page.link%2Fitem%3Fid%3D"
        assert(link?.startsWith(linkPrefix) ?: false)
    }
}