package com.example.instagramapp.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.instagramapp.AccountSettingsActivity
import com.example.instagramapp.SingInActivity
import com.example.instagramapp.adapter.MyPhotosAdapter
import com.example.instagramapp.databinding.FragmentProfileBinding
import com.example.instagramapp.model.Post
import com.example.instagramapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.squareup.picasso.Picasso
import java.util.Collections


class ProfileFragment : Fragment() {

    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var profileId : String
    private lateinit var firebaseUser : FirebaseUser
    private lateinit var firebaseDatabase : FirebaseDatabase
    private lateinit var myPhotosList: List<Post>
    private lateinit var myPhotosAdapter: MyPhotosAdapter

    private lateinit var myPhotosListSaved: List<Post>
    private lateinit var myPhotosAdapterSaved: MyPhotosAdapter
    private lateinit var mySavesImg: List<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.recyclerviewMyPhotosPost.visibility = View.VISIBLE
        binding.recyclerviewMyPhotosSaved.visibility = View.GONE

        binding.imagesGridViewBtn.setOnClickListener {
            binding.recyclerviewMyPhotosPost.visibility = View.VISIBLE
            binding.recyclerviewMyPhotosSaved.visibility = View.GONE
        }

        binding.imagesSaveBtn.setOnClickListener {
            binding.recyclerviewMyPhotosSaved.visibility = View.VISIBLE
            binding.recyclerviewMyPhotosPost.visibility = View.GONE

        }


       // Recycilirview my Post Photos
        val linearLayoutManager : LinearLayoutManager = GridLayoutManager(requireContext(),3)
        binding.recyclerviewMyPhotosPost.layoutManager = linearLayoutManager
        binding.recyclerviewMyPhotosPost.setHasFixedSize(true)
        myPhotosList = ArrayList()
        myPhotosAdapter = MyPhotosAdapter(myPhotosList)
        binding.recyclerviewMyPhotosPost.adapter = myPhotosAdapter


        // Recycilirview my Saved Photos
        val linearLayoutManager2 : LinearLayoutManager = GridLayoutManager(requireContext(),3)
        binding.recyclerviewMyPhotosSaved.layoutManager = linearLayoutManager2
        binding.recyclerviewMyPhotosSaved.setHasFixedSize(true)
        myPhotosListSaved = ArrayList()
        myPhotosAdapterSaved = MyPhotosAdapter(myPhotosListSaved)
        binding.recyclerviewMyPhotosSaved.adapter = myPhotosAdapterSaved



        if ( FirebaseAuth.getInstance().currentUser == null){
            Intent(requireContext(), SingInActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)

        }
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        firebaseDatabase = FirebaseDatabase.getInstance()
        val pref =  this.context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null){

            this.profileId = pref.getString("profileId","noon").toString()
        }

        if (profileId == firebaseUser.uid){

            binding.editProfileBtn.setText("Edit Profile")

        }else if (profileId != firebaseUser.uid){

            checkFollowAndFollowingButtonStatus()
        }


        //profile edit button islemleri
        binding.editProfileBtn.setOnClickListener {
            //startActivity(Intent(requireContext(),AccountSettingsActivity::class.java))
            val getButtonText = binding.editProfileBtn.text.toString()
            when {
                getButtonText == "Edit Profile" -> startActivity(Intent(requireContext(),
                    AccountSettingsActivity::class.java))

                // kullaniciyi takip etmek icin follow and following button click
                getButtonText == "Follow" -> {
                    firebaseUser.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(profileId)
                            .setValue(true)
                    }
                    firebaseUser.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(it1.toString())
                            .setValue(true)
                    }
                }

                getButtonText == "Following" -> {
                    firebaseUser.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(profileId)
                            .removeValue()
                    }
                    firebaseUser.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(it1.toString())
                            .removeValue()
                    }
                }
                else -> {

                }
            }

        }



        getFollowing()
        getFollowers()
        getUserInfo()
        myPhotos()
        getTotalNumberOfPost()
        mySaves()
        return view
    }


    // get myPostPhotos
    private fun myPhotos(){

        val  myPhotoRef = firebaseDatabase.reference.child("Post")
        myPhotoRef.addValueEventListener(object : ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    (myPhotosList as ArrayList<Post>).clear()

                    for (snap in snapshot.children){

                        val post = snap.getValue(Post::class.java)!!
                        if (post.publisher == profileId){

                            (myPhotosList as ArrayList<Post>).add(post)
                        }
                        Collections.reverse(myPhotosList)
                    }
                    myPhotosAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })


    }

    private fun checkFollowAndFollowingButtonStatus() {
        val followingRef = firebaseUser.uid.let { uid ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(uid.toString())
                .child("Following")

        }
        if (followingRef != null ){

            followingRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child(profileId).exists()){

                        binding.editProfileBtn.text = "Following"

                    }else{

                        binding.editProfileBtn.text = "Follow"

                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }

            })
        }

    }


    private fun getFollowing(){

        val followingRef = firebaseUser?.uid.let { uid ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(uid.toString())
                .child("Following")

        }
        followingRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    binding.profileFallowingId.text = snapshot.childrenCount.toString()
                }

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
    private fun getFollowers(){

        val followersRef = firebaseUser?.uid.let { uid ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(uid.toString())
                .child("Followers")

        }

        followersRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    binding.profileFallowersId.text = snapshot.childrenCount.toString()
                }

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
    private fun getUserInfo(){

        val usersRaf = firebaseDatabase.reference.child("users").child(profileId)

        usersRaf.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {


                if (snapshot.exists()){
                    val user = snapshot.getValue<User>(User::class.java)
                    user.let {
                        binding.userNameFragmentProfile.text = user?.userName
                        binding.userFullNameFragmentProfile.text = user?.fullName
                        binding.userBioFragmentProfile.text = user?.bio
                        Picasso.get().load(user?.image).into(binding.userImageFragmentProfile)
                    }

                }


            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun onStop() {
        super.onStop()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId",firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId",firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId",firebaseUser.uid)
        pref?.apply()
    }

    // paylasilan resim sayisini getirmek
    private fun getTotalNumberOfPost(){

        val postRef = FirebaseDatabase.getInstance().reference.child("Post")

        postRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    var numberCounter = 0

                    for (snap in snapshot.children){

                        val post = snap.getValue(Post::class.java)!!
                        if (post.publisher == profileId){
                            numberCounter++
                        }
                    }
                    binding.profilePostId.text  = " " + numberCounter
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
    private fun mySaves(){

        mySavesImg = ArrayList()

        val savedRef = FirebaseDatabase.getInstance().reference
            .child("Saves")
            .child(firebaseUser.uid)

        savedRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){

                    for (snap in snapshot.children){
                        (mySavesImg as ArrayList<String>).add(snap.key.toString())
                    }
                    readSavedImagesData()

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    // kaydedilen resimleri cekmek
    private fun readSavedImagesData(){

        val postRef = FirebaseDatabase.getInstance().reference
            .child("Post")


        postRef.addValueEventListener(object : ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){

                    (myPhotosListSaved as ArrayList<Post>).clear()

                    for(snap in snapshot.children){

                        val post = snap.getValue(Post::class.java)

                        for (key in mySavesImg){

                            if (post!!.postId == key){

                                (myPhotosListSaved as ArrayList<Post>).add(post)

                            }

                        }
                    }
                    myPhotosAdapterSaved.notifyDataSetChanged()
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })


    }


}