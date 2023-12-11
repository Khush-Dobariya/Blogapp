package com.example.blogapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.blogapp.Model.BlogItemModel
import com.example.blogapp.databinding.ActivityEditBlogBinding
import com.google.firebase.database.FirebaseDatabase

class EditBlogActivity : AppCompatActivity() {
    private val binding: ActivityEditBlogBinding by lazy {
        ActivityEditBlogBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }

        val BlogItemModel = intent.getParcelableExtra<BlogItemModel>("blogItem")

        binding.blogTitle.editText?.setText(BlogItemModel?.heading)
        binding.blogDescription.editText?.setText(BlogItemModel?.post)

        binding.saveBlogButton.setOnClickListener {
            val updatedTitle = binding.blogTitle.editText?.text.toString().trim()
            val updatedDescription = binding.blogDescription.editText?.text.toString().trim()

            if (updatedTitle.isEmpty() || updatedDescription.isEmpty()) {
                Toast.makeText(this, "Pleads Fill All the Details", Toast.LENGTH_SHORT).show()
            } else {
                BlogItemModel?.heading = updatedTitle
                BlogItemModel?.post = updatedDescription

                if (BlogItemModel != null) {
                    updatedDataInFirebase(BlogItemModel)
                }
            }

        }

    }

    private fun updatedDataInFirebase(blogItemModel: BlogItemModel) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("blogs")
        val postId = blogItemModel.postId

        databaseReference.child(postId!!).setValue(blogItemModel)
            .addOnSuccessListener {
                Toast.makeText(this, "Blog Updated Successful", Toast.LENGTH_SHORT).show()
                finish()

            }.addOnFailureListener {
                Toast.makeText(this, "Blog Updated UnSuccessful", Toast.LENGTH_SHORT).show()
            }
    }
}