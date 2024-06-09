package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import android.app.Dialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nbcfinalteam2.ddaraogae.databinding.ItemDialogDatePickerBinding

class DialogAdapter(private val year: Int, private val dialog: Dialog, private val onMonthClickListener: HistoryOnClickListener) : RecyclerView.Adapter<DialogAdapter.ViewHolder>() {

    private val months = arrayOf(
        "1월", "2월", "3월", "4월", "5월", "6월",
        "7월", "8월", "9월", "10월", "11월", "12월"
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDialogDatePickerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(months[position], position + 1, year, dialog, onMonthClickListener)
    }

    override fun getItemCount(): Int {
        return months.size
    }

    class ViewHolder(private val binding: ItemDialogDatePickerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(month: String, monthNumber: Int, year: Int, dialog: Dialog, onMonthClickListener: HistoryOnClickListener) {
            binding.tvMonths.text = month
            binding.tvMonths.setOnClickListener {
                onMonthClickListener.onMonthClick(year, monthNumber)
                dialog.dismiss()
            }
        }
    }
}