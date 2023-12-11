package com.example.blogapp

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.blogapp.Model.UserData
import com.example.blogapp.databinding.ActivitySignInandRegistrationBinding
import com.example.blogapp.register.WelcomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlin.math.log

class SignInandRegistrationActivity : AppCompatActivity() {
    private val binding: ActivitySignInandRegistrationBinding by lazy {
        ActivitySignInandRegistrationBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //Initialized Firebase Authentication.
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        // For Visibility Off Fields
        val action = intent.getStringExtra("action")
        //adjust Visibility for login
        if (action == "login") {
            binding.loginEmail.visibility = View.VISIBLE
            binding.loginPassword.visibility = View.VISIBLE
            binding.loginButton.visibility = View.VISIBLE

            binding.registerButton.isEnabled = false
            binding.registerButton.alpha = 0.5f
            binding.registerEmail.visibility = View.GONE
            binding.registerPassword.visibility = View.GONE
            binding.cardView.visibility = View.GONE
            binding.registerName.visibility = View.GONE
            binding.registerNewHere.isEnabled = false
            binding.registerNewHere.alpha = 0.5f

            binding.loginButton.setOnClickListener {
                val loginEmail = binding.loginEmail.text.toString()
                val loginPassword = binding.loginPassword.text.toString()
                if (loginEmail.isEmpty() || loginPassword.isEmpty()) {
                    Toast.makeText(this, "Pleas Fill All The Details", Toast.LENGTH_SHORT).show()
                } else {
                    auth.signInWithEmailAndPassword(loginEmail, loginPassword)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT)
                                    .show()
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(this, "Login Failed Pleas Enter Correct Details", Toast.LENGTH_SHORT).show()
                            }

                        }
                }
            }

        } else if (action == "register") {
            binding.loginButton.isEnabled = false
            binding.loginButton.alpha = 0.5f

            binding.registerButton.setOnClickListener {

                //Get data From Edit text Field.
                val registerName = binding.registerName.text.toString()
                val registerEmail = binding.registerEmail.text.toString()
                val registerPassword = binding.registerPassword.text.toString()
                if (registerName.isEmpty() || registerEmail.isEmpty() || registerPassword.isEmpty()) {
                    Toast.makeText(this, "Pleas Fill All The Details", Toast.LENGTH_SHORT).show()
                } else {

                    auth.createUserWithEmailAndPassword(registerEmail, registerPassword)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {

                                val user = auth.currentUser
                                auth.signOut()

                                user?.let {

                                    //Save user Data Into Firebase RealTime database.
                                    val userReference = database.getReference("users")
                                    val userId = user.uid
                                    val userData = UserData(registerName, registerEmail)

                                    userReference.child(userId).setValue(userData)
                                    //Upload Image To Firebase Storage
                                    val storageReference =
                                        storage.reference.child("profile_image/$userId.jpg")
                                    storageReference.putFile(imageUri!!)

                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                storageReference.downloadUrl.addOnCompleteListener { imageUri ->
                                                    if (imageUri.isSuccessful) {
                                                        val imageUrl = imageUri.result.toString()

                                                        //Save The Image Url To The Real Time data base.
                                                        userReference.child(userId).child("profileImage").setValue(imageUrl)
                                                       // Glide.with(this).load(imageUri).apply(RequestOptions.circleCropTransform()).into(binding.registerUserImage)
                                                    }
                                                }
                                            }
                                        }
                                    Toast.makeText(this, "User Register Successfully", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, WelcomeActivity::class.java))
                                    finish()
                                }

                            } else {
                                Toast.makeText(this, "User Register failed", Toast.LENGTH_SHORT)
                                    .show()
                            }

                        }
                }
            }

        }

        //set On Click Listner For the Choose Image
        binding.cardView.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
        }
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("SuspiciousIndentation")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null)
            imageUri = data.data
        Glide.with(this)
            .load(imageUri)
            .apply(RequestOptions.circleCropTransform())
            .into(binding.registerUserImage)
    }
}