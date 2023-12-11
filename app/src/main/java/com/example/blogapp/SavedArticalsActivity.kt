package com.example.blogapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blogapp.Adapter.BlogAdapter
import com.example.blogapp.Model.BlogItemModel
import com.example.blogapp.databinding.ActivityAddArticalBinding
import com.example.blogapp.databinding.ActivitySavedArticalsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SavedArticalsActivity : AppCompatActivity() {
    private val binding: ActivitySavedArticalsBinding by lazy {
        ActivitySavedArticalsBinding.inflate(layoutInflater)
    }

    private val savedBlogsArticles = mutableListOf<BlogItemModel>()
    private lateinit var blogAdapter: BlogAdapter
    private val auth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

         //Initialize BlogAdapter.
        blogAdapter = BlogAdapter(savedBlogsArticles.filter { it.isSaved }.toMutableList())
        val recyclerView = binding.savedArticalRecyclerView
        recyclerView.adapter = blogAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)


        val userId = auth.currentUser?.uid
        if (userId != null){

            val userReference = FirebaseDatabase.getInstance()
                .getReference("users").child(userId).child("savBlogPost")

            userReference.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children){
                        val postId = postSnapshot.key
                        val isSaved = postSnapshot.value as Boolean
                        if (postId != null && isSaved){
                            //Fetch The Corresponding Blog Item On PostId using A Coroutine.
                            CoroutineScope(Dispatchers.IO).launch {
                                val blogItem = fetchBlogItem(postId)
                                if (blogItem != null){
                                    savedBlogsArticles.add(blogItem)

                                    launch(Dispatchers.Main){
                                        blogAdapter.updateData(savedBlogsArticles)
                                    }

                                }
                            }


                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

        }


        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }
    }

    private suspend fun fetchBlogItem(postId: String): BlogItemModel? {
        val blogReference = FirebaseDatabase.getInstance()
            .getReference("blogs")
        return try {
            val dataSnapshot = blogReference.child(postId).get().await()
            val blogData = dataSnapshot.getValue(BlogItemModel::class.java)
            blogData
        }catch (e:Exception){
            null
        }
    }
}