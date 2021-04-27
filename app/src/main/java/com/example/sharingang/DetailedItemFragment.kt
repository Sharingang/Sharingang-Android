package com.example.sharingang

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.sharingang.databinding.FragmentDetailedItemBinding
import com.example.sharingang.items.Item
import com.example.sharingang.items.ItemsViewModel
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
    private val itemViewModel: ItemsViewModel by viewModels()
    private lateinit var binding: FragmentDetailedItemBinding
    private var soldStatus: Boolean = false

    private lateinit var observer: ImageAccess

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        observer = ImageAccess(requireActivity())
        lifecycle.addObserver(observer)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detailed_item, container, false)
        soldStatus = args.item.sold
        binding.item = args.item
        observer.setupImageView(binding.detailedItemImage)
        args.item.imageUri?.let {
            binding.detailedItemImage.setImageURI(Uri.parse(it))
        }

        initiateWishlistButton()
        initRating()

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_detailed, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val sell = menu.findItem(R.id.menuSell)
        val resell = menu.findItem(R.id.menuResell)
        if (!args.item.userId.equals(currentUserProvider.getCurrentUserId())) {
            menu.findItem(R.id.menuEdit).isVisible = false
            sell.isVisible = false
            resell.isVisible = false
        } else {
            sell.isVisible = !soldStatus
            resell.isVisible = soldStatus
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuEdit -> {
                view?.findNavController()?.navigate(
                    DetailedItemFragmentDirections.actionDetailedItemFragmentToNewEditFragment(args.item)
                )
                true
            }
            R.id.menuSell -> {
                updateSold(true)
                true
            }
            R.id.menuResell -> {
                updateSold(false)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateSold(sold: Boolean) {
        itemViewModel.sellItem(args.item)
        soldStatus = sold
        activity?.invalidateOptionsMenu()
    }

    private fun initiateWishlistButton(){
        if(currentUserProvider.getCurrentUserId() != null){
            viewModel.wishlistContains.observe(viewLifecycleOwner, {
                binding.addToWishlist.text = getButtonText(it)
            })
            binding.addToWishlist.setOnClickListener { updateWishlist(binding) }
            viewModel.wishlistContains(args.item)
        }else{
            binding.addToWishlist.visibility = View.GONE
        }
    }

    private fun initRating(){
        itemViewModel.setRated(args.item)
        itemViewModel.rated.observe(viewLifecycleOwner, {
            val visibility = if(!it && args.item.userId != null
                && args.item.sold && currentUserProvider.getCurrentUserId() != null)
                View.VISIBLE
            else View.GONE
            binding.ratingVisibility = visibility
        })

        binding.ratingButton.setOnClickListener {
            val selectedOption: Int = binding.radioGroup1.checkedRadioButtonId
            if(selectedOption != -1){
                val rating = when(selectedOption){
                    binding.radioButton1.id -> 1
                    binding.radioButton2.id -> 2
                    binding.radioButton3.id -> 3
                    binding.radioButton4.id -> 4
                    binding.radioButton5.id -> 5
                    else -> 0
                }
                viewModel.updateUserRating(args.item.userId, rating)
                itemViewModel.rateItem(args.item)
            }
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
