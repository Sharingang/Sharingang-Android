package com.example.sharingang

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.sharingang.ui.activities.MainActivity
import com.example.sharingang.utils.navigate_to
import com.example.sharingang.utils.waitAfterSaveItem
import com.example.sharingang.utils.withMenuIdOrText
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
        navigate_to(R.id.newEditFragment)
        onView(withId(R.id.itemPrompt))
            .check(matches(withText("New Item")))
        onView(withId(R.id.itemTitle)).perform(
            ViewActions.typeText(itemTitle),
            ViewActions.closeSoftKeyboard()
        )
        val buttonCreate = onView(withId(R.id.saveItemButton))
        buttonCreate.check(matches(withText("Create Item")))
        buttonCreate.perform(ViewActions.click())
        waitAfterSaveItem()

        onView(withId(R.id.item_list_view_title)).perform(ViewActions.click())

        onView(withMenuIdOrText(R.id.menuSell, R.string.sell)).perform(ViewActions.click())

        onView(withMenuIdOrText(R.id.menuResell, R.string.unsell)).check(matches(isDisplayed()))
    }
}
