package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nbcfinalteam2.ddaraogae.databinding.ItemHomeAllStampBinding
import com.nbcfinalteam2.ddaraogae.presentation.model.StampModel

class AllStampAdapter() : ListAdapter<StampModel, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            ItemHomeAllStampBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemHomeAllStampBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StampModel) {
            with(binding) {
                tvStampName.text = item.name
                tvStampNum.text = item.stampNum.toString()
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