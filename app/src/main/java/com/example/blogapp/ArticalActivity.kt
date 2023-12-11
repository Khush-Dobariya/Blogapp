package com.example.blogapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blogapp.Adapter.ArticleAdapter
import com.example.blogapp.Adapter.BlogAdapter
import com.example.blogapp.Model.BlogItemModel
import com.example.blogapp.databinding.ActivityArticalBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ArticalActivity : AppCompatActivity() {
    private val binding: ActivityArticalBinding by lazy {
        ActivityArticalBinding.inflate(layoutInflater)
    }
    private lateinit var databaseReference: DatabaseReference
    private val auth = FirebaseAuth.getInstance()
    private lateinit var blogAdapter: ArticleAdapter
    private val EDIT_BLOG_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }

        val currentUserId = auth.currentUser?.uid
        val recyclerView = binding.articalrecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        if (currentUserId != null) {
            blogAdapter =
                ArticleAdapter(this, emptyList(), object : ArticleAdapter.OnItemClickListener {
                    override fun onEditClick(blogItem: BlogItemModel) {
                        val intent = Intent(this@ArticalActivity, EditBlogActivity::class.java)
                        intent.putExtra("blogItem", blogItem)
                        startActivity(intent)
                    }

                    override fun onReadMoreClick(blogItem: BlogItemModel) {
                        val intent = Intent(this@ArticalActivity, ReadMoreActivity::class.java)
                        intent.putExtra("blogItem", blogItem)
                        startActivityForResult(intent, EDIT_BLOG_REQUEST_CODE)
                    }

                    override fun onDeleteClick(blogItem: BlogItemModel) {
                        deleteBlogPost(blogItem)
                    }
                })
        }
       
        recyclerView.adapter = blogAdapter


        //get Saved Blog  Data From database.
        databaseReference = FirebaseDatabase.getInstance().getReference("blogs")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val blogSavedList = ArrayList<BlogItemModel>()
                for (postSnapshot in snapshot.children) {
                    val blogSaved = postSnapshot.getValue(BlogItemModel::class.java)
                    if (blogSaved != null && currentUserId == blogSaved.userId) {
                        blogSavedList.add(blogSaved)

                    }
                }
                blogAdapter.setData(blogSavedList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@ArticalActivity,
                    "Error Loading Saved Blogs",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun deleteBlogPost(blogItem: BlogItemModel) {
        val postId = blogItem.postId
        val blogPostReference = databaseReference.child(postId!!)

        //Remove The Blog Post.
        blogPostReference.removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Blog post Deleted Successfully", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Blog post Deleted UnSuccessfully", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_BLOG_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

        }
    }

}