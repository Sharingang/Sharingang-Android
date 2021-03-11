package com.example.sharingang

import android.os.Build
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MapActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MapActivity::class.java)
    //@get:Rule 
    //var permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)
    // code taken from https://alexzh.com/ui-testing-of-android-runtime-permissions/
    fun grantPermission() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        if (Build.VERSION.SDK_INT >= 23) {
            val allowPermission = UiDevice.getInstance(instrumentation).findObject(UiSelector().text(
                    when {
                        Build.VERSION.SDK_INT == 23 -> "Allow"
                        Build.VERSION.SDK_INT <= 28 -> "ALLOW"
                        Build.VERSION.SDK_INT == 29 -> "Allow only while using the app"
                        else -> "While using the app"
                    }
            ))
            if (allowPermission.exists()) {
                allowPermission.click()
            }
        }
    }
    fun denyPermission() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        if (Build.VERSION.SDK_INT >= 23) {
            val denyPermission = UiDevice.getInstance(instrumentation).findObject(UiSelector().text(
                    when (Build.VERSION.SDK_INT) {
                        in 24..28 -> "DENY"
                        else -> "Deny"
                    }
            ))
            if (denyPermission.exists()) {
                denyPermission.click()
            }
        }
    }
    @Test
    fun clickingOnButtonDisplayLocation(){
        val button = onView(withId(R.id.button_get_localisation))
        button.check(matches(withText("Get localisation")))
        val text = onView(withId(R.id.localisation_display))
        text.check(matches(withText("")))

        button.perform(click())
        grantPermission()
    }
}