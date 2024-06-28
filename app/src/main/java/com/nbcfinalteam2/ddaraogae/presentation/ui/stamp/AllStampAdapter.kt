package com.nbcfinalteam2.ddaraogae.presentation.ui.stamp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nbcfinalteam2.ddaraogae.databinding.ItemHomeAllStampBinding
import com.nbcfinalteam2.ddaraogae.presentation.model.StampListModel
import com.nbcfinalteam2.ddaraogae.presentation.model.StampModel

class AllStampAdapter(private val onClick: (StampModel) -> Unit) : ListAdapter<StampModel, AllStampAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemHomeAllStampBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onClick)
    }

    class ViewHolder(private val binding: ItemHomeAllStampBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StampModel, onClick: (StampModel) -> Unit) {
            with(binding) {
                tvStampName.text = item.title
                btnStampClick.setOnClickListener {
                    onClick(item)
                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StampModel>() {
            override fun areItemsTheSame(oldItem: StampModel, newItem: StampModel): Boolean {
                return oldItem.num == newItem.num
            }

            override fun areContentsTheSame(oldItem: StampModel, newItem: StampModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}

