package com.nbcfinalteam2.ddaraogae.presentation.ui.stamp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nbcfinalteam2.ddaraogae.databinding.ItemStampDetailBinding
import com.nbcfinalteam2.ddaraogae.presentation.model.StampModel
import com.nbcfinalteam2.ddaraogae.presentation.util.DateFormatter

class StampDetailAdapter : ListAdapter<StampModel, StampDetailAdapter.ViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStampDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemStampDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StampModel) {
            with(binding) {
                tvStampDetailDate.text = item.getDateTime?.let { DateFormatter.getDateFormatter(it) }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StampModel>() {
            override fun areItemsTheSame(oldItem: StampModel, newItem: StampModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: StampModel, newItem: StampModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}