package com.example.myactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class MessageActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var receiverUserId: String
    private lateinit var receiverImage: CircleImageView
    private lateinit var receiverName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        receiverUserId = intent.getStringExtra("receiverUserId").toString()

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Initialize views
        messagesRecyclerView = findViewById(R.id.messagesRecyclerView)
        messagesRecyclerView.layoutManager = LinearLayoutManager(this)
        messageAdapter = MessageAdapter(mutableListOf())
        messagesRecyclerView.adapter = messageAdapter
        messageEditText = findViewById(R.id.messageEditText)
        sendButton = findViewById(R.id.sendButton)
        receiverImage = findViewById(R.id.receiver_image)
        receiverName = findViewById(R.id.receiver_name)
        sendButton.setOnClickListener { sendMessage() }

        // Fetch receiver's profile image and name
        firestore.collection("users").document(receiverUserId).get()
            .addOnSuccessListener { documentSnapshot ->
                val receiverProfileImageUrl = documentSnapshot.getString("profileImage")
                val receiverNameText = documentSnapshot.getString("name")

                // Load receiver's profile image using Picasso
                Picasso.get().load(receiverProfileImageUrl).into(receiverImage)

                // Set receiver's name
                receiverName.text = receiverNameText
            }
            .addOnFailureListener { exception ->
                // Handle failure
            }

        // Fetch all past messages sent by the current user to the receiver user
        val senderUserId = auth.currentUser?.uid ?: ""
        val chatId = getChatId(senderUserId, receiverUserId)

        // Update the query to fetch messages for the sender or receiver
        val query = firestore.collection("chats").document(chatId)
            .collection("messages")
            .whereIn("senderId", listOf(senderUserId, receiverUserId))
            .whereIn("receiverId", listOf(senderUserId, receiverUserId))
            .orderBy("timestamp", Query.Direction.ASCENDING)

        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val messageList =
                    task.result?.documents?.mapNotNull { it.toObject<Message>() } ?: mutableListOf()
                messageAdapter.updateMessages(messageList)
                scrollToBottom()
            }
        }
    }

    private fun scrollToBottom() {
        messagesRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)
    }

    private fun sendMessage() {
        val senderUserId = auth.currentUser?.uid ?: ""
        val chatId = getChatId(senderUserId, receiverUserId)
        val messageText = messageEditText.text.toString().trim()

        if (messageText.isNotEmpty()) {
            val timestamp = System.currentTimeMillis()
            val messageId = firestore.collection("chats").document(chatId)
                .collection("messages").document().id

            val message = Message(messageId, senderUserId, receiverUserId, messageText, timestamp)
            firestore.collection("chats").document(chatId)
                .collection("messages")
                .document(messageId)
                .set(message)
                .addOnSuccessListener {
                    messageEditText.text.clear()
                    scrollToBottom()
                }
                .addOnFailureListener { exception ->
                    // Handle failure
                }
        }
    }

    private fun getChatId(senderId: String, receiverId: String): String {
        return if (senderId < receiverId) {
            "$senderId-$receiverId"
        } else if (senderId > receiverId) {
            "$receiverId-$senderId"
        } else {
            "$senderId-self"
        }
    }
}
