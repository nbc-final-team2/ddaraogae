package com.nbcfinalteam2.ddaraogae.presentation.ui.finish

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.DialogFragment
import com.nbcfinalteam2.ddaraogae.databinding.FragmentStampDialogBinding
import com.nbcfinalteam2.ddaraogae.domain.entity.StampEntity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StampDialogFragment : DialogFragment() {

    private var _binding: FragmentStampDialogBinding?= null
    private val binding get() = _binding!!

    companion object {
        const val ARG_STAMP_LIST = "stamp"

        fun newInstance(stampList: ArrayList<StampEntity>): StampDialogFragment {
            val fragment = StampDialogFragment()
            val args = Bundle()
            args.putParcelableArrayList(ARG_STAMP_LIST, stampList)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStampDialogBinding.inflate(inflater, container, false)

        dialog?.window?.let { window ->
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window.requestFeature(Window.FEATURE_NO_TITLE)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDialogUI()
        initView()
    }

    private fun setDialogUI() {
        val windowManager = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if (Build.VERSION.SDK_INT < 30) {
            val display = windowManager.defaultDisplay
            val size = Point()

            display.getSize(size)

            val window = this.dialog?.window
            val x = (size.x * 0.8).toInt()

            window?.setLayout(x, ViewGroup.LayoutParams.WRAP_CONTENT)

        } else {
            val rect = windowManager.currentWindowMetrics.bounds
            val window = this.dialog?.window
            val x = (rect.width() * 0.8).toInt()

            window?.setLayout(x, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    private fun initView() {
        val stampList: ArrayList<StampEntity>? = arguments?.getParcelableArrayList(ARG_STAMP_LIST)

        val adapter = stampList?.let { FinishStampViewPagerAdapter(it) }
        binding.vpWalkFinishStampList.adapter = adapter
        binding.diWalkFinishStampList.attachTo(binding.vpWalkFinishStampList)

        binding.btnStampDone.setOnClickListener {
            dismiss()
            (activity as? FinishActivity)?.finish()
        }
    }

    override fun onResume() {
        super.onResume()
    }
}