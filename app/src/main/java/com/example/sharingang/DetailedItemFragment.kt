package com.example.sharingang

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.example.sharingang.databinding.FragmentDetailedItemBinding
import com.example.sharingang.items.Item
import com.example.sharingang.users.CurrentUserProvider
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLink
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DetailedItemFragment : Fragment() {

    private val args: DetailedItemFragmentArgs by navArgs()
    @Inject
    lateinit var currentUserProvider: CurrentUserProvider
    private val userViewModel : UserProfileViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel.setUser(currentUserProvider.getCurrentUserId())
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentDetailedItemBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_detailed_item, container, false)

        binding.item = args.item

        initiateWishlistButton(binding)

        binding.shareButton.setOnClickListener { shareItem() }
        binding.addToWishlist.setOnClickListener { updateWishlist(binding) }

        return binding.root
    }

    private fun initiateWishlistButton(binding: FragmentDetailedItemBinding){
        userViewModel.wishlistContains.observe(viewLifecycleOwner, {
            binding.addToWishlist.text = getButtonText(it)
        })
        userViewModel.wishlistContains(args.item, currentUserProvider.getCurrentUserId())
    }

    private fun getButtonText(contains: Boolean): String {
        return if(contains) getString(R.string.remove_wishlist)
        else getString(R.string.add_wishlist)
    }

    private fun updateWishlist(binding: FragmentDetailedItemBinding){
        val userId = currentUserProvider.getCurrentUserId()
        userViewModel.modifyWishList(args.item, userId)
    }

    private fun shareItem() {
        val item = args.item
        val link = generateFirebaseDynamicLink(item)
        val shareIntent = Intent.createChooser(Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, link.toString())
            putExtra(Intent.EXTRA_TITLE, generateLinkTitle(item))
            type = "text/plain"
        }, null)
        startActivity(shareIntent)
    }

    private fun generateFirebaseDynamicLink(item: Item): Uri {
        val dynamicLink = Firebase.dynamicLinks.dynamicLink {
            link = generateDeepLink(item)
            domainUriPrefix = "https://sharingang.page.link"
            // Open links with this app on Android
            androidParameters { }
        }

        return dynamicLink.uri
    }

    private fun generateDeepLink(item: Item): Uri {
        val itemDeepLinkPrefix = "https://sharingang.page.link/item?id="
        val deepLink = itemDeepLinkPrefix + item.id
        return Uri.parse(deepLink)
    }

    private fun generateLinkTitle(item: Item): String {
        return item.title + " - " + getString(R.string.app_name)
    }
}
