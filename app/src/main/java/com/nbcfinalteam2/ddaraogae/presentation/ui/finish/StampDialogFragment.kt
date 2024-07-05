package com.nbcfinalteam2.ddaraogae.presentation.ui.finish

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
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

        setDialogUI()

        return binding.root
    }

    private fun setDialogUI() {
        val windowManager = requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)

        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = size.x
        params?.width = (deviceWidth * 0.8).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
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
}