package com.example.sharingang.ar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sharingang.R

/**
 * An Activity we use for the AR interactions.
 *
 * We create a separate activity instead of a fragment, because we need to listen
 * to extra sensors and other things like that, and we want to avoid cluttering up
 * the main activity with all of that.
 */
class ARActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aractivity)
    }
}