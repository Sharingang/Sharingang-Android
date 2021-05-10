package com.example.sharingang

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class MessageAdapter(
    private val context: Context, private var chats: MutableList<Chat>,
    private val currentUserId: String
) :
    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    init {
        chats = mutableListOf()
    }

    companion object {
        const val MSG_TYPE_SEND = 0
        const val MSG_TYPE_RECEIVE = 1
    }

    class ViewHolder(messageEntryView: View) : RecyclerView.ViewHolder(messageEntryView) {
        var text: TextView = messageEntryView.findViewById(R.id.messageText)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            if (viewType == MSG_TYPE_RECEIVE)
                LayoutInflater.from(context)
                    .inflate(R.layout.message_entry_left, parent, false)
            else
                LayoutInflater.from(context)
                    .inflate(R.layout.message_entry_right, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message: String = chats[position].message
        holder.text.text = message
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    override fun getItemViewType(position: Int): Int {
        return (
                if (chats[position].from == currentUserId) MSG_TYPE_SEND
                else MSG_TYPE_RECEIVE
                )

    }

    fun submitList(newData: List<Chat>) {
        chats.clear()
        chats.addAll(newData)
        notifyDataSetChanged()
    }

}