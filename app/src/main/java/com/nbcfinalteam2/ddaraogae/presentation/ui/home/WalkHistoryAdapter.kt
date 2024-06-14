package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nbcfinalteam2.ddaraogae.databinding.ItemHomeHistoryWalkBinding
import com.nbcfinalteam2.ddaraogae.presentation.model.WalkingInfo

class WalkHistoryAdapter(private val onPolyLineClick: () -> Unit) : ListAdapter<WalkingInfo, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

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
        (holder as ViewHolder).bind(getItem(position), onPolyLineClick)
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    class ViewHolder(private val binding: ItemHomeHistoryWalkBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: WalkingInfo, onPolyLineClick: () -> Unit) {
            with(binding) {
                Glide.with(ivWalkPolyLine.context)
                    .load(item.walkingImage)
                    .into(ivWalkPolyLine)
                tvWalkHistoryDate.text = item.endDateTime.toString()
                tvDistance.text = item.distance.toString()
                tvDuration.text = item.timeTaken.toString()

                ivWalkPolyLine.setOnClickListener {
                    onPolyLineClick()
                }
            }
        }
    }
}