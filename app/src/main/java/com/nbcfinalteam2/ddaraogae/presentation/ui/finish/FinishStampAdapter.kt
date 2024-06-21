package com.nbcfinalteam2.ddaraogae.presentation.ui.finish

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nbcfinalteam2.ddaraogae.databinding.ItemWalkFinishStampBinding
import com.nbcfinalteam2.ddaraogae.domain.entity.StampEntity


class FinishStampAdapter(private val items: List<StampEntity>) :
    RecyclerView.Adapter<FinishStampAdapter.FinishStampViewHolder>() {

    class FinishStampViewHolder(private val binding: ItemWalkFinishStampBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: StampEntity) {
            binding.tvItemStampName.text = item.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FinishStampViewHolder {
        val binding = ItemWalkFinishStampBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FinishStampViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: FinishStampViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }
}