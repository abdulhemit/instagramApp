package com.example.instagramapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.instagramapp.databinding.ActivitySignUpBinding
import com.example.instagramapp.databinding.ActivitySingInBinding
import com.google.firebase.auth.FirebaseAuth

class SingInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySingInBinding
    private lateinit var firebaseAuth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        binding.signupLinkBtn.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
        }
        binding.loginBtn.setOnClickListener {
            loginuser()
        }
    }

    private fun loginuser() {
        val email = binding.emailLogin.text.toString()
        val password = binding.passwordLogin.text.toString()

        when {
            TextUtils.isEmpty(email) -> Toast.makeText(this,"email  is requared",Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this,"password is requared",Toast.LENGTH_LONG).show()
            else -> {
                val progressDialog = ProgressDialog(this@SingInActivity)
                progressDialog.setTitle("login")
                progressDialog.setMessage("Please wait, This may take a while...")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                 firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task->
                     if (task.isSuccessful){
                         progressDialog.dismiss()
                         Toast.makeText(this,"Account has been created Successfully",Toast.LENGTH_LONG).show()
                         startActivity(Intent(this@SingInActivity,MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
                         finish()

                     }else {
                         val message = task.exception.toString()
                         Toast.makeText(this,"Error: $message",Toast.LENGTH_LONG).show()
                         firebaseAuth.signOut()
                         progressDialog.dismiss()
                     }
                 }

            }
        }


    }

    override fun onStart() {
        super.onStart()
        if(firebaseAuth.currentUser != null){
            startActivity(Intent(this@SingInActivity,MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
            finish()
        }
    }
}