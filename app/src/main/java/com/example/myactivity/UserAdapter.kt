package com.example.myactivity


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.example.myactivity.User
import com.example.myactivity.R

class UserAdapter(private val userList: MutableList<User>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_users, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun updateUsers(users: List<User>) {
        userList.clear()
        userList.addAll(users)
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(user: User)
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.userNameTextView)
        private val emailTextView: TextView = itemView.findViewById(R.id.userEmailTextView)
        private val bioTextView: TextView = itemView.findViewById(R.id.userBioTextView)
        private val profileImageView: ImageView = itemView.findViewById(R.id.users_profileimage)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val user = userList[position]
                    itemClickListener?.onItemClick(user)
                }
            }
        }

        fun bind(user: User) {
            nameTextView.text = user.name
            emailTextView.text = user.email
            bioTextView.text = user.bio

            // Load profile picture using Picasso library
            Picasso.get()
                .load(user.profilePicUrl)
                .placeholder(R.drawable.profile) // Set a placeholder image if needed
                .into(profileImageView)
        }
    }
}
