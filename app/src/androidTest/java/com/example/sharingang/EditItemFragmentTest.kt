package com.example.sharingang

import android.content.Intent
import android.provider.MediaStore
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.example.sharingang.ui.activities.MainActivity
import com.example.sharingang.utils.*
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class EditItemFragmentTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    // We start with the main activity, and then navigate where we want
    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule(order = 2)
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.CAMERA
    )

    private val item = "Test"
    private val editedItem = "Edited"

    @Test
    fun canEditItemCategory() {
        navigate_to(R.id.newEditFragment)
        onView(withId(R.id.itemTitle)).perform(
            typeText(item),
            closeSoftKeyboard()
        )

        onView(withId(R.id.category_spinner)).perform(click())
        onView(withText("Book")).perform(click())

        onView(withId(R.id.saveItemButton)).perform(scrollTo(), click())
        waitAfterSaveItem()

        onView(withText(item)).check(matches(isDisplayed()))
        onView(withId(R.id.item_list_view_title)).perform(click())

        onView(withMenuIdOrText(R.id.menuEdit, R.string.edit_item)).perform(click())

        onView(withId(R.id.itemPrompt)).check(matches(withText("Edit Item")))
        onView(withId(R.id.category_spinner)).check(matches(withSpinnerText("Book")))
        onView(withId(R.id.category_spinner)).perform(click())
        onView(withText("Games")).perform(click())

        onView(withId(R.id.saveItemButton)).perform(scrollTo(), click())
        waitAfterSaveItem()

        onView(withMenuIdOrText(R.id.menuEdit, R.string.edit_item)).perform(click())

        onView(withId(R.id.category_spinner)).check(matches(withSpinnerText("Games")))
    }


    @Test
    fun anItemCanBeEditedAndSeenOnItemsListFragment() {
        val activity = getActivity(activityRule)
        savePickedImage(activity)
        val imgGalleryResult = createImageGallerySetResultStub(activity)
        Intents.init()
        intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(imgGalleryResult)
        intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(imgGalleryResult)

        navigate_to(R.id.newEditFragment)
        onView(withId(R.id.itemPrompt))
            .check(matches(withText("New Item")))
        onView(withId(R.id.itemTitle)).perform(
            typeText(item),
            closeSoftKeyboard()
        )
        val buttonCreate = onView(withId(R.id.saveItemButton))
        buttonCreate.check(matches(withText("Create Item")))
        buttonCreate.perform(scrollTo(), click())
        waitAfterSaveItem()

        onView(withText(item))
            .check(matches(isDisplayed()))

        onView(withId(R.id.item_list_view_title)).perform(click())

        onView(withMenuIdOrText(R.id.menuEdit, R.string.edit_item)).perform(click())

        onView(withId(R.id.itemPrompt))
            .check(matches(withText("Edit Item")))

        onView(withId(R.id.item_image)).perform(click())
        onView(withId(R.id.item_image)).check(matches(hasContentDescription()))

        onView(withId(R.id.item_take_picture)).perform(click())
        onView(withId(R.id.item_image)).check(matches(hasContentDescription()))

        onView(withId(R.id.itemTitle)).perform(
            typeText(editedItem),
            closeSoftKeyboard()
        )

        onView(withId(R.id.saveItemButton)).perform(scrollTo(), click())
        waitAfterSaveItem()

        pressBack()

        onView(withText(item + editedItem))
            .check(matches(isDisplayed()))
        Intents.release()
    }

    @Test
    fun aPictureCanBeTakenAndDisplayed() {
        val activity = getActivity(activityRule)
        savePickedImage(activity)
        val imgGalleryResult = createImageGallerySetResultStub(activity)
        Intents.init()
        intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(imgGalleryResult)

        navigate_to(R.id.newEditFragment)
        onView(withId(R.id.itemPrompt)).check(matches(withText("New Item")))

        onView(withId(R.id.item_take_picture)).perform(click())
        onView(withId(R.id.item_image)).check(matches(hasContentDescription()))
        Intents.release()
    }
}
