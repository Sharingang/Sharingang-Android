package com.example.sharingang

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
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
class RandomItemTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    // We start with the main activity, and then navigate where we want
    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)


    @Test
    fun weCanFindTheRandomItemWeMade() {
        navigate_to(R.id.newEditFragment)
        Espresso.onView(withId(R.id.itemPrompt))
            .check(matches(withText("New Item")))

        Espresso.onView(withId(R.id.itemTitle)).perform(
            typeText("A"),
            closeSoftKeyboard()
        )
        val button = Espresso.onView(withId(R.id.saveItemButton))
        button.check(matches(withText("Create Item")))
        button.perform(scrollTo(), click())
        waitAfterSaveItem()
        navigate_to(R.id.nav_random_item)
        Espresso.onView(withId(R.id.itemTitle))
            .check(matches(withText("A")))
    }
}
