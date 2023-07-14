package com.example.myactivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class ChatAdapter(
    private val userList: MutableList<User>,
    private val onItemClick: (User) -> Unit
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.userNameTextView)
        private val emailTextView: TextView = itemView.findViewById(R.id.userEmailTextView)
        private val bioTextView: TextView = itemView.findViewById(R.id.userBioTextView)
        private val profileImageView: ImageView = itemView.findViewById(R.id.users_profileimage)

        fun bind(userItem: User) {
            nameTextView.text = userItem.name
            emailTextView.text = userItem.email
            bioTextView.text = userItem.bio

            // Load profile image using Picasso or any other library
            Picasso.get().load(userItem.profilePicUrl).into(profileImageView)

            itemView.setOnClickListener {
                onItemClick(userItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapter.ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_users, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val userItem = userList[position]
        holder.bind(userItem)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun updateUsers(users: List<User>) {
        userList.clear()
        userList.addAll(users)
        notifyDataSetChanged()
    }
}
