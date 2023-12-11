package com.example.blogapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.blogapp.Model.BlogItemModel
import com.example.blogapp.databinding.ActivityAddArticalBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date

class AddArticalActivity : AppCompatActivity() {
    private val binding: ActivityAddArticalBinding by lazy {
        ActivityAddArticalBinding.inflate(layoutInflater)
    }

    private val databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("blogs")
    private val userReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("users")
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.addBlogButton.setOnClickListener {

            val title = binding.blogTitle.editText?.text.toString().trim()
            val description = binding.blogDescription.editText?.text.toString().trim()

            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Pleas Fill All The Fields", Toast.LENGTH_SHORT).show()

            }

            //Get Current User
            val user: FirebaseUser? = auth.currentUser

            if (user != null) {
                val userId = user.uid
                val userName = user.displayName ?: "Anonymous"
                val userImageUrl = user.photoUrl ?: ""

                //Fetch User name and User profile From The database

                userReference.child(userId)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        @SuppressLint("SimpleDateFormat")
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val userData =
                                snapshot.getValue(com.example.blogapp.Model.UserData::class.java)
                            if (userData != null) {
                                val userNameFromDB = userData.name
                                val userImageUrlFromDB = userData.profileImage

                                // val CurrentDate = SimpleDateFormat("yyyy-MM-dd").format(Date())
                                val CurrentDate = SimpleDateFormat("yyyy-MM-dd").format(Date())

                                //Create A BlogItem
                                val blogItem = BlogItemModel(
                                    title,
                                    userNameFromDB,
                                    CurrentDate,
                                    description,
                                    userId,
                                    0,
                                    userImageUrlFromDB

                                )
                                //generate Unique Key For The Blog post.
                                val key = databaseReference.push().key
                                if (key != null) {

                                    blogItem.postId = key
                                    val blogReference = databaseReference.child(key)
                                    blogReference.setValue(blogItem).addOnCompleteListener {
                                        if (it.isSuccessful) {


                                            finish()
                                        } else {
                                            Toast.makeText(this@AddArticalActivity, "Failed To Add Blog", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })
            }


        }
    }
}