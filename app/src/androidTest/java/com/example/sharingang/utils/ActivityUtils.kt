package com.example.sharingang.utils

import android.app.Activity
import androidx.test.ext.junit.rules.ActivityScenarioRule

fun <A : Activity> getActivity(activityRule: ActivityScenarioRule<A>): A {
    var activity: A? = null
    activityRule.scenario.onActivity {
        activity = it
    }
    return activity!!
}
