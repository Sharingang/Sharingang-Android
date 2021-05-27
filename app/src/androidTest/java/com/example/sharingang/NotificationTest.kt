package com.example.sharingang

import android.app.NotificationManager
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.example.sharingang.ui.activities.MainActivity
import com.example.sharingang.utils.constants.NotificationFields
import com.example.sharingang.utils.getActivity
import com.example.sharingang.utils.navigate_to
import com.example.sharingang.utils.notification.sendNotification
import com.example.sharingang.utils.waitAfterSaveItem
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class NotificationTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    // We start with the main activity, and then navigate where we want
    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun aNotificationCanBeReceived() {
        val itemTitle = "T"

        navigate_to(R.id.newEditFragment)
        Espresso.onView(ViewMatchers.withId(R.id.itemTitle)).perform(
            ViewActions.typeText(itemTitle),
            ViewActions.closeSoftKeyboard()
        )
        val buttonCreate = Espresso.onView(ViewMatchers.withId(R.id.saveItemButton))
        buttonCreate.check(ViewAssertions.matches(ViewMatchers.withText("Create Item")))
        buttonCreate.perform(ViewActions.scrollTo(), ViewActions.click())
        waitAfterSaveItem()

        Espresso.onView(ViewMatchers.withId(R.id.item_list_view_title)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.itemTitle))
            .check(ViewAssertions.matches(ViewMatchers.withText(itemTitle)))

        Intents.init()
        Espresso.onView(ViewMatchers.withId(R.id.shareButton)).perform(ViewActions.click())

        // The ACTION_SHARE intent is inside an ACTION_CHOOSER intent
        Intents.intended(IntentMatchers.hasAction(Intent.ACTION_CHOOSER))
        val actionChooserIntent = Intents.getIntents().firstOrNull()
        val sendIntent = actionChooserIntent?.extras?.get(Intent.EXTRA_INTENT) as Intent?
        val link = sendIntent?.extras?.getString(Intent.EXTRA_TEXT)
        Intents.release()

        val linkPrefix =
            "https://sharingang.page.link?apn=com.example.sharingang&link=https%3A%2F%2Fsharingang.page.link%2Fitem%3Fid%3D"
        assert(link?.startsWith(linkPrefix) ?: false)

        Thread.sleep(1000)
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.pressBack()
        device.pressBack()
        Thread.sleep(1000)

        val deepLink = Uri.parse(link).getQueryParameter("link")!!

        val activity = getActivity(activityRule)
        val applicationContext = activity.applicationContext
        val notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.sendNotification("Test message body", "Test Title", NotificationFields.Companion.NEW_ITEM_CHANNEL_ID, deepLink, applicationContext)
        // Wait for notification to clear up so later tests don't have the top of the screen obstructed
        Thread.sleep(6000)
    }
}
