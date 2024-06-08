package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nbcfinalteam2.ddaraogae.databinding.ItemDialogDatePickerBinding

class DialogAdapter(private val onMonthClickListener: OnClickListener) : RecyclerView.Adapter<DialogAdapter.ViewHolder>() {

    private val months = arrayOf(
        "1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DialogAdapter.ViewHolder {
        val binding = ItemDialogDatePickerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DialogAdapter.ViewHolder, position: Int) {
        holder.bind(months[position], position + 1, onMonthClickListener)
    }

    override fun getItemCount(): Int {
        return months.size
    }

    class ViewHolder(private val binding: ItemDialogDatePickerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(month: String, monthNumber: Int, onMonthClickListener: OnClickListener) {
            binding.tvMonths.text = month
            binding.tvMonths.setOnClickListener {
                onMonthClickListener.onMonthClick(monthNumber)
            }
        }
    }
}