package com.example.sharingang.ar

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import androidx.navigation.navArgs
import com.example.sharingang.DetailedItemFragmentArgs
import com.example.sharingang.R
import com.example.sharingang.databinding.ActivityAractivityBinding

/**
 * An Activity we use for the AR interactions.
 *
 * We create a separate activity instead of a fragment, because we need to listen
 * to extra sensors and other things like that, and we want to avoid cluttering up
 * the main activity with all of that.
 */
class ARActivity : AppCompatActivity() {
    // The arguments passed in when navigating to this activity
    // This is how we get information about the item we're looking at
    private val args: ARActivityArgs by navArgs()
    // The view model holding the state of this activity
    private val viewModel: ARViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aractivity)

        val binding: ActivityAractivityBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_aractivity)

        binding.viewModel = viewModel
        viewModel.setItemLocation(Location(args.item.latitude, args.item.longitude))
    }
}