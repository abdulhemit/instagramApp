package com.example.instagramapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramapp.R
import com.example.instagramapp.databinding.PostLayoutBinding
import com.example.instagramapp.model.Post
import com.example.instagramapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class PostAdapter(val mPostList:List<Post>):RecyclerView.Adapter<PostAdapter.PostHolder>() {

    class PostHolder(val binding : PostLayoutBinding):RecyclerView.ViewHolder(binding.root)
    private lateinit var firebaseUser: FirebaseUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        var binding = PostLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PostAdapter.PostHolder(binding)
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val post = mPostList[position]

        Picasso.get().load(post.postImage).placeholder(R.drawable.ekrankaydi).into(holder.binding.postImageHome)
        holder.binding.description.setText(post.discription)

        post.publisher?.let {
            publisherInfo(holder.binding.profileImage,holder.binding.userName,holder.binding.publisher,
                it
            )
        }
    }

    override fun getItemCount(): Int {
        return mPostList.size
    }
    private fun publisherInfo(
        profileImage: CircleImageView,
        userName: TextView,
        publisher: TextView,
        publisher1: String
    ) {
        val usersRaf = FirebaseDatabase.getInstance().reference.child("users").child(publisher1)

        usersRaf.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){
                    val user = snapshot.getValue<User>(User::class.java)
                    user.let {
                        userName.text = it!!.userName
                        publisher.text = it.fullName
                        Picasso.get().load(user?.image).into(profileImage)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }
}