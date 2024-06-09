package com.nbcfinalteam2.ddaraogae.presentation.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.nbcfinalteam2.ddaraogae.databinding.FragmentDialogBinding
import java.util.Calendar

class DialogFragment : DialogFragment() {

    private var _binding: FragmentDialogBinding? = null
    private val binding get() = _binding!!
    private lateinit var listener: HistoryOnClickListener
    private var selectedYear: Int = Calendar.getInstance().get(Calendar.YEAR)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvYear.text = selectedYear.toString()

        binding.btnPrevYear.setOnClickListener {
            selectedYear--
            binding.tvYear.text = selectedYear.toString()
        }

        binding.btnNextYear.setOnClickListener {
            selectedYear++
            binding.tvYear.text = selectedYear.toString()
        }
        setupMonthButtons()
    }

    private fun setupMonthButtons() {
        with(binding) {
            val months = arrayOf(
                btnMonth1, btnMonth2, btnMonth3,
                btnMonth4, btnMonth5, btnMonth6,
                btnMonth7, btnMonth8, btnMonth9,
                btnMonth10,btnMonth11, btnMonth12
            )
            months.forEachIndexed { index, button ->
                button.setOnClickListener {
                    listener.onMonthClick(selectedYear, index + 1)
                    dismiss()
                }
            }
        }
    }

    fun setOnMonthClickListener(listener: HistoryOnClickListener) {
        this.listener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
