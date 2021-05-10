package com.example.sharingang

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.CoreMatchers.not
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class ChatTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun usersCannotChatThemselves() {
        navigate_to(R.id.userProfileFragment)
        Thread.sleep(2000)
        onView(withId(R.id.btnChat)).check(matches(not(isDisplayed())))
    }

    @Test
    fun loggedOutUserSeesLoggedOutInfo() {
        FakeCurrentUserProvider.instance = 0
        navigate_to(R.id.chatsFragment)
        Thread.sleep(2000)
        onView(withId(R.id.loggedOutInfo)).check(matches(isDisplayed()))
        onView(withId(R.id.loggedOutInfo)).check(matches(withText("You need to be logged in to chat.")))
    }

    @Test
    fun loggedInUserCanSendAMessageToAUser() {
        FakeCurrentUserProvider.instance = 1
        navigate_to(R.id.newEditFragment)
        onView(withId(R.id.itemTitle)).perform(
            typeText("TestItem"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.createItemButton)).perform(click())
        waitAfterSaveItem()
        FakeCurrentUserProvider.instance = 2
        onView(withId(R.id.item_list_view_title)).perform(click())
        onView(withId(R.id.itemPostedBy)).perform(click())
        Thread.sleep(2000)
        onView(withId(R.id.btnChat)).perform(click())
        Thread.sleep(2000)
        onView(withId(R.id.chatPartnerUsername)).check(matches(withText(FakeCurrentUserProvider.fakeUser1.name)))
        Thread.sleep(2000)
        onView(withId(R.id.messageEditText)).perform(
            typeText("Hello"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.btnSend)).perform(click())
        Thread.sleep(3000)
        onView(withText("Hello")).check(matches(isDisplayed()))
        Thread.sleep(1000)
        Espresso.pressBack()
        Espresso.pressBack()
        Espresso.pressBack()
        FakeCurrentUserProvider.instance = 1
        navigate_to(R.id.chatsFragment)
        onView(withId(R.id.chatPartnerUsername)).perform(click())
        onView(withText("Hello")).check(matches(isDisplayed()))
    }


}