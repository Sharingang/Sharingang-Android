package com.example.sharingang


import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
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
class BlockTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    // We start with the main activity, and then navigate where we want
    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun usersCannotBlockThemselves() {
        FakeCurrentUserProvider.instance = FakeCurrentUserProvider.Instance.FAKE_USER_1
        navigate_to(R.id.userProfileFragment)
        onView(withId(R.id.btn_block)).check(matches(not(isDisplayed())))
    }

    @Test
    fun userCanBlockAnotherUser() {
        FakeCurrentUserProvider.instance = FakeCurrentUserProvider.Instance.FAKE_USER_1
        navigate_to(R.id.newEditFragment)
        onView(withId(R.id.itemTitle)).perform(replaceText("Title"))
        onView(withId(R.id.saveItemButton)).perform(scrollTo(), click())
        waitAfterSaveItem()
        FakeCurrentUserProvider.instance = FakeCurrentUserProvider.Instance.FAKE_USER_2
        onView(withId(R.id.item_list_view_title)).perform(click())
        onView(withId(R.id.itemPostedBy)).perform(click())
        onView(withId(R.id.btn_block)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_block)).check(matches(withText("Block User")))
        onView(withId(R.id.btn_block)).perform(click())
        onView(withId(R.id.text_blockedUsername)).check(matches(withText(FakeCurrentUserProvider.fakeUser1.name)))
        onView(withId(R.id.button_ok)).check(matches(not(isEnabled())))
        onView(withId(R.id.radio_rude)).perform(click())
        onView(withId(R.id.button_ok)).check(matches(isEnabled()))
        onView(withId(R.id.block_description)).perform(replaceText("Description"))
        onView(withId(R.id.button_ok)).perform(click())
        onView(withId(R.id.btn_block)).check(matches(not(isDisplayed())))
    }

    @Test
    fun loggedOutUserCannotBlockAUser() {
        FakeCurrentUserProvider.instance = FakeCurrentUserProvider.Instance.FAKE_USER_1
        navigate_to(R.id.newEditFragment)
        onView(withId(R.id.itemTitle)).perform(replaceText("Title"))
        onView(withId(R.id.saveItemButton)).perform(scrollTo(), click())
        waitAfterSaveItem()
        FakeCurrentUserProvider.instance = FakeCurrentUserProvider.Instance.LOGGED_OUT
        onView(withId(R.id.item_list_view_title)).perform(click())
        onView(withId(R.id.itemPostedBy)).perform(click())
        onView(withId(R.id.btn_block)).check(matches(not(isDisplayed())))
    }

    @Test
    fun blockedUserCannotClickDetailedItemPostedBy() {
        FakeCurrentUserProvider.instance = FakeCurrentUserProvider.Instance.FAKE_USER_1
        navigate_to(R.id.newEditFragment)
        onView(withId(R.id.itemTitle)).perform(replaceText("Title"))
        onView(withId(R.id.saveItemButton)).perform(scrollTo(), click())
        waitAfterSaveItem()
        FakeCurrentUserProvider.instance = FakeCurrentUserProvider.Instance.FAKE_USER_2
        onView(withId(R.id.item_list_view_title)).perform(click())
        onView(withId(R.id.itemPostedBy)).perform(click())
        onView(withId(R.id.btn_block)).perform(click())
        onView(withId(R.id.radio_scam)).perform(click())
        onView(withId(R.id.button_ok)).perform(click())
        Espresso.pressBack()
        Espresso.pressBack()
        navigate_to(R.id.newEditFragment)
        onView(withId(R.id.itemTitle)).perform(replaceText("Title2"))
        onView(withId(R.id.saveItemButton)).perform(scrollTo(), click())
        waitAfterSaveItem()
        FakeCurrentUserProvider.instance = FakeCurrentUserProvider.Instance.FAKE_USER_1
        onView(withText("Title2")).perform(click())
        onView(withId(R.id.itemPostedBy)).check(matches(not(isEnabled())))
    }

    @Test
    fun blockedUserIsSeenInBlockedUsersList() {
        FakeCurrentUserProvider.instance = FakeCurrentUserProvider.Instance.FAKE_USER_1
        navigate_to(R.id.newEditFragment)
        onView(withId(R.id.itemTitle)).perform(replaceText("Title"))
        onView(withId(R.id.saveItemButton)).perform(scrollTo(), click())
        waitAfterSaveItem()
        FakeCurrentUserProvider.instance = FakeCurrentUserProvider.Instance.FAKE_USER_2
        onView(withId(R.id.item_list_view_title)).perform(click())
        onView(withId(R.id.itemPostedBy)).perform(click())
        onView(withId(R.id.btn_block)).perform(click())
        onView(withId(R.id.radio_other)).perform(click())
        onView(withId(R.id.button_ok)).perform(click())
        Espresso.pressBack()
        Espresso.pressBack()
        navigate_to(R.id.blockedUsersFragment)
        onView(withText(FakeCurrentUserProvider.fakeUser1.name)).check(matches(isDisplayed()))
    }

    @Test
    fun informationIsDisplayedWhenSeeingBlockInfo() {
        FakeCurrentUserProvider.instance = FakeCurrentUserProvider.Instance.FAKE_USER_1
        navigate_to(R.id.newEditFragment)
        onView(withId(R.id.itemTitle)).perform(replaceText("Title"))
        onView(withId(R.id.saveItemButton)).perform(scrollTo(), click())
        waitAfterSaveItem()
        FakeCurrentUserProvider.instance = FakeCurrentUserProvider.Instance.FAKE_USER_2
        onView(withId(R.id.item_list_view_title)).perform(click())
        onView(withId(R.id.itemPostedBy)).perform(click())
        onView(withId(R.id.btn_block)).perform(click())
        onView(withId(R.id.radio_scam)).perform(click())
        onView(withId(R.id.block_description)).perform(replaceText("Scammer"))
        onView(withId(R.id.button_ok)).perform(click())
        Espresso.pressBack()
        Espresso.pressBack()
        navigate_to(R.id.blockedUsersFragment)
        onView(withId(R.id.buttonBlockInfo)).perform(click())
        onView(withText("Reason: Scamming\nDescription: Scammer")).check(matches(isDisplayed()))
        onView(withText("OK")).perform(click())
    }

    @Test
    fun usersCanBlockWithoutADescription() {
        FakeCurrentUserProvider.instance = FakeCurrentUserProvider.Instance.FAKE_USER_1
        navigate_to(R.id.newEditFragment)
        onView(withId(R.id.itemTitle)).perform(replaceText("Title"))
        onView(withId(R.id.saveItemButton)).perform(scrollTo(), click())
        waitAfterSaveItem()
        FakeCurrentUserProvider.instance = FakeCurrentUserProvider.Instance.FAKE_USER_2
        onView(withId(R.id.item_list_view_title)).perform(click())
        onView(withId(R.id.itemPostedBy)).perform(click())
        onView(withId(R.id.btn_block)).perform(click())
        onView(withId(R.id.radio_scam)).perform(click())
        onView(withId(R.id.button_ok)).perform(click())
        Espresso.pressBack()
        Espresso.pressBack()
        navigate_to(R.id.blockedUsersFragment)
        onView(withId(R.id.buttonBlockInfo)).perform(click())
        onView(withText("Reason: Scamming\nDescription: ")).check(matches(isDisplayed()))
        onView(withText("OK")).perform(click())
    }

    @Test
    fun blockedUsersGetRemovedFromChatListWhileBlocked() {
        FakeCurrentUserProvider.instance = FakeCurrentUserProvider.Instance.FAKE_USER_1
        navigate_to(R.id.newEditFragment)
        onView(withId(R.id.itemTitle)).perform(replaceText("Title"))
        onView(withId(R.id.saveItemButton)).perform(scrollTo(), click())
        waitAfterSaveItem()
        FakeCurrentUserProvider.instance = FakeCurrentUserProvider.Instance.FAKE_USER_2
        onView(withId(R.id.item_list_view_title)).perform(click())
        onView(withId(R.id.itemPostedBy)).perform(click())
        onView(withId(R.id.btnChat)).perform(click())
        val message = "abcdefg"
        onView(withId(R.id.messageEditText)).perform(replaceText(message))
        onView(withId(R.id.btnSend)).perform(click())
        Thread.sleep(1000)
        Espresso.pressBack()
        Espresso.pressBack()
        Espresso.pressBack()
        onView(withId(R.id.item_list_view_title)).perform(click())
        onView(withId(R.id.itemPostedBy)).perform(click())
        onView(withId(R.id.btn_block)).perform(click())
        onView(withId(R.id.radio_scam)).perform(click())
        onView(withId(R.id.button_ok)).perform(click())
        onView(withId(R.id.btnChat)).check(matches(not(isDisplayed())))
        Espresso.pressBack()
        Espresso.pressBack()
        navigate_to(R.id.chatsFragment)
        onView(withText(FakeCurrentUserProvider.fakeUser1.name)).check(doesNotExist())
        Espresso.pressBack()
        FakeCurrentUserProvider.instance = FakeCurrentUserProvider.Instance.FAKE_USER_1
        navigate_to(R.id.chatsFragment)
        onView(withText(FakeCurrentUserProvider.fakeUser2.name)).check(doesNotExist())
        Espresso.pressBack()
        FakeCurrentUserProvider.instance = FakeCurrentUserProvider.Instance.FAKE_USER_2
        navigate_to(R.id.blockedUsersFragment)
        onView(withId(R.id.btnUnblock)).check(matches(withText("unblock")))
        onView(withId(R.id.btnUnblock)).perform(click())
        onView(withText(FakeCurrentUserProvider.fakeUser1.name)).check(doesNotExist())
        Espresso.pressBack()
        navigate_to(R.id.blockedUsersFragment)
        onView(withText(FakeCurrentUserProvider.fakeUser1.name)).check(doesNotExist())
    }

}