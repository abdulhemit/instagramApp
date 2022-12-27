package com.example.instagramapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramapp.R
import com.example.instagramapp.databinding.MyPhotosItemBinding
import com.example.instagramapp.fragments.PostDetailsFragment
import com.example.instagramapp.model.Post
import com.google.firebase.database.core.Context
import com.squareup.picasso.Picasso

class MyPhotosAdapter(val mPhotosList: List<Post>):RecyclerView.Adapter<MyPhotosAdapter.MyPhotosHolder>() {
    class MyPhotosHolder(val binding: MyPhotosItemBinding):RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPhotosHolder {
        var binding = MyPhotosItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyPhotosHolder(binding)
    }

    @SuppressLint("CommitPrefEdits")
    override fun onBindViewHolder(holder: MyPhotosHolder, position: Int) {
        Picasso.get().load(mPhotosList[position].postImage).into(holder.binding.myPhotos)

        holder.binding.myPhotos.setOnClickListener {
            val editor = holder.itemView.context.getSharedPreferences("MyPhotoId",android.content.Context.MODE_PRIVATE).edit()
            editor.putString("PostId",mPhotosList[position].postId)
            editor.apply()
            (holder.itemView.context as FragmentActivity)
                .supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container,PostDetailsFragment()).commit()

        }
    }

    override fun getItemCount(): Int {
        return mPhotosList.size
    }
}