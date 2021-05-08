package com.example.sharingang.users

import android.content.Context
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


class UserAdapter(private val context: Context, private var users: MutableList<User>) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    init {
        users = mutableListOf()
    }

    class ViewHolder(userEntryView: View) : RecyclerView.ViewHolder(userEntryView) {
        var username: TextView = userEntryView.findViewById(R.id.user_entry_username)
        var imageView: ImageView = userEntryView.findViewById(R.id.user_entry_profilepicture)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_entry, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user: User = users[position]
        holder.username.text = user.name
        Glide.with(context).load(user.profilePicture).into(holder.imageView)
        holder.itemView.setOnClickListener { view ->
            view.findNavController().navigate(
                ChatsFragmentDirections.actionChatsFragmentToMessageFragment(
                    user.id!!, user.name, user.profilePicture!!
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }

    fun submitList(newData: List<User>) {
        users.clear()
        users.addAll(newData)
        notifyDataSetChanged()
    }

}