package com.example.sharingang

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sharingang.databinding.FragmentChatsBinding
import com.example.sharingang.users.User
import com.example.sharingang.users.UserAdapter
import com.example.sharingang.users.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@AndroidEntryPoint
class ChatsFragment : Fragment() {

    private lateinit var binding: FragmentChatsBinding
    private var currentUser: FirebaseUser? = null
    private lateinit var userAdapter: UserAdapter
    @Inject
    lateinit var auth: FirebaseAuth
    @Inject
    lateinit var firebaseFirestore: FirebaseFirestore
    @Inject
    lateinit var userRepository: UserRepository
    private val usersLiveData: MutableLiveData<List<User>> = MutableLiveData(listOf())

    override fun onCreateView (
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        currentUser = auth.currentUser
        binding = FragmentChatsBinding.inflate(inflater, container, false)
        if(currentUser != null) {
            binding.chatUsersList.layoutManager = LinearLayoutManager(requireContext())
            usersLiveData.observe(viewLifecycleOwner, {
                (binding.chatUsersList.adapter as UserAdapter).submitList(it)
            })
        }
        setupUI()
        return binding.root
    }

    private fun setupUI() {
        if(currentUser != null) {
            binding.loggedOutInfo.visibility = View.GONE
            val listUsers = mutableListOf<User>()
            userAdapter = UserAdapter(requireContext(), listUsers)
            binding.chatUsersList.adapter = userAdapter
            lifecycleScope.launch(Dispatchers.IO) {
                val chatPartners = firebaseFirestore.collection("users")
                    .document(currentUser!!.uid).collection("messagePartners").get().await()
                listUsers.clear()
                for (document in chatPartners.documents) {
                    listUsers.add(userRepository.get(document.id)!!)
                }
                usersLiveData.postValue(listUsers)
            }
        }
        else {
            binding.chatUsersList.visibility = View.GONE
            binding.loggedOutInfo.visibility = View.VISIBLE
        }
    }
}
