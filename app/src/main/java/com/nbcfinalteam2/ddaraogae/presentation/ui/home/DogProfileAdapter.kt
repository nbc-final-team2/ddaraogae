package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ItemHomeDogAddBinding
import com.nbcfinalteam2.ddaraogae.databinding.ItemHomeDogSelectionBinding
import com.nbcfinalteam2.ddaraogae.domain.entity.DogEntity

class DogProfileAdapter(private val items: List<DogEntity>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val DOG_ADD = 0
        private const val DOG_SELECTION = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == items.size) {
            DOG_ADD
        } else {
            DOG_SELECTION
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            DOG_ADD -> {
                val binding = ItemHomeDogAddBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AddViewHolder(binding)
            }
            DOG_SELECTION -> {
                val binding = ItemHomeDogSelectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                SelectionViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Error")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SelectionViewHolder -> holder.bind(items[position])
            is AddViewHolder -> holder.bind()
        }
    }

    override fun getItemCount(): Int {
        return items.size + 1
    }

    class SelectionViewHolder(private val binding: ItemHomeDogSelectionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DogEntity) {
            binding.apply {
                Glide.with(ivDogImage.context)
                    .load(item.thumbnailUrl)
                    .into(ivDogImage)
                tvDogName.text = item.name
            }
        }
    }

    class AddViewHolder(private val binding: ItemHomeDogAddBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.apply {
                Glide.with(ivDogAdd.context)
                    .load(R.drawable.ic_dog_add)
                    .into(ivDogAdd)
            }
        }
    }
}