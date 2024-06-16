package com.nbcfinalteam2.ddaraogae.presentation.ui.walk

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.presentation.model.DogInfo

class FinishDogAdapter(private val dogList: List<DogInfo>) : RecyclerView.Adapter<FinishDogAdapter.FinishDogViewHolder>() {

    class FinishDogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_finish_dogs_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FinishDogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_finish_dogs, parent, false)
        return FinishDogViewHolder(view)
    }

    override fun onBindViewHolder(holder: FinishDogViewHolder, position: Int) {
        val item = dogList[position]
        Glide.with(holder.itemView.context)
            .load(item.thumbnailUrl)
            .error(R.drawable.ic_dog_thumbnail)
            .fallback(R.drawable.ic_dog_thumbnail)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int = dogList.size
}