package com.example.sharingang

import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sharingang.databinding.FragmentChatsBinding
import com.example.sharingang.users.CurrentUserProvider
import com.example.sharingang.users.User
import com.example.sharingang.users.UserAdapter
import com.example.sharingang.users.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * ChatsFragment takes care of displaying and representing the current
 * chat partners of the logged-in user.
 */
@AndroidEntryPoint
class ChatsFragment : Fragment() {

    private lateinit var binding: FragmentChatsBinding
    private var currentUserId: String? = null
    private lateinit var userAdapter: UserAdapter

    @Inject
    lateinit var currentUserProvider: CurrentUserProvider

    @Inject
    lateinit var firebaseFirestore: FirebaseFirestore

    private val listUsers: MutableList<User> = mutableListOf()

    @Inject
    lateinit var userRepository: UserRepository
    private val usersLiveData: MutableLiveData<List<User>> = MutableLiveData(listOf())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        currentUserId = currentUserProvider.getCurrentUserId()
        binding = FragmentChatsBinding.inflate(inflater, container, false)
        if (currentUserId != null) {
            userAdapter = UserAdapter(requireContext(), listUsers)
            binding.chatUsersList.adapter = userAdapter
            setRecyclerViewDecoration(margin = 10)
            binding.chatUsersList.layoutManager = LinearLayoutManager(requireContext())
            usersLiveData.observe(viewLifecycleOwner, { newList ->
                (binding.chatUsersList.adapter as UserAdapter).submitList(newList)
            })
        }
        setupUI()
        return binding.root
    }

    /**
     * Sets up the UI (views) with the help of the UserAdapter
     */
    private fun setupUI() {
        listUsers.clear()
        if (currentUserId != null) {
            binding.loggedOutInfo.visibility = View.GONE
            lifecycleScope.launch(Dispatchers.IO) {
                userRepository.refreshUsers()
                // We get a snapshot of the collection containing the current user's message
                // partners (user Ids), so that we can then use this snapshot to read
                // the data of the documents we need inside.
                val chatPartners = firebaseFirestore.collection(getString(R.string.users))
                    .document(currentUserId!!).collection(getString(R.string.messagePartners)).get()
                    .await()
                chatPartners.documents.forEach {
                    val user = userRepository.get(it.id)
                    listUsers.add(user!!)
                }
                usersLiveData.postValue(listUsers)

            }
        } else {
            binding.chatUsersList.visibility = View.GONE
            binding.loggedOutInfo.visibility = View.VISIBLE
        }
    }

    /**
     * Decorates the displayed list of users with a margin between elements.
     *
     * @param margin the margin between items
     */
    private fun setRecyclerViewDecoration(margin: Int) {
        binding.chatUsersList.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State,
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                if (parent.getChildAdapterPosition(view) > 0) {
                    outRect.top = margin
                }
            }
        })
    }
}
