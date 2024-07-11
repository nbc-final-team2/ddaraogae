package com.nbcfinalteam2.ddaraogae.presentation.util

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.nbcfinalteam2.ddaraogae.databinding.FragmentSetDialogBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DialogMaker(
    private val dialogButtonListener: DialogButtonListener,
    private val title: String,
    private val message: String
): DialogFragment() {

    private var _binding: FragmentSetDialogBinding? = null
    private val binding: FragmentSetDialogBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSetDialogBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvSetTitle.text = title
        binding.tvSetMessage1.text = message

        binding.btnConfirm.setOnClickListener {
            dialogButtonListener.onPositiveButtonClicked()
            dismiss()
        }
        binding.btnCancel.setOnClickListener {
            dialogButtonListener.onNegativeButtonClicked()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface DialogButtonListener {
        fun onPositiveButtonClicked()
        fun onNegativeButtonClicked()
    }
}


