package com.nbcfinalteam2.ddaraogae.presentation.ui.mypage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nbcfinalteam2.ddaraogae.databinding.ItemEditPetDogSelectionBinding
import com.nbcfinalteam2.ddaraogae.presentation.ui.model.DogItemModel

class DetailPetAdapter(
    private val onItemClick:(DogItemModel) -> Unit
) : ListAdapter<DogItemModel,DetailPetAdapter.ViewHolder>(
    object :DiffUtil.ItemCallback<DogItemModel>(){
        override fun areItemsTheSame(oldItem: DogItemModel, newItem: DogItemModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: DogItemModel, newItem: DogItemModel): Boolean {
            return oldItem == newItem
        }
    }
) {
    abstract class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        abstract fun bind(item:DogItemModel)
    }

    class ItemViewHolder(
        private val binding:ItemEditPetDogSelectionBinding,
        private val context: Context,
        private val onItemClick: (DogItemModel) -> Unit
    ):ViewHolder(binding.root){
        override fun bind(dogData: DogItemModel) = with(binding){
            Glide.with(context)
                .load(dogData.thumbnailUrl)
                .into(ivDogImage)
            dogName.text = dogData.name
            ivDogImage.setOnClickListener {
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

    override fun onBindViewHolder(holder: DetailPetAdapter.ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}