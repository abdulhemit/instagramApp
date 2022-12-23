package com.example.instagramapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.instagramapp.R
import com.example.instagramapp.adapter.PostAdapter
import com.example.instagramapp.databinding.FragmentHomeBinding
import com.example.instagramapp.databinding.FragmentProfileBinding
import com.example.instagramapp.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue


class HomeFragment : Fragment() {

    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var postAdapter: PostAdapter
    private lateinit var postList: MutableList<Post>
    private lateinit var followingList: MutableList<Post>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        binding.idRecyclerviewHomeFragment.layoutManager = linearLayoutManager

        postList = ArrayList()
        postAdapter = PostAdapter(postList as ArrayList<Post>)
        binding.idRecyclerviewHomeFragment.adapter = postAdapter



        checkFollowing()
        return  view
    }

    private fun checkFollowing() {
        followingList = ArrayList()

           val followingRef = FirebaseDatabase.getInstance().reference
                .child("Follow").child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child("Following")


        followingRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){

                    (followingList as ArrayList<String>).clear()

                    for (snap in snapshot.children){
                        snap.key?.let { (followingList as ArrayList<String>).add(it) }
                    }
                    retriewPost()

                }
            }
            override fun onCancelled(error: DatabaseError) {
            }

        })


    }

    private fun retriewPost() {
        val postRef = FirebaseDatabase.getInstance().reference.child("Post")
        postRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                postList.clear()
                for (snap in snapshot.children){


                    val post = snap.getValue<Post>(Post::class.java)

                    for(id in (followingList as ArrayList<String>)){

                        if(post!!.publisher == id){

                            postList.add(post)

                        }
                        postAdapter.notifyDataSetChanged()

                    }


                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }


}