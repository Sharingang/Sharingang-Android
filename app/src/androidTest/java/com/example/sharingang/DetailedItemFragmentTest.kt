package com.example.sharingang

import android.provider.MediaStore
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers
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

    @get:Rule(order = 2)
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )

    @get:Rule(order = 3)
    var mActivityTestRule = IntentsTestRule(MainActivity::class.java)

    @Test
    fun canSeeCategoryInDetailedItemFragment() {
        val testTitle = "Book Item"
        val testCategory = "Book"
        navigate_to(R.id.newEditFragment)
        onView(withId(R.id.itemTitle)).perform(
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
    fun canRateAUser() {
        val testTitle = "Book Item"

        navigate_to(R.id.userProfileFragment)
        onView(withId(R.id.rating_textview)).check(matches(withText("No Ratings")))
        pressBack()

        navigate_to(R.id.newEditFragment)
        onView(withId(R.id.itemTitle)).perform(
            typeText(testTitle),
            closeSoftKeyboard()
        )
        onView(withId(R.id.createItemButton)).perform(click())
        onView(withId(R.id.item_list_view_title)).perform(click())

        tryOpenOptionsMenu()
        onView(withId(R.id.menuSell)).perform(click())
        pressBack()

        onView(withText(testTitle)).perform(click())
        onView(withId(R.id.radioButton1)).check(matches(isDisplayed()))
        onView(withId(R.id.ratingButton)).check(matches(isDisplayed()))
        onView(withId(R.id.radioButton5)).perform(click())
        onView(withId(R.id.ratingButton)).perform(click())
        Thread.sleep(500)
        pressBack()
        navigate_to(R.id.userProfileFragment)
        onView(withId(R.id.rating_textview)).check(matches(withText("5.00")))
    }

    @Test
    fun weCanSeeTheImageWePickedForAnItem() {
        savePickedImage(mActivityTestRule.activity)
        val imgGalleryResult = createImageGallerySetResultStub(mActivityTestRule.activity)
        Intents.intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(imgGalleryResult)

        navigate_to(R.id.newEditFragment)
        onView(withId(R.id.item_take_picture)).perform(click())
        onView(withId(R.id.item_image)).check(matches(hasContentDescription()))
        val buttonCreate = onView(withId(R.id.createItemButton))
        buttonCreate.perform(click())

        onView(withId(R.id.item_list_view_title)).perform(click())

        onView(withId(R.id.detailed_item_image)).check(matches(isDisplayed()))
        onView(withId(R.id.itemPostedBy)).perform(click())
        val textView = onView(withId(R.id.nameText))
        textView.check(matches(withText(FakeCurrentUserProvider.fakeUser1.name)))

        val backButton =
            onView(Matchers.allOf(withContentDescription("Navigate up"), isDisplayed()))
        backButton.perform(click())
    }

    @Test
    fun canDeleteAnItem() {
        val testTitle = "To be deleted"
        navigate_to(R.id.newEditFragment)
        onView(withId(R.id.itemTitle)).perform(
            typeText(testTitle),
            closeSoftKeyboard()
        )
        onView(withId(R.id.createItemButton)).perform(click())
        // TODO wait for saving after PR #175

        onView(withText(testTitle)).perform(click())
        tryOpenOptionsMenu()
        onView(withId(R.id.menuDelete)).perform(click())
        // TODO wait for delete after PR #175
        onView(withText(testTitle)).check(doesNotExist())
    }
}
