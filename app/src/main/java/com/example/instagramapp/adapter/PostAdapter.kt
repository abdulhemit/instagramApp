package com.example.instagramapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramapp.CommentsActivity
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


        // kullanici paylasan resimleri begenmek
        isLikes(post.postId,holder.binding.postImageLikeBtn)

        // bullanicinin paylasilan resminin begeni sayisi
        numberOfLikes(post.postId,holder.binding.likes)

        // comment sayisini getirmek
        getTotalComments(post.postId,holder.binding.comments)

        // paylasilan resimin begeni kontrolu
        checkSavesStatus(post.postId,holder.binding.postSaveCommentBtn)




        Picasso.get().load(post.postImage).placeholder(R.drawable.select).into(holder.binding.postImageHome)
        holder.binding.description.setText(post.discription)

        post.publisher?.let {
            publisherInfo(holder.binding.profileImage,holder.binding.userName,holder.binding.publisher,
                it
            )
        }

        holder.binding.postImageCommentBtn.setOnClickListener {
            val intent = Intent(holder.itemView.context,CommentsActivity::class.java)
            intent.putExtra("postId",post.postId)
            intent.putExtra("publisherId",post.publisher)
            holder.itemView.context.startActivity(intent)
        }

        holder.binding.comments.setOnClickListener {
            val intent = Intent(holder.itemView.context,CommentsActivity::class.java)
            intent.putExtra("postId",post.postId)
            intent.putExtra("publisherId",post.publisher)
            holder.itemView.context.startActivity(intent)
        }


        holder.binding.postImageLikeBtn.setOnClickListener {

            if (holder.binding.postImageLikeBtn.tag == "Like"){
                FirebaseDatabase.getInstance().reference.child("Likes")
                    .child(post.postId!!)
                    .child(firebaseUser.uid)
                    .setValue(true)
                holder.binding.likes.visibility = View.VISIBLE

            }else{
                FirebaseDatabase.getInstance().reference.child("Likes")
                    .child(post.postId!!)
                    .child(firebaseUser.uid)
                    .removeValue()


                holder.binding.likes.visibility = View.GONE
            }

        }
        holder.binding.postSaveCommentBtn.setOnClickListener {

            if (holder.binding.postSaveCommentBtn.tag == "Save"){

                FirebaseDatabase.getInstance().reference.
                child("Saves")
                    .child(firebaseUser.uid)
                    .child(post.postId!!)
                    .setValue(true)

            }else{

                FirebaseDatabase.getInstance().reference
                    .child("Saves")
                    .child(firebaseUser.uid)
                    .child(post.postId!!)
                    .removeValue()

            }
        }
    }

    private fun checkSavesStatus(postId: String?, postImageSaveBtn: ImageView) {

        val savesRef = FirebaseDatabase.getInstance().reference
            .child("Saves")
            .child(firebaseUser.uid)

        savesRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(postId!!).exists()){

                    postImageSaveBtn.setImageResource(R.drawable.save_large_icon)
                    postImageSaveBtn.tag = "Saved"

                } else {

                    postImageSaveBtn.setImageResource(R.drawable.save_unfilled_large_icon)
                    postImageSaveBtn.tag = "Save"

                }

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }


    private fun numberOfLikes(postId: String?, likes: TextView) {

        val LikesRef =  FirebaseDatabase.getInstance()
            .reference
            .child("Likes")
            .child(postId!!)

        LikesRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){

                    likes.text = snapshot.childrenCount.toString() + " Likes"
                }

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }

    private fun getTotalComments(postId: String?, comments: TextView) {

        val LikesRef =  FirebaseDatabase.getInstance()
            .reference
            .child("Comments")
            .child(postId!!)

        LikesRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){

                    comments.text = "View all " + snapshot.childrenCount.toString() + " Comments"
                }

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }



    private fun isLikes(postId: String?, postImageLikeBtn: ImageView) {

        val LikesRef =  FirebaseDatabase.getInstance()
            .reference
            .child("Likes")
            .child(postId!!)

        LikesRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(firebaseUser.uid).exists()){

                    postImageLikeBtn.setImageResource(R.drawable.heart_clicked)
                    postImageLikeBtn.tag = "Liked"

                }else {

                    postImageLikeBtn.setImageResource(R.drawable.heart)
                    postImageLikeBtn.tag = "Like"

                }

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

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