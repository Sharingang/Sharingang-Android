package com.example.sharingang

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.sharingang.databinding.FragmentSubscriptionBinding
import com.example.sharingang.users.CurrentUserProvider
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SubscriptionFragment : Fragment() {

    private lateinit var binding: FragmentSubscriptionBinding

    @Inject
    lateinit var currentUserProvider: CurrentUserProvider
    private val userViewmodel: UserProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_subscription, container, false)

        val userId = currentUserProvider.getCurrentUserId()
        userViewmodel.setUser(userId)
        userViewmodel.user.observe(viewLifecycleOwner) { user ->
            // More than one since the first item is always empty
            if (user != null && user.subscriptions.size > 1) {
                val message = formatSubscriptions(user.subscriptions)
                binding.subscriptions.text = message
            }
        }

        return binding.root
    }

    private fun formatSubscriptions(subscriptions: List<String>): String {
        var message = context?.getString(R.string.subscribed_to)
        message = message.plus(TextUtils.join(", ", subscriptions.drop(1))).plus(".")
        return message
    }
}