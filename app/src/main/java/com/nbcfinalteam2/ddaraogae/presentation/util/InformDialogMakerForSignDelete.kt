package com.nbcfinalteam2.ddaraogae.presentation.util

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.nbcfinalteam2.ddaraogae.databinding.FragmentSetDialogBinding
import com.nbcfinalteam2.ddaraogae.databinding.FragmentSetDialogForSignDeleteBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InformDialogMakerForSignDelete(
) : DialogFragment() {

    private var dialogButtonListenerForSignDelete: DialogButtonListenerForSignDelete? = null
    private var title: String = ""
    private var customView: View? = null
    private var _binding: FragmentSetDialogForSignDeleteBinding? = null
    private val binding: FragmentSetDialogForSignDeleteBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = arguments?.getString("title") ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSetDialogForSignDeleteBinding.inflate(inflater, container, false)
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
        val window = dialogFragment.dialog?.window
        if (window != null) {
            if (Build.VERSION.SDK_INT < 30) {
                val display = windowManager.defaultDisplay
                val size = Point()
                display.getSize(size)
                val x = (size.x * width).toInt()
                val y = (size.y * height).toInt()
                window.setLayout(x, y)
            } else {
                val rect = windowManager.currentWindowMetrics.bounds
                val x = (rect.width() * width).toInt()
                val y = (rect.height() * height).toInt()
                window.setLayout(x, y)
            }

            val params = window.attributes
            params.gravity = Gravity.CENTER
            window.attributes = params
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvSetTitle.text = title
        // messageView가 설정되어 있으면 이를 사용하고, 그렇지 않으면 기본 메시지를 설정합니다.
        if (customView != null) {
            // 기존 messageView에 해당하는 TextView를 messageView로 교체
            val index = (binding.root as ViewGroup).indexOfChild(binding.tvSetView)
            (binding.root as ViewGroup).removeViewAt(index)
            (binding.root as ViewGroup).addView(customView, index)
        } else {
            binding.tvSetView.text = arguments?.getString("customView") ?: ""
        }

        binding.btnConfirm.setOnClickListener {
            dialogButtonListenerForSignDelete?.onPositiveButtonClicked()
            dismiss()
        }
        binding.btnCancel.setOnClickListener {
            dialogButtonListenerForSignDelete?.onNegativeButtonClicked()
            dismiss()
        }
    }

    fun setCustomView(view: View) {
        customView = view
    }

    override fun onStart() {
        super.onStart()
        requireContext().dialogFragmentResize(this@InformDialogMakerForSignDelete, 0.6f, 0.3f)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun registerCallBackLister(dialogButtonListenerForSignDelete: DialogButtonListenerForSignDelete) {
        this.dialogButtonListenerForSignDelete = dialogButtonListenerForSignDelete

    }

    private fun unRegisterCallBackListener() {
        this.dialogButtonListenerForSignDelete = null
    }

    override fun onDestroy() {
        super.onDestroy()
        unRegisterCallBackListener()
    }

    companion object {
        fun newInstance(title: String): InformDialogMakerForSignDelete {
            val args = bundleOf("title" to title)

            val fragment = InformDialogMakerForSignDelete()
            fragment.arguments = args
            return fragment
        }
    }
}

interface DialogButtonListenerForSignDelete {
    fun onPositiveButtonClicked()
    fun onNegativeButtonClicked()
}


