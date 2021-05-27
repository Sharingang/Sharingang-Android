package com.example.sharingang

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.sharingang.dependencyinjection.FakeRepositoryModule.WEATHER
import com.example.sharingang.ui.activities.MainActivity
import com.example.sharingang.utils.navigate_to
import com.example.sharingang.utils.waitAfterSaveItem
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class WeatherFragmentTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    // We start with the main activity, and then navigate where we want
    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)


    @Test
    fun canCheckWeather() {
        val testTitle = "T"
        navigate_to(R.id.newEditFragment)
        Espresso.onView(ViewMatchers.withId(R.id.itemTitle)).perform(
            ViewActions.typeText(testTitle),
            ViewActions.closeSoftKeyboard()
        )
        Espresso.onView(ViewMatchers.withId(R.id.saveItemButton))
            .perform(ViewActions.scrollTo(), ViewActions.click())
        waitAfterSaveItem()
        Espresso.onView(ViewMatchers.withText(testTitle)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.weatherButton)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.weather_text)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    WEATHER.toString()
                )
            )
        )
    }
}