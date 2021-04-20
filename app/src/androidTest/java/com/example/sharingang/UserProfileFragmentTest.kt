package com.example.sharingang


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class UserProfileFragmentTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    // We start with the main activity, and then navigate where we want
    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private val fakeText = "Fake"

    @Test
    fun aUserCanSeeTheirItems() {
        navigate_to(R.id.newItemFragment)
        onView(withId(R.id.newItemPrompt)).check(matches(withText("New Item")))

        onView(withId(R.id.editItemTitle)).perform(
            ViewActions.typeText(fakeText),
            ViewActions.closeSoftKeyboard()
        )
        val button = onView(withId(R.id.createItemButton))
        button.check(matches(withText("Create Item")))
        button.perform(ViewActions.click())

        navigate_to(R.id.userProfileFragment)

        val textView = onView(withId(R.id.nameText))
        textView.check(matches(withText(FakeCurrentUserProvider.fakeUser.name)))
        onView(withText(fakeText)).check(matches(ViewMatchers.isDisplayed()))
    }
}
