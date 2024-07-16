package com.nbcfinalteam2.ddaraogae.presentation.util

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.nbcfinalteam2.ddaraogae.databinding.FragmentSetDialogBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InformDialogMaker(
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

    private fun Context.dialogFragmentResize(
        dialogFragment: DialogFragment,
        width: Float,
        height: Float,
    ) {
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if (Build.VERSION.SDK_INT < 30) {
            val display = windowManager.defaultDisplay
            val size = Point()

            display.getSize(size)

            val window = dialogFragment.dialog?.window

            val x = (size.x * width).toInt()
            val y = (size.y * height).toInt()
            window?.setLayout(x, y)
        } else {
            val rect = windowManager.currentWindowMetrics.bounds

            val window = dialogFragment.dialog?.window

            val x = (rect.width() * width).toInt()
            val y = (rect.height() * height).toInt()

            window?.setLayout(x, y)
        }
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

    override fun onResume() {
        super.onResume()
        requireContext().dialogFragmentResize(this@InformDialogMaker, 0.5f, 0.2f)

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
        fun newInstance(title: String, message: String): InformDialogMaker {
            val args = bundleOf("title" to title, "message" to message)

            val fragment = InformDialogMaker()
            fragment.arguments = args
            return fragment
        }
    }
}

interface DialogButtonListener {
    fun onPositiveButtonClicked()
    fun onNegativeButtonClicked()
}


