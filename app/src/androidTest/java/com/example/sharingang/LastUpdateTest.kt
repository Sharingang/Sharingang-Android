package com.example.sharingang


import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.sharingang.auth.FakeCurrentUserProvider
import com.example.sharingang.ui.activities.MainActivity
import com.example.sharingang.utils.navigate_to
import com.example.sharingang.utils.waitAfterSaveItem
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.CoreMatchers.containsString
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class LastUpdateTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    // We start with the main activity, and then navigate where we want
    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun creatingItemShows0Days() {
        FakeCurrentUserProvider.instance = FakeCurrentUserProvider.Instance.FAKE_USER_1
        navigate_to(R.id.newEditFragment)
        onView(withId(R.id.itemTitle)).perform(
            replaceText("Title")
        )
        onView(withId(R.id.saveItemButton)).perform(
            scrollTo(), click()
        )
        waitAfterSaveItem()
        onView(withText("0s")).check(matches(isDisplayed()))
        onView(withId(R.id.item_list_view_title)).perform(click())
        onView(withText("0s")).check(matches(isDisplayed()))
        onView(withId(R.id.menuEdit)).perform(click())
        onView(withId(R.id.itemTitle)).perform(
            replaceText("newTitle")
        )
        onView(withId(R.id.saveItemButton)).perform(
            scrollTo(), click()
        )
        waitAfterSaveItem()
        Espresso.pressBack()
        Thread.sleep(1000)
        onView(withText("1s")).check(matches(isDisplayed()))
    }
}