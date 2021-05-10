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

@AndroidEntryPoint
class ChatsFragment : Fragment() {

    private lateinit var binding: FragmentChatsBinding
    private var currentUserId: String? = null
    private lateinit var userAdapter: UserAdapter

    @Inject
    lateinit var currentUserProvider: CurrentUserProvider

    private var firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()

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
            setRecyclerViewDecoration()
            binding.chatUsersList.layoutManager = LinearLayoutManager(requireContext())
            usersLiveData.observe(viewLifecycleOwner, { newList ->
                (binding.chatUsersList.adapter as UserAdapter).submitList(newList)
            })
        }
        setupUI()
        return binding.root
    }

    private fun setupUI() {
        if (currentUserId != null) {
            binding.loggedOutInfo.visibility = View.GONE
            val listUsers = mutableListOf<User>()
            userAdapter = UserAdapter(requireContext(), listUsers)
            binding.chatUsersList.adapter = userAdapter
            lifecycleScope.launch(Dispatchers.IO) {
                val chatPartners = firebaseFirestore.collection(getString(R.string.users))
                    .document(currentUserId!!).collection(getString(R.string.messagePartners))
                    .get().await()
                listUsers.clear()
                for (document in chatPartners.documents) {
                    listUsers.add(userRepository.get(document.id)!!)
                }
                usersLiveData.postValue(listUsers)
            }
        } else {
            binding.chatUsersList.visibility = View.GONE
            binding.loggedOutInfo.visibility = View.VISIBLE
        }
    }

    private fun setRecyclerViewDecoration() {
        binding.chatUsersList.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State,
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                if (parent.getChildAdapterPosition(view) > 0) {
                    outRect.top = 10
                }
            }
        })
    }
}
