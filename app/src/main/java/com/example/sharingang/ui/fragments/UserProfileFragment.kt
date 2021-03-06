package com.example.sharingang.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.sharingang.R
import com.example.sharingang.auth.AuthHelper
import com.example.sharingang.auth.CurrentUserProvider
import com.example.sharingang.database.repositories.UserRepository
import com.example.sharingang.databinding.UserProfileFragmentBinding
import com.example.sharingang.imagestore.ImageStore
import com.example.sharingang.models.Item
import com.example.sharingang.models.User
import com.example.sharingang.ui.adapters.ItemListener
import com.example.sharingang.ui.adapters.ItemsAdapter
import com.example.sharingang.utils.ImageAccess
import com.example.sharingang.viewmodels.ItemsViewModel
import com.example.sharingang.viewmodels.UserProfileViewModel
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UserProfileFragment : Fragment() {
    private val userViewModel: UserProfileViewModel by viewModels()
    private val itemsViewModel: ItemsViewModel by viewModels()
    private val args: UserProfileFragmentArgs by navArgs()
    private lateinit var binding: UserProfileFragmentBinding
    private lateinit var authHelper: AuthHelper
    private var currentUserId: String? = null
    private lateinit var imageAccess: ImageAccess
    private var currentUser: FirebaseUser? = null
    private lateinit var userType: UserType
    private var shownUserProfileId: String? = null
    private var loggedInUserEmail: String? = null
    private var imageUri: Uri? = null

    private enum class UserType {
        LOGGED_OUT_SELF,
        LOGGED_OUT,
        VISITOR,
        SELF
    }

    @Inject
    lateinit var currentUserProvider: CurrentUserProvider

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var imageStore: ImageStore

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var authUI: AuthUI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = UserProfileFragmentBinding.inflate(inflater, container, false)
        currentUserId = currentUserProvider.getCurrentUserId()
        shownUserProfileId = when (args.userId) {
            null, "" -> currentUserId
            else -> args.userId
        }
        authHelper = AuthHelper(
            requireContext(), auth, authUI, lifecycleScope, userRepository, this,
            currentUserProvider
        ) { user: FirebaseUser, userId: String -> execAfterSignIn(user, userId) }
        currentUser = auth.currentUser
        setUserType()
        userViewModel.setUser(shownUserProfileId)
        imageAccess = ImageAccess(requireActivity())
        imageAccess.setupImageView(binding.imageView)
        lifecycle.addObserver(imageAccess)
        binding.viewModel = userViewModel
        initializeFields()
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.user_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (currentUserProvider.getCurrentUserId() == null) {
            menu.findItem(R.id.sold_list).isVisible = false
            menu.findItem(R.id.subscription_list).isVisible = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sold_list -> {
                view?.findNavController()?.navigate(
                    UserProfileFragmentDirections.actionUserProfileFragmentToSoldItemList()
                )
                true
            }
            R.id.subscription_list -> {
                view?.findNavController()?.navigate(
                    UserProfileFragmentDirections.actionUserProfileFragmentToSubscriptionFragment()
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initializeFields() {
        currentUserId = currentUserProvider.getCurrentUserId()
        setUserType()
        loggedInUserEmail = currentUserProvider.getCurrentUserEmail()
        userViewModel.user.observe(viewLifecycleOwner, { user ->
            displayUserFields(user)
            if (user!!.profilePicture != null) {
                imageUri = Uri.parse(user.profilePicture)
            }
        })
        setupRecyclerView(shownUserProfileId)
        setupAuthenticationButtons()
        initSetup()
        if (userType == UserType.SELF) binding.textEmail.text = loggedInUserEmail
        setVisibilities()
        setupViews()
        setupReportAndBlockButton()
        setupRatingView()
        setupChatButton()
    }

    private fun setUserType() {
        currentUser = auth.currentUser
        userType = when (currentUserId) {
            null -> if (shownUserProfileId == null) UserType.LOGGED_OUT_SELF else UserType.LOGGED_OUT
            shownUserProfileId -> UserType.SELF
            else -> UserType.VISITOR
        }
    }

    private fun initSetup() {
        listOf(
            binding.upfTopinfo, binding.imageView, binding.btnOpenCamera, binding.btnOpenGallery,
            binding.nameText, binding.textEmail, binding.ratingTextview,
            binding.offersRequestsGroup, binding.btnReport, binding.userItemList, binding.btnLogout,
            binding.btnLogin, binding.btnChat, binding.btnBlock
        ).forEach { view -> view.visibility = View.GONE }
    }

    private fun setVisibilities() {
        val visibleViews = when (userType) {
            UserType.LOGGED_OUT -> listOf(
                binding.imageView, binding.nameText, binding.ratingTextview, binding.userItemList,
                binding.offersRequestsGroup)
            UserType.VISITOR -> listOf(
                binding.imageView, binding.nameText, binding.ratingTextview, binding.userItemList,
                binding.btnReport, binding.btnChat, binding.offersRequestsGroup, binding.btnBlock)
            UserType.SELF -> listOf(
                binding.imageView,
                binding.nameText,
                binding.ratingTextview,
                binding.userItemList,
                binding.textEmail,
                binding.btnLogout,
                binding.offersRequestsGroup,
                binding.btnOpenGallery,
                binding.btnOpenCamera)
            else -> listOf(binding.upfTopinfo, binding.btnLogin)
        }
        visibleViews.forEach { view ->
            if (view != binding.btnBlock && view != binding.btnReport && view != binding.btnChat)
                view.visibility = View.VISIBLE
        }
    }

    private fun setupRatingView() {
        userViewModel.refreshRating(shownUserProfileId)
        userViewModel.rating.observe(viewLifecycleOwner, {
            var text = resources.getString(R.string.default_rating)
            if (it > 0) text = String.format("%.2f", it)
            binding.ratingTextview.text = text
        })
    }

    private fun setupRecyclerView(userId: String?) {
        val onView = { item: Item -> itemsViewModel.onViewItem(item) }
        val adapter =  ItemsAdapter(ItemListener(onView), requireContext())
        binding.userItemList.adapter = adapter
        listOf(binding.offersButton, binding.requestsButton).forEach {
            it.setOnClickListener {
                itemsViewModel.getUserOffersAndRequests(userId, binding.requestsButton.isChecked)
            }
        }
        itemsViewModel.getUserOffersAndRequests(userId, binding.requestsButton.isChecked)
        itemsViewModel.addObserver(
            viewLifecycleOwner,
            adapter,
            ItemsViewModel.OBSERVABLES.USER_ITEMS_AND_REQUESTS
        )
        itemsViewModel.setupItemNavigation(viewLifecycleOwner, this.findNavController(),
            { item ->
                UserProfileFragmentDirections.actionUserProfileFragmentToDetailedItemFragment(
                    item
                )
            })
    }

    private fun setupViews() {
        val buttons = listOf(
            binding.btnOpenCamera, binding.btnOpenGallery
        )
        val topInfo = binding.upfTopinfo
        buttons.forEach { button -> button.setOnClickListener {
            if (button == binding.btnOpenGallery) imageAccess.openGallery()
            else imageAccess.openCamera()
        } }
        topInfo.text = getString(R.string.userNotLoggedInInfo)
        if (userType == UserType.SELF) binding.textEmail.text = loggedInUserEmail
    }

    private fun displayUserFields(requestedUser: User?) {
        if (requestedUser != null) {
            val requestedUserDisplayName = binding.nameText
            requestedUserDisplayName.text = requestedUser.name
            val userPictureUri = requestedUser.profilePicture
            val userProfilePicture = binding.imageView
            Glide.with(this).load(userPictureUri).into(userProfilePicture)
            if (userType == UserType.SELF) binding.textEmail.text = loggedInUserEmail
        }
    }

    private fun changeImage(imageUri: Uri?) {
        lifecycleScope.launch(Dispatchers.IO) {
            val imageUrl = imageUri?.let {
                if (!it.toString().startsWith("https://")) imageStore.store(it).toString()
                else it.toString()
            }
            userRepository.add(
                userRepository.get(currentUserId!!)!!.copy(
                    profilePicture = imageUrl
                )
            )
        }
    }

    private fun setupReportAndBlockButton() {
        if (userType == UserType.VISITOR) {
            lifecycleScope.launch(Dispatchers.IO) {
                val hasBeenReported =
                    userRepository.hasBeenReported(currentUserId!!, shownUserProfileId!!)
                lifecycleScope.launch(Dispatchers.Main) {
                    binding.btnReport.visibility =
                        if (hasBeenReported) View.GONE
                        else View.VISIBLE
                }
            }
            binding.btnReport.setOnClickListener { view ->
                view.findNavController().navigate(
                    UserProfileFragmentDirections.actionUserProfileFragmentToReportFragment(
                        currentUserId!!, shownUserProfileId!!, binding.nameText.text.toString()
                    )
                )
            }
            setupBlockButton()
        }
    }

    private fun execAfterSignIn(loggedInUser: FirebaseUser, loggedInUserId: String) {
        currentUser = loggedInUser
        currentUserId = loggedInUserId
        shownUserProfileId = loggedInUserId
        userViewModel.loginResubscribe(currentUserId!!)
        initializeFields()
        binding.nameText.text = currentUser!!.displayName
        if (currentUser!!.photoUrl != null) Glide.with(this).load(currentUser!!.photoUrl)
            .into(binding.imageView)
    }

    private fun setupAuthenticationButtons() {
        binding.btnLogin.setOnClickListener {
            authHelper.signIn()
        }
        binding.btnLogout.setOnClickListener {
            userViewModel.logoutUnsubscribe(currentUserId!!)
            authHelper.signOut()
            initSetup()
            userType = UserType.LOGGED_OUT_SELF
            setupViews()
            setVisibilities()
        }
    }

    private fun setupChatButton() {
        if(currentUserId != null) {
            lifecycleScope.launch(Dispatchers.IO) {
                if(!userRepository.hasBeenBlocked(shownUserProfileId!!, by = currentUserId!!)
                    && userType == UserType.VISITOR) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        binding.btnChat.visibility = View.VISIBLE
                    }
                }
            }
        }
        binding.btnChat.setOnClickListener {
            view?.findNavController()?.navigate(
                UserProfileFragmentDirections
                    .actionUserProfileFragmentToMessageFragment(
                        shownUserProfileId!!, binding.nameText.text.toString(), imageUri?.toString()
                    )
            )
        }
    }


    override fun onResume() {
        super.onResume()
        if(imageAccess.getImageUri() != null) {
            val currentImageUri = imageAccess.getImageUri()
            binding.imageView.setImageURI(currentImageUri)
            changeImage(currentImageUri)
        }
    }

    private fun setupBlockButton() {
        lifecycleScope.launch(Dispatchers.Main) {
            val blocked = userRepository.hasBeenBlocked(shownUserProfileId!!, by = currentUserId!!)
            binding.btnBlock.visibility =
                if(blocked) View.GONE else View.VISIBLE
            if(!blocked) {
                binding.btnBlock.setOnClickListener { view ->
                    view.findNavController().navigate(
                        UserProfileFragmentDirections
                            .actionUserProfileFragmentToBlockFragment(
                                blockedName =  binding.nameText.text.toString(),
                                blockedId = shownUserProfileId!!,
                                blockerId = currentUserId!!)
                    )
                }
            }
        }
    }
}
