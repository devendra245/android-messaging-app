package com.example.myactivity

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class Chats : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var chatRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chats, container, false)

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Initialize views
        chatRecyclerView = view.findViewById(R.id.chatRecyclerView)
        chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        chatAdapter = ChatAdapter(mutableListOf()) { userItem ->
            navigateToMessageActivity(userItem.id)
        }
        chatRecyclerView.adapter = chatAdapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUserId = auth.currentUser?.uid

        // Fetch chats for the current user
        firestore.collection("chats")
            .whereEqualTo("participants.$currentUserId", true)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val userList = mutableListOf<User>()

                for (chatDocument in querySnapshot.documents) {
                    val participants = chatDocument["participants"] as? Map<*, *>
                    participants?.keys?.filterNot { it == currentUserId }?.forEach { userId ->
                        val userRef = firestore.collection("users").document(userId.toString())
                        userRef.get().addOnSuccessListener { userDocument ->
                            val userId = userDocument.id
                            val name = userDocument.getString("name") ?: ""
                            val email = userDocument.getString("email") ?: ""
                            val bio = userDocument.getString("bio") ?: ""
                            val profilePicUrl = userDocument.getString("profileImageUrl") ?: ""

                            val userItem = User(userId, name, email, bio, profilePicUrl)
                            userList.add(userItem)

                            // Update the adapter with the user list
                            chatAdapter.updateUsers(userList)
                        }.addOnFailureListener { exception ->
                            // Handle failure
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Handle failure
            }
    }

    private fun navigateToMessageActivity(userId: String) {
        val intent = Intent(requireContext(), MessageActivity::class.java)
        intent.putExtra("receiverUserId", userId)
        startActivity(intent)
    }
}
