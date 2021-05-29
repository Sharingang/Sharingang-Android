package com.example.sharingang.ui.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sharingang.R
import com.example.sharingang.database.repositories.UserRepository
import com.example.sharingang.models.User
import com.example.sharingang.ui.fragments.BlockedUsersFragmentDirections
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * UserAdapter takes care of adapting a list of users into a Recycler View.
 *
 * @property context the context
 * @property users the list of users we are adapting
 */
class BlockedUserAdapter(
    private val context: Context, private var users: MutableList<User>,
    private val currentUserId: String, private val userRepository: UserRepository,
    private val lifecycleScope: LifecycleCoroutineScope
) :
    RecyclerView.Adapter<BlockedUserAdapter.ViewHolder>() {

    init {
        users = mutableListOf()
    }

    /**
     * ViewHolder holds the fields of a user element inside a View.
     *
     * @param userEntryView the designed View for a user entry
     */
    class ViewHolder(userEntryView: View) : RecyclerView.ViewHolder(userEntryView) {
        var username: Button = userEntryView.findViewById(R.id.blockedUserName)
        var imageView: ImageView = userEntryView.findViewById(R.id.blockedUserPic)
        var blockedInfo: Button = userEntryView.findViewById(R.id.buttonBlockInfo)
        var btnUnblock: Button = userEntryView.findViewById(R.id.btnUnblock)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.blocked_user_entry, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position < users.size) {
            val user: User = users[position]
            holder.username.text = user.name
            Glide.with(context).load(user.profilePicture).into(holder.imageView)
            holder.blockedInfo.setOnClickListener {
                showInformation(currentUserId, user.id!!)
            }
            holder.btnUnblock.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    userRepository.unblock(currentUserId, user.id!!)
                }
                users.remove(user)
                submitList(users)
            }
            holder.username.setOnClickListener { view ->
                view.findNavController().navigate(
                    BlockedUsersFragmentDirections.actionBlockedUsersFragmentToUserProfileFragment(
                        user.id
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

    private fun showInformation(currentUserId: String, blockedUserId: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val information = userRepository.getBlockInformation(currentUserId, blockedUserId)
            lifecycleScope.launch(Dispatchers.Main) {
                val builder1: AlertDialog.Builder = AlertDialog.Builder(context)
                builder1.setMessage(information)
                builder1.setCancelable(false)
                builder1.setPositiveButton(
                    "OK"
                ) { dialog, _ -> dialog.cancel() }
                val alert11: AlertDialog = builder1.create()
                alert11.show()
            }
        }
    }
}