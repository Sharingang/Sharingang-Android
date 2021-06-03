package com.example.sharingang.ui.fragments

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.sharingang.R
import com.example.sharingang.auth.CurrentUserProvider
import com.example.sharingang.database.repositories.ItemRepository
import com.example.sharingang.databinding.FragmentDetailedItemBinding
import com.example.sharingang.models.Item
import com.example.sharingang.models.User
import com.example.sharingang.payment.PaymentProvider
import com.example.sharingang.utils.ImageAccess
import com.example.sharingang.viewmodels.ItemsViewModel
import com.example.sharingang.viewmodels.UserProfileViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLink
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.graphics.Paint
import com.example.sharingang.database.repositories.UserRepository
import com.example.sharingang.utils.DateHelper
import java.util.*

@AndroidEntryPoint
class DetailedItemFragment : Fragment() {

    private val args: DetailedItemFragmentArgs by navArgs()

    @Inject
    lateinit var currentUserProvider: CurrentUserProvider

    @Inject
    lateinit var itemRepository: ItemRepository

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var paymentProvider: PaymentProvider

    private val viewModel: UserProfileViewModel by viewModels()
    private val itemViewModel: ItemsViewModel by viewModels()
    private lateinit var binding: FragmentDetailedItemBinding
    private var item: Item? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        paymentProvider.initialize(this, requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_detailed_item, container, false)
        val itemId = args.item.id!!
        lifecycleScope.launch(Dispatchers.IO) { loadItem(itemId) }
        args.item.image?.let {
            Glide.with(this).load(it).into(binding.detailedItemImage)
        }
        initWishlistButton()
        initRating(args.item)
        initBuy()
        if (args.item.discount) binding.itemPrice.paintFlags =
            binding.itemPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        binding.shareButton.setOnClickListener { shareItem() }
        binding.locateButton.setOnClickListener { locateItem() }
        viewModel.setUser(args.item.userId)
        viewModel.user.observe(viewLifecycleOwner, this::onUserChange)
        setLastUpdated()
        return binding.root
    }

    /**
     * Initialize the buy button, display it only if the item is not sold, has a price and the user
     * is not the seller
     */
    private fun initBuy() {
        val currentUserId = currentUserProvider.getCurrentUserId()
        val availableForSale = !args.item.sold && args.item.price >= 0.01 && !args.item.request &&
                currentUserId != null && args.item.userId != currentUserId
        binding.sellerVisibility = if (availableForSale) View.VISIBLE else View.GONE
        binding.buyButton.setOnClickListener { buyItem() }
        lifecycleScope.launch(Dispatchers.IO) {
            if (currentUserId != null &&
                userRepository.hasBeenBlocked(currentUserId, by = args.item.userId)
            ) {
                lifecycleScope.launch(Dispatchers.Main) {
                    binding.buyButton.visibility = View.GONE
                    binding.buyQuantity.visibility = View.GONE
                    binding.itemPostedBy.isEnabled = false
                    binding.itemPostedBy.setTextColor(Color.GRAY)
                }
            }
        }
    }

    private fun onUserChange(user: User?) {
        binding.username = getString(R.string.posterUsername, user?.name)
        binding.itemPostedBy.visibility = if (user != null) View.VISIBLE else View.GONE
        binding.textViewPostedBy.visibility = if (user != null) View.VISIBLE else View.GONE
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
        if (args.item.userId != currentUserProvider.getCurrentUserId()) {
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
                Snackbar.make(
                    binding.root,
                    getString(R.string.item_deleted_success),
                    Snackbar.LENGTH_SHORT
                ).show()
                lifecycleScope.launch(Dispatchers.Main) { findNavController().popBackStack() }
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
                binding.addToWishlist.text =
                    if (it) getString(R.string.remove_wishlist)
                    else getString(R.string.add_wishlist)
            })
            binding.addToWishlist.setOnClickListener { updateWishlist() }
            viewModel.wishlistContains(args.item)
        } else {
            binding.addToWishlist.visibility = View.GONE
        }
    }

    private fun initRating(item: Item) {
        itemViewModel.setReviews(item)
        val currentUserId = currentUserProvider.getCurrentUserId()
        itemViewModel.reviews.observe(viewLifecycleOwner, {
            val visibility = if (it.keys.contains(currentUserId)
                && currentUserId != null && it[currentUserId]!!
            ) View.VISIBLE
            else View.GONE
            binding.ratingVisibility = visibility
        })
        binding.ratingButton.setOnClickListener {
            val rating = when (binding.radioGroup.checkedRadioButtonId) {
                binding.radioButton1.id -> 1
                binding.radioButton2.id -> 2
                binding.radioButton3.id -> 3
                binding.radioButton4.id -> 4
                binding.radioButton5.id -> 5
                else -> 0
            }
            viewModel.updateUserRating(item.userId, rating)
            itemViewModel.updateReview(item, currentUserProvider.getCurrentUserId(), false)
        }
    }

    private fun updateWishlist() {
        viewModel.modifyWishList(args.item)
    }

    private fun buyItem() {
        val quantity: Int = binding.quantity?.toIntOrNull() ?: 1
        val boughtItem = args.item
        if (quantity > boughtItem.quantity || quantity < 1) {
            Toast.makeText(context, getString(R.string.incorrect_quantity), Toast.LENGTH_SHORT)
                .show()
        } else {
            lifecycleScope.launch {
                binding.buyButton.isEnabled = true
                if (paymentProvider.requestPayment(boughtItem, quantity)) {
                    val newQuantity = boughtItem.quantity - quantity
                    binding.sellerVisibility = if (newQuantity == 0) View.GONE else View.VISIBLE
                    binding.buyButton.isEnabled = newQuantity != 0
                    updateBoughtItem(boughtItem, newQuantity)
                    viewModel.buyItem(currentUserProvider.getCurrentUserId()!!, boughtItem.id!!)
                }
            }
        }
    }

    private fun updateBoughtItem(item: Item, quantity: Int) {
        val itemToUpdate = item.copy(quantity = quantity, sold = (quantity == 0))
        binding.item = itemToUpdate
        itemViewModel.updateReview(
            itemToUpdate, currentUserProvider.getCurrentUserId(), true
        )
        initRating(itemToUpdate)
    }

    private fun shareItem() {
        val link = generateFirebaseDynamicLink(args.item)
        val shareIntent = Intent.createChooser(Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, link.toString())
            putExtra(Intent.EXTRA_TITLE, args.item.title + " - " + getString(R.string.app_name))
            type = "text/plain"
        }, null)
        startActivity(shareIntent)
    }

    private fun generateFirebaseDynamicLink(item: Item): Uri {
        return Firebase.dynamicLinks.dynamicLink {
            link = generateDeepLink(item)
            domainUriPrefix = "https://sharingang.page.link"
            androidParameters { }
        }.uri
    }

    private fun generateDeepLink(item: Item): Uri {
        return Uri.parse("https://sharingang.page.link/item?id=${item.id}")
    }

    /**
     * The callback for when a user wants to locate her item
     */
    private fun locateItem() {
        item?.let {
            view?.findNavController()?.navigate(
                DetailedItemFragmentDirections.actionDetailedItemFragmentToARActivity(it)
            )
        }
    }

    /**
     * Sets the text for when the item was last updated
     */
    private fun setLastUpdated() {
        val dateHelper = DateHelper(requireContext())
        binding.lastUpdateText.text = dateHelper.getDateDifferenceString(
            startDate = args.item.updatedAt ?: args.item.createdAt!!, endDate = Date()
        )
    }
}
