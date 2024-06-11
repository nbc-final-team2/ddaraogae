package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ItemHomeDogAddBinding
import com.nbcfinalteam2.ddaraogae.databinding.ItemHomeDogSelectionBinding
import com.nbcfinalteam2.ddaraogae.presentation.model.DogInfo
import kotlinx.coroutines.withContext

class DogProfileAdapter(
    private val onDogClick: (DogInfo) -> Unit,
    private val onAddClick: () -> Unit,
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
                AddViewHolder(binding, onAddClick)
            }

            DOG_SELECTION -> {
                val binding = ItemHomeDogSelectionBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                SelectionViewHolder(binding, onDogClick)
            }

            else -> throw IllegalArgumentException("Error")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AddViewHolder) {
            holder.bind()
        } else if (holder is SelectionViewHolder) {
            holder.bind(getItem(position))
        }
    }

    override fun getItemCount(): Int {
        return currentList.size + 1
    }

    inner class SelectionViewHolder(
        private val binding: ItemHomeDogSelectionBinding,
        private val onDogClick: (DogInfo) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DogInfo) {
            binding.apply {
                Glide.with(ivDogImage.context)
                    .load(item.thumbnailUrl)
                    .into(ivDogImage)
                tvDogName.text = item.name

                ivDogImage.setOnClickListener {
                    onDogClick(item)
                }
            }
        }
    }

    class AddViewHolder(
        private val binding: ItemHomeDogAddBinding,
        private val onAddClick: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.apply {
                Glide.with(ivDogAdd.context)
                    .load(R.drawable.ic_dog_add)
                    .into(ivDogAdd)
                ivDogAdd.setOnClickListener {
                    onAddClick()
                }
            }
        }
    }
}

