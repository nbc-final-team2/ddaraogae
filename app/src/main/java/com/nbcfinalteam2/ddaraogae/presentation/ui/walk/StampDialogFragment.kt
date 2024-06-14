package com.nbcfinalteam2.ddaraogae.presentation.ui.walk

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.nbcfinalteam2.ddaraogae.databinding.FragmentStampDialogBinding
import com.nbcfinalteam2.ddaraogae.domain.entity.StampEntity

class StampDialogFragment : DialogFragment() {

    private var _binding: FragmentStampDialogBinding?= null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_STAMP_LIST = "stamp"

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val stampList: ArrayList<StampEntity>? = arguments?.getParcelableArrayList(ARG_STAMP_LIST)
        stampList?.let {
            // 전달받은 stampList를 사용하여 필요한 작업 수행
            Log.d("StampDialogFragment", "Stamp list: $it")
        }

        val adapter = stampList?.let { FinishStampAdapter(it) }
        binding.rvWalkFinishStampList.adapter = adapter

        binding.btnStampDone.setOnClickListener {
            dismiss()
            (activity as? FinishActivity)?.finish()
        }
    }
}