package com.example.instagramapp.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramapp.R
import com.example.instagramapp.adapter.UserAdapter
import com.example.instagramapp.databinding.FragmentSearchBinding
import com.example.instagramapp.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchFragment : Fragment() {

    private var _binding : FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var userList : MutableList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSearchBinding.inflate(inflater,container,false)
        val view = binding
        recyclerView = binding.idRecyclerviewSearchFragment
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        userList = ArrayList<User>()
        userAdapter = UserAdapter(userList,true)
        recyclerView.adapter = userAdapter

        binding.searchEditTExt.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.searchEditTExt.text.toString() == ""){

                }else{
                    binding.idRecyclerviewSearchFragment.visibility = View.VISIBLE
                    retrieveUsers()
                    searchUser(p0.toString().toLowerCase())
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })


        return view.root
    }

    private fun retrieveUsers() {
        val users = FirebaseDatabase.getInstance().reference.child("users")
        users.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (binding.searchEditTExt.text.toString() == ""){
                    for (snap in snapshot.children){
                        val user = snap.getValue(User::class.java)
                        userList.clear()
                        if (user != null){
                            userList.add(user)
                        }
                    }
                    userAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun searchUser(input: String) {
        val query = FirebaseDatabase.getInstance().reference
          .child("users")
          .orderByChild("fullName")
          .startAt(input)
          .endAt(input + "\uf8ff ")

        query.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                userList.clear()
                for (snap in snapshot.children){
                    val user = snap.getValue(User::class.java)
                    if (user != null){
                        userList.add(user)
                    }
                }
                userAdapter.notifyDataSetChanged()

            }
            override fun onCancelled(error: DatabaseError) {

            }
        })

    }


}