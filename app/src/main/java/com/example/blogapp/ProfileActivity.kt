package com.example.blogapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.sax.StartElementListener
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.blogapp.databinding.ActivityProfileBinding
import com.example.blogapp.register.WelcomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileActivity : AppCompatActivity() {
    private val binding: ActivityProfileBinding by lazy {
        ActivityProfileBinding.inflate(layoutInflater)
    }

    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //to Go To Add Article Page.
        binding.addNewBlogButton.setOnClickListener {
            startActivity(Intent(this, AddArticalActivity::class.java))
        }

        //To LogOut.
        binding.logOutButton.setOnClickListener {
            auth.signOut()

            //Navigate
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        }

            //To Go To Your Article Activity
        binding.articalsButton.setOnClickListener{
            startActivity(Intent(this,ArticalActivity::class.java))
        }


        //Initialized Firebase.
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference.child("users")

        val userId = auth.currentUser?.uid

        if (userId != null) {
            loadUserPrfileData(userId)
        }
    }

    private fun loadUserPrfileData(userId: String) {
        val userReference = databaseReference.child(userId)

        //Load User Profile Image
        userReference.child("profileImage").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val profileImageUrl = snapshot.getValue(String::class.java)
                if (profileImageUrl != null) {
                    Glide.with(this@ProfileActivity)
                        .load(profileImageUrl)
                        .into(binding.userProfile)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProfileActivity, "Failed to load user image", Toast.LENGTH_SHORT).show()
            }
        })

        //Load UserName.
        userReference.child("name").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userName = snapshot.getValue(String::class.java)

                if (userName != null) {
                    binding.userName.text = userName
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }
}