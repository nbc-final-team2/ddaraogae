package com.nbcfinalteam2.ddaraogae.presentation.ui.stamp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nbcfinalteam2.ddaraogae.databinding.ItemHomeAllStampBinding
import com.nbcfinalteam2.ddaraogae.presentation.model.StampListModel
import com.nbcfinalteam2.ddaraogae.presentation.model.StampModel

class AllStampAdapter : ListAdapter<StampListModel, AllStampAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemHomeAllStampBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemHomeAllStampBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StampListModel) {
            with(binding) {
                tvStampName.text = item.title
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StampListModel>() {
            override fun areItemsTheSame(oldItem: StampListModel, newItem: StampListModel): Boolean {
                return oldItem.num == newItem.num
            }

            override fun areContentsTheSame(oldItem: StampListModel, newItem: StampListModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}

