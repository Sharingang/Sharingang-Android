package com.example.sharingang

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
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
class ItemShareLinkTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    // We start with the main activity, and then navigate where we want
    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun anItemBeSharedFromDetailedItemFragment() {
        val itemTitle = "T"

        navigate_to(R.id.editItemFragment)
        onView(withId(R.id.itemTitle)).perform(
            typeText(itemTitle),
            closeSoftKeyboard()
        )
        val buttonCreate = onView(withId(R.id.createItemButton))
        buttonCreate.check(matches(withText("Create Item")))
        buttonCreate.perform(click())

        onView(withId(R.id.item_list_view_title)).perform(click())

        onView(withId(R.id.itemTitle))
            .check(matches(withText(itemTitle)))
        onView(withId(R.id.detailed_item_image)).check(matches(isDisplayed()))

        Intents.init()
        onView(withId(R.id.shareButton)).perform(click())

        // The ACTION_SHARE intent is inside an ACTION_CHOOSER intent
        intended(hasAction(Intent.ACTION_CHOOSER))
        val actionChooserIntent = Intents.getIntents().firstOrNull()
        val sendIntent = actionChooserIntent?.extras?.get(Intent.EXTRA_INTENT) as Intent?
        val link = sendIntent?.extras?.getString(Intent.EXTRA_TEXT)
        Intents.release()

        val linkPrefix =
            "https://sharingang.page.link?apn=com.example.sharingang&link=https%3A%2F%2Fsharingang.page.link%2Fitem%3Fid%3D"
        assert(link?.startsWith(linkPrefix) ?: false)
    }
}