package com.example.instagramapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.instagramapp.databinding.ActivitySignUpBinding
import com.example.instagramapp.databinding.ActivitySingInBinding

class SingInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySingInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.signupLinkBtn.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
        }
    }
}