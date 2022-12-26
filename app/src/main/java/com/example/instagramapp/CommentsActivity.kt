package com.example.instagramapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.instagramapp.databinding.ActivityCommentsBinding
import com.example.instagramapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class CommentsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommentsBinding
    private lateinit var postId : String
    private lateinit var publisher: String
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var firebaseDatabase: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val view = binding

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        firebaseDatabase = FirebaseDatabase.getInstance()
        getUserInfo()

        val intent = intent
        postId = intent.getStringExtra("postId").toString()
        publisher = intent.getStringExtra("publisherId").toString()

       binding.postCommentCommentActivity.setOnClickListener {
           if (binding.theComment.text.toString() == ""){

               Toast.makeText(this@CommentsActivity,"please write your comment...",Toast.LENGTH_LONG).show()

           }else{

               addComment()
           }
       }

    }

    private fun addComment() {

        val commentReg = firebaseDatabase.reference
            .child("Comments")
            .child(postId)

        val commentMap = HashMap<String,Any>()
        commentMap["comment"] = binding.theComment.text.toString()
        commentMap["publisher"] = firebaseUser.uid.toString()

        commentReg.push().setValue(commentMap)
        binding.theComment.text.clear()


    }

    private fun getUserInfo(){

        val usersRaf = firebaseDatabase.reference.child("users").child(firebaseUser.uid)

        usersRaf.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {


                if (snapshot.exists()){
                    val user = snapshot.getValue<User>(User::class.java)
                    user.let {

                        Picasso.get().load(user?.image).into(binding.profileIdCommentActivity)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}