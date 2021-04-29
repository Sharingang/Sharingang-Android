package com.example.sharingang

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class WishlistViewTest {

    private val firstItem = "Test Item"
    private val secondItem = "Hello the world"
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    // We start with the main activity, and then navigate where we want
    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)


    @Test
    fun canAddAndRemoveItemToWishlist(){
        addItemsToDb(firstItem)
        addItemToWishList(firstItem)

        navigate_to(R.id.wishlistViewFragment)
        onView(withText(firstItem)).check(matches(isDisplayed()))
        onView(isRoot()).perform(pressBack())

        onView(withText(firstItem)).perform(click())
        onView(withId(R.id.addToWishlist)).check(matches(withText(R.string.remove_wishlist)))
        onView(withId(R.id.addToWishlist)).perform(click())
        onView(withId(R.id.addToWishlist)).check(matches(withText(R.string.add_wishlist)))
    }

    @Test
    fun canViewWishListItems(){
        addItemsToDb(firstItem, secondItem)
        addItemToWishList(firstItem, secondItem)

        navigate_to(R.id.wishlistViewFragment)
        onView(withText(firstItem)).perform(click())
        onView(withId(R.id.addToWishlist)).check(matches(withText(R.string.remove_wishlist)))
        onView(withId(R.id.addToWishlist)).perform(click())
        onView(isRoot()).perform(pressBack())
        Thread.sleep(1000)
        onView(withText(secondItem)).check(matches(isDisplayed()))
    }

    private fun addItemToWishList(vararg itemNames: String){
        for(itemName in itemNames){
            onView(withText(itemName)).perform(click())
            onView(withId(R.id.addToWishlist)).perform(click())
            onView(withId(R.id.addToWishlist)).check(matches(withText(R.string.remove_wishlist)))
            onView(isRoot()).perform(pressBack())
        }
    }


    private fun addItemsToDb(vararg itemNames: String){
        for(itemName in itemNames){
            navigate_to(R.id.newEditFragment)
            onView(withId(R.id.itemTitle)).perform(
                    typeText(itemName),
                    closeSoftKeyboard()
            )
            onView(withId(R.id.createItemButton)).perform(click())
            waitAfterSaveItem()
        }
    }
}
