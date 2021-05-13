package com.example.sharingang

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.sharingang.databinding.FragmentDetailedItemBinding
import com.example.sharingang.items.Item
import com.example.sharingang.items.ItemRepository
import com.example.sharingang.items.ItemsViewModel
import com.example.sharingang.payment.PaymentProvider
import com.example.sharingang.users.CurrentUserProvider
import com.example.sharingang.users.User
import com.example.sharingang.utils.ImageAccess
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLink
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DetailedItemFragment : Fragment() {

    private val args: DetailedItemFragmentArgs by navArgs()

    @Inject
    lateinit var currentUserProvider: CurrentUserProvider

    @Inject
    lateinit var itemRepository: ItemRepository

    @Inject
    lateinit var paymentProvider: PaymentProvider

    private val viewModel: UserProfileViewModel by viewModels()
    private val itemViewModel: ItemsViewModel by viewModels()
    private lateinit var binding: FragmentDetailedItemBinding
    private var item: Item? = null

    private lateinit var observer: ImageAccess

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        observer = ImageAccess(requireActivity())
        lifecycle.addObserver(observer)
        paymentProvider.initialize(this, requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_detailed_item, container, false)
        val itemId = args.item.id!!
        lifecycleScope.launch(Dispatchers.IO) {
            loadItem(itemId)
        }
        observer.setupImageView(binding.detailedItemImage)
        args.item.image?.let {
            Glide.with(this).load(it).into(binding.detailedItemImage)
        }

        initWishlistButton()
        initRating(args.item)
        initBuy()

        binding.shareButton.setOnClickListener { shareItem() }

        viewModel.setUser(args.item.userId)
        viewModel.user.observe(viewLifecycleOwner, this::onUserChange)
        return binding.root
    }

    private fun initBuy() {
        val currentUserId = currentUserProvider.getCurrentUserId()
        val availableForSale = !args.item.sold && currentUserId != null && args.item.userId != currentUserId
        binding.buyButton.visibility = if (availableForSale) View.VISIBLE else View.GONE
        binding.buyButton.setOnClickListener { buyItem() }
    }

    private fun onUserChange(user: User?) {
        binding.username = getString(R.string.posted_by, user?.name)
        binding.itemPostedBy.visibility = if (user != null) View.VISIBLE else View.GONE
        binding.itemPostedBy.setOnClickListener { view ->
            view.findNavController().navigate(
                DetailedItemFragmentDirections.actionDetailedItemFragmentToUserProfileFragment(
                    user?.id
                )
            )
        }
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
            menu.findItem(R.id.menuDelete).isVisible = false
            sell.isVisible = false
            resell.isVisible = false
        } else {
            resell.isVisible = item?.sold ?: args.item.sold
            sell.isVisible = !resell.isVisible
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
            R.id.menuDelete -> {
                deleteItem(args.item.id!!)
                true
            }
            R.id.menuSell, R.id.menuResell -> {
                updateSold(args.item.id!!)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun deleteItem(itemId: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            if (itemRepository.delete(itemId)) {
                Snackbar.make(binding.root, getString(R.string.item_deleted_success), Snackbar.LENGTH_SHORT)
                    .show()
                lifecycleScope.launch(Dispatchers.Main) {
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun updateSold(itemId: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            itemViewModel.sellItem(item)
            loadItem(itemId)
        }
    }

    private suspend fun loadItem(itemId: String) {
        item = itemRepository.get(itemId)
        lifecycleScope.launch(Dispatchers.Main) {
            binding.item = item
            activity?.invalidateOptionsMenu()
        }
    }

    private fun initWishlistButton() {
        if (currentUserProvider.getCurrentUserId() != null && !args.item.request) {
            viewModel.wishlistContains.observe(viewLifecycleOwner, {
                binding.addToWishlist.text = getButtonText(it)
            })
            binding.addToWishlist.setOnClickListener { updateWishlist() }
            viewModel.wishlistContains(args.item)
        } else {
            binding.addToWishlist.visibility = View.GONE
        }
    }

    private fun initRating(item: Item) {
        itemViewModel.setRated(item)
        itemViewModel.rated.observe(viewLifecycleOwner, {
            val visibility = if (!it && item.userId != null
                && item.sold && currentUserProvider.getCurrentUserId() != null
            ) View.VISIBLE
            else View.GONE
            binding.ratingVisibility = visibility
        })
        binding.ratingButton.setOnClickListener {
            val selectedOption: Int = binding.radioGroup1.checkedRadioButtonId
            if (selectedOption != -1) {
                val rating = when (selectedOption) {
                    binding.radioButton1.id -> 1
                    binding.radioButton2.id -> 2
                    binding.radioButton3.id -> 3
                    binding.radioButton4.id -> 4
                    binding.radioButton5.id -> 5
                    else -> 0
                }
                viewModel.updateUserRating(item.userId, rating)
                itemViewModel.rateItem(item)
            }
        }
    }


    private fun getButtonText(contains: Boolean): String {
        return if (contains) getString(R.string.remove_wishlist)
        else getString(R.string.add_wishlist)
    }

    private fun updateWishlist() {
        viewModel.modifyWishList(args.item)
    }

    private fun buyItem() {
        binding.buyButton.isEnabled = false
        lifecycleScope.launch {
            val status = paymentProvider.requestPayment(args.item)
            if (status) {
                itemViewModel.sellItem(args.item)
                binding.buyButton.visibility = View.GONE
                initRating(args.item.copy(sold = true))
            } else {
                binding.buyButton.isEnabled = true
            }
        }
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
