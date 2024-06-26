package com.nbcfinalteam2.ddaraogae.presentation.ui.alarm

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nbcfinalteam2.ddaraogae.R
import com.nbcfinalteam2.ddaraogae.databinding.ItemAlarmBinding
import com.nbcfinalteam2.ddaraogae.presentation.model.AlarmModel

class AlarmAdapter(
    private val onDeleteClick: (String) -> Unit
) : ListAdapter<AlarmModel, AlarmAdapter.AlarmViewHolder>(DIFF_UTIL) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        return AlarmViewHolder(
            ItemAlarmBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    inner class AlarmViewHolder(
        private val binding: ItemAlarmBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(item: AlarmModel) {
            binding.tvMeridiem.text = binding.tvMeridiem.context.getString(
                if (item.isPm) R.string.alarm_pm else R.string.alarm_am
            )
            binding.tvTime.text = binding.tvTime.context.getString(
                R.string.alarm_time_text, item.setHour, item.setMinute
            )
            binding.ivDeleteButton.setOnClickListener {
                onDeleteClick(item.id)
            }
        }
    }

    companion object {
        private val DIFF_UTIL = object: DiffUtil.ItemCallback<AlarmModel>() {
            override fun areItemsTheSame(oldItem: AlarmModel, newItem: AlarmModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: AlarmModel, newItem: AlarmModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}