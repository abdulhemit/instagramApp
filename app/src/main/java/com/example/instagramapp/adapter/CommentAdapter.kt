package com.example.instagramapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramapp.R
import com.example.instagramapp.databinding.CommentItemBinding
import com.example.instagramapp.databinding.PostLayoutBinding
import com.example.instagramapp.model.Comment
import com.example.instagramapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class CommentAdapter(val commentList: List<Comment>):RecyclerView.Adapter<CommentAdapter.CommentHolder>() {

    private lateinit var firebaseUser: FirebaseUser
    class CommentHolder(val binding: CommentItemBinding):RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentHolder {
        var binding = CommentItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CommentHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        holder.binding.commentItem.text = commentList[position].comment
        getUserInfo(holder.binding.profileImageCommentItem,holder.binding.userNameCommentItem,commentList[position].publisher)


    }

    private fun getUserInfo(
        profileImageCommentItem: CircleImageView,
        userNameCommentItem: TextView,
        publisher: String?
    ) {

        val userRef = FirebaseDatabase.getInstance().reference.child("users").child(publisher!!)

        userRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){

                    val user = snapshot.getValue(User::class.java)
                    userNameCommentItem.text = user!!.userName
                    Picasso.get().load(user.image).placeholder(R.drawable.profile).into(profileImageCommentItem)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }

    override fun getItemCount(): Int {
        return commentList.size
    }
}