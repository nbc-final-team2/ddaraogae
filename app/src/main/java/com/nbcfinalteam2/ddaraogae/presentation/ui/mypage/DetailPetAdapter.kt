package com.nbcfinalteam2.ddaraogae.presentation.ui.mypage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nbcfinalteam2.ddaraogae.databinding.ItemEditPetDogSelectionBinding

class DetailPetAdapter(val context: Context) : RecyclerView.Adapter<DetailPetAdapter.ItemViewHolder>() {
    var items = listOf<DogModel>()
    private lateinit var itemClickListener: OnItemClickListener
    interface OnItemClickListener{
        fun onClick(v: View, position: Int)
    }
    fun setItemClickListener(onItemClickListener:OnItemClickListener){
        this.itemClickListener = onItemClickListener
    }
    inner class ItemViewHolder(private val binding:ItemEditPetDogSelectionBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(dogData:DogModel) = with(binding){
            Glide.with(context)
                .load(dogData.thumbnailUrl)
                .into(ivDogImage)
            dogName.text = dogData.name
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DetailPetAdapter.ItemViewHolder {
        val binding = ItemEditPetDogSelectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetailPetAdapter.ItemViewHolder, position: Int) {
        holder.bind(items[position])
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }
    }

    override fun getItemCount(): Int = items.size

}