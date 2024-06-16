package com.nbcfinalteam2.ddaraogae.presentation.ui.walk

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ItemWalkDogsBinding
import com.nbcfinalteam2.ddaraogae.presentation.model.DogInfo

class WalkDogAdapter(
    private val onClick: (String) -> Unit
) : ListAdapter<DogInfo, WalkDogAdapter.WalkDogViewHolder>(DIFF_UTIL) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalkDogViewHolder {
        return WalkDogViewHolder(
            ItemWalkDogsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: WalkDogViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    inner class WalkDogViewHolder(
        private val binding: ItemWalkDogsBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(item: DogInfo) {
            with(binding.civWalkDogsItem) {
                Glide.with(this.context)
                    .load(item.thumbnailUrl)
                    .error(R.drawable.ic_dog_default_thumbnail)
                    .fallback(R.drawable.ic_dog_default_thumbnail)
                    .into(this)

                borderColor = ContextCompat.getColor(
                    this.context,
                    if (item.isSelected) R.color.very_soft_orange else R.color.dark_grey
                )
            }
            binding.root.setOnClickListener {
                onClick(item.id ?: "")
            }
        }
    }

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<DogInfo>() {
            override fun areItemsTheSame(oldItem: DogInfo, newItem: DogInfo): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: DogInfo, newItem: DogInfo): Boolean {
                return oldItem == newItem
            }

        }
    }
}