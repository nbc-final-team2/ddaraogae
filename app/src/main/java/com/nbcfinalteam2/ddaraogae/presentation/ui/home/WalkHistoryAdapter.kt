package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
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

                val distance = item.distance ?: 0.0
                tvDistance.text = if (distance >= 1) {
                    String.format("%.1f km", distance / 1)
                } else {
                    String.format("%d m", distance.toInt())
                } // "home_history_walk_adapter_km"

                val timeTaken = item.timeTaken ?: 0
                val hours = timeTaken / 3600
                val minutes = (timeTaken % 3600) / 60
                val seconds = timeTaken % 60

                tvDuration.text = when {
                    hours > 0 -> String.format("%d시간 %d분 %d초", hours, minutes, seconds)
                    minutes > 0 -> String.format("%d분 %d초", minutes, seconds)
                    else -> String.format("%d초", seconds)
                } // "home_history_walk_adapter_minute"

                ivWalkMap.setOnClickListener {
                    onMapClick(item.walkingImage ?: "")
                }
            }
        }
    }
}