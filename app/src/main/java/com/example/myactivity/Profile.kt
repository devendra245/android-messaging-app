package com.example.myactivity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class Profile : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storageRef: StorageReference
    private lateinit var db: DocumentReference

    private lateinit var nameTextview: TextView
    private lateinit var usernameTextview: TextView
    private lateinit var phoneNumberTextview: TextView
    private lateinit var bioTextview: TextView
    private lateinit var saveButton: Button
    private lateinit var updateButton: Button
    private lateinit var profileImageView: ImageView
    private lateinit var editProfileImageButton: ImageButton
    private lateinit var nameEdittext: TextInputLayout
    private lateinit var usernameEdittext: TextInputLayout
    private lateinit var phonenumberEdittext: TextInputLayout
    private lateinit var bioEdittext: TextInputLayout
    private lateinit var editname: TextInputEditText
    private lateinit var editusername: TextInputEditText
    private lateinit var editphonenumber: TextInputEditText
    private lateinit var editbio: TextInputEditText
    private lateinit var editemail : TextInputEditText
    private lateinit var emailEdittext :  TextInputLayout
    private lateinit var emailTextview  : TextView
    private lateinit var userId: String

    private val PICK_IMAGE_REQUEST = 1
    private val PERMISSION_CODE = 2


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storageRef = FirebaseStorage.getInstance().reference
        userId = auth.currentUser!!.uid.toString()


       editemail = view.findViewById(R.id.profileemailET)
        emailEdittext = view.findViewById(R.id.profile_email)
        emailTextview = view.findViewById(R.id.emailEditText)
        editname = view.findViewById(R.id.profilenameET)
        editusername = view.findViewById(R.id.profileusernameET)
        editphonenumber = view.findViewById(R.id.profile_phonenumberET)
        editbio = view.findViewById(R.id.profilebioET)
        nameTextview = view.findViewById(R.id.nameEditText)
        usernameTextview = view.findViewById(R.id.usernameEditText)
        phoneNumberTextview = view.findViewById(R.id.phoneNumberEditText)
        bioTextview = view.findViewById(R.id.bioEditText)
        nameEdittext = view.findViewById(R.id.profile_name)
        usernameEdittext = view.findViewById(R.id.profile_username)
        phonenumberEdittext = view.findViewById(R.id.profile_phonenumber)
        bioEdittext = view.findViewById(R.id.profile_bio)
        updateButton = view.findViewById(R.id.updateButton)

        saveButton = view.findViewById(R.id.saveButton)
        profileImageView = view.findViewById(R.id.profile_image)
        editProfileImageButton = view.findViewById(R.id.editProfileImageButton)
        updateButton.visibility = View.VISIBLE

        db = firestore.collection("users").document(userId)
        db.addSnapshotListener { value, error ->
            if (error != null) {
                Log.d("Error", "Unable to fetch data")
            } else {
                nameTextview.text = value?.getString("name")
                usernameTextview.text = value?.getString("username")
                emailTextview.text = value?.getString("email")
                phoneNumberTextview.text = value?.getString("phonenumber")
                bioTextview.text = value?.getString("bio")

                // Fetch and display profile picture
                val profileImageURL = value?.getString("profileImage")
                if (profileImageURL != null) {
                    Picasso.get().load(profileImageURL).into(profileImageView)
                }
            }
        }

        editProfileImageButton.setOnClickListener {
            openImageChooser()
        }

        updateButton.setOnClickListener {
            emailTextview.visibility = View.GONE
            nameTextview.visibility = View.GONE
            usernameTextview.visibility = View.GONE
            phoneNumberTextview.visibility = View.GONE
            bioTextview.visibility = View.GONE
            updateButton.visibility = View.GONE
            nameEdittext.visibility = View.VISIBLE
            emailEdittext.visibility = View.VISIBLE
            usernameEdittext.visibility = View.VISIBLE
            phonenumberEdittext.visibility = View.VISIBLE
            bioEdittext.visibility = View.VISIBLE
            saveButton.visibility = View.VISIBLE
            editProfileImageButton.visibility = View.VISIBLE
            editname.text = Editable.Factory.getInstance().newEditable(nameTextview.text.toString())
            editusername.text =
                Editable.Factory.getInstance().newEditable(usernameTextview.text.toString())
            editphonenumber.text =
                Editable.Factory.getInstance().newEditable(phoneNumberTextview.text.toString())
            editbio.text = Editable.Factory.getInstance().newEditable(bioTextview.text.toString())
            editemail.text = Editable.Factory.getInstance().newEditable(emailTextview.text.toString())
        }
        saveButton.setOnClickListener {
            emailTextview.visibility = View.VISIBLE
            nameTextview.visibility = View.VISIBLE
            usernameTextview.visibility = View.VISIBLE
            phoneNumberTextview.visibility = View.VISIBLE
            bioTextview.visibility = View.VISIBLE
            updateButton.visibility = View.VISIBLE
            nameEdittext.visibility = View.GONE
            usernameEdittext.visibility = View.GONE
            phonenumberEdittext.visibility = View.GONE
            emailEdittext.visibility = View.GONE
            bioEdittext.visibility = View.GONE
            saveButton.visibility = View.GONE
            editProfileImageButton.visibility  =  View.GONE

            val userData = hashMapOf(
                "username" to editusername.text.toString(),
                "name" to editname.text.toString(),
                "email" to editemail.text.toString(),
                "phonenumber" to editphonenumber.text.toString(),
                "bio" to editbio.text.toString()

                // Add any additional user data as needed
            )
            db.set(userData, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d("success", "Data successfully updated")
                }
                .addOnFailureListener { e ->
                    Log.d("failure", "Error saving user data: ${e.message}")
                }
        }
        return view
    }

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            profileImageView.setImageURI(imageUri)
            uploadProfileImage(imageUri)
        }
    }

    private fun uploadProfileImage(imageUri: Uri?) {
        val imageRef = storageRef.child("profile_images/$userId.jpg")
        imageRef.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val userData = hashMapOf(
                        "profileImage" to uri.toString()
                        // Add any additional user data as needed
                    )
                    db.set(userData, SetOptions.merge())
                        .addOnSuccessListener {
                            Log.d("success", "Profile image uploaded and URL saved")
                        }
                        .addOnFailureListener { e ->
                            Log.d("failure", "Error saving profile image URL: ${e.message}")
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.d("failure", "Error uploading profile image: ${e.message}")
            }
    }
}
