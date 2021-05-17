package com.example.sharingang.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sharingang.R
import com.example.sharingang.models.Chat

/**
 * MessageAdapter takes care of adapting a list of messages into a Recycler View.
 *
 * @property context the context
 * @property chats the list of chats we are adapting
 * @property currentUserId the current logged in user's id
 */
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

    /**
     * ViewHolder holds the fields of a message element inside a View.
     *
     * @param messageEntryView the designed View for a message
     */
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

    /**
     * Updates the list of messages based on new incoming data.
     *
     * @param newData the incoming data
     */
    fun submitList(newData: List<Chat>) {
        chats.clear()
        chats.addAll(newData)
        notifyDataSetChanged()
    }

}
