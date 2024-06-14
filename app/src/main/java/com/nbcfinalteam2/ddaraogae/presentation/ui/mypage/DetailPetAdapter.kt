package com.nbcfinalteam2.ddaraogae.presentation.ui.mypage

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ItemEditPetDogSelectionBinding
import com.nbcfinalteam2.ddaraogae.presentation.ui.model.DogItemModel

class DetailPetAdapter(
    private val onItemClick:(DogItemModel) -> Unit
) : ListAdapter<DogItemModel,DetailPetAdapter.ItemViewHolder>(
    object :DiffUtil.ItemCallback<DogItemModel>(){

        override fun areItemsTheSame(oldItem: DogItemModel, newItem: DogItemModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DogItemModel, newItem: DogItemModel): Boolean {
            return oldItem == newItem
        }
    }
) {
    private var selectPos = 0

    inner class ItemViewHolder(
        private val binding:ItemEditPetDogSelectionBinding,
        private val context: Context,
        private val onItemClick: (DogItemModel) -> Unit
    ): RecyclerView.ViewHolder(binding.root){
        fun bind(dogData: DogItemModel, position: Int) = with(binding){
            Glide.with(context)
                .load(dogData.thumbnailUrl)
                .into(ivDogImage)
            dogName.text = dogData.name
            if (selectPos == position) binding.ivDogImage.borderColor = context.resources.getColor(R.color.banana)
            else binding.ivDogImage.borderColor = context.resources.getColor(R.color.white)

            ivDogImage.setOnClickListener {
                val oldPos = selectPos
                selectPos = position

                notifyItemChanged(oldPos)
                notifyItemChanged(selectPos)

                onItemClick(dogData)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DetailPetAdapter.ItemViewHolder {
        return ItemViewHolder(
            ItemEditPetDogSelectionBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), parent.context, onItemClick
        )
    }

    override fun onBindViewHolder(holder: DetailPetAdapter.ItemViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }
}