package com.example.blogapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.blogapp.Model.BlogItemModel
import com.example.blogapp.databinding.ActivityReadMoreBinding

class ReadMoreActivity : AppCompatActivity() {
    private lateinit var binding : ActivityReadMoreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReadMoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }
        
        val blogs:BlogItemModel? = intent.getParcelableExtra<BlogItemModel>("blogItem")
        
        if (blogs != null){
            
            //Retrieve User Related Data here e.x blog title etc.
            binding.titleText.text = blogs.heading
            binding.userName.text = blogs.username
            binding.date.text = blogs.date
            binding.blogDescriptionTextView.text = blogs.post

            val userImageUrl = blogs.profileImage
            Glide.with(this)
                .load(userImageUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.profileImage)
            
        }
        else{
            Toast.makeText(this, "Failed To Load Blogs", Toast.LENGTH_SHORT).show()
        }
    }
}