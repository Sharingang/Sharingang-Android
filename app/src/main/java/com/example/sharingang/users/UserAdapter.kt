package com.example.sharingang.users

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sharingang.R


class UserAdapter(private val context: Context, private val users: List<User>) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    class ViewHolder(userEntryView: View) : RecyclerView.ViewHolder(userEntryView) {
        var username: TextView = userEntryView.findViewById(R.id.chat_partner_username)
        var imageView: ImageView = userEntryView.findViewById(R.id.profilePicture)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_entry, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user: User = users[position]
        holder.username.text = user.name
        Glide.with(context).load(user.profilePicture).into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return users.size
    }

}