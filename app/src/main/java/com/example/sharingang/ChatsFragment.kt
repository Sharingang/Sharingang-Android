package com.example.sharingang

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.liveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sharingang.databinding.FragmentChatsBinding
import com.example.sharingang.items.ItemsViewModel
import com.example.sharingang.users.User
import com.example.sharingang.users.UserAdapter
import com.example.sharingang.users.UserRepository
import com.example.sharingang.utils.ChatsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@AndroidEntryPoint
class ChatsFragment : Fragment() {

    private lateinit var binding: FragmentChatsBinding
    private lateinit var currentUser: FirebaseUser
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
        binding = FragmentChatsBinding.inflate(inflater, container, false)
        binding.chatUsersList.layoutManager = LinearLayoutManager(requireContext())
        usersLiveData.observe(viewLifecycleOwner, {
            (binding.chatUsersList.adapter as UserAdapter).submitList(it)
        })
        readUsers()
        Log.e("xxx", "finished loading")
        return binding.root
    }

    fun readUsers() {
        //userAdapter = UserAdapter(requireContext(), listUsers)
        currentUser = auth.currentUser!!
        val listUsers = mutableListOf<User>()
        userAdapter = UserAdapter(requireContext(), listUsers)
        binding.chatUsersList.adapter = userAdapter
        lifecycleScope.launch(Dispatchers.IO) {
            val refr = firebaseFirestore.collection("users")
                .document(currentUser.uid).collection("messagePartners").get().await()
            listUsers.clear()
            for (document in refr.documents) {
                listUsers.add(userRepository.get(document.id)!!)
                //Log.e("xxx", "${listUsers.size}")
                Log.e("xxx", "listUsersSize = ${listUsers.size}")
                Log.e("xxx", "posted value")
            }
            usersLiveData.postValue(listUsers)
        }

    }

        /*
        val ref =
            FirebaseDatabase.getInstance().getReference("users/${currentUser.uid}/messagePartners")
        ref.addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.e("we're here", "HERE")

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("cancelled chat", "CANCELLED CHAT")
                    return
                }
            }
        )
         */
}
