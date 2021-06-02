package com.example.sharingang.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sharingang.R
import com.example.sharingang.database.repositories.UserRepository
import com.example.sharingang.models.User
import com.example.sharingang.ui.fragments.ChatsFragment
import com.example.sharingang.ui.fragments.ChatsFragmentDirections
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * UserAdapter takes care of adapting a list of users into a Recycler View.
 *
 * @property context the context
 * @property users the list of users we are adapting
 */
class UserAdapter(
    private val context: Context, private var users: MutableList<User>,
    private val userRepository: UserRepository, private val currentUserId: String,
    private val lifecycleScope: LifecycleCoroutineScope,
    private val attachedFragment: ChatsFragment
) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    init {
        users = mutableListOf()
    }

    /**
     * ViewHolder holds the fields of a user element inside a View.
     *
     * @param userEntryView the designed View for a user entry
     */
    class ViewHolder(userEntryView: View) : RecyclerView.ViewHolder(userEntryView) {
        var username: TextView = userEntryView.findViewById(R.id.chatPartnerUsername)
        var imageView: ImageView = userEntryView.findViewById(R.id.chatPartnerPic)
        var indicator: TextView = userEntryView.findViewById(R.id.numUnread)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_entry, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position < users.size) {
            val user: User = users[position]
            holder.username.text = user.name
            Glide.with(context).load(user.profilePicture).into(holder.imageView)
            setupActions(holder, user)
            lifecycleScope.launch(Dispatchers.Main) {
                displayNumUnread(holder, user.id!!)
                userRepository.setupConversationRefresh(currentUserId, user.id) {
                    if (attachedFragment.isAdded) {
                        displayNumUnread(holder, user.id)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }

    private fun displayNumUnread(holder: ViewHolder, targetId: String) {
        lifecycleScope.launch(Dispatchers.Main) {
            val nUnread = userRepository.getNumUnread(userId = currentUserId, with = targetId)
            holder.indicator.text = nUnread.toString()
            holder.indicator.visibility = if (nUnread == 0L) View.GONE else View.VISIBLE
        }
    }

    /**
     * Updates the current list of users based on new incoming data.
     *
     * @param newData the incoming data
     */
    fun submitList(newData: List<User>) {
        users.clear()
        users.addAll(newData)
        notifyDataSetChanged()
    }

    private fun setupActions(holder: ViewHolder, user: User) {
        holder.itemView.setOnClickListener { view ->
            val partnerPicture = user.profilePicture
            view.findNavController().navigate(
                ChatsFragmentDirections.actionChatsFragmentToMessageFragment(
                    user.id!!, user.name, partnerPicture
                )
            )
        }
    }
}
