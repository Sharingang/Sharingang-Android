package com.example.sharingang

import android.Manifest
import android.content.Intent
import android.provider.MediaStore
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class NewItemFragmentTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    // We start with the main activity, and then navigate where we want
    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule(order = 2)
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.CAMERA
    )

    @get:Rule(order = 3)
    var mActivityTestRule = IntentsTestRule(MainActivity::class.java)

    private val firstItem = "First Item"
    private val secondItem = "Second Item"

    @Test
    fun aDescriptionCanBeEnteredAndSeenOnMainActivity() {
        navigate_to(R.id.newEditFragment)
        onView(withId(R.id.newItemPrompt)).check(matches(withText("New Item")))

        onView(withId(R.id.itemTitle)).perform(
            typeText(firstItem),
            closeSoftKeyboard()
        )
        val button = onView(withId(R.id.createItemButton))
        button.check(matches(withText("Create Item")))
        button.perform(click())
        waitAfterSaveItem()

        onView(withText(firstItem)).check(matches(isDisplayed()))

        navigate_to(R.id.newEditFragment)
        onView(withId(R.id.itemTitle)).perform(
            typeText(secondItem),
            closeSoftKeyboard()
        )
        onView(withId(R.id.createItemButton)).perform(click())
        waitAfterSaveItem()

        onView(withText(firstItem)).check(matches(isDisplayed()))
        onView(withText(secondItem)).check(matches(isDisplayed()))
    }

    @Test
    fun anImageCanBeChosenFromGallery() {
        savePickedImage(mActivityTestRule.activity)
        val imgGalleryResult = createImageGallerySetResultStub(mActivityTestRule.activity)
        Intents.intending(IntentMatchers.hasAction(Intent.ACTION_GET_CONTENT))
            .respondWith(imgGalleryResult)

        navigate_to(R.id.newEditFragment)
        onView(withId(R.id.newItemPrompt)).check(matches(withText("New Item")))

        onView(withId(R.id.item_image)).perform(click())
        onView(withId(R.id.item_image)).check(matches(hasContentDescription()))
    }

    @Test
    fun aPictureCanBeTakenAndDisplayed() {
        savePickedImage(mActivityTestRule.activity)
        val imgGalleryResult = createImageGallerySetResultStub(mActivityTestRule.activity)
        Intents.intending(IntentMatchers.hasAction(MediaStore.ACTION_IMAGE_CAPTURE))
            .respondWith(imgGalleryResult)

        navigate_to(R.id.newEditFragment)
        onView(withId(R.id.newItemPrompt)).check(matches(withText("New Item")))

        onView(withId(R.id.item_take_picture)).perform(click())
        onView(withId(R.id.item_image)).check(matches(hasContentDescription()))
    }

    @Test
    fun clickingOnGetLocationDisplaysLocation() {
        navigate_to(R.id.newEditFragment)
        val button = onView(withId(R.id.item_get_location))
        button.check(matches(withText("Get Location")))
        button.perform(click())
        Thread.sleep(5000)
        onView(withId(R.id.postal_address)).check(matches(not(withText(""))))
    }

    @Test
    fun aLocationCanBeSetInNewItemFragment() {
        navigate_to(R.id.newEditFragment)
        onView(withId(R.id.autocomplete_fragment)).perform(click())
        onView(withHint("Enter Address")).perform(typeText("Taj"), closeSoftKeyboard())
        Thread.sleep(3000)
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.click(device.displayWidth / 2, device.displayHeight / 2)
        Thread.sleep(3000)
        onView(withId(R.id.postal_address)).check(matches(withText(containsString("Taj"))))
    }
}
