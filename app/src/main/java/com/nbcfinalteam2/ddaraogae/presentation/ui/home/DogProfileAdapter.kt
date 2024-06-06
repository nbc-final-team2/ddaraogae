package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nbcfinalteam2.ddaraogae.databinding.ItemHomeDogSelectionBinding
import com.nbcfinalteam2.ddaraogae.domain.entity.DogEntity

class DogProfileAdapter(private val items: List<DogEntity>) : RecyclerView.Adapter<DogProfileAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DogProfileAdapter.ViewHolder {
        val binding = ItemHomeDogSelectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DogProfileAdapter.ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(private val binding: ItemHomeDogSelectionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DogEntity) {
            binding.apply {
                Glide.with(ivDogImage.context)
                    .load(item.thumbnailUrl)
                    .into(ivDogImage)
                tvDogName.text = item.name
            }

        }
    }
}