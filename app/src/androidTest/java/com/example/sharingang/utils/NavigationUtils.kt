package com.example.sharingang.utils

import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import com.example.sharingang.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher

private fun childAtPosition(
    parentMatcher: Matcher<View>, position: Int
): Matcher<View> {

    return object : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description) {
            description.appendText("Child at position $position in parent ")
            parentMatcher.describeTo(description)
        }

        public override fun matchesSafely(view: View): Boolean {
            val parent = view.parent
            return parent is ViewGroup && parentMatcher.matches(parent)
                    && view == parent.getChildAt(position)
        }
    }
}

fun navigate_to(id: Int) {
    val appCompatImageButton = Espresso.onView(
        Matchers.allOf(
            ViewMatchers.withContentDescription("Open navigation drawer"),
            childAtPosition(
                Matchers.allOf(
                    ViewMatchers.withId(R.id.toolbar),
                    childAtPosition(
                        ViewMatchers.withClassName(Matchers.`is`("android.widget.LinearLayout")),
                        0
                    )
                ),
                1
            ),
            ViewMatchers.isDisplayed()
        )
    )
    appCompatImageButton.perform(ViewActions.click())

    val navigationMenuItemView = Espresso.onView(
        Matchers.allOf(
            ViewMatchers.withId(id),
            ViewMatchers.isDisplayed()
        )
    )
    navigationMenuItemView.perform(ViewActions.click())
}

