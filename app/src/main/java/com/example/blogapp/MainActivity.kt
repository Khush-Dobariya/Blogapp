package com.example.blogapp

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.blogapp.Adapter.BlogAdapter
import com.example.blogapp.Model.BlogItemModel
import com.example.blogapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var databaseReference: DatabaseReference
    private val blogItems = mutableListOf<BlogItemModel>()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //To Go Save artical page.
        binding.saveArticalButton.setOnClickListener {
            startActivity(Intent(this,SavedArticalsActivity::class.java))
        }

        //To Go Profile Activity.
        binding.profileImage.setOnClickListener{
            startActivity(Intent(this,ProfileActivity::class.java))
        }

        binding.cardView2.setOnClickListener{
            startActivity(Intent(this,ProfileActivity::class.java))
        }


        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference.child("blogs")

        val userId = auth.currentUser?.uid


        //Set User  Profile
        if (userId != null) {
            loadUserProfileImage(userId)
        }
        //Set Blog Post Into RecyclerView

        //Initialize The Recycler View And Set Adapter
        val recyclerView = binding.blogRecyclerView
        val blogAdapter = BlogAdapter(blogItems)
        recyclerView.adapter = blogAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        //Fetch Data From Firebase Database.
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                blogItems.clear()

                for (snapshot in snapshot.children) {
                    val blogItem = snapshot.getValue(BlogItemModel::class.java)
                    if (blogItem != null) {
                        blogItems.add(blogItem)
                    }
                }
                //Reverse The List.
                blogItems.reverse()

                //Notify The Adapter That the data has changed.
                blogAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Blog Loading Failed", Toast.LENGTH_SHORT).show()
            }
        })



        binding.floatingAddArticalButton.setOnClickListener {
            startActivity(Intent(this, AddArticalActivity::class.java))
        }

    }

    private fun loadUserProfileImage(userId: String) {
        val userReference = FirebaseDatabase.getInstance().reference.child("users").child(userId)

        userReference.child("profileImage").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val profileImageUrl = snapshot.getValue(String::class.java)

                if (profileImageUrl != null) {
                    Glide.with(this@MainActivity)
                        .load(profileImageUrl)
                        .into(binding.profileImage)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error Loading Profile Image", Toast.LENGTH_SHORT)
                    .show()
            }

        })
    }
}