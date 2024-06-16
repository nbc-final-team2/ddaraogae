package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ItemHomeDogSelectionBinding
import com.nbcfinalteam2.ddaraogae.presentation.model.DogInfo

class DogProfileAdapter(
    private val onItemClick:(DogInfo) -> Unit
) : ListAdapter<DogInfo,DogProfileAdapter.ItemViewHolder>(
    object :DiffUtil.ItemCallback<DogInfo>(){

        override fun areItemsTheSame(oldItem: DogInfo, newItem: DogInfo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DogInfo, newItem: DogInfo): Boolean {
            return oldItem == newItem
        }
    }
) {

    inner class ItemViewHolder(
        private val binding:ItemHomeDogSelectionBinding,
        private val context: Context,
        private val onItemClick: (DogInfo) -> Unit
    ): RecyclerView.ViewHolder(binding.root){
        fun bind(dogData: DogInfo) = with(binding){
            Glide.with(context)
                .load(dogData.thumbnailUrl)
                .into(ivDogImage)
            tvDogName.text = dogData.name
            if (dogData.isSelected) binding.ivDogImage.borderColor = context.resources.getColor(R.color.banana)
            else binding.ivDogImage.borderColor = context.resources.getColor(R.color.white)

            binding.root.setOnClickListener {
                onItemClick(dogData)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DogProfileAdapter.ItemViewHolder {
        return ItemViewHolder(
            ItemHomeDogSelectionBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), parent.context, onItemClick
        )
    }

    override fun onBindViewHolder(holder: DogProfileAdapter.ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}