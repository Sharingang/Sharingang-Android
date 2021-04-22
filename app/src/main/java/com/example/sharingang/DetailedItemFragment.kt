package com.example.sharingang

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.sharingang.databinding.FragmentDetailedItemBinding
import com.example.sharingang.items.Item
import com.example.sharingang.users.CurrentUserProvider
import com.example.sharingang.utils.ImageAccess
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
    private val viewModel: UserProfileViewModel by viewModels()

    private lateinit var observer: ImageAccess

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observer = ImageAccess(requireActivity())
        lifecycle.addObserver(observer)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentDetailedItemBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_detailed_item, container, false)

        binding.item = args.item
        observer.setupImageView(binding.detailedItemImage)
        args.item.imageUri?.let {
            binding.detailedItemImage.setImageURI(Uri.parse(it))
        }

        initiateWishlistButton(binding)

        binding.shareButton.setOnClickListener { shareItem() }

        viewModel.setUser(args.item.userId)
        viewModel.user.observe(viewLifecycleOwner, { user ->
            binding.username = "Posted by ${user?.name}"
            binding.itemPostedBy.visibility = if (user != null) View.VISIBLE else View.GONE
            binding.itemPostedBy.setOnClickListener { view ->
                view.findNavController().navigate(
                    DetailedItemFragmentDirections.actionDetailedItemFragmentToUserProfileFragment(
                        user?.id
                    )
                )
            }
        })
        return binding.root
    }

    private fun initiateWishlistButton(binding: FragmentDetailedItemBinding){
        if(currentUserProvider.getCurrentUserId() != null){
            viewModel.wishlistContains.observe(viewLifecycleOwner, {
                binding.addToWishlist.text = getButtonText(it)
            })
            binding.addToWishlist.setOnClickListener { updateWishlist(binding) }
            viewModel.wishlistContains(args.item)
        }else{
            binding.addToWishlist.visibility = View.GONE;
        }

    }

    private fun getButtonText(contains: Boolean): String {
        return if(contains) getString(R.string.remove_wishlist)
        else getString(R.string.add_wishlist)
    }

    private fun updateWishlist(binding: FragmentDetailedItemBinding){
        viewModel.modifyWishList(args.item)
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
