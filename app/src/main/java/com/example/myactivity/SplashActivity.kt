package com.example.myactivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private val SPLASH_DELAY: Long = 2000 // Delay in milliseconds for how long the splash screen should be displayed
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        auth = FirebaseAuth.getInstance()

        // Using a Handler to delay the execution of code after a specified time
        Handler().postDelayed({
            // Check if user is already logged in
            val currentUser = auth.currentUser
            if (currentUser != null) {
                // User is logged in, redirect to MainActivity
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
            } else {
                // User is not logged in, redirect to LoginActivity
                val intent = Intent(this@SplashActivity, Login::class.java)
                startActivity(intent)
            }
            finish()
        }, SPLASH_DELAY)
    }
}