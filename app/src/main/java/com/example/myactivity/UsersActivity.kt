package com.example.myactivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class UsersActivity : AppCompatActivity(), UserAdapter.OnItemClickListener {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var storageRef: StorageReference
    private lateinit var usersRecyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)

        firestore = FirebaseFirestore.getInstance()
        storageRef = FirebaseStorage.getInstance().reference
        usersRecyclerView = findViewById(R.id.usersRecyclerView)
        usersRecyclerView.layoutManager = LinearLayoutManager(this)
        userAdapter = UserAdapter(mutableListOf())
        userAdapter.setOnItemClickListener(this)
        usersRecyclerView.adapter = userAdapter
        auth = FirebaseAuth.getInstance()

        fetchUsersData()
    }

    override fun onItemClick(user: User) {
        val intent = Intent(this, MessageActivity::class.java)
        intent.putExtra("receiverUserId", user.id) // Change "userId" to "receiverUserId"
        startActivity(intent)
    }

    private fun fetchUsersData() {
        firestore.collection("users").get()
            .addOnSuccessListener { querySnapshot ->
                val userList = mutableListOf<User>()
                for (document in querySnapshot.documents) { // Use documents instead of querySnapshot directly
                    val name = document.getString("name") ?: ""
                    val bio = document.getString("bio") ?: ""
                    val email =  document.getString("email") ?: ""
                    val profilePicUrl = document.getString("profilePicUrl") ?: ""

                    // Fetch the email address from Firebase Authentication
                    val userId = document.id

                    // Construct the profile picture path using the document ID
                    val profilePicPath = "profile_images/$userId.jpg"

                    // Fetch the profile picture from Firebase Storage
                    val profilePicRef = storageRef.child(profilePicPath)
                    profilePicRef.downloadUrl.addOnSuccessListener { uri ->
                        val user = User(userId, name, email, bio, uri.toString())
                        userList.add(user)
                        userAdapter.updateUsers(userList)
                    }.addOnFailureListener { exception ->
                        Log.d("Error", "Failed to fetch profile picture: ${exception.message}")
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Error", "Failed to fetch users: ${exception.message}")
            }
    }
}
