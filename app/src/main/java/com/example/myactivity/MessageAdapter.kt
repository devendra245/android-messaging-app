package com.example.myactivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myactivity.Message
import com.example.myactivity.R
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(private val messageList: MutableList<Message>) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
    val auth : FirebaseAuth = FirebaseAuth.getInstance()
    val senderUserId = auth.currentUser?.uid ?: ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messageList[position]
        holder.bind(message)
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    fun updateMessages(messages: List<Message>) {
        messageList.clear()
        messageList.addAll(messages)
        notifyDataSetChanged()
    }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val senderMessageTextView: TextView = itemView.findViewById(R.id.senderMessageTextView)
        private val receiverMessageTextView: TextView = itemView.findViewById(R.id.receiverMessageTextView)

        fun bind(message: Message) {
            if (message.senderId == senderUserId) { // Replace "currentUserId" with the actual ID of the current user
                senderMessageTextView.visibility = View.VISIBLE
                receiverMessageTextView.visibility = View.GONE
                senderMessageTextView.text = message.messageText
            } else {
                senderMessageTextView.visibility = View.GONE
                receiverMessageTextView.visibility = View.VISIBLE
                receiverMessageTextView.text = message.messageText
            }
        }
    }
}
