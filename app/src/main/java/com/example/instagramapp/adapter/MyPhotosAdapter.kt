package com.example.instagramapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramapp.databinding.MyPhotosItemBinding
import com.example.instagramapp.model.Post
import com.squareup.picasso.Picasso

class MyPhotosAdapter(val mPhotosList: List<Post>):RecyclerView.Adapter<MyPhotosAdapter.MyPhotosHolder>() {
    class MyPhotosHolder(val binding: MyPhotosItemBinding):RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPhotosHolder {
        var binding = MyPhotosItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyPhotosHolder(binding)
    }

    override fun onBindViewHolder(holder: MyPhotosHolder, position: Int) {
        Picasso.get().load(mPhotosList[position].postImage).into(holder.binding.myPhotos)

    }

    override fun getItemCount(): Int {
        return mPhotosList.size
    }
}