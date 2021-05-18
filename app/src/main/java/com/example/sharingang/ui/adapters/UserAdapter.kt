package com.example.sharingang.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Database
import com.bumptech.glide.Glide
import com.example.sharingang.R
import com.example.sharingang.models.User
import com.example.sharingang.ui.fragments.ChatsFragmentDirections
import com.example.sharingang.utils.constants.DatabaseFields
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


/**
 * UserAdapter takes care of adapting a list of users into a Recycler View.
 *
 * @property context the context
 * @property users the list of users we are adapting
 */
class UserAdapter(private val context: Context, private var users: MutableList<User>,
private val firebaseFirestore: FirebaseFirestore, private val currentUserId: String,
private val lifecycleScope: LifecycleCoroutineScope) :
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
        var numUnread: TextView = userEntryView.findViewById(R.id.numUnread)
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
            holder.itemView.setOnClickListener { view ->
                val partnerPicture = user.profilePicture
                view.findNavController().navigate(
                    ChatsFragmentDirections.actionChatsFragmentToMessageFragment(
                        user.id!!, user.name, partnerPicture
                    )
                )
            }
            getUnreadMessagesIntoHolder(user = user, holder = holder)
        }
    }

    override fun getItemCount(): Int {
        return users.size
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

    private fun getUnreadMessagesIntoHolder(user: User, holder: ViewHolder) {
        lifecycleScope.launch(Dispatchers.IO) {
            // get the number of unread messages for that particular user
            val numUnread =
                firebaseFirestore.collection(DatabaseFields.DBFIELD_USERS).document(currentUserId)
                    .collection(DatabaseFields.DBFIELD_MESSAGEPARTNERS).document(user.id!!)
                    .get().await().getLong(DatabaseFields.DBFIELD_NUM_UNREAD)
            lifecycleScope.launch(Dispatchers.Main) {
                holder.numUnread.visibility =
                    if(numUnread != null && numUnread > 0) View.VISIBLE else View.GONE
                holder.numUnread.text = numUnread.toString()
            }
        }
    }

}
