package com.example.myactivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class Credits : Fragment() {

        private lateinit var creditsTextView: TextView

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val view = inflater.inflate(R.layout.fragment_credits, container, false)
            creditsTextView = view.findViewById(R.id.creditsTextView)
            return view
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            displayCredits()
        }

        private fun displayCredits() {
            val creditsText = """
            Credits:
            
            Development Team:
            - Devendra kr. jha: Lead Developer
            - Nishtha Jain: UI/UX Designer
            - Mark Johnson: Backend Developer
            
            
            Third-Party Libraries and Resources:
            - Firebase by Google: Backend services and authentication
            - Picasso: Image loading and caching library
            - Retrofit: HTTP client for API communication
            - Material Design Icons: Icons used in the application
            - Unsplash: Royalty-free images for app design
            
            Open Source Projects:
            - Kotlin: Programming language for Android development
            - Android Jetpack Components: Architecture components for building robust Android apps
            - OkHttp: HTTP client for making network requests
            - Gson: JSON parsing library
            - CircleImageView: Circular image view library
            - ViewPager2: Improved version of ViewPager for swipe-based navigation
            - TabLayout: UI component for displaying tabs
            
            We would like to express our gratitude to all the individuals and organizations mentioned above for their valuable contributions to the development and success of our application. Their expertise, tools, and resources have played a significant role in creating a high-quality and feature-rich app for our users.
            
            We also extend our thanks to our users for their continued support and feedback, which helps us improve the application and deliver a better user experience.
        """.trimIndent()

            creditsTextView.text = creditsText
        }
    }
