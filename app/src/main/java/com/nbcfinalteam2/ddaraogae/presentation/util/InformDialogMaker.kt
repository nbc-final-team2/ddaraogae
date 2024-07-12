package com.nbcfinalteam2.ddaraogae.presentation.util

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.nbcfinalteam2.ddaraogae.databinding.FragmentSetDialogBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DialogMaker(
) : DialogFragment() {

    private var dialogButtonListener: DialogButtonListener? = null
    private var title: String = ""
    private var message: String = ""
    private var _binding: FragmentSetDialogBinding? = null
    private val binding: FragmentSetDialogBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = arguments?.getString("title") ?: ""
        message = arguments?.getString("message") ?: ""
    }

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
        binding.tvSetMessage.text = message

        binding.btnConfirm.setOnClickListener {
            dialogButtonListener?.onPositiveButtonClicked()
            dismiss()
        }
        binding.btnCancel.setOnClickListener {
            dialogButtonListener?.onNegativeButtonClicked()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun registerCallBackLister(dialogButtonListener: DialogButtonListener) {
        this.dialogButtonListener = dialogButtonListener

    }

    private fun unRegisterCallBackListener() {
        this.dialogButtonListener = null
    }

    override fun onDestroy() {
        super.onDestroy()
        unRegisterCallBackListener()
    }

    companion object {
        fun newInstance(title: String, message: String): DialogMaker {
            val args = bundleOf("title" to title, "message" to message)

            val fragment = DialogMaker()
            fragment.arguments = args
            return fragment
        }
    }
}

interface DialogButtonListener {
    fun onPositiveButtonClicked()
    fun onNegativeButtonClicked()
}


