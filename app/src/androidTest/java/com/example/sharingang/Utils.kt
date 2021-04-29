package com.example.sharingang

import androidx.test.espresso.Espresso
import androidx.test.platform.app.InstrumentationRegistry
import java.lang.Exception

fun tryOpenOptionsMenu() {
    try {
        Espresso.openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
    } catch (e: Exception) {
        // Don't have an overflow in the action bar
    }
}
