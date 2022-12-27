package com.example.instagramapp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.instagramapp.R
import com.example.instagramapp.adapter.PostAdapter
import com.example.instagramapp.databinding.FragmentPostDetailsBinding
import com.example.instagramapp.databinding.FragmentProfileBinding
import com.example.instagramapp.model.Post
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PostDetailsFragment : Fragment() {
    private var _binding : FragmentPostDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var postAdapter: PostAdapter
    private lateinit var postList: MutableList<Post>
    private var postId = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("CommitPrefEdits")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostDetailsBinding.inflate(inflater, container, false)
        val view = binding.root

        val preference = requireContext().getSharedPreferences("MyPhotoId",android.content.Context.MODE_PRIVATE)
        if (preference != null){
            postId = preference.getString("PostId","noon").toString()
        }


        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        binding.idRecyclerviewPostDetailsFragment.layoutManager = linearLayoutManager

        postList = ArrayList()
        postAdapter = PostAdapter(postList as ArrayList<Post>)
        binding.idRecyclerviewPostDetailsFragment.adapter = postAdapter


        retriewPost()
        return view
    }
    private fun retriewPost() {
        val postRef = FirebaseDatabase.getInstance().reference
            .child("Post")
            .child(postId)


        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                postList.clear()

                val post = snapshot.getValue<Post>(Post::class.java)

                postList.add(post!!)

                postAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }


}