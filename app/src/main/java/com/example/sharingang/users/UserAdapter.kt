package com.example.sharingang.users

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sharingang.ChatsFragmentDirections
import com.example.sharingang.R


/**
 * UserAdapter takes care of adapting a list of users into a Recycler View.
 *
 * @param context the context
 * @param users the list of users we are adapting
 */
class UserAdapter(private val context: Context, private var users: MutableList<User>) :
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_entry, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.e("xxx", "itemcount = $itemCount")
        if(position < users.size) {
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

}