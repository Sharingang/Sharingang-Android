package com.example.sharingang

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class MessageAdapter(private val context: Context, private var messages: MutableList<String>) :
    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    init {
        messages = mutableListOf()
    }

    companion object {
        enum class MessageSource {
            CURRENT,
            OTHER
        }
        lateinit var sourceType: MessageSource
    }

    class ViewHolder(messageEntryView: View) : RecyclerView.ViewHolder(messageEntryView) {
        var text: TextView = messageEntryView.findViewById(R.id.messageText)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            if(sourceType == MessageSource.OTHER)
                LayoutInflater.from(context)
                    .inflate(R.layout.message_entry_left, parent, false)
            else
                LayoutInflater.from(context)
                    .inflate(R.layout.message_entry_right, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message: String = messages[position]
        holder.text.text = message
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    fun submitList(newData: List<String>) {
        messages.clear()
        messages.addAll(newData)
        notifyDataSetChanged()
    }

}