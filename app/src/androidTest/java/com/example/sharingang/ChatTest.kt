package com.example.sharingang

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.example.sharingang.auth.FakeCurrentUserProvider
import com.example.sharingang.ui.activities.MainActivity
import com.example.sharingang.utils.navigate_to
import com.example.sharingang.utils.waitAfterSaveItem
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
        onView(withId(R.id.btnChat)).check(matches(not(isDisplayed())))
    }

    @Test
    fun loggedOutUserSeesLoggedOutInfo() {
        FakeCurrentUserProvider.instance = FakeCurrentUserProvider.Instance.LOGGED_OUT
        navigate_to(R.id.chatsFragment)
        onView(withId(R.id.loggedOutInfo)).check(matches(isDisplayed()))
        onView(withId(R.id.loggedOutInfo)).check(matches(withText("You need to be logged in to chat.")))
    }

    @Test
    fun loggedInUserCanSendAMessageToAUser() {
        FakeCurrentUserProvider.instance = FakeCurrentUserProvider.Instance.FAKE_USER_1
        navigate_to(R.id.newEditFragment)
        onView(withId(R.id.itemTitle)).perform(
            typeText("TestItem"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.createItemButton)).perform(click())
        waitAfterSaveItem()
        FakeCurrentUserProvider.instance = FakeCurrentUserProvider.Instance.FAKE_USER_2
        onView(withId(R.id.item_list_view_title)).perform(click())
        onView(withId(R.id.itemPostedBy)).perform(click())
        onView(withId(R.id.btnChat)).perform(click())
        onView(withId(R.id.chatPartnerUsername)).check(matches(withText(FakeCurrentUserProvider.fakeUser1.name)))
        val message = getRandomString(15)
        onView(withId(R.id.messageEditText)).check(matches(isClickable()))
        onView(withId(R.id.messageEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.messageEditText)).perform(
            replaceText(message),
            closeSoftKeyboard()
        )
        Thread.sleep(1000)
        onView(withId(R.id.btnSend)).perform(click())
        onView(withText(message)).check(matches(isDisplayed()))
        onView(withId(R.id.messageEditText)).check(matches(withText("")))
        onView(withId(R.id.btnSend)).check(matches(not(isEnabled())))
        Espresso.pressBack()
        Espresso.pressBack()
        Espresso.pressBack()
        FakeCurrentUserProvider.instance = FakeCurrentUserProvider.Instance.FAKE_USER_1
        navigate_to(R.id.chatsFragment)
        onView(withText("Test User 2")).perform(click())
        onView(withText(message)).check(matches(isDisplayed()))
        onView(withId(R.id.messageEditText)).check(matches(withText("")))
        onView(withId(R.id.btnSend)).check(matches(not(isEnabled())))
        val message2 = getRandomString(15)
        onView(withId(R.id.messageEditText)).perform(
            replaceText(message2),
            closeSoftKeyboard()
        )
        onView(withId(R.id.btnSend)).perform(click())
        Thread.sleep(1000)
        onView(withId(R.id.messageEditText)).check(matches(withText("")))
        onView(withText(message2)).check(matches(isDisplayed()))
        onView(withId(R.id.btnSend)).check(matches(not(isEnabled())))
        Espresso.pressBack()
        onView(withText("Test User 2")).check(matches(isDisplayed()))
    }

    /**
     * Generate a random string of chosen length. This is used for generating
     * a random message to send. Because of caching issues, when testing, we want
     * to make sure that no multiple views match the same text.
     *
     * @param length the length of the string
     */
    private fun getRandomString(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }


}
