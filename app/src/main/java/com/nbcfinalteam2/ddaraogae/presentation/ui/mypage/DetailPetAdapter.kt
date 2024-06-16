package com.nbcfinalteam2.ddaraogae.presentation.ui.mypage

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ItemEditPetDogSelectionBinding
import com.nbcfinalteam2.ddaraogae.presentation.model.DogInfo

class DetailPetAdapter(
    private val onItemClick:(DogInfo) -> Unit
) : ListAdapter<DogInfo,DetailPetAdapter.ItemViewHolder>(
    object :DiffUtil.ItemCallback<DogInfo>(){

        override fun areItemsTheSame(oldItem: DogInfo, newItem: DogInfo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DogInfo, newItem: DogInfo): Boolean {
            return oldItem == newItem
        }
    }
) {
    private var selectPos = 0

    inner class ItemViewHolder(
        private val binding:ItemEditPetDogSelectionBinding,
        private val context: Context,
        private val onItemClick: (DogInfo) -> Unit
    ): RecyclerView.ViewHolder(binding.root){
        fun bind(dogData: DogInfo, position: Int) = with(binding){
            Glide.with(context)
                .load(dogData.thumbnailUrl)
                .error(R.drawable.ic_dog_default_thumbnail)
                .fallback(R.drawable.ic_dog_default_thumbnail)
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