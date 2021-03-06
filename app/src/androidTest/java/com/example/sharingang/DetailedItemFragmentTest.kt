package com.example.sharingang

import android.provider.MediaStore
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.example.sharingang.auth.FakeCurrentUserProvider
import com.example.sharingang.payment.FakePaymentProvider
import com.example.sharingang.ui.activities.MainActivity
import com.example.sharingang.utils.*
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
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.CAMERA
    )

    private val firstItem = "Test Item"
    private val secondItem = "Hello the world"

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
        onView(withId(R.id.saveItemButton)).perform(scrollTo(), click())
        waitAfterSaveItem()

        onView(withText(testTitle)).perform(click())
        onView(withId(R.id.itemCategory)).check(matches(withText(testCategory)))
    }

    @Test
    fun canRateAUser() {
        val testTitle = "Book Item"
        FakeCurrentUserProvider.instance = FakeCurrentUserProvider.Instance.FAKE_USER_1
        navigate_to(R.id.userProfileFragment)
        onView(withId(R.id.rating_textview)).check(matches(withText("No Ratings")))
        pressBack()
        addItemsToDb(testTitle)
        waitAfterSaveItem()
        FakeCurrentUserProvider.instance = FakeCurrentUserProvider.Instance.FAKE_USER_2
        onView(withText(testTitle)).perform(click())
        FakePaymentProvider.paymentStatus = FakePaymentProvider.Status.ALWAYS_ACCEPT
        onView(withId(R.id.buyButton)).perform(click())
        pressBack()
        Thread.sleep(1000)
        onView(withText(testTitle)).perform(click())
        onView(withId(R.id.radioButton1)).check(matches(isDisplayed()))
        onView(withId(R.id.ratingButton)).check(matches(isDisplayed()))
        onView(withId(R.id.radioButton5)).perform(click())
        onView(withId(R.id.ratingButton)).perform(click())
        Thread.sleep(500)
        pressBack()
        FakeCurrentUserProvider.instance = FakeCurrentUserProvider.Instance.FAKE_USER_1
        navigate_to(R.id.userProfileFragment)
        onView(withId(R.id.rating_textview)).check(matches(withText("5.00")))
    }

    @Test
    fun weCanSeeTheImageWePickedForAnItem() {
        val activity = getActivity(activityRule)
        savePickedImage(activity)
        val imgGalleryResult = createImageGallerySetResultStub(activity)
        Intents.init()
        Intents.intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(imgGalleryResult)

        navigate_to(R.id.newEditFragment)
        onView(withId(R.id.itemTitle)).perform(
            typeText("Test"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.item_take_picture)).perform(click())
        onView(withId(R.id.item_image)).check(matches(hasContentDescription()))
        val buttonCreate = onView(withId(R.id.saveItemButton))
        buttonCreate.perform(scrollTo(), click())
        waitAfterSaveItem()

        onView(withId(R.id.item_list_view_title)).perform(click())

        onView(withId(R.id.detailed_item_image)).check(matches(isDisplayed()))
        onView(withId(R.id.itemPostedBy)).perform(click())
        val textView = onView(withId(R.id.nameText))
        textView.check(matches(withText(FakeCurrentUserProvider.fakeUser1.name)))

        val backButton =
            onView(Matchers.allOf(withContentDescription("Navigate up"), isDisplayed()))
        backButton.perform(click())
        Intents.release()
    }

    @Test
    fun canAddAndRemoveItemToWishlist() {
        FakeCurrentUserProvider.instance = FakeCurrentUserProvider.Instance.FAKE_USER_2
        addItemsToDb(firstItem)
        FakeCurrentUserProvider.instance = FakeCurrentUserProvider.Instance.FAKE_USER_1
        addItemToWishList(firstItem)
        navigate_to(R.id.wishlistViewFragment)
        onView(withText(firstItem)).check(matches(isDisplayed()))
        onView(isRoot()).perform(ViewActions.pressBack())

        onView(withText(firstItem)).perform(click())
        onView(withId(R.id.addToWishlist)).check(matches(withText(R.string.remove_wishlist)))
        onView(withId(R.id.addToWishlist)).perform(click())
        onView(withId(R.id.addToWishlist)).check(matches(withText(R.string.add_wishlist)))
    }

    @Test
    fun canViewWishListItems() {
        FakeCurrentUserProvider.instance = FakeCurrentUserProvider.Instance.FAKE_USER_2
        addItemsToDb(firstItem, secondItem)
        FakeCurrentUserProvider.instance = FakeCurrentUserProvider.Instance.FAKE_USER_1
        addItemToWishList(firstItem, secondItem)

        navigate_to(R.id.wishlistViewFragment)
        onView(withText(firstItem)).perform(click())
        onView(withId(R.id.addToWishlist)).check(matches(withText(R.string.remove_wishlist)))
        onView(withId(R.id.addToWishlist)).perform(click())
        onView(isRoot()).perform(ViewActions.pressBack())
        Thread.sleep(1000)
        onView(withText(secondItem)).check(matches(isDisplayed()))
    }

    private fun addItemToWishList(vararg itemNames: String) {
        for (itemName in itemNames) {
            onView(withText(itemName)).perform(click())
            onView(withId(R.id.addToWishlist)).perform(click())
            onView(withId(R.id.addToWishlist)).check(matches(withText(R.string.remove_wishlist)))
            onView(isRoot()).perform(ViewActions.pressBack())
        }
    }


    private fun addItemsToDb(vararg itemNames: String) {
        for (itemName in itemNames) {
            navigate_to(R.id.newEditFragment)
            onView(withId(R.id.itemTitle)).perform(
                typeText(itemName),
                closeSoftKeyboard()
            )
            onView(withId(R.id.itemPrice)).perform(
                typeText("10"),
                closeSoftKeyboard()
            )
            onView(withId(R.id.saveItemButton)).perform(scrollTo(), click())
            waitAfterSaveItem()
        }
    }

    @Test
    fun canDeleteAnItem() {
        val testTitle = "To be deleted"
        navigate_to(R.id.newEditFragment)
        onView(withId(R.id.itemTitle)).perform(
            typeText(testTitle),
            closeSoftKeyboard()
        )
        onView(withId(R.id.saveItemButton)).perform(scrollTo(), click())
        waitAfterSaveItem()
        onView(withText(testTitle)).perform(click())
        onView(withMenuIdOrText(R.id.menuDelete, R.string.delete_item)).perform(click())
        waitAfterSaveItem()
        onView(withText(testTitle)).check(doesNotExist())
    }


    @Test
    fun canBuyMultiple(){
        FakeCurrentUserProvider.instance = FakeCurrentUserProvider.Instance.FAKE_USER_2
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
            typeText("5"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.saveItemButton)).perform(scrollTo(), click())
        waitAfterSaveItem()

        FakeCurrentUserProvider.instance = FakeCurrentUserProvider.Instance.FAKE_USER_1
        onView(withText(firstItem)).perform(click())

        FakePaymentProvider.paymentStatus = FakePaymentProvider.Status.ALWAYS_ACCEPT
        onView(withId(R.id.buyQuantity)).perform(
            typeText("2"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.buyButton)).perform(click())
        waitAfterSaveItem()
        onView(withId(R.id.buyButton)).check(matches(isDisplayed()))
        onView(withId(R.id.ratingButton)).check(matches(isDisplayed()))
        onView(withId(R.id.detailedItemQuantity)).check(matches(withText("Quantity: 3")))

    }

    @Test
    fun canBuyAnItem() {
        // User 2 creates an item
        FakeCurrentUserProvider.instance = FakeCurrentUserProvider.Instance.FAKE_USER_2
        val testTitle = "For sale"
        navigate_to(R.id.newEditFragment)
        onView(withId(R.id.itemTitle)).perform(
            typeText(testTitle),
            closeSoftKeyboard()
        )
        onView(withId(R.id.itemPrice)).perform(
            typeText("42"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.saveItemButton)).perform(scrollTo(), click())
        waitAfterSaveItem()

        // User 1 clicks on the item
        FakeCurrentUserProvider.instance = FakeCurrentUserProvider.Instance.FAKE_USER_1
        onView(withText(testTitle)).perform(click())

        // If the payment is rejected, the button is still visible
        FakePaymentProvider.paymentStatus = FakePaymentProvider.Status.ALWAYS_REJECT
        onView(withId(R.id.buyButton)).perform(click())
        Thread.sleep(1000)
        onView(withId(R.id.buyButton)).check(matches(isDisplayed()))

        // If the payment is accepted, the button disappears
        FakePaymentProvider.paymentStatus = FakePaymentProvider.Status.ALWAYS_ACCEPT
        onView(withId(R.id.buyButton)).perform(click())
        Thread.sleep(1000)
        // Display rating form after purchase
        onView(withId(R.id.ratingButton)).check(matches(isDisplayed()))
    }
}
