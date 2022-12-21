package com.example.instagramapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramapp.R
import com.example.instagramapp.databinding.UserItemBinding
import com.example.instagramapp.model.User
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class UserAdapter (var mUser: List<User>, var isFragment : Boolean = false): RecyclerView.Adapter<UserAdapter.UserHolder>() {

    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    class UserHolder(var binding : UserItemBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        var binding = UserItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return UserHolder(binding)
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        val user = mUser[position]
        holder.binding.userNamaSearchItem.setText(mUser.get(position).userName)
        holder.binding.userFullNamaSearchItem.setText(mUser.get(position).fullName)
        Picasso.get().load(mUser.get(position).image).placeholder(R.drawable.profile).into(holder.binding.userImageSearchItem)

        checkFollowingStatus(user.uid,holder.binding.followBtnSearch)

        holder.binding.followBtnSearch.setOnClickListener {
            if (holder.binding.followBtnSearch.text.toString() == "Follow"){
                firebaseUser?.uid.let { uid ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(uid.toString())
                        .child("Following").child(user.uid.toString())
                        .setValue(true)
                        .addOnCompleteListener { task->
                            if (task.isSuccessful){
                                firebaseUser?.uid.let { uid ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(user.uid.toString())
                                        .child("Followers").child(uid.toString())
                                        .setValue(true)
                                        .addOnCompleteListener { task->
                                            if (task.isSuccessful){

                                            }

                                        }
                                }
                            }

                        }
                }


            } else {
                firebaseUser?.uid.let { uid ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(uid.toString())
                        .child("Following").child(user.uid.toString())
                        .removeValue()
                        .addOnCompleteListener { task->
                            if (task.isSuccessful){
                                firebaseUser?.uid.let { uid ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(user.uid.toString())
                                        .child("Followers").child(uid.toString())
                                        .removeValue()
                                        .addOnCompleteListener { task->
                                            if (task.isSuccessful){

                                            }

                                        }
                                }
                            }

                        }
                }
            }
        }

    }

    private fun checkFollowingStatus(mUser_uid: String?, followBtnSearch: MaterialButton) {

        val followingRef = firebaseUser?.uid.let { uid ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(uid.toString())
                .child("Following")

        }
        followingRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(mUser_uid.toString()).exists()){

                    followBtnSearch.text = "Following"

                }else{

                    followBtnSearch.text = "Follow"

                }

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }

    override fun getItemCount(): Int {
        return mUser.size
    }
}