package com.example.blogapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.blogapp.databinding.ActivitySplashBinding
import com.example.blogapp.register.WelcomeActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
private val binding : ActivitySplashBinding by lazy {
    ActivitySplashBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        
        Handler(Looper.getMainLooper()).postDelayed({
            intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}