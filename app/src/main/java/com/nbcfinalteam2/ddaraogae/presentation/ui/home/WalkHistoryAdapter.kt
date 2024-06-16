package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ItemHomeHistoryWalkBinding
import com.nbcfinalteam2.ddaraogae.presentation.model.WalkingInfo
import com.nbcfinalteam2.ddaraogae.presentation.util.DateFormatter

class WalkHistoryAdapter(private val onMapClick: (String) -> Unit) :
    ListAdapter<WalkingInfo, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<WalkingInfo>() {
            override fun areItemsTheSame(oldItem: WalkingInfo, newItem: WalkingInfo): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: WalkingInfo, newItem: WalkingInfo): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            ItemHomeHistoryWalkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(getItem(position), onMapClick)
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    class ViewHolder(private val binding: ItemHomeHistoryWalkBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: WalkingInfo, onMapClick: (String) -> Unit) {
            with(binding) {
                tvWalkHistoryDate.text = DateFormatter.getHistoryDate(item.startDateTime ?: return)
                tvDistance.text = String.format("%.1f km", item.distance) // "home_history_walk_adapter_km"
                tvDuration.text = String.format("%.0f 분", item.timeTaken?.toDouble()) // "home_history_walk_adapter_minute"

                ivWalkMap.setOnClickListener {
                    onMapClick(item.walkingImage ?: "")
                }
            }
        }
    }
}