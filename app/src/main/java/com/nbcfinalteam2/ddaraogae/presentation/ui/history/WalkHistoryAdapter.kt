package com.nbcfinalteam2.ddaraogae.presentation.ui.history

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ItemHomeHistoryWalkBinding
import com.nbcfinalteam2.ddaraogae.presentation.model.WalkingInfo
import com.nbcfinalteam2.ddaraogae.presentation.util.DateFormatter
import com.nbcfinalteam2.ddaraogae.presentation.util.TextConverter

class WalkHistoryAdapter(private val onMapClick: (WalkingInfo) -> Unit) :
    ListAdapter<WalkingInfo, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            ItemHomeHistoryWalkBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            parent.context
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(getItem(position), onMapClick)
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    class ViewHolder(
        private val binding: ItemHomeHistoryWalkBinding,
        private val context: Context
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: WalkingInfo, onMapClick: (WalkingInfo) -> Unit) {
            with(binding) {
                tvWalkHistoryDate.text = DateFormatter.getDateFormatter(item.startDateTime ?: return)

                val distance = item.distance ?: 0.0
                tvDistance.text = TextConverter.distanceDoubleToString(distance)

                val timeTaken = item.timeTaken ?: 0
                val hours = timeTaken / 3600
                val minutes = (timeTaken % 3600) / 60
                val seconds = timeTaken % 60

                tvDuration.text = when {
                    hours > 0 -> String.format("%d시간 %d분 %d초", hours, minutes, seconds)
                    minutes > 0 -> String.format("%d분 %d초", minutes, seconds)
                    else -> String.format("%d초", seconds)
                } // "home_history_walk_adapter_minute"

                Glide.with(context)
                    .load(item.walkingImage)
                    .error(R.drawable.img_map_default)
                    .fallback(R.drawable.img_map_default)
                    .into(ivWalkMap)

                ivWalkMap.setOnClickListener {
                    onMapClick(item)
                }
            }
        }
    }

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
}