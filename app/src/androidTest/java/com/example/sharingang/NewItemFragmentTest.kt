package com.example.sharingang

import android.Manifest
import android.content.Intent
import android.provider.MediaStore
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import com.example.sharingang.ui.activities.MainActivity
import com.example.sharingang.utils.*
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

    private val firstItem = "First Item"
    private val secondItem = "Second Item"

    @Test
    fun aDescriptionCanBeEnteredAndSeenOnMainActivity() {
        navigate_to(R.id.newEditFragment)
        onView(withId(R.id.itemPrompt)).check(matches(withText("New Item")))

        onView(withId(R.id.itemTitle)).perform(
            typeText(firstItem),
            closeSoftKeyboard()
        )
        val button = onView(withId(R.id.saveItemButton))
        button.check(matches(withText("Create Item")))
        button.perform(scrollTo(), click())
        waitAfterSaveItem()

        onView(withText(firstItem)).check(matches(isDisplayed()))

        navigate_to(R.id.newEditFragment)
        onView(withId(R.id.itemTitle)).perform(
            typeText(secondItem),
            closeSoftKeyboard()
        )
        onView(withId(R.id.saveItemButton)).perform(scrollTo(), click())
        waitAfterSaveItem()

        onView(withText(firstItem)).check(matches(isDisplayed()))
        onView(withText(secondItem)).check(matches(isDisplayed()))
    }

    @Test
    fun anImageCanBeChosenFromGallery() {
        val activity = getActivity(activityRule)
        savePickedImage(activity)
        val imgGalleryResult = createImageGallerySetResultStub(activity)
        Intents.init()
        Intents.intending(IntentMatchers.hasAction(Intent.ACTION_GET_CONTENT))
            .respondWith(imgGalleryResult)

        navigate_to(R.id.newEditFragment)
        onView(withId(R.id.itemPrompt)).check(matches(withText("New Item")))

        onView(withId(R.id.item_image)).perform(click())
        onView(withId(R.id.item_image)).check(matches(hasContentDescription()))
        Intents.release()
    }

    @Test
    fun aPictureCanBeTakenAndDisplayed() {
        val activity = getActivity(activityRule)
        savePickedImage(activity)
        val imgGalleryResult = createImageGallerySetResultStub(activity)
        Intents.init()
        Intents.intending(IntentMatchers.hasAction(MediaStore.ACTION_IMAGE_CAPTURE))
            .respondWith(imgGalleryResult)

        navigate_to(R.id.newEditFragment)
        onView(withId(R.id.itemPrompt)).check(matches(withText("New Item")))

        onView(withId(R.id.item_take_picture)).perform(click())
        onView(withId(R.id.item_image)).check(matches(hasContentDescription()))
        Intents.release()
    }

    @Test
    fun clickingOnGetLocationDisplaysLocation() {
        navigate_to(R.id.newEditFragment)
        val button = onView(withId(R.id.item_get_location))
        button.perform(scrollTo(), click())
        Thread.sleep(5000)
        onView(withId(R.id.postal_address)).check(matches(not(withText(""))))
    }

    @Test
    fun aLocationCanBeSetInNewItemFragment() {
        navigate_to(R.id.newEditFragment)
        onView(withId(R.id.autocomplete_fragment)).perform(scrollTo(), click())
        onView(withHint("Enter Address")).perform(typeText("Taj"), closeSoftKeyboard())
        Thread.sleep(3000)
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.click(device.displayWidth / 2, device.displayHeight / 2)
        Thread.sleep(3000)
        onView(withId(R.id.postal_address)).check(matches(withText(containsString("Taj"))))
    }

    @Test
    fun addressSearchCanBeCanceled() {
        navigate_to(R.id.newEditFragment)
        onView(withId(R.id.autocomplete_fragment)).perform(scrollTo(), click())
        val device = UiDevice.getInstance((InstrumentationRegistry.getInstrumentation()))
        device.pressBack()
        // On Cirrus we only have to press back once because the soft keyboard is disabled
        try {
            onView(withId(R.id.postal_address)).check(matches(withText("")))
        } catch (_: NoMatchingViewException) {
            device.pressBack()
        }
        onView(withId(R.id.postal_address)).check(matches(withText("")))
    }

    @Test
    fun canSetQuantity() {
        navigate_to(R.id.newEditFragment)
        onView(withId(R.id.itemTitle)).perform(
            typeText(firstItem),
            closeSoftKeyboard()
        )
        onView(withId(R.id.itemPrice)).perform(
            typeText("10"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.itemQuantity)).perform(
            typeText("2"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.saveItemButton)).perform(scrollTo(), click())
        waitAfterSaveItem()
        onView(withText(firstItem)).perform(click())
        onView(withId(R.id.detailedItemQuantity)).check(matches(withText("Quantity: 2")))
    }

}
