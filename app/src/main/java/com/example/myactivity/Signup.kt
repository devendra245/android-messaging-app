package com.example.myactivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Signup : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailEt: EditText
    private lateinit var passwordEt: EditText
    private lateinit var usernameEt: EditText
    private lateinit var signupbt: Button
    private lateinit var nameEt: EditText
    private lateinit var phonenumberEt: EditText
    private lateinit var bioEt: EditText

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        emailEt = findViewById(R.id.etEMailsu)
        passwordEt = findViewById(R.id.etPasswordsu)
        usernameEt = findViewById(R.id.etUsernamesu)
        signupbt = findViewById(R.id.btsignup)
        nameEt = findViewById(R.id.etNamesu)
        phonenumberEt = findViewById(R.id.etphonenumbersu)
        bioEt = findViewById(R.id.etBiosu)



        auth = FirebaseAuth.getInstance()

        signupbt.setOnClickListener {
            val email = emailEt.text.toString()
            val password = passwordEt.text.toString()
            val username = usernameEt.text.toString()
            val name = nameEt.text.toString()
            val phonenumber = phonenumberEt.text.toString()
            val bio =   bioEt.text.toString()
            signUp(email, password, username,name,phonenumber,bio)

        }
    }

    private fun signUp(email: String, password: String, username: String,name : String,phonenumber : String,bio : String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // User creation successful
                    val user = auth.currentUser
                    user?.let {
                        val userId = it.uid
                        val userData = hashMapOf(
                            "username" to username,
                            "name" to  name,
                            "phonenumber" to phonenumber,
                            "bio" to bio
                            // Add any additional user data as needed
                        )
                        firestore.collection("users")
                            .document(userId)
                            .set(userData)
                            .addOnSuccessListener {
                                val intent = Intent(this@Signup, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                                 // Optional: Finish the current Signup activity to prevent going back to it with the back button
                            }
                            .addOnFailureListener { e ->
                                // Error saving user data
                                Toast.makeText(
                                    this,
                                    "Error saving user data: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                } else {
                    // User creation failed
                    Toast.makeText(
                        this,
                        "Signup failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}