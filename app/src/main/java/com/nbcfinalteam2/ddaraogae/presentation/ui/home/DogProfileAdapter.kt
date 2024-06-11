package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ItemHomeDogAddBinding
import com.nbcfinalteam2.ddaraogae.databinding.ItemHomeDogSelectionBinding
import com.nbcfinalteam2.ddaraogae.presentation.model.DogInfo

class DogProfileAdapter(
    private val onDogClickListener: HomeOnClickListener
) : ListAdapter<DogInfo, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private const val DOG_ADD = 0
        private const val DOG_SELECTION = 1

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DogInfo>() {
            override fun areItemsTheSame(oldItem: DogInfo, newItem: DogInfo): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: DogInfo, newItem: DogInfo): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == currentList.size) {
            DOG_ADD
        } else {
            DOG_SELECTION
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            DOG_ADD -> {
                val binding = ItemHomeDogAddBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                AddViewHolder(binding)
            }

            DOG_SELECTION -> {
                val binding = ItemHomeDogSelectionBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                SelectionViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Error")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position == currentList.size) {
            if (holder is AddViewHolder) {
                holder.bind(onDogClickListener)
            }
        } else {
            if (holder is SelectionViewHolder) {
                holder.bind(getItem(position), onDogClickListener)
            }
        }
    }

    override fun getItemCount(): Int {
        return currentList.size + 1
    }

    class SelectionViewHolder(private val binding: ItemHomeDogSelectionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DogInfo, onDogClickListener: HomeOnClickListener) {
            binding.apply {
                Glide.with(ivDogImage.context)
                    .load(item.thumbnailUrl)
                    .into(ivDogImage)
                tvDogName.text = item.name
                ivDogImage.setOnClickListener {
                    onDogClickListener.onDogClick(item)
                }
            }
        }
    }

    class AddViewHolder(private val binding: ItemHomeDogAddBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(onAddClickListener: HomeOnClickListener) {
            binding.apply {
                Glide.with(ivDogAdd.context)
                    .load(R.drawable.ic_dog_add)
                    .into(ivDogAdd)
                ivDogAdd.setOnClickListener {
                    onAddClickListener.onAddClick()
                }
            }
        }
    }
}