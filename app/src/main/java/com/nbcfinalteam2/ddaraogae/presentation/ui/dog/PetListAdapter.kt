package com.nbcfinalteam2.ddaraogae.presentation.ui.dog

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ItemPetListBinding
import com.nbcfinalteam2.ddaraogae.presentation.model.DogInfo

class PetListAdapter(
    private val onItemClick:(DogInfo) -> Unit
) : ListAdapter<DogInfo, PetListAdapter.ItemViewHolder>(
    object : DiffUtil.ItemCallback<DogInfo>(){

        override fun areItemsTheSame(oldItem: DogInfo, newItem: DogInfo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DogInfo, newItem: DogInfo): Boolean {
            return oldItem == newItem
        }
    }
){
    inner class ItemViewHolder(
        private val binding:ItemPetListBinding,
        private val context:Context,
        private val onItemClick: (DogInfo) -> Unit
    ) : RecyclerView.ViewHolder(binding.root){
        fun bind(dogData:DogInfo) = with(binding){
            Glide.with(context)
                .load(dogData.thumbnailUrl)
                .error(R.drawable.ic_dog_default_thumbnail)
                .fallback(R.drawable.ic_dog_default_thumbnail)
                .into(civPetListImg)
            tvPetListName.text = dogData.name
            if(dogData.gender == 0) tvPetListGender.text = context.resources.getString(R.string.mypage_edit_pet_dog_gender_male)
            else tvPetListGender.text = context.resources.getString(R.string.mypage_edit_pet_dog_gender_female)

            clItemPetList.setOnClickListener{
                onItemClick(dogData)
            }
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(ItemPetListBinding.inflate(LayoutInflater.from(parent.context), parent, false), parent.context, onItemClick)
    }
}