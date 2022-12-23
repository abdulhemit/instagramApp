package com.example.instagramapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.instagramapp.databinding.ActivityMainBinding
import com.example.instagramapp.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var firebaseDatabase : FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        binding.singInLinkBtn.setOnClickListener {
            startActivity(Intent(this,SingInActivity::class.java))
        }
        binding.signupBtn.setOnClickListener {
            CreateAccount()
        }

    }

    private fun CreateAccount() {
        val fullName = binding.FullNameLogin.text.toString()
        val userName = binding.UserNameLogin.text.toString()
        val email = binding.emailLogin.text.toString()
        val password = binding.passwordLogin.text.toString()

        when {
            TextUtils.isEmpty(fullName) -> Toast.makeText(this,"full name is requared",Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(userName) -> Toast.makeText(this,"user name is requared",Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(email) -> Toast.makeText(this,"email  is requared",Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this,"password is requared",Toast.LENGTH_LONG).show()

            else -> {
                val progressDialog = ProgressDialog(this@SignUpActivity)
                progressDialog.setTitle("Sing up")
                progressDialog.setMessage("Please wait, This may take a while...")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        saveUserInfo(fullName,userName,email,progressDialog)

                    }else{

                        val message = task.exception.toString()
                        Toast.makeText(this,"Error: $message",Toast.LENGTH_LONG).show()
                        firebaseAuth.signOut()
                        progressDialog.dismiss()

                    }
                }

            }

        }
    }

    private fun saveUserInfo(fullName: String, userName: String, email: String,progressDialog: ProgressDialog) {
        val currentUserId = firebaseAuth.currentUser!!.uid
        val usersRaf = firebaseDatabase.reference.child("users")
        val userMap = HashMap<String,Any>()
        userMap["uid"] = currentUserId
        userMap["fullName"] = fullName
        userMap["userName"] = userName
        userMap["email"] = email
        userMap["bio"] = "Hey I am using Instagram App."
        userMap["image"] = "https://firebasestorage.googleapis.com/v0/b/instagramapp-f58db.appspot.com/o/Default%20Images%2Fprofile.png?alt=media&token=f8912827-73d2-4166-b441-552c9a87954c"

        usersRaf.child(currentUserId).setValue(userMap).addOnCompleteListener {
            if (it.isSuccessful){
                progressDialog.dismiss()
                Toast.makeText(this,"Account has been created Successfully",Toast.LENGTH_LONG).show()

                FirebaseDatabase.getInstance().reference
                    .child("Follow").child(currentUserId)
                    .child("Following").child(currentUserId)
                    .setValue(true)


                startActivity(Intent(this@SignUpActivity,MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
                finish()
            }else{

                val message = it.exception.toString()
                Toast.makeText(this,"Error: $message",Toast.LENGTH_LONG).show()
                firebaseAuth.signOut()
                progressDialog.dismiss()

            }
        }


    }


}