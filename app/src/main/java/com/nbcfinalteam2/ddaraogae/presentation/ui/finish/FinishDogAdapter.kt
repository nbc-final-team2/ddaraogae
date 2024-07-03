package com.nbcfinalteam2.ddaraogae.presentation.ui.finish

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ItemFinishDogsBinding
import com.nbcfinalteam2.ddaraogae.presentation.model.DogInfo

class FinishDogAdapter(private val dogList: List<DogInfo>) :
    RecyclerView.Adapter<FinishDogAdapter.FinishDogViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FinishDogViewHolder {
        return FinishDogViewHolder(
            ItemFinishDogsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FinishDogViewHolder, position: Int) {
        holder.bind(dogList[position])
    }

    override fun getItemCount(): Int = dogList.size

    class FinishDogViewHolder(
        private val binding: ItemFinishDogsBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(dogData: DogInfo) {
            Glide.with(binding.ivFinishDogImage)
                .load(dogData.thumbnailUrl)
                .error(R.drawable.ic_dog_default_thumbnail)
                .fallback(R.drawable.ic_dog_default_thumbnail)
                .into(binding.ivFinishDogImage)

            binding.tvFinishDogName.text = dogData.name
        }
    }
}