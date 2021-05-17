package com.example.sharingang.utils

import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.Matcher

fun withMenuIdOrText(@IdRes id: Int, @StringRes menuText: Int): Matcher<View?>? {
    val matcher = withId(id)
    return try {
        onView(matcher).check(matches(isDisplayed()))
        matcher
    } catch (_: java.lang.Exception) {
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        withText(menuText)
    }
}

